package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gateway_config")
data class GatewayConfig(
  @PrimaryKey val id: Int = 1,
  val provider: GatewayProvider = GatewayProvider.SANDBOX_SIMULATOR,
  val merchantId: String = "ROYALCARE_MCH_019",
  val appKey: String = "rc_live_app_key_secure_demo",
  val appSecretMasked: String = "••••••••••••••••3A9F",
  val isSandbox: Boolean = true,
  val isLiveReady: Boolean = false,
  val webhookUrl: String = "https://api.royalcare.org/v1/payments/webhook",
  val webhookSecret: String = "whsec_royalcare_hmac_sha256_verified",
  val bkashEnabled: Boolean = true,
  val nagadEnabled: Boolean = true,
  val rocketEnabled: Boolean = true,
  val upayEnabled: Boolean = true,
  val cardEnabled: Boolean = true,
  val autoVerifyEnabled: Boolean = true,
  val verificationTimeoutSeconds: Int = 30,
  val updatedAt: Long = System.currentTimeMillis()
)
