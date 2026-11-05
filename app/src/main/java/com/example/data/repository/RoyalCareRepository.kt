package com.example.data.repository

import com.example.data.local.RoyalCareDatabase
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.AuditLog
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.Doctor
import com.example.data.model.GatewayConfig
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentOrder
import com.example.data.model.PaymentStatus
import com.example.data.model.RefundRecord
import com.example.payment.gateway.PaymentGatewayEngine
import com.example.payment.gateway.VerificationResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoyalCareRepository(
  private val database: RoyalCareDatabase
) {
  private val doctorDao = database.doctorDao()
  private val orderDao = database.paymentOrderDao()
  private val appointmentDao = database.appointmentDao()
  private val gatewayDao = database.gatewayConfigDao()
  private val auditDao = database.auditLogDao()
  private val refundDao = database.refundDao()

  val allDoctors: Flow<List<Doctor>> = doctorDao.getAllDoctors()
  val allOrders: Flow<List<PaymentOrder>> = orderDao.getAllOrders()
  val allAppointments: Flow<List<Appointment>> = appointmentDao.getAllAppointments()
  val gatewayConfig: Flow<GatewayConfig?> = gatewayDao.getConfig()
  val allAuditLogs: Flow<List<AuditLog>> = auditDao.getAllLogs()
  val allRefunds: Flow<List<RefundRecord>> = refundDao.getAllRefunds()

  fun getDoctorById(id: String): Flow<Doctor?> = doctorDao.getDoctorById(id)
  fun getOrderById(id: String): Flow<PaymentOrder?> = orderDao.getOrderById(id)
  fun getAppointmentById(id: String): Flow<Appointment?> = appointmentDao.getAppointmentById(id)

  suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
    if (gatewayDao.getConfigDirect() == null) {
      RoyalCareDatabase.populateInitialData(database)
    }
  }

  /**
   * Step 4 & 5: Create a unique payment order with fee breakdown, idempotency token, and HMAC signature
   */
  suspend fun createPaymentOrder(
    patientName: String,
    patientPhone: String,
    doctor: Doctor,
    consultationType: ConsultationType,
    bookingType: BookingType,
    scheduledSlot: String
  ): PaymentOrder = withContext(Dispatchers.IO) {
    val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    val randomNum = Random.nextInt(1000, 9999)
    val orderId = "RC-ORD-$dateStr-$randomNum"
    val appointmentId = "RC-APT-$dateStr-${Random.nextInt(10000, 99999)}"

    // Calculate exact fee breakdown
    val baseConsultationFee = when (consultationType) {
      ConsultationType.VIDEO -> doctor.videoFee
      ConsultationType.AUDIO -> doctor.audioFee
    }

    val typeFee = when (bookingType) {
      BookingType.INSTANT -> baseConsultationFee + doctor.instantFeePremium
      BookingType.SCHEDULED -> baseConsultationFee
    }

    val platformServiceFee = 50.0 // Standard digital platform charge
    val vatAmount = (typeFee + platformServiceFee) * 0.05 // 5% VAT in Bangladesh
    val totalAmount = typeFee + platformServiceFee + vatAmount

    val config = gatewayDao.getConfigDirect() ?: GatewayConfig()
    val idempotencyKey = PaymentGatewayEngine.generateIdempotencyKey(orderId, patientPhone)
    val signature = PaymentGatewayEngine.generateOrderSignature(
      orderId = orderId,
      amount = totalAmount,
      currency = "BDT",
      patientPhone = patientPhone,
      secretKey = config.webhookSecret
    )

    val newOrder = PaymentOrder(
      orderId = orderId,
      appointmentId = appointmentId,
      patientName = patientName,
      patientPhone = patientPhone,
      doctorId = doctor.id,
      doctorName = doctor.name,
      consultationType = consultationType,
      bookingType = bookingType,
      scheduledSlot = scheduledSlot,
      baseFee = typeFee,
      vatAmount = vatAmount,
      serviceFee = platformServiceFee,
      totalAmount = totalAmount,
      currency = "BDT",
      status = PaymentStatus.INITIATED,
      paymentMethod = null,
      trxId = null,
      idempotencyKey = idempotencyKey,
      signature = signature,
      createdAt = System.currentTimeMillis(),
      verifiedAt = null,
      isSandbox = config.isSandbox,
      failureReason = null,
      isVerifiedByServer = false
    )

    orderDao.insertOrder(newOrder)

    // Audit log
    val hash = PaymentGatewayEngine.computeAuditHash(
      orderId = orderId,
      eventType = "ORDER_INITIATED",
      actor = "PATIENT_APP",
      details = "Order created for ${doctor.name} (${consultationType.label}, ${bookingType.label}). Payable: ৳ $totalAmount BDT",
      timestamp = System.currentTimeMillis()
    )

    auditDao.insertLog(
      AuditLog(
        orderId = orderId,
        eventType = "ORDER_INITIATED",
        actor = "PATIENT_APP",
        statusBefore = "NONE",
        statusAfter = PaymentStatus.INITIATED.name,
        details = "Payment order generated with idempotency key $idempotencyKey",
        hashSignature = hash
      )
    )

    newOrder
  }

  /**
   * Step 7, 8 & 9: Complete payment, verify through server-side verification API,
   * mark as PAID, generate appointment and activate consultation room.
   */
  suspend fun processAndVerifyPayment(
    orderId: String,
    paymentMethod: PaymentMethod,
    simulateFailure: Boolean = false,
    failureReason: String? = null
  ): ProcessPaymentResult = withContext(Dispatchers.IO) {
    val order = orderDao.getOrderByIdDirect(orderId)
      ?: return@withContext ProcessPaymentResult.Error("Order $orderId not found.")

    val config = gatewayDao.getConfigDirect() ?: GatewayConfig()

    // Guard: Prevent double-processing
    if (order.status == PaymentStatus.SUCCESSFUL) {
      return@withContext ProcessPaymentResult.AlreadyPaid(order)
    }

    // Step 8: Call server-side verification API
    val generatedTrxId = PaymentGatewayEngine.generateTrxId(paymentMethod)
    val verificationResult = PaymentGatewayEngine.executeServerSideVerification(
      order = order,
      trxId = generatedTrxId,
      paymentMethod = paymentMethod,
      config = config,
      simulateFailure = simulateFailure,
      failureReason = failureReason
    )

    when (verificationResult) {
      is VerificationResult.Success -> {
        // Step 9: Mark as PAID only after confirmed verification
        orderDao.markOrderPaid(
          orderId = order.orderId,
          status = PaymentStatus.SUCCESSFUL,
          verifiedAt = verificationResult.timestamp,
          trxId = verificationResult.trxId,
          verified = true
        )

        // Step 10: Generate unique appointment and digital receipt
        val webrtcRoomId = "rc-room-${order.orderId.takeLast(6)}-${Random.nextInt(100, 999)}"
        val initialAppointmentStatus = if (order.bookingType == BookingType.INSTANT) {
          AppointmentStatus.READY_TO_JOIN
        } else {
          AppointmentStatus.SCHEDULED
        }

        val appointment = Appointment(
          appointmentId = order.appointmentId,
          orderId = order.orderId,
          doctorId = order.doctorId,
          doctorName = order.doctorName,
          patientName = order.patientName,
          patientPhone = order.patientPhone,
          consultationType = order.consultationType,
          bookingType = order.bookingType,
          scheduledTime = order.scheduledSlot,
          feePaid = order.totalAmount,
          status = initialAppointmentStatus,
          doctorAcceptedCall = (order.bookingType != BookingType.INSTANT), // Scheduled appointments auto-accepted
          webrtcRoomId = webrtcRoomId,
          digitalPrescription = "",
          patientNotes = "Patient confirmed payment of ৳ ${order.totalAmount} BDT via ${paymentMethod.displayName} (TRX: ${verificationResult.trxId})",
          createdAt = System.currentTimeMillis()
        )

        appointmentDao.insertAppointment(appointment)

        // Immutable Audit Trail
        val auditHash = PaymentGatewayEngine.computeAuditHash(
          orderId = order.orderId,
          eventType = "PAYMENT_CONFIRMED",
          actor = "VERIFICATION_ENGINE",
          details = "Server-side verified: TRX ${verificationResult.trxId}, Amount: ৳ ${verificationResult.verifiedAmount} ${verificationResult.currency}",
          timestamp = System.currentTimeMillis()
        )

        auditDao.insertLog(
          AuditLog(
            orderId = order.orderId,
            eventType = "PAYMENT_CONFIRMED",
            actor = "VERIFICATION_ENGINE",
            statusBefore = order.status.name,
            statusAfter = PaymentStatus.SUCCESSFUL.name,
            details = "Verified via ${config.provider.displayName}. Webhook signature validated. Appointment ${order.appointmentId} created.",
            hashSignature = auditHash
          )
        )

        val updatedOrder = orderDao.getOrderByIdDirect(orderId)!!
        ProcessPaymentResult.Success(updatedOrder, appointment)
      }

      is VerificationResult.Failed -> {
        orderDao.markOrderFailed(
          orderId = order.orderId,
          status = PaymentStatus.FAILED,
          reason = verificationResult.reason
        )

        val auditHash = PaymentGatewayEngine.computeAuditHash(
          orderId = order.orderId,
          eventType = "PAYMENT_FAILED",
          actor = "VERIFICATION_ENGINE",
          details = "Payment failed: ${verificationResult.reason}",
          timestamp = System.currentTimeMillis()
        )

        auditDao.insertLog(
          AuditLog(
            orderId = order.orderId,
            eventType = "PAYMENT_FAILED",
            actor = "VERIFICATION_ENGINE",
            statusBefore = order.status.name,
            statusAfter = PaymentStatus.FAILED.name,
            details = verificationResult.reason,
            hashSignature = auditHash
          )
        )

        ProcessPaymentResult.Failed(verificationResult.reason)
      }
    }
  }

  /**
   * Step 4: Doctor accept or reject instant call
   */
  suspend fun respondToInstantCall(
    appointmentId: String,
    accept: Boolean
  ) = withContext(Dispatchers.IO) {
    val appointment = appointmentDao.getAppointmentByIdDirect(appointmentId) ?: return@withContext
    if (accept) {
      appointmentDao.updateDoctorCallAcceptance(
        id = appointmentId,
        accepted = true,
        status = AppointmentStatus.READY_TO_JOIN
      )

      val hash = PaymentGatewayEngine.computeAuditHash(
        orderId = appointment.orderId,
        eventType = "DOCTOR_CALL_ACCEPTED",
        actor = "DOCTOR_PORTAL",
        details = "Doctor ${appointment.doctorName} accepted instant call. Room ${appointment.webrtcRoomId} is active.",
        timestamp = System.currentTimeMillis()
      )
      auditDao.insertLog(
        AuditLog(
          orderId = appointment.orderId,
          eventType = "DOCTOR_CALL_ACCEPTED",
          actor = "DOCTOR_PORTAL",
          statusBefore = appointment.status.name,
          statusAfter = AppointmentStatus.READY_TO_JOIN.name,
          details = "Secure room opened.",
          hashSignature = hash
        )
      )
    } else {
      // Doctor rejected call -> update appointment and trigger automatic full refund
      appointmentDao.updateDoctorCallAcceptance(
        id = appointmentId,
        accepted = false,
        status = AppointmentStatus.CANCELLED
      )

      val order = orderDao.getOrderByIdDirect(appointment.orderId)
      if (order != null && order.status == PaymentStatus.SUCCESSFUL) {
        processRefund(
          orderId = order.orderId,
          refundAmount = order.totalAmount,
          reason = "Doctor unavailable/rejected instant consultation request. Automated full refund issued.",
          initiatedBy = "AUTOMATED_SYSTEM"
        )
      }
    }
  }

  /**
   * Complete consultation & generate digital prescription
   */
  suspend fun completeConsultation(
    appointmentId: String,
    prescriptionText: String
  ) = withContext(Dispatchers.IO) {
    appointmentDao.completeConsultation(appointmentId, prescriptionText)
    val appointment = appointmentDao.getAppointmentByIdDirect(appointmentId) ?: return@withContext

    val hash = PaymentGatewayEngine.computeAuditHash(
      orderId = appointment.orderId,
      eventType = "CONSULTATION_COMPLETED",
      actor = "DOCTOR_PAD",
      details = "Consultation completed. Digital prescription generated.",
      timestamp = System.currentTimeMillis()
    )
    auditDao.insertLog(
      AuditLog(
        orderId = appointment.orderId,
        eventType = "CONSULTATION_COMPLETED",
        actor = "DOCTOR_PAD",
        statusBefore = appointment.status.name,
        statusAfter = AppointmentStatus.COMPLETED.name,
        details = "Consultation closed with prescription notes.",
        hashSignature = hash
      )
    )
  }

  /**
   * Step 5: Admin Refund Execution
   */
  suspend fun processRefund(
    orderId: String,
    refundAmount: Double,
    reason: String,
    initiatedBy: String = "ADMIN"
  ): Boolean = withContext(Dispatchers.IO) {
    val order = orderDao.getOrderByIdDirect(orderId) ?: return@withContext false
    if (order.status != PaymentStatus.SUCCESSFUL && order.status != PaymentStatus.PARTIALLY_REFUNDED) {
      return@withContext false
    }

    val refundId = "RC-REF-${Random.nextInt(10000, 99999)}"
    val gatewayRefundTrx = "REF_TRX_${Random.nextInt(100000, 999999)}"

    val newStatus = if (refundAmount >= order.totalAmount) {
      PaymentStatus.REFUNDED
    } else {
      PaymentStatus.PARTIALLY_REFUNDED
    }

    orderDao.updateOrderRefund(
      orderId = orderId,
      status = newStatus,
      refundStatus = "REFUNDED_VIA_GATEWAY",
      amount = refundAmount
    )

    val refundRecord = RefundRecord(
      refundId = refundId,
      orderId = orderId,
      trxId = order.trxId ?: "N/A",
      originalAmount = order.totalAmount,
      refundAmount = refundAmount,
      reason = reason,
      status = "SUCCESSFUL",
      gatewayRefundTrx = gatewayRefundTrx,
      initiatedBy = initiatedBy,
      timestamp = System.currentTimeMillis()
    )

    refundDao.insertRefund(refundRecord)

    // Audit log
    val hash = PaymentGatewayEngine.computeAuditHash(
      orderId = orderId,
      eventType = "REFUND_EXECUTED",
      actor = initiatedBy,
      details = "Refund of ৳ $refundAmount BDT issued. Reason: $reason (Gateway Ref: $gatewayRefundTrx)",
      timestamp = System.currentTimeMillis()
    )

    auditDao.insertLog(
      AuditLog(
        orderId = orderId,
        eventType = "REFUND_EXECUTED",
        actor = initiatedBy,
        statusBefore = order.status.name,
        statusAfter = newStatus.name,
        details = "Refund Record $refundId generated for original TRX ${order.trxId}",
        hashSignature = hash
      )
    )

    true
  }

  /**
   * Admin: Configure doctor fees & schedules
   */
  suspend fun updateDoctorFees(
    doctorId: String,
    videoFee: Double,
    audioFee: Double,
    instantPremium: Double,
    scheduledFee: Double,
    duration: Int,
    hours: String,
    isOnline: Boolean
  ) = withContext(Dispatchers.IO) {
    doctorDao.updateDoctorFeesAndSchedule(
      doctorId = doctorId,
      videoFee = videoFee,
      audioFee = audioFee,
      instantPremium = instantPremium,
      scheduledFee = scheduledFee,
      duration = duration,
      hours = hours
    )
    doctorDao.updateOnlineStatus(doctorId, isOnline)
  }

  /**
   * Admin: Update Gateway Configuration
   */
  suspend fun updateGatewayConfig(config: GatewayConfig) = withContext(Dispatchers.IO) {
    gatewayDao.updateConfig(config)
  }
}

sealed class ProcessPaymentResult {
  data class Success(val order: PaymentOrder, val appointment: Appointment) : ProcessPaymentResult()
  data class AlreadyPaid(val order: PaymentOrder) : ProcessPaymentResult()
  data class Failed(val reason: String) : ProcessPaymentResult()
  data class Error(val message: String) : ProcessPaymentResult()
}
