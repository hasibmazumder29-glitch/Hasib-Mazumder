package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentOrder
import com.example.data.model.PaymentStatus
import com.example.ui.components.PaymentMethodBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHistoryScreen(
  orders: List<PaymentOrder>,
  onViewReceipt: (PaymentOrder) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredOrders = orders.filter { order ->
    order.doctorName.contains(searchQuery, ignoreCase = true) ||
        order.orderId.contains(searchQuery, ignoreCase = true) ||
        order.appointmentId.contains(searchQuery, ignoreCase = true) ||
        (order.trxId?.contains(searchQuery, ignoreCase = true) == true)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Consultation Payment History",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = RoyalNavy
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("patient_history_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Search
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by doctor, TRX, or appointment ID...") },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = RoyalTeal)
          },
          shape = RoundedCornerShape(14.dp),
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = RoyalTeal
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("history_search_input")
        )
      }

      if (filteredOrders.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
          ) {
            Column(
              modifier = Modifier.padding(32.dp).fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = RoyalTextSecondary,
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No consultation payments found",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = RoyalNavy
              )
              Text(
                text = "Completed consultations and payment receipts will appear here.",
                fontSize = 12.sp,
                color = RoyalTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(filteredOrders, key = { it.orderId }) { order ->
          val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
            .format(Date(order.verifiedAt ?: order.createdAt))

          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onViewReceipt(order) }
              .testTag("history_item_${order.orderId}")
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = order.doctorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = RoyalNavy
                  )
                  Text(
                    text = "${order.consultationType.label} • ${order.bookingType.label}",
                    fontSize = 12.sp,
                    color = RoyalTextSecondary
                  )
                }

                StatusBadge(status = order.status)
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Apt ID: ${order.appointmentId}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = RoyalNavyMedium,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "TRX: ${order.trxId ?: "N/A"}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = RoyalTextSecondary
                  )
                }

                Text(
                  text = "৳ ${"%.2f".format(order.totalAmount)}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = RoyalNavyDeep
                )
              }

              Divider(modifier = Modifier.padding(vertical = 10.dp), color = RoyalBorderLight)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  PaymentMethodBadge(method = order.paymentMethod)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = RoyalTextSecondary
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "View Receipt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalTeal
                  )
                  Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = RoyalTeal,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
