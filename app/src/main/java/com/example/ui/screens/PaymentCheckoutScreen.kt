package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GatewayConfig
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentOrder
import com.example.ui.components.SandboxWarningBanner
import com.example.ui.components.SecurityGuaranteesCard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.CardNavy
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketViolet
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyLight
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalSurfaceVariant
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.UpayNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutScreen(
  order: PaymentOrder,
  gatewayConfig: GatewayConfig?,
  isProcessing: Boolean,
  errorMessage: String?,
  onPay: (PaymentMethod, Boolean, String?) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val config = gatewayConfig ?: GatewayConfig()

  // Available methods based on gateway config
  val availableMethods = mutableListOf<PaymentMethod>().apply {
    if (config.bkashEnabled) add(PaymentMethod.BKASH)
    if (config.nagadEnabled) add(PaymentMethod.NAGAD)
    if (config.rocketEnabled) add(PaymentMethod.ROCKET)
    if (config.upayEnabled) add(PaymentMethod.UPAY)
    if (config.cardEnabled) add(PaymentMethod.CARD)
  }

  var selectedMethod by remember {
    mutableStateOf(availableMethods.firstOrNull() ?: PaymentMethod.BKASH)
  }

  var showSimulatedGatewayDialog by remember { mutableStateOf(false) }
  var simulateFailure by remember { mutableStateOf(false) }

  // Simulated gateway dialog form states
  var gatewayMobileNumber by remember { mutableStateOf(order.patientPhone) }
  var gatewayOtp by remember { mutableStateOf("123456") }
  var gatewayPin by remember { mutableStateOf("•••••") }
  var gatewayStep by remember { mutableStateOf(1) } // 1: Phone, 2: OTP, 3: PIN

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Secure Payment Checkout",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = RoyalNavy
            )
            Text(
              text = "Order: ${order.orderId}",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              color = RoyalTextSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_button")) {
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
        .testTag("payment_checkout_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Order Summary Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = RoyalNavyDeep),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "CONSULTATION ORDER SUMMARY",
                color = RoyalGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
              )
              Surface(
                color = RoyalGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = order.bookingType.label.uppercase(),
                  color = RoyalGold,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = order.doctorName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "${order.consultationType.label} • ${order.scheduledSlot}",
              color = Color(0xFFCFD6E4),
              fontSize = 12.sp
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = RoyalNavyLight)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Payable Consultation Amount", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text(text = "(Includes VAT & digital service charge)", color = Color(0xFF64748B), fontSize = 10.sp)
              }

              Text(
                text = "৳ ${"%.2f".format(order.totalAmount)} BDT",
                color = RoyalGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              )
            }
          }
        }
      }

      // Sandbox environment banner
      item {
        SandboxWarningBanner(isSandbox = config.isSandbox)
      }

      // Error message if any
      if (errorMessage != null) {
        item {
          Surface(
            color = Color(0xFFFEF2F2),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = StatusFailed, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = "Payment Verification Failed", color = StatusFailed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = errorMessage, color = Color(0xFF991B1B), fontSize = 11.5.sp)
              }
            }
          }
        }
      }

      // Payment Methods Selection (bKash, Nagad, Rocket, Upay, Cards)
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "SELECT PAYMENT METHOD",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (availableMethods.isEmpty()) {
              Text(
                text = "No payment methods currently enabled. Please enable methods in Gateway Settings.",
                color = StatusFailed,
                fontSize = 12.sp
              )
            } else {
              availableMethods.forEach { method ->
                val isSelected = selectedMethod == method
                val brandColor = when (method) {
                  PaymentMethod.BKASH -> BkashPink
                  PaymentMethod.NAGAD -> NagadOrange
                  PaymentMethod.ROCKET -> RocketViolet
                  PaymentMethod.UPAY -> UpayNavy
                  PaymentMethod.CARD -> CardNavy
                }

                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = if (isSelected) brandColor.copy(alpha = 0.06f) else Color.White,
                  border = BorderStroke(
                    if (isSelected) 1.8.dp else 1.dp,
                    if (isSelected) brandColor else RoyalBorderLight
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedMethod = method }
                    .testTag("method_${method.name}")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      RadioButton(
                        selected = isSelected,
                        onClick = { selectedMethod = method },
                        colors = RadioButtonDefaults.colors(
                          selectedColor = brandColor,
                          unselectedColor = Color(0xFF94A3B8)
                        )
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = method.displayName,
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp,
                          color = RoyalNavy
                        )
                        Text(
                          text = method.channel,
                          fontSize = 11.sp,
                          color = RoyalTextSecondary
                        )
                      }
                    }

                    // Brand tag
                    Surface(
                      color = brandColor,
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = when (method) {
                          PaymentMethod.BKASH -> "bKash"
                          PaymentMethod.NAGAD -> "Nagad"
                          PaymentMethod.ROCKET -> "Rocket"
                          PaymentMethod.UPAY -> "Upay"
                          PaymentMethod.CARD -> "Cards"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Security Guarantees Card (Never store PIN, HMAC verification)
      item {
        SecurityGuaranteesCard()
      }

      // Test Mode Simulator Switch
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, RoyalBorderLight)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Simulate Gateway Failure (Test Edge Case)",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalNavy
              )
              Text(
                text = "Tests failure handling, error messages, and retry safety",
                fontSize = 11.sp,
                color = RoyalTextSecondary
              )
            }
            Switch(
              checked = simulateFailure,
              onCheckedChange = { simulateFailure = it },
              colors = SwitchDefaults.colors(checkedThumbColor = StatusFailed),
              modifier = Modifier.testTag("simulate_failure_switch")
            )
          }
        }
      }

      // Pay Button
      item {
        if (isProcessing) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator(color = RoyalTeal, modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Verifying with Gateway Server...",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RoyalNavy
              )
              Text(
                text = "Checking cryptographic order signature & webhook callback",
                fontSize = 11.5.sp,
                color = RoyalTextSecondary
              )
            }
          }
        } else {
          Button(
            onClick = {
              showSimulatedGatewayDialog = true
              gatewayStep = 1
            },
            enabled = availableMethods.isNotEmpty(),
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("proceed_to_payment_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = when (selectedMethod) {
                PaymentMethod.BKASH -> BkashPink
                PaymentMethod.NAGAD -> NagadOrange
                PaymentMethod.ROCKET -> RocketViolet
                PaymentMethod.UPAY -> UpayNavy
                PaymentMethod.CARD -> RoyalNavy
              },
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Pay ৳ ${"%.2f".format(order.totalAmount)} with ${selectedMethod.displayName}",
              fontWeight = FontWeight.Bold,
              fontSize = 14.5.sp
            )
          }
        }
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }

  // Interactive Payment Gateway Modal Simulator (bKash, Nagad, etc.)
  if (showSimulatedGatewayDialog) {
    val methodColor = when (selectedMethod) {
      PaymentMethod.BKASH -> BkashPink
      PaymentMethod.NAGAD -> NagadOrange
      PaymentMethod.ROCKET -> RocketViolet
      PaymentMethod.UPAY -> UpayNavy
      PaymentMethod.CARD -> CardNavy
    }

    AlertDialog(
      onDismissRequest = { showSimulatedGatewayDialog = false },
      shape = RoundedCornerShape(24.dp),
      containerColor = Color.White,
      modifier = Modifier.testTag("gateway_checkout_dialog"),
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(methodColor),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = selectedMethod.displayName.take(1),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "${selectedMethod.displayName} Payment Checkout",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = RoyalNavy
              )
              Text(
                text = if (config.isSandbox) "SANDBOX GATEWAY SIMULATOR" else "LIVE MERCHANT CHECKOUT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (config.isSandbox) Color(0xFFD97706) else StatusSuccess
              )
            }
          }
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Amount & Merchant Info
          Surface(
            color = RoyalSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "Merchant: Royal Care Telemedicine", fontSize = 11.sp, color = RoyalTextSecondary)
                Text(text = "Invoice: ${order.orderId}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = RoyalNavy)
              }
              Text(
                text = "৳ ${order.totalAmount.toInt()} BDT",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = methodColor
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3-step Gateway flow
          when (gatewayStep) {
            1 -> {
              Text(
                text = "Enter Your ${selectedMethod.displayName} Account Number:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalNavy
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = gatewayMobileNumber,
                onValueChange = { gatewayMobileNumber = it },
                label = { Text("Mobile Account (11 digits)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("gateway_account_input"),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White,
                  focusedIndicatorColor = methodColor
                )
              )
            }

            2 -> {
              Text(
                text = "Verification Code (OTP) Sent to $gatewayMobileNumber:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalNavy
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = gatewayOtp,
                onValueChange = { gatewayOtp = it },
                label = { Text("6-digit Verification Code (e.g. 123456)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("gateway_otp_input"),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White,
                  focusedIndicatorColor = methodColor
                )
              )
            }

            3 -> {
              Text(
                text = "Enter ${selectedMethod.displayName} Secret PIN:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalNavy
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = gatewayPin,
                onValueChange = { gatewayPin = it },
                label = { Text("Account PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("gateway_pin_input"),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White,
                  focusedIndicatorColor = methodColor
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Mandatory Security Guarantee: We never store PIN or OTP
          Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = RoyalTeal,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "PIN & OTP are processed directly on gateway channel and never stored on Royal Care servers.",
                fontSize = 10.sp,
                color = RoyalTextSecondary,
                lineHeight = 13.sp
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (gatewayStep < 3) {
              gatewayStep++
            } else {
              showSimulatedGatewayDialog = false
              onPay(
                selectedMethod,
                simulateFailure,
                if (simulateFailure) "Simulated sandbox transaction rejection: Insufficient funds or invalid PIN" else null
              )
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = methodColor,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("gateway_continue_button")
        ) {
          Text(
            text = if (gatewayStep < 3) "Next (Step $gatewayStep/3)" else "Confirm & Pay ৳ ${order.totalAmount.toInt()}",
            fontWeight = FontWeight.Bold
          )
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = { showSimulatedGatewayDialog = false },
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(text = "Cancel", color = RoyalNavy)
        }
      }
    )
  }
}
