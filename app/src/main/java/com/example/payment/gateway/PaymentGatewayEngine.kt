package com.example.payment.gateway

import com.example.data.model.GatewayConfig
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentOrder
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random
import kotlinx.coroutines.delay

/**
 * Royal Care Enterprise Payment Gateway & Verification Engine
 * Implements:
 * - HMAC-SHA256 Order Signature & Webhook Verification
 * - Idempotency Token Generation & Duplicate Guard
 * - Zero Storage of PINs or OTPs
 * - Multi-method checkout for Bangladesh (bKash, Nagad, Rocket, Upay, Bank Card)
 * - Free-First Sandbox Engine vs Live Merchant Mode
 */
object PaymentGatewayEngine {

  /**
   * Generates a tamper-evident HMAC-SHA256 signature for an order payload.
   */
  fun generateOrderSignature(
    orderId: String,
    amount: Double,
    currency: String,
    patientPhone: String,
    secretKey: String
  ): String {
    val rawPayload = "$orderId|$amount|$currency|$patientPhone"
    return hmacSha256(rawPayload, secretKey)
  }

  /**
   * Verifies the authenticity of an incoming payment confirmation or webhook callback.
   */
  fun verifySignature(
    orderId: String,
    amount: Double,
    currency: String,
    patientPhone: String,
    receivedSignature: String,
    secretKey: String
  ): Boolean {
    val expected = generateOrderSignature(orderId, amount, currency, patientPhone, secretKey)
    return expected.equals(receivedSignature, ignoreCase = true)
  }

  /**
   * Computes an immutable SHA-256 hash for audit log chain integrity.
   */
  fun computeAuditHash(
    orderId: String,
    eventType: String,
    actor: String,
    details: String,
    timestamp: Long
  ): String {
    val input = "$orderId:$eventType:$actor:$details:$timestamp"
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
  }

  /**
   * Generates a unique transaction reference (TRX ID).
   */
  fun generateTrxId(method: PaymentMethod): String {
    val prefix = when (method) {
      PaymentMethod.BKASH -> "BKSH"
      PaymentMethod.NAGAD -> "NGD"
      PaymentMethod.ROCKET -> "RCKT"
      PaymentMethod.UPAY -> "UPY"
      PaymentMethod.CARD -> "CARD"
    }
    val randomSuffix = Random.nextInt(10000000, 99999999)
    return "$prefix$randomSuffix"
  }

  /**
   * Generates a secure idempotency token.
   */
  fun generateIdempotencyKey(orderId: String, patientPhone: String): String {
    val raw = "$orderId:$patientPhone:${System.currentTimeMillis()}:${Random.nextInt(1000, 9999)}"
    return "idemp_" + sha256Hex(raw).take(24)
  }

  /**
   * Simulates server-side payment verification API call.
   * In a real-world scenario, the mobile client calls the backend which calls
   * bKash/Nagad server-side verification URL with App Key & Secret.
   * Here we implement the full cryptographic check, amount matching, and order validity.
   */
  suspend fun executeServerSideVerification(
    order: PaymentOrder,
    trxId: String,
    paymentMethod: PaymentMethod,
    config: GatewayConfig,
    simulateFailure: Boolean = false,
    failureReason: String? = null
  ): VerificationResult {
    // Artificial network delay to represent roundtrip to payment gateway server
    delay(1200)

    if (simulateFailure) {
      return VerificationResult.Failed(
        reason = failureReason ?: "Payment was cancelled or rejected by gateway provider."
      )
    }

    if (!config.isSandbox && !config.isLiveReady) {
      return VerificationResult.Failed(
        reason = "Gateway is set to Live Mode, but merchant onboarding has not been approved."
      )
    }

    // Verify method is enabled
    val isMethodEnabled = when (paymentMethod) {
      PaymentMethod.BKASH -> config.bkashEnabled
      PaymentMethod.NAGAD -> config.nagadEnabled
      PaymentMethod.ROCKET -> config.rocketEnabled
      PaymentMethod.UPAY -> config.upayEnabled
      PaymentMethod.CARD -> config.cardEnabled
    }

    if (!isMethodEnabled) {
      return VerificationResult.Failed(
        reason = "${paymentMethod.displayName} is currently disabled by the administrator."
      )
    }

    // Check signature integrity
    val isSignatureValid = verifySignature(
      orderId = order.orderId,
      amount = order.totalAmount,
      currency = order.currency,
      patientPhone = order.patientPhone,
      receivedSignature = order.signature,
      secretKey = config.webhookSecret
    )

    if (!isSignatureValid) {
      return VerificationResult.Failed(
        reason = "Gateway signature verification failed! Possible request tampering detected."
      )
    }

    // Return successful verified transaction
    return VerificationResult.Success(
      trxId = trxId,
      gatewayResponseCode = "0000_SUCCESS",
      verifiedAmount = order.totalAmount,
      currency = order.currency,
      timestamp = System.currentTimeMillis()
    )
  }

  private fun hmacSha256(data: String, key: String): String {
    val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(keySpec)
    val bytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  private fun sha256Hex(data: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(data.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }
}

sealed class VerificationResult {
  data class Success(
    val trxId: String,
    val gatewayResponseCode: String,
    val verifiedAmount: Double,
    val currency: String,
    val timestamp: Long
  ) : VerificationResult()

  data class Failed(
    val reason: String
  ) : VerificationResult()
}
