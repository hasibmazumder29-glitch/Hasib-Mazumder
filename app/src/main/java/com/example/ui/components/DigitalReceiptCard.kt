package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.PaymentOrder
import com.example.data.model.PaymentStatus
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalSurfaceVariant
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextMuted
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary
import com.example.ui.theme.StatusSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DigitalReceiptCard(
  order: PaymentOrder,
  appointment: Appointment?,
  onJoinConsultation: (() -> Unit)? = null,
  onShareReceipt: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
    .format(Date(order.verifiedAt ?: order.createdAt))

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("digital_receipt_card_${order.orderId}"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(modifier = Modifier.padding(22.dp)) {
      // Header: Brand & Official Receipt Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(RoyalNavy),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "RC",
                color = RoyalGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ROYAL CARE",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = RoyalNavyDeep,
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Official Telemedicine Payment Voucher",
            fontSize = 11.sp,
            color = RoyalTextSecondary
          )
        }

        StatusBadge(status = order.status)
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Success Check Banner
      Surface(
        color = Color(0xFFF0FDF4),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = StatusSuccess,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = if (order.isSandbox) "PAYMENT VERIFIED (SANDBOX)" else "PAYMENT CONFIRMED (LIVE)",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF166534)
            )
            Text(
              text = "Authenticated via Gateway Server Signature",
              fontSize = 11.sp,
              color = Color(0xFF15803D)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Receipt Metadata Grid
      ReceiptField(label = "Appointment ID", value = order.appointmentId, isMonospace = true)
      ReceiptField(label = "Order ID", value = order.orderId, isMonospace = true)
      ReceiptField(label = "Transaction Reference (TRX)", value = order.trxId ?: "N/A", isMonospace = true, isHighlight = true)
      ReceiptField(label = "Consulting Doctor", value = order.doctorName)
      ReceiptField(label = "Consultation Type", value = "${order.consultationType.label} (${order.bookingType.label})")
      ReceiptField(label = "Scheduled Slot / Queue", value = order.scheduledSlot)
      ReceiptField(label = "Patient Name & Contact", value = "${order.patientName} (${order.patientPhone})")
      ReceiptField(label = "Payment Method", value = order.paymentMethod?.displayName ?: "Digital Gateway")
      ReceiptField(label = "Payment Timestamp", value = formattedDate)

      Divider(modifier = Modifier.padding(vertical = 12.dp), color = RoyalBorderLight)

      // Amount Breakdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "Consultation Base Fee", fontSize = 12.sp, color = RoyalTextSecondary)
        Text(text = "৳ ${"%.2f".format(order.baseFee)} BDT", fontSize = 12.sp, color = RoyalNavy)
      }
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "Digital Platform Service", fontSize = 12.sp, color = RoyalTextSecondary)
        Text(text = "৳ ${"%.2f".format(order.serviceFee)} BDT", fontSize = 12.sp, color = RoyalNavy)
      }
      Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(text = "Government VAT (5%)", fontSize = 12.sp, color = RoyalTextSecondary)
        Text(text = "৳ ${"%.2f".format(order.vatAmount)} BDT", fontSize = 12.sp, color = RoyalNavy)
      }

      Divider(modifier = Modifier.padding(vertical = 10.dp), color = RoyalBorderLight)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Net Amount Paid",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = RoyalNavyDeep
        )
        Text(
          text = "৳ ${"%.2f".format(order.totalAmount)} BDT",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = RoyalNavyDeep
        )
      }

      if (order.status == PaymentStatus.REFUNDED || order.status == PaymentStatus.PARTIALLY_REFUNDED) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          color = Color(0xFFF5F3FF),
          shape = RoundedCornerShape(10.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "REFUND STATUS: ${order.status.label.uppercase()}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color(0xFF6D28D9)
            )
            Text(
              text = "Amount Refunded: ৳ ${"%.2f".format(order.refundedAmount)} BDT returned to source account.",
              fontSize = 11.sp,
              color = Color(0xFF5B21B6)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Digital Signature & QR Verification Simulation
      Surface(
        color = RoyalNavyDeep,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = "QR Code Verification",
            tint = RoyalGold,
            modifier = Modifier.size(42.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "DIGITAL CRYPTO SIGNATURE",
              color = RoyalGold,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.8.sp
            )
            Text(
              text = order.signature.take(28) + "...",
              color = Color(0xFF94A3B8),
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "Tamper-evident verification token",
              color = Color(0xFFCBD5E1),
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Action: Join Call or Open Room
      if (onJoinConsultation != null) {
        val isReady = appointment?.status == AppointmentStatus.READY_TO_JOIN ||
            appointment?.status == AppointmentStatus.IN_CALL ||
            order.bookingType == com.example.data.model.BookingType.INSTANT

        Button(
          onClick = onJoinConsultation,
          enabled = isReady,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("join_consultation_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = RoyalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VideoCall,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = RoyalGold
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isReady) "Join Consultation Call" else "Appointment Scheduled (Awaiting Time)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Share or Print
      OutlinedButton(
        onClick = onShareReceipt,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("share_receipt_button"),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBorderLight)
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = RoyalNavy
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Share Digital Voucher",
          color = RoyalNavy,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )
      }
    }
  }
}

@Composable
private fun ReceiptField(
  label: String,
  value: String,
  isMonospace: Boolean = false,
  isHighlight: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      color = RoyalTextSecondary,
      modifier = Modifier.weight(0.45f)
    )
    Text(
      text = value,
      fontSize = 12.5.sp,
      fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
      color = if (isHighlight) RoyalGoldDark else RoyalNavy,
      fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
      modifier = Modifier.weight(0.55f),
      textAlign = androidx.compose.ui.text.style.TextAlign.End
    )
  }
}
