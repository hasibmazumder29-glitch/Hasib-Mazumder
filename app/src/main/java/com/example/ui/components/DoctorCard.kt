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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.Doctor
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

@Composable
fun DoctorCard(
  doctor: Doctor,
  onBookInstant: () -> Unit,
  onBookScheduled: () -> Unit,
  onEditFees: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("doctor_card_${doctor.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header: Doctor Info + Online Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Row(modifier = Modifier.weight(1f)) {
          // Doctor Avatar Monogram with Luxury Gold border
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(RoyalNavyDeep)
              .border(2.dp, RoyalGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = doctor.name.split(" ").filter { it.isNotBlank() }
                .takeLast(2)
                .map { it.firstOrNull() ?: 'D' }
                .joinToString(""),
              color = RoyalGold,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = doctor.name,
              style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = RoyalNavy,
              fontSize = 16.sp
            )
            Text(
              text = doctor.title,
              color = RoyalTextSecondary,
              fontSize = 12.sp,
              lineHeight = 15.sp
            )
            Text(
              text = doctor.specialty,
              color = RoyalTeal,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }

        // Online or Offline pill
        Surface(
          color = if (doctor.isOnline) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (doctor.isOnline) Color(0xFFA7F3D0) else Color(0xFFCBD5E1)
          )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (doctor.isOnline) StatusSuccess else Color(0xFF94A3B8))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = if (doctor.isOnline) "ONLINE" else "OFFLINE",
              color = if (doctor.isOnline) Color(0xFF065F46) else Color(0xFF64748B),
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Experience & BMDC Reg
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = RoyalGold,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${doctor.rating} (${doctor.reviewCount} reviews)",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = RoyalNavy
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "• ${doctor.experienceYears}+ yrs exp",
            color = RoyalTextSecondary,
            fontSize = 12.sp
          )
        }

        Text(
          text = doctor.bmdcRegNo,
          color = RoyalTextMuted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Consultation Hours & Duration
      Surface(
        color = RoyalSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector = Icons.Default.AccessTime,
              contentDescription = null,
              tint = RoyalNavyMedium,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = doctor.availableHours,
              fontSize = 11.sp,
              color = RoyalNavy,
              fontWeight = FontWeight.Medium,
              maxLines = 1
            )
          }
          Text(
            text = "${doctor.consultationDurationMinutes} mins/call",
            fontSize = 11.sp,
            color = RoyalTeal,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Consultation Fees Breakdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FeePill(
          icon = Icons.Default.Videocam,
          label = "Video Fee",
          amount = doctor.videoFee,
          modifier = Modifier.weight(1f)
        )
        FeePill(
          icon = Icons.Default.Call,
          label = "Audio Fee",
          amount = doctor.audioFee,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons: Instant Call & Book Appointment
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (doctor.isOnline) {
          Button(
            onClick = onBookInstant,
            modifier = Modifier
              .weight(1f)
              .testTag("instant_call_btn_${doctor.id}"),
            colors = ButtonDefaults.buttonColors(
              containerColor = RoyalGold,
              contentColor = RoyalNavyDeep
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.FlashOn,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = RoyalNavyDeep
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Instant Call",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp
            )
          }
        }

        OutlinedButton(
          onClick = onBookScheduled,
          modifier = Modifier
            .weight(1f)
            .testTag("book_appointment_btn_${doctor.id}"),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = RoyalNavy
          ),
          border = androidx.compose.foundation.BorderStroke(1.2.dp, RoyalNavy),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Event,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = RoyalNavy
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (doctor.isOnline) "Schedule Slot" else "Book Appointment",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        if (onEditFees != null) {
          IconButton(
            onClick = onEditFees,
            modifier = Modifier.size(40.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Doctor Fees",
              tint = RoyalNavyMedium
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FeePill(
  icon: ImageVector,
  label: String,
  amount: Double,
  modifier: Modifier = Modifier
) {
  Surface(
    color = RoyalSurfaceVariant.copy(alpha = 0.5f),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBorderLight),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = RoyalTeal,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(text = label, fontSize = 10.sp, color = RoyalTextSecondary)
        Text(
          text = "৳ ${amount.toInt()} BDT",
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          color = RoyalNavy
        )
      }
    }
  }
}
