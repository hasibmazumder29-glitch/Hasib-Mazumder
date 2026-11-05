package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GatewayConfig
import com.example.data.model.GatewayProvider
import com.example.ui.components.SandboxWarningBanner
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalSurfaceVariant
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatewayConfigScreen(
  currentConfig: GatewayConfig?,
  onSaveConfig: (GatewayConfig) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val config = currentConfig ?: GatewayConfig()

  var selectedProvider by remember { mutableStateOf(config.provider) }
  var providerDropdownExpanded by remember { mutableStateOf(false) }

  var merchantId by remember { mutableStateOf(config.merchantId) }
  var appKey by remember { mutableStateOf(config.appKey) }
  var appSecret by remember { mutableStateOf(config.appSecretMasked) }
  var isSandbox by remember { mutableStateOf(config.isSandbox) }
  var webhookUrl by remember { mutableStateOf(config.webhookUrl) }
  var webhookSecret by remember { mutableStateOf(config.webhookSecret) }

  // Method toggles
  var bkashEnabled by remember { mutableStateOf(config.bkashEnabled) }
  var nagadEnabled by remember { mutableStateOf(config.nagadEnabled) }
  var rocketEnabled by remember { mutableStateOf(config.rocketEnabled) }
  var upayEnabled by remember { mutableStateOf(config.upayEnabled) }
  var cardEnabled by remember { mutableStateOf(config.cardEnabled) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Payment Gateway Settings",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = RoyalNavy
            )
            Text(
              text = "Free-First Sandbox & Merchant Onboarding",
              fontSize = 11.sp,
              color = RoyalTextSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("gateway_config_back_btn")) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = RoyalNavy)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("gateway_config_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Banner: Free-First Policy
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = RoyalNavyDeep),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = RoyalGold)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "FREE-FIRST INTEGRATION STANDARD",
                color = RoyalGold,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Payment gateway integration remains in Sandbox mode until legitimate merchant credentials are provided by the administrator. Live bKash/Nagad transactions require verified merchant onboarding.",
              color = Color(0xFFCFD6E4),
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }
      }

      item {
        SandboxWarningBanner(isSandbox = isSandbox)
      }

      // Gateway Provider Selector
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "GATEWAY PROVIDER & ENVIRONMENT",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
              expanded = providerDropdownExpanded,
              onExpandedChange = { providerDropdownExpanded = !providerDropdownExpanded }
            ) {
              OutlinedTextField(
                value = selectedProvider.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Payment Provider") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerDropdownExpanded) },
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth()
                  .testTag("provider_dropdown"),
                shape = RoundedCornerShape(12.dp)
              )

              ExposedDropdownMenu(
                expanded = providerDropdownExpanded,
                onDismissRequest = { providerDropdownExpanded = false }
              ) {
                GatewayProvider.values().forEach { provider ->
                  DropdownMenuItem(
                    text = { Text(provider.displayName, fontSize = 13.sp) },
                    onClick = {
                      selectedProvider = provider
                      providerDropdownExpanded = false
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sandbox Mode Toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Sandbox Test Mode",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = RoyalNavy
                )
                Text(
                  text = "Simulates authentic bKash, Nagad, and Card payments without deducting real money.",
                  fontSize = 11.sp,
                  color = RoyalTextSecondary
                )
              }
              Switch(
                checked = isSandbox,
                onCheckedChange = { isSandbox = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFD97706)),
                modifier = Modifier.testTag("sandbox_mode_switch")
              )
            }
          }
        }
      }

      // Merchant API Credentials
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "MERCHANT API CREDENTIALS",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = merchantId,
              onValueChange = { merchantId = it },
              label = { Text("Merchant ID (e.g. ROYALCARE_MCH_019)") },
              modifier = Modifier.fillMaxWidth().testTag("merchant_id_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = appKey,
              onValueChange = { appKey = it },
              label = { Text("API App Key") },
              modifier = Modifier.fillMaxWidth().testTag("app_key_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = appSecret,
              onValueChange = { appSecret = it },
              label = { Text("API App Secret (Masked)") },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier.fillMaxWidth().testTag("app_secret_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // Webhook & Signature Verification Settings
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "WEBHOOK & SIGNATURE VERIFICATION",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = webhookUrl,
              onValueChange = { webhookUrl = it },
              label = { Text("Server Webhook Callback URL") },
              modifier = Modifier.fillMaxWidth().testTag("webhook_url_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = webhookSecret,
              onValueChange = { webhookSecret = it },
              label = { Text("Webhook HMAC-SHA256 Signing Secret") },
              modifier = Modifier.fillMaxWidth().testTag("webhook_secret_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      // Supported Bangladesh Payment Methods Toggles
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "ENABLE / DISABLE PAYMENT METHODS",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Text(
              text = "Activate only the payment methods supported by approved merchant agreements.",
              fontSize = 11.sp,
              color = RoyalTextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            MethodToggleRow(name = "bKash (MFS Checkout)", isEnabled = bkashEnabled, onToggle = { bkashEnabled = it })
            MethodToggleRow(name = "Nagad (Post Office Digital)", isEnabled = nagadEnabled, onToggle = { nagadEnabled = it })
            MethodToggleRow(name = "Rocket (DBBL Banking)", isEnabled = rocketEnabled, onToggle = { rocketEnabled = it })
            MethodToggleRow(name = "Upay (UCB Fintech)", isEnabled = upayEnabled, onToggle = { upayEnabled = it })
            MethodToggleRow(name = "Visa / Mastercard / Amex", isEnabled = cardEnabled, onToggle = { cardEnabled = it })
          }
        }
      }

      // Save Button & Reset
      item {
        Button(
          onClick = {
            val updated = config.copy(
              provider = selectedProvider,
              merchantId = merchantId,
              appKey = appKey,
              appSecretMasked = appSecret,
              isSandbox = isSandbox,
              webhookUrl = webhookUrl,
              webhookSecret = webhookSecret,
              bkashEnabled = bkashEnabled,
              nagadEnabled = nagadEnabled,
              rocketEnabled = rocketEnabled,
              upayEnabled = upayEnabled,
              cardEnabled = cardEnabled,
              updatedAt = System.currentTimeMillis()
            )
            onSaveConfig(updated)
            Toast.makeText(context, "Gateway configuration updated successfully.", Toast.LENGTH_SHORT).show()
            onBack()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("save_gateway_config_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = RoyalGold)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Save Gateway Configuration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
private fun MethodToggleRow(
  name: String,
  isEnabled: Boolean,
  onToggle: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = name,
      fontSize = 13.5.sp,
      fontWeight = FontWeight.Medium,
      color = RoyalNavy
    )
    Switch(
      checked = isEnabled,
      onCheckedChange = onToggle,
      colors = SwitchDefaults.colors(checkedThumbColor = RoyalTeal)
    )
  }
}
