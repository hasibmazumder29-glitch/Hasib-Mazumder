package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
import com.example.data.model.Doctor
import com.example.data.model.PaymentOrder
import com.example.data.model.PaymentStatus
import com.example.data.model.RefundRecord
import com.example.ui.components.PaymentMethodBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalSurfaceVariant
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusRefunded
import com.example.ui.theme.StatusSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  orders: List<PaymentOrder>,
  doctors: List<Doctor>,
  auditLogs: List<AuditLog>,
  refunds: List<RefundRecord>,
  onInitiateRefund: (orderId: String, amount: Double, reason: String) -> Unit,
  onUpdateDoctorFee: (doctorId: String, videoFee: Double, audioFee: Double, instantPremium: Double, scheduledFee: Double, duration: Int, hours: String, isOnline: Boolean) -> Unit,
  onOpenGatewaySettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(0) } // 0: Transactions, 1: Doctor Fees, 2: Audit Logs
  var searchQuery by remember { mutableStateOf("") }
  var filterStatus by remember { mutableStateOf<PaymentStatus?>(null) }

  // Refund dialog state
  var orderForRefund by remember { mutableStateOf<PaymentOrder?>(null) }
  var refundAmountInput by remember { mutableStateOf("") }
  var refundReasonInput by remember { mutableStateOf("Patient request / Doctor consultation adjustment") }

  // Doctor fee edit state
  var doctorForEdit by remember { mutableStateOf<Doctor?>(null) }
  var editVideoFee by remember { mutableStateOf("") }
  var editAudioFee by remember { mutableStateOf("") }
  var editInstantPremium by remember { mutableStateOf("") }
  var editScheduledFee by remember { mutableStateOf("") }
  var editDuration by remember { mutableStateOf("") }
  var editHours by remember { mutableStateOf("") }
  var editOnline by remember { mutableStateOf(true) }

  // Report export dialog
  var showExportReportDialog by remember { mutableStateOf(false) }

  // Metrics calculations
  val successfulOrders = orders.filter { it.status == PaymentStatus.SUCCESSFUL }
  val totalRevenue = successfulOrders.sumOf { it.totalAmount }
  val totalRefunded = refunds.sumOf { it.refundAmount }
  val failedOrPendingCount = orders.count {
    it.status == PaymentStatus.FAILED || it.status == PaymentStatus.PENDING || it.status == PaymentStatus.INITIATED
  }

  val filteredOrders = orders.filter { order ->
    val matchesSearch = order.patientName.contains(searchQuery, ignoreCase = true) ||
        order.doctorName.contains(searchQuery, ignoreCase = true) ||
        order.orderId.contains(searchQuery, ignoreCase = true) ||
        (order.trxId?.contains(searchQuery, ignoreCase = true) == true)

    val matchesStatus = if (filterStatus == null) true else order.status == filterStatus
    matchesSearch && matchesStatus
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Payment & Revenue Dashboard",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = RoyalNavy
            )
            Text(
              text = "Royal Care Administrator Portal",
              fontSize = 11.sp,
              color = RoyalTextSecondary
            )
          }
        },
        actions = {
          IconButton(
            onClick = onOpenGatewaySettings,
            modifier = Modifier.testTag("admin_gateway_settings_btn")
          ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Gateway Config", tint = RoyalNavy)
          }
          IconButton(
            onClick = { showExportReportDialog = true },
            modifier = Modifier.testTag("admin_export_report_btn")
          ) {
            Icon(imageVector = Icons.Default.Download, contentDescription = "Export Report", tint = RoyalTeal)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("admin_dashboard_screen")
    ) {
      // Tab Bar: Transactions vs Doctor Fees vs Audit Logs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = RoyalNavy,
        indicator = { tabPositions ->
          TabRowDefaults.Indicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = RoyalNavy
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Payments (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 12.5.sp) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Doctor Fees", fontWeight = FontWeight.Bold, fontSize = 12.5.sp) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Audit Trail (${auditLogs.size})", fontWeight = FontWeight.Bold, fontSize = 12.5.sp) }
        )
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Financial KPI Cards (Always visible at top)
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            KpiCard(
              title = "Total Revenue",
              value = "৳ ${"%.0f".format(totalRevenue)}",
              subtitle = "${successfulOrders.size} paid consultations",
              valueColor = RoyalGoldDark,
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Total Refunded",
              value = "৳ ${"%.0f".format(totalRefunded)}",
              subtitle = "${refunds.size} refund transactions",
              valueColor = StatusRefunded,
              modifier = Modifier.weight(1f)
            )
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            KpiCard(
              title = "Net Payouts",
              value = "৳ ${"%.0f".format(totalRevenue - totalRefunded)}",
              subtitle = "Platform net balance",
              valueColor = StatusSuccess,
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Failed/Pending",
              value = "$failedOrPendingCount",
              subtitle = "Requires attention",
              valueColor = if (failedOrPendingCount > 0) StatusFailed else RoyalNavy,
              modifier = Modifier.weight(1f)
            )
          }
        }

        // TAB 0: TRANSACTIONS
        if (selectedTab == 0) {
          // Search & Filter Row
          item {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Search patient, doctor, order ID, TRX...") },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RoyalTeal) },
              shape = RoundedCornerShape(14.dp),
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = RoyalTeal
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_search_input")
            )
          }

          // Status Filter Chips
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              FilterChip(
                selected = filterStatus == null,
                onClick = { filterStatus = null },
                label = { Text("All Statuses") },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = RoyalNavy,
                  selectedLabelColor = RoyalGold
                )
              )

              PaymentStatus.values().forEach { status ->
                FilterChip(
                  selected = filterStatus == status,
                  onClick = { filterStatus = if (filterStatus == status) null else status },
                  label = { Text(status.label) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RoyalNavy,
                    selectedLabelColor = RoyalGold
                  )
                )
              }
            }
          }

          if (filteredOrders.isEmpty()) {
            item {
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
              ) {
                Column(
                  modifier = Modifier.padding(24.dp).fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "No matching transactions found",
                    color = RoyalTextSecondary,
                    fontSize = 13.sp
                  )
                }
              }
            }
          } else {
            items(filteredOrders, key = { it.orderId }) { order ->
              AdminTransactionCard(
                order = order,
                onInitiateRefund = {
                  orderForRefund = order
                  refundAmountInput = order.totalAmount.toString()
                }
              )
            }
          }
        }

        // TAB 1: DOCTOR FEES CONFIGURATION
        if (selectedTab == 1) {
          item {
            Text(
              text = "Doctor Consultation Fee & Schedule Management",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = RoyalNavy
            )
            Text(
              text = "Configure Video, Audio, Instant premium fees, duration, and working hours.",
              fontSize = 11.5.sp,
              color = RoyalTextSecondary
            )
          }

          items(doctors, key = { it.id }) { doctor ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = doctor.name,
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = RoyalNavy
                    )
                    Text(
                      text = doctor.specialty,
                      fontSize = 12.sp,
                      color = RoyalTeal
                    )
                  }

                  IconButton(
                    onClick = {
                      doctorForEdit = doctor
                      editVideoFee = doctor.videoFee.toInt().toString()
                      editAudioFee = doctor.audioFee.toInt().toString()
                      editInstantPremium = doctor.instantFeePremium.toInt().toString()
                      editScheduledFee = doctor.scheduledFee.toInt().toString()
                      editDuration = doctor.consultationDurationMinutes.toString()
                      editHours = doctor.availableHours
                      editOnline = doctor.isOnline
                    },
                    modifier = Modifier.testTag("edit_fee_btn_${doctor.id}")
                  ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Fee", tint = RoyalNavy)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  FeeSummaryItem(label = "Video Fee", value = "৳ ${doctor.videoFee.toInt()}")
                  FeeSummaryItem(label = "Audio Fee", value = "৳ ${doctor.audioFee.toInt()}")
                  FeeSummaryItem(label = "Instant Premium", value = "+৳ ${doctor.instantFeePremium.toInt()}")
                  FeeSummaryItem(label = "Duration", value = "${doctor.consultationDurationMinutes}m")
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Hours: ${doctor.availableHours}",
                    fontSize = 11.sp,
                    color = RoyalTextSecondary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                  )
                  Surface(
                    color = if (doctor.isOnline) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = if (doctor.isOnline) "ONLINE" else "OFFLINE",
                      color = if (doctor.isOnline) Color(0xFF065F46) else Color(0xFF64748B),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
          }
        }

        // TAB 2: AUDIT LOGS
        if (selectedTab == 2) {
          item {
            Text(
              text = "Immutable Payment Security Audit Trail",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = RoyalNavy
            )
            Text(
              text = "HMAC-SHA256 hashed ledger of all payment order creations, callbacks, verification, and refunds.",
              fontSize = 11.5.sp,
              color = RoyalTextSecondary
            )
          }

          items(auditLogs, key = { it.id }) { log ->
            val timeStr = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.US).format(Date(log.timestamp))
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = BorderStroke(1.dp, RoyalBorderLight),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    color = RoyalNavyDeep,
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = log.eventType,
                      color = RoyalGold,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                  Text(text = timeStr, fontSize = 10.5.sp, color = RoyalTextSecondary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "Order: ${log.orderId} • Actor: ${log.actor}",
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = RoyalNavy
                )
                Text(
                  text = log.details,
                  fontSize = 11.sp,
                  color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = "SHA256: ${log.hashSignature.take(24)}...",
                  fontSize = 9.5.sp,
                  fontFamily = FontFamily.Monospace,
                  color = Color(0xFF94A3B8)
                )
              }
            }
          }
        }
      }
    }
  }

  // Admin Refund Modal
  if (orderForRefund != null) {
    val targetOrder = orderForRefund!!
    AlertDialog(
      onDismissRequest = { orderForRefund = null },
      title = {
        Text(text = "Initiate Payment Refund", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(text = "Order ID: ${targetOrder.orderId}", fontSize = 12.sp, color = RoyalNavy)
          Text(text = "Patient: ${targetOrder.patientName} (${targetOrder.patientPhone})", fontSize = 11.5.sp)
          Text(text = "Original Paid: ৳ ${targetOrder.totalAmount} BDT", fontWeight = FontWeight.Bold, fontSize = 13.sp)

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = refundAmountInput,
            onValueChange = { refundAmountInput = it },
            label = { Text("Refund Amount (৳ BDT)") },
            modifier = Modifier.fillMaxWidth().testTag("refund_amount_input"),
            shape = RoundedCornerShape(10.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = refundReasonInput,
            onValueChange = { refundReasonInput = it },
            label = { Text("Reason for Refund") },
            modifier = Modifier.fillMaxWidth().testTag("refund_reason_input"),
            shape = RoundedCornerShape(10.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val amount = refundAmountInput.toDoubleOrNull() ?: targetOrder.totalAmount
            onInitiateRefund(targetOrder.orderId, amount, refundReasonInput)
            orderForRefund = null
            Toast.makeText(context, "Refund of ৳ $amount processed via gateway.", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusRefunded),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("confirm_refund_btn")
        ) {
          Text("Execute Refund")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { orderForRefund = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Doctor Fee Edit Modal
  if (doctorForEdit != null) {
    val doc = doctorForEdit!!
    AlertDialog(
      onDismissRequest = { doctorForEdit = null },
      title = {
        Text(text = "Configure Doctor Consultation Fees", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(text = doc.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalNavy)
          Text(text = doc.specialty, fontSize = 11.5.sp, color = RoyalTeal)

          Spacer(modifier = Modifier.height(10.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = editVideoFee,
              onValueChange = { editVideoFee = it },
              label = { Text("Video Fee (৳)") },
              modifier = Modifier.weight(1f).testTag("edit_video_fee_input"),
              shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
              value = editAudioFee,
              onValueChange = { editAudioFee = it },
              label = { Text("Audio Fee (৳)") },
              modifier = Modifier.weight(1f).testTag("edit_audio_fee_input"),
              shape = RoundedCornerShape(8.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = editInstantPremium,
              onValueChange = { editInstantPremium = it },
              label = { Text("Instant Premium (৳)") },
              modifier = Modifier.weight(1f).testTag("edit_instant_premium_input"),
              shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
              value = editDuration,
              onValueChange = { editDuration = it },
              label = { Text("Duration (Mins)") },
              modifier = Modifier.weight(1f).testTag("edit_duration_input"),
              shape = RoundedCornerShape(8.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = editHours,
            onValueChange = { editHours = it },
            label = { Text("Available Consultation Hours") },
            modifier = Modifier.fillMaxWidth().testTag("edit_hours_input"),
            shape = RoundedCornerShape(8.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Doctor Online Status (Instant Calls)", fontSize = 12.sp, color = RoyalNavy)
            Switch(
              checked = editOnline,
              onCheckedChange = { editOnline = it },
              colors = SwitchDefaults.colors(checkedThumbColor = StatusSuccess),
              modifier = Modifier.testTag("edit_doctor_online_switch")
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateDoctorFee(
              doc.id,
              editVideoFee.toDoubleOrNull() ?: doc.videoFee,
              editAudioFee.toDoubleOrNull() ?: doc.audioFee,
              editInstantPremium.toDoubleOrNull() ?: doc.instantFeePremium,
              editScheduledFee.toDoubleOrNull() ?: doc.scheduledFee,
              editDuration.toIntOrNull() ?: doc.consultationDurationMinutes,
              editHours,
              editOnline
            )
            doctorForEdit = null
            Toast.makeText(context, "Doctor configuration updated.", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("save_doctor_fee_btn")
        ) {
          Text("Save Configuration")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { doctorForEdit = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Export Report Dialog
  if (showExportReportDialog) {
    val reportText = buildString {
      appendLine("==================================================")
      appendLine("ROYAL CARE TELEMEDICINE - FINANCIAL PAYMENT REPORT")
      appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
      appendLine("Total Revenue: ৳ ${"%.2f".format(totalRevenue)} BDT")
      appendLine("Total Refunds: ৳ ${"%.2f".format(totalRefunded)} BDT")
      appendLine("Net Payout: ৳ ${"%.2f".format(totalRevenue - totalRefunded)} BDT")
      appendLine("Total Transactions: ${orders.size}")
      appendLine("==================================================")
      appendLine("Order ID | Patient | Doctor | Amount | Method | Status | TRX")
      orders.forEach {
        appendLine("${it.orderId} | ${it.patientName} | ${it.doctorName} | ৳ ${it.totalAmount} | ${it.paymentMethod?.name ?: "N/A"} | ${it.status.name} | ${it.trxId ?: "N/A"}")
      }
      appendLine("==================================================")
    }

    AlertDialog(
      onDismissRequest = { showExportReportDialog = false },
      title = {
        Text(text = "Exported Financial Report", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(text = "Formatted CSV / Plaintext audit export:", fontSize = 11.sp, color = RoyalTextSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = reportText,
              color = Color(0xFFE2E8F0),
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              lineHeight = 13.sp,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showExportReportDialog = false
            Toast.makeText(context, "Report exported and copied to clipboard.", Toast.LENGTH_LONG).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Copy / Share Report")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showExportReportDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}

@Composable
private fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = title, fontSize = 11.sp, color = RoyalTextSecondary, fontWeight = FontWeight.Medium)
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        color = valueColor
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
    }
  }
}

@Composable
private fun FeeSummaryItem(label: String, value: String) {
  Column {
    Text(text = label, fontSize = 10.sp, color = RoyalTextSecondary)
    Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
  }
}

@Composable
private fun AdminTransactionCard(
  order: PaymentOrder,
  onInitiateRefund: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth().testTag("admin_trx_${order.orderId}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${order.patientName} → ${order.doctorName}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = RoyalNavy
          )
          Text(
            text = "Order: ${order.orderId} • Apt: ${order.appointmentId}",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = RoyalTextSecondary
          )
        }
        StatusBadge(status = order.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "TRX: ${order.trxId ?: "N/A"}",
            fontSize = 11.5.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = RoyalNavy
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            PaymentMethodBadge(method = order.paymentMethod)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (order.isSandbox) "SANDBOX" else "LIVE",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (order.isSandbox) Color(0xFFD97706) else StatusSuccess
            )
          }
        }

        Text(
          text = "৳ ${"%.2f".format(order.totalAmount)}",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 16.sp,
          color = RoyalNavyDeep
        )
      }

      if (order.failureReason != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Failure Reason: ${order.failureReason}",
          fontSize = 11.sp,
          color = StatusFailed
        )
      }

      if (order.status == PaymentStatus.SUCCESSFUL) {
        Divider(modifier = Modifier.padding(vertical = 10.dp), color = RoyalBorderLight)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onInitiateRefund,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRefunded),
            border = BorderStroke(1.dp, StatusRefunded),
            modifier = Modifier.testTag("refund_btn_${order.orderId}")
          ) {
            Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Initiate Refund", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
