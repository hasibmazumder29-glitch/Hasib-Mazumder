package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.Doctor
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalSurfaceVariant
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary

@Composable
fun FeeBreakdownCard(
  doctor: Doctor,
  consultationType: ConsultationType,
  bookingType: BookingType,
  modifier: Modifier = Modifier
) {
  val baseFee = when (consultationType) {
    ConsultationType.VIDEO -> doctor.videoFee
    ConsultationType.AUDIO -> doctor.audioFee
  }

  val instantPremium = if (bookingType == BookingType.INSTANT) doctor.instantFeePremium else 0.0
  val consultationSubtotal = baseFee + instantPremium
  val serviceFee = 50.0
  val vat = (consultationSubtotal + serviceFee) * 0.05
  val total = consultationSubtotal + serviceFee + vat

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Text(
        text = "FEE BREAKDOWN & PRICING",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        color = RoyalNavy
      )

      Spacer(modifier = Modifier.height(14.dp))

      FeeRow(
        title = "${consultationType.label} (Base Fee)",
        subtitle = "${doctor.consultationDurationMinutes} mins telemedicine session",
        amount = baseFee
      )

      if (bookingType == BookingType.INSTANT) {
        FeeRow(
          title = "Instant Doctor Call Priority Queue",
          subtitle = "Immediate doctor dispatch & priority room",
          amount = instantPremium,
          isHighlight = true
        )
      }

      FeeRow(
        title = "Royal Care Platform Digital Service",
        subtitle = "Encrypted WebRTC bandwidth & electronic prescription",
        amount = serviceFee
      )

      FeeRow(
        title = "Government VAT (5%)",
        subtitle = "Statutory digital healthcare tax",
        amount = vat
      )

      Divider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = Color(0xFFE2E8F0)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total Payable Amount",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = RoyalNavyDeep
          )
          Text(
            text = "Guaranteed refund if doctor is unavailable",
            fontSize = 11.sp,
            color = RoyalTeal
          )
        }

        Text(
          text = "৳ ${"%.2f".format(total)} BDT",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 19.sp,
          color = RoyalNavyDeep
        )
      }
    }
  }
}

@Composable
private fun FeeRow(
  title: String,
  subtitle: String,
  amount: Double,
  isHighlight: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
        color = if (isHighlight) RoyalGoldDark else RoyalTextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = RoyalTextSecondary
      )
    }

    Text(
      text = "৳ ${"%.2f".format(amount)}",
      fontSize = 13.5.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (isHighlight) RoyalGoldDark else RoyalNavy
    )
  }
}
