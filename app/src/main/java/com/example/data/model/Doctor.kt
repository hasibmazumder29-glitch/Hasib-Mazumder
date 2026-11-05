package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
  @PrimaryKey val id: String,
  val name: String,
  val title: String,
  val specialty: String,
  val hospital: String,
  val bmdcRegNo: String,
  val experienceYears: Int,
  val rating: Float,
  val reviewCount: Int,
  val isOnline: Boolean,
  val availableHours: String,
  val videoFee: Double,
  val audioFee: Double,
  val instantFeePremium: Double,
  val scheduledFee: Double,
  val consultationDurationMinutes: Int,
  val bio: String
)
