package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "refund_records")
data class RefundRecord(
  @PrimaryKey val refundId: String,
  val orderId: String,
  val trxId: String,
  val originalAmount: Double,
  val refundAmount: Double,
  val reason: String,
  val status: String, // "SUCCESSFUL", "FAILED"
  val gatewayRefundTrx: String,
  val initiatedBy: String,
  val timestamp: Long = System.currentTimeMillis()
)
