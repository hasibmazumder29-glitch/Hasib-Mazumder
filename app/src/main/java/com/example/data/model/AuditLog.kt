package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLog(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val orderId: String,
  val eventType: String,
  val actor: String,
  val statusBefore: String,
  val statusAfter: String,
  val details: String,
  val hashSignature: String
)
