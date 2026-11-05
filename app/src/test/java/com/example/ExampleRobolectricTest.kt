package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.RoyalCareDatabase
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.Doctor
import com.example.data.model.GatewayConfig
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.repository.ProcessPaymentResult
import com.example.data.repository.RoyalCareRepository
import com.example.payment.gateway.PaymentGatewayEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var database: RoyalCareDatabase
  private lateinit var repository: RoyalCareRepository

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, RoyalCareDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = RoyalCareRepository(database)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testAppNameString() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Royal Care", appName)
  }

  @Test
  fun testHmacSha256SignatureIntegrity() {
    val secret = "test_webhook_secret_key"
    val orderId = "RC-ORD-20261105-1234"
    val amount = 1312.50
    val phone = "01711223344"

    val signature = PaymentGatewayEngine.generateOrderSignature(
      orderId = orderId,
      amount = amount,
      currency = "BDT",
      patientPhone = phone,
      secretKey = secret
    )

    assertNotNull(signature)
    assertTrue(signature.length >= 32)

    val isValid = PaymentGatewayEngine.verifySignature(
      orderId = orderId,
      amount = amount,
      currency = "BDT",
      patientPhone = phone,
      receivedSignature = signature,
      secretKey = secret
    )
    assertTrue("Signature should verify successfully", isValid)

    val isTampered = PaymentGatewayEngine.verifySignature(
      orderId = orderId,
      amount = amount + 100.0, // Tampered amount
      currency = "BDT",
      patientPhone = phone,
      receivedSignature = signature,
      secretKey = secret
    )
    assertFalse("Tampered amount should fail verification", isTampered)
  }

  @Test
  fun testCreateOrderAndProcessPaymentFlow() = runBlocking {
    val testDoctor = Doctor(
      id = "doc-test-1",
      name = "Dr. Test Specialist",
      title = "MBBS, FCPS",
      specialty = "Cardiology",
      hospital = "Royal Care Central",
      bmdcRegNo = "BMDC A-99999",
      experienceYears = 10,
      rating = 5.0f,
      reviewCount = 50,
      isOnline = true,
      availableHours = "09:00 AM - 05:00 PM",
      videoFee = 1000.0,
      audioFee = 700.0,
      instantFeePremium = 200.0,
      scheduledFee = 900.0,
      consultationDurationMinutes = 20,
      bio = "Test Doctor"
    )

    database.doctorDao().insertDoctor(testDoctor)
    database.gatewayConfigDao().insertConfig(GatewayConfig())

    // Create Order: Video + Instant
    val order = repository.createPaymentOrder(
      patientName = "Rahim Ahmed",
      patientPhone = "01812345678",
      doctor = testDoctor,
      consultationType = ConsultationType.VIDEO,
      bookingType = BookingType.INSTANT,
      scheduledSlot = "Instant Queue"
    )

    // Base: 1000 + 200 = 1200. Service fee: 50. VAT (5% of 1250): 62.5. Total: 1312.5
    assertEquals(1200.0, order.baseFee, 0.01)
    assertEquals(50.0, order.serviceFee, 0.01)
    assertEquals(62.5, order.vatAmount, 0.01)
    assertEquals(1312.5, order.totalAmount, 0.01)
    assertEquals(PaymentStatus.INITIATED, order.status)

    // Verify Payment via bKash
    val payResult = repository.processAndVerifyPayment(
      orderId = order.orderId,
      paymentMethod = PaymentMethod.BKASH
    )

    assertTrue("Payment must succeed", payResult is ProcessPaymentResult.Success)
    val success = payResult as ProcessPaymentResult.Success
    assertEquals(PaymentStatus.SUCCESSFUL, success.order.status)
    assertNotNull(success.order.trxId)
    assertTrue(success.order.trxId!!.startsWith("BKSH"))

    // Check appointment was created and marked READY_TO_JOIN
    assertEquals(order.appointmentId, success.appointment.appointmentId)

    // Test Admin Refund
    val refundSuccess = repository.processRefund(
      orderId = order.orderId,
      refundAmount = order.totalAmount,
      reason = "Patient request cancellation"
    )
    assertTrue("Refund must succeed", refundSuccess)

    val updatedOrder = database.paymentOrderDao().getOrderByIdDirect(order.orderId)
    assertEquals(PaymentStatus.REFUNDED, updatedOrder?.status)
    assertEquals(order.totalAmount, updatedOrder?.refundedAmount ?: 0.0, 0.01)
  }

  @Test
  fun testDoctorFeeUpdate() = runBlocking {
    val doctor = Doctor(
      id = "doc-test-2",
      name = "Dr. Fee Update",
      title = "MBBS",
      specialty = "Dermatology",
      hospital = "Royal Care",
      bmdcRegNo = "BMDC A-11111",
      experienceYears = 8,
      rating = 4.8f,
      reviewCount = 20,
      isOnline = false,
      availableHours = "10:00 AM - 01:00 PM",
      videoFee = 800.0,
      audioFee = 500.0,
      instantFeePremium = 150.0,
      scheduledFee = 750.0,
      consultationDurationMinutes = 15,
      bio = "Dermatologist"
    )
    database.doctorDao().insertDoctor(doctor)

    repository.updateDoctorFees(
      doctorId = doctor.id,
      videoFee = 1500.0,
      audioFee = 1000.0,
      instantPremium = 300.0,
      scheduledFee = 1400.0,
      duration = 25,
      hours = "02:00 PM - 06:00 PM",
      isOnline = true
    )

    val updated = database.doctorDao().getDoctorByIdDirect(doctor.id)
    assertNotNull(updated)
    assertEquals(1500.0, updated?.videoFee ?: 0.0, 0.01)
    assertEquals(1000.0, updated?.audioFee ?: 0.0, 0.01)
    assertEquals(25, updated?.consultationDurationMinutes)
    assertTrue(updated?.isOnline == true)
  }
}
