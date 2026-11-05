package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
  @Query("SELECT * FROM appointments ORDER BY createdAt DESC")
  fun getAllAppointments(): Flow<List<Appointment>>

  @Query("SELECT * FROM appointments WHERE appointmentId = :id LIMIT 1")
  fun getAppointmentById(id: String): Flow<Appointment?>

  @Query("SELECT * FROM appointments WHERE appointmentId = :id LIMIT 1")
  suspend fun getAppointmentByIdDirect(id: String): Appointment?

  @Query("SELECT * FROM appointments WHERE orderId = :orderId LIMIT 1")
  suspend fun getAppointmentByOrderId(orderId: String): Appointment?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAppointment(appointment: Appointment)

  @Update
  suspend fun updateAppointment(appointment: Appointment)

  @Query("UPDATE appointments SET status = :status WHERE appointmentId = :id")
  suspend fun updateStatus(id: String, status: AppointmentStatus)

  @Query("UPDATE appointments SET doctorAcceptedCall = :accepted, status = :status WHERE appointmentId = :id")
  suspend fun updateDoctorCallAcceptance(id: String, accepted: Boolean, status: AppointmentStatus)

  @Query("UPDATE appointments SET digitalPrescription = :prescription, status = :status WHERE appointmentId = :id")
  suspend fun completeConsultation(id: String, prescription: String, status: AppointmentStatus = AppointmentStatus.COMPLETED)
}
