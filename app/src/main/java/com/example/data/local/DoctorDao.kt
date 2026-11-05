package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Doctor
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
  @Query("SELECT * FROM doctors ORDER BY isOnline DESC, rating DESC")
  fun getAllDoctors(): Flow<List<Doctor>>

  @Query("SELECT * FROM doctors WHERE id = :doctorId LIMIT 1")
  fun getDoctorById(doctorId: String): Flow<Doctor?>

  @Query("SELECT * FROM doctors WHERE id = :doctorId LIMIT 1")
  suspend fun getDoctorByIdDirect(doctorId: String): Doctor?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDoctors(doctors: List<Doctor>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDoctor(doctor: Doctor)

  @Update
  suspend fun updateDoctor(doctor: Doctor)

  @Query("UPDATE doctors SET isOnline = :isOnline WHERE id = :doctorId")
  suspend fun updateOnlineStatus(doctorId: String, isOnline: Boolean)

  @Query("UPDATE doctors SET videoFee = :videoFee, audioFee = :audioFee, instantFeePremium = :instantPremium, scheduledFee = :scheduledFee, consultationDurationMinutes = :duration, availableHours = :hours WHERE id = :doctorId")
  suspend fun updateDoctorFeesAndSchedule(
    doctorId: String,
    videoFee: Double,
    audioFee: Double,
    instantPremium: Double,
    scheduledFee: Double,
    duration: Int,
    hours: String
  )
}
