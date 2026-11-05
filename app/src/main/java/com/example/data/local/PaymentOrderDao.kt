package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PaymentOrder
import com.example.data.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentOrderDao {
  @Query("SELECT * FROM payment_orders ORDER BY createdAt DESC")
  fun getAllOrders(): Flow<List<PaymentOrder>>

  @Query("SELECT * FROM payment_orders WHERE orderId = :orderId LIMIT 1")
  fun getOrderById(orderId: String): Flow<PaymentOrder?>

  @Query("SELECT * FROM payment_orders WHERE orderId = :orderId LIMIT 1")
  suspend fun getOrderByIdDirect(orderId: String): PaymentOrder?

  @Query("SELECT * FROM payment_orders WHERE idempotencyKey = :idempotencyKey LIMIT 1")
  suspend fun getOrderByIdempotencyKey(idempotencyKey: String): PaymentOrder?

  @Query("SELECT * FROM payment_orders WHERE status = :status ORDER BY createdAt DESC")
  fun getOrdersByStatus(status: PaymentStatus): Flow<List<PaymentOrder>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: PaymentOrder)

  @Update
  suspend fun updateOrder(order: PaymentOrder)

  @Query("UPDATE payment_orders SET status = :status, verifiedAt = :verifiedAt, trxId = :trxId, isVerifiedByServer = :verified WHERE orderId = :orderId")
  suspend fun markOrderPaid(
    orderId: String,
    status: PaymentStatus,
    verifiedAt: Long,
    trxId: String,
    verified: Boolean
  )

  @Query("UPDATE payment_orders SET status = :status, failureReason = :reason WHERE orderId = :orderId")
  suspend fun markOrderFailed(orderId: String, status: PaymentStatus, reason: String)

  @Query("UPDATE payment_orders SET status = :status, refundStatus = :refundStatus, refundedAmount = :amount WHERE orderId = :orderId")
  suspend fun updateOrderRefund(
    orderId: String,
    status: PaymentStatus,
    refundStatus: String,
    amount: Double
  )
}
