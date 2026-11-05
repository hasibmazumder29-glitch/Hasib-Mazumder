package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.model.ConsultationType
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusSuccess
import kotlinx.coroutines.delay

@Composable
fun TelemedicineRoomScreen(
  appointment: Appointment,
  onEndCall: (prescription: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isMuted by remember { mutableStateOf(false) }
  var isVideoEnabled by remember {
    mutableStateOf(appointment.consultationType == ConsultationType.VIDEO)
  }
  var callDurationSeconds by remember { mutableStateOf(0) }
  var showPrescriptionDialog by remember { mutableStateOf(false) }
  var prescriptionText by remember {
    mutableStateOf(
      if (appointment.digitalPrescription.isNotBlank()) appointment.digitalPrescription
      else "Rx: Tab. Paracetamol 500mg (1+1+1 for 3 days)\nAdvice: Drink plenty of water and rest. Follow up in 7 days."
    )
  }

  // Call timer effect
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      callDurationSeconds++
    }
  }

  val minutes = callDurationSeconds / 60
  val seconds = callDurationSeconds % 60
  val timerFormatted = "%02d:%02d".format(minutes, seconds)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalNavyDeep)
      .testTag("telemedicine_room_screen")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Room Header
      Surface(
        color = RoyalNavy.copy(alpha = 0.95f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(StatusSuccess)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = appointment.doctorName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "WebRTC Room: ${appointment.webrtcRoomId}",
                color = RoyalGold,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          // Call Timer Badge
          Surface(
            color = RoyalNavyDeep,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = RoyalTeal,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = timerFormatted,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // Video Feed Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(16.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(RoyalNavyMedium),
        contentAlignment = Alignment.Center
      ) {
        if (appointment.consultationType == ConsultationType.VIDEO && isVideoEnabled) {
          // Simulated high-definition doctor feed
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(RoyalNavyDeep)
                .border(3.dp, RoyalGold, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = appointment.doctorName.take(2),
                color = RoyalGold,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = appointment.doctorName,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = "1080p HD Encrypted Medical Stream Active",
              color = RoyalTeal,
              fontSize = 12.sp
            )
          }

          // Picture in Picture (Patient Self View)
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(14.dp)
              .size(width = 90.dp, height = 120.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Color.Black.copy(alpha = 0.7f))
              .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(RoyalTeal),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = appointment.patientName.take(1),
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "You",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        } else {
          // Audio Call Mode Canvas
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(RoyalNavyDeep)
                .border(2.dp, RoyalTeal, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = appointment.doctorName.take(2),
                color = RoyalGold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Audio Consultation Connected",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "End-to-End Encrypted Voice Channel",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp
            )
          }
        }
      }

      // Bottom Control Bar (Mute, Video Toggle, Prescription, End Call)
      Surface(
        color = RoyalNavy.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Mute Toggle
          IconButton(
            onClick = { isMuted = !isMuted },
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(if (isMuted) StatusFailed.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f))
              .testTag("call_mute_toggle")
          ) {
            Icon(
              imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
              contentDescription = "Mute",
              tint = if (isMuted) StatusFailed else Color.White
            )
          }

          // Video Toggle
          IconButton(
            onClick = { isVideoEnabled = !isVideoEnabled },
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(if (!isVideoEnabled) StatusFailed.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.15f))
              .testTag("call_video_toggle")
          ) {
            Icon(
              imageVector = if (isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
              contentDescription = "Video Toggle",
              tint = if (!isVideoEnabled) StatusFailed else Color.White
            )
          }

          // Digital Prescription Pad
          IconButton(
            onClick = { showPrescriptionDialog = true },
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(RoyalTeal.copy(alpha = 0.3f))
              .testTag("open_prescription_pad_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Description,
              contentDescription = "Prescription",
              tint = RoyalGold
            )
          }

          // End Call Button
          IconButton(
            onClick = { onEndCall(prescriptionText) },
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(StatusFailed)
              .testTag("end_call_button")
          ) {
            Icon(
              imageVector = Icons.Default.CallEnd,
              contentDescription = "End Consultation",
              tint = Color.White
            )
          }
        }
      }
    }
  }

  // Doctor Digital Prescription Dialog
  if (showPrescriptionDialog) {
    AlertDialog(
      onDismissRequest = { showPrescriptionDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = RoyalNavy)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Digital Prescription Pad", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Doctor: ${appointment.doctorName}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = RoyalNavy
          )
          Text(
            text = "Patient: ${appointment.patientName} (${appointment.patientPhone})",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = prescriptionText,
            onValueChange = { prescriptionText = it },
            label = { Text("Rx Notes & Medications") },
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .testTag("prescription_input"),
            shape = RoundedCornerShape(12.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showPrescriptionDialog = false
            Toast.makeText(context, "Prescription updated", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Save to Appointment")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showPrescriptionDialog = false }) {
          Text("Close")
        }
      }
    )
  }
}
