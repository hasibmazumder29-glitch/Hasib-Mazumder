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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.ConsultationType
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusSuccess

@Composable
fun IncomingCallModal(
  appointment: Appointment,
  onAccept: () -> Unit,
  onReject: () -> Unit
) {
  AlertDialog(
    onDismissRequest = {},
    modifier = Modifier.testTag("incoming_call_dialog"),
    shape = RoundedCornerShape(24.dp),
    containerColor = RoyalNavyDeep,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(RoyalGold.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.NotificationsActive,
            contentDescription = null,
            tint = RoyalGold,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "DOCTOR CONSULTATION ALERT",
            color = RoyalGold,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp
          )
          Text(
            text = "Incoming Patient Request",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
          color = RoyalNavy,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Patient: ${appointment.patientName}",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "Phone: ${appointment.patientPhone}",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (appointment.consultationType == ConsultationType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = null,
                tint = RoyalTeal,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${appointment.consultationType.label} (Paid: ৳ ${appointment.feePaid.toInt()} BDT)",
                color = RoyalTeal,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Note: If rejected or missed, the patient will immediately receive an automated full refund with transaction audit record.",
          color = Color(0xFFCBD5E1),
          fontSize = 11.5.sp,
          lineHeight = 16.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onAccept,
        colors = ButtonDefaults.buttonColors(
          containerColor = StatusSuccess,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("accept_incoming_call_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Call,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Accept & Open Room", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onReject,
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = StatusFailed
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusFailed),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("reject_incoming_call_btn")
      ) {
        Icon(
          imageVector = Icons.Default.CallEnd,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Reject & Refund", fontWeight = FontWeight.Bold)
      }
    }
  )
}
