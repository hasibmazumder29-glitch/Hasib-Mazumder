package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
  @PrimaryKey val appointmentId: String,
  val orderId: String,
  val doctorId: String,
  val doctorName: String,
  val patientName: String,
  val patientPhone: String,
  val consultationType: ConsultationType,
  val bookingType: BookingType,
  val scheduledTime: String,
  val feePaid: Double,
  val status: AppointmentStatus,
  val doctorAcceptedCall: Boolean,
  val webrtcRoomId: String,
  val digitalPrescription: String = "",
  val patientNotes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
