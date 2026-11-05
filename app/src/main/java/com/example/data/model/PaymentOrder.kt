package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_orders")
data class PaymentOrder(
  @PrimaryKey val orderId: String,
  val appointmentId: String,
  val patientName: String,
  val patientPhone: String,
  val doctorId: String,
  val doctorName: String,
  val consultationType: ConsultationType,
  val bookingType: BookingType,
  val scheduledSlot: String,
  val baseFee: Double,
  val vatAmount: Double,
  val serviceFee: Double,
  val totalAmount: Double,
  val currency: String = "BDT",
  val status: PaymentStatus,
  val paymentMethod: PaymentMethod?,
  val trxId: String?,
  val idempotencyKey: String,
  val signature: String,
  val createdAt: Long,
  val verifiedAt: Long?,
  val isSandbox: Boolean,
  val failureReason: String?,
  val isVerifiedByServer: Boolean,
  val refundStatus: String? = null,
  val refundedAmount: Double = 0.0
)
