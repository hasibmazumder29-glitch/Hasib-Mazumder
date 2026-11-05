package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.model.RefundRecord
import com.example.data.repository.ProcessPaymentResult
import com.example.data.repository.RoyalCareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val title: String) {
  object Doctors : Screen("Consultation")
  object BookingConfirm : Screen("Fee Breakdown")
  object Checkout : Screen("Secure Checkout")
  object Receipt : Screen("Digital Receipt")
  object TelemedicineRoom : Screen("Consultation Room")
  object PatientHistory : Screen("Payment History")
  object AdminDashboard : Screen("Admin Dashboard")
  object GatewayConfigView : Screen("Gateway Settings")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val database = RoyalCareDatabase.getDatabase(application, viewModelScope)
  val repository = RoyalCareRepository(database)

  init {
    viewModelScope.launch {
      repository.ensureInitialized()
    }
  }

  val doctors: StateFlow<List<Doctor>> = repository.allDoctors
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val orders: StateFlow<List<PaymentOrder>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val appointments: StateFlow<List<Appointment>> = repository.allAppointments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val gatewayConfig: StateFlow<GatewayConfig?> = repository.gatewayConfig
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val auditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val refunds: StateFlow<List<RefundRecord>> = repository.allRefunds
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Navigation & Active Flow State
  private val _currentScreen = MutableStateFlow<Screen>(Screen.Doctors)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
  val selectedDoctor: StateFlow<Doctor?> = _selectedDoctor.asStateFlow()

  private val _selectedConsultationType = MutableStateFlow(ConsultationType.VIDEO)
  val selectedConsultationType: StateFlow<ConsultationType> = _selectedConsultationType.asStateFlow()

  private val _selectedBookingType = MutableStateFlow(BookingType.INSTANT)
  val selectedBookingType: StateFlow<BookingType> = _selectedBookingType.asStateFlow()

  private val _selectedSlot = MutableStateFlow("Today, Instant Queue (Within 5 mins)")
  val selectedSlot: StateFlow<String> = _selectedSlot.asStateFlow()

  private val _currentOrder = MutableStateFlow<PaymentOrder?>(null)
  val currentOrder: StateFlow<PaymentOrder?> = _currentOrder.asStateFlow()

  private val _activeAppointment = MutableStateFlow<Appointment?>(null)
  val activeAppointment: StateFlow<Appointment?> = _activeAppointment.asStateFlow()

  private val _isPaymentProcessing = MutableStateFlow(false)
  val isPaymentProcessing: StateFlow<Boolean> = _isPaymentProcessing.asStateFlow()

  private val _paymentErrorMessage = MutableStateFlow<String?>(null)
  val paymentErrorMessage: StateFlow<String?> = _paymentErrorMessage.asStateFlow()

  // Doctor Incoming Instant Call Alert State (simulation)
  private val _incomingCallAppointment = MutableStateFlow<Appointment?>(null)
  val incomingCallAppointment: StateFlow<Appointment?> = _incomingCallAppointment.asStateFlow()

  fun navigateTo(screen: Screen) {
    _currentScreen.value = screen
  }

  fun selectDoctorForBooking(
    doctor: Doctor,
    consultationType: ConsultationType = ConsultationType.VIDEO,
    bookingType: BookingType = if (doctor.isOnline) BookingType.INSTANT else BookingType.SCHEDULED
  ) {
    _selectedDoctor.value = doctor
    _selectedConsultationType.value = consultationType
    _selectedBookingType.value = bookingType
    _selectedSlot.value = if (bookingType == BookingType.INSTANT) {
      "Today, Instant Queue (Within 5 mins)"
    } else {
      "Tomorrow, ${doctor.availableHours.split(",").firstOrNull()?.trim() ?: "10:00 AM"}"
    }
    _currentScreen.value = Screen.BookingConfirm
  }

  fun setConsultationType(type: ConsultationType) {
    _selectedConsultationType.value = type
  }

  fun setBookingType(type: BookingType) {
    _selectedBookingType.value = type
    val doc = _selectedDoctor.value
    if (doc != null) {
      _selectedSlot.value = if (type == BookingType.INSTANT) {
        "Today, Instant Queue (Within 5 mins)"
      } else {
        "Tomorrow, ${doc.availableHours.split(",").firstOrNull()?.trim() ?: "10:00 AM"}"
      }
    }
  }

  fun setSlot(slot: String) {
    _selectedSlot.value = slot
  }

  fun confirmBookingAndProceedToCheckout(patientName: String, patientPhone: String) {
    val doc = _selectedDoctor.value ?: return
    viewModelScope.launch {
      val order = repository.createPaymentOrder(
        patientName = patientName.ifBlank { "Md. Shahinur Alam" },
        patientPhone = patientPhone.ifBlank { "01712345678" },
        doctor = doc,
        consultationType = _selectedConsultationType.value,
        bookingType = _selectedBookingType.value,
        scheduledSlot = _selectedSlot.value
      )
      _currentOrder.value = order
      _paymentErrorMessage.value = null
      _currentScreen.value = Screen.Checkout
    }
  }

  fun processPayment(
    paymentMethod: PaymentMethod,
    simulateFailure: Boolean = false,
    failureReason: String? = null
  ) {
    val order = _currentOrder.value ?: return
    _isPaymentProcessing.value = true
    _paymentErrorMessage.value = null

    viewModelScope.launch {
      val result = repository.processAndVerifyPayment(
        orderId = order.orderId,
        paymentMethod = paymentMethod,
        simulateFailure = simulateFailure,
        failureReason = failureReason
      )
      _isPaymentProcessing.value = false

      when (result) {
        is ProcessPaymentResult.Success -> {
          _currentOrder.value = result.order
          _activeAppointment.value = result.appointment
          _currentScreen.value = Screen.Receipt

          // If instant booking, simulate doctor receiving incoming call notification
          if (result.appointment.bookingType == BookingType.INSTANT) {
            _incomingCallAppointment.value = result.appointment
          }
        }
        is ProcessPaymentResult.AlreadyPaid -> {
          _currentOrder.value = result.order
          _currentScreen.value = Screen.Receipt
        }
        is ProcessPaymentResult.Failed -> {
          _paymentErrorMessage.value = result.reason
        }
        is ProcessPaymentResult.Error -> {
          _paymentErrorMessage.value = result.message
        }
      }
    }
  }

  fun openReceiptForOrder(order: PaymentOrder) {
    _currentOrder.value = order
    viewModelScope.launch {
      val apt = orders.value.find { it.orderId == order.orderId }?.let {
        appointments.value.find { apt -> apt.orderId == order.orderId }
      }
      _activeAppointment.value = apt
      _currentScreen.value = Screen.Receipt
    }
  }

  fun openConsultationRoom(appointment: Appointment) {
    _activeAppointment.value = appointment
    _currentScreen.value = Screen.TelemedicineRoom
  }

  fun respondToIncomingCall(appointmentId: String, accept: Boolean) {
    viewModelScope.launch {
      repository.respondToInstantCall(appointmentId, accept)
      _incomingCallAppointment.value = null
      val updatedApt = appointments.value.find { it.appointmentId == appointmentId }
      if (accept && updatedApt != null) {
        _activeAppointment.value = updatedApt
        _currentScreen.value = Screen.TelemedicineRoom
      }
    }
  }

  fun dismissIncomingCallDialog() {
    _incomingCallAppointment.value = null
  }

  fun completeConsultation(appointmentId: String, prescription: String) {
    viewModelScope.launch {
      repository.completeConsultation(appointmentId, prescription)
      val updated = appointments.value.find { it.appointmentId == appointmentId }?.copy(
        status = AppointmentStatus.COMPLETED,
        digitalPrescription = prescription
      )
      _activeAppointment.value = updated
    }
  }

  fun executeAdminRefund(orderId: String, amount: Double, reason: String) {
    viewModelScope.launch {
      repository.processRefund(
        orderId = orderId,
        refundAmount = amount,
        reason = reason,
        initiatedBy = "ADMIN_CONSOLE"
      )
    }
  }

  fun updateDoctorFeeConfig(
    doctorId: String,
    videoFee: Double,
    audioFee: Double,
    instantPremium: Double,
    scheduledFee: Double,
    duration: Int,
    hours: String,
    isOnline: Boolean
  ) {
    viewModelScope.launch {
      repository.updateDoctorFees(
        doctorId = doctorId,
        videoFee = videoFee,
        audioFee = audioFee,
        instantPremium = instantPremium,
        scheduledFee = scheduledFee,
        duration = duration,
        hours = hours,
        isOnline = isOnline
      )
    }
  }

  fun updateGatewayConfiguration(config: GatewayConfig) {
    viewModelScope.launch {
      repository.updateGatewayConfig(config)
    }
  }
}
