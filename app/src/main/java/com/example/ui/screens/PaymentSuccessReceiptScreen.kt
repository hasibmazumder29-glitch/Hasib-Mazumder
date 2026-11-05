package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.PaymentOrder
import com.example.ui.components.DigitalReceiptCard
import com.example.ui.theme.RoyalNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSuccessReceiptScreen(
  order: PaymentOrder,
  appointment: Appointment?,
  onJoinConsultation: () -> Unit,
  onGoHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Payment Confirmed",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = RoyalNavy
          )
        },
        navigationIcon = {
          IconButton(onClick = onGoHome, modifier = Modifier.testTag("receipt_home_btn")) {
            Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = RoyalNavy)
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
        .testTag("payment_success_receipt_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        DigitalReceiptCard(
          order = order,
          appointment = appointment,
          onJoinConsultation = onJoinConsultation,
          onShareReceipt = {
            Toast.makeText(
              context,
              "Payment voucher shared! Appointment: ${order.appointmentId}, TRX: ${order.trxId}",
              Toast.LENGTH_LONG
            ).show()
          }
        )
      }

      item {
        Button(
          onClick = onGoHome,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("return_home_btn"),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = RoyalNavy
          ),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, RoyalNavy),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(text = "Return to Doctor Consultations", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
