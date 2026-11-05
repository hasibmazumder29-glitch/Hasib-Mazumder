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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.Doctor
import com.example.ui.components.FeeBreakdownCard
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalSurfaceVariant
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationBookingScreen(
  doctor: Doctor,
  consultationType: ConsultationType,
  bookingType: BookingType,
  selectedSlot: String,
  onConsultationTypeChange: (ConsultationType) -> Unit,
  onBookingTypeChange: (BookingType) -> Unit,
  onSlotChange: (String) -> Unit,
  onConfirmBooking: (patientName: String, patientPhone: String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var patientName by remember { mutableStateOf("Md. Shahinur Alam") }
  var patientPhone by remember { mutableStateOf("01712345678") }
  var agreeTerms by remember { mutableStateOf(true) }

  val availableSlots = listOf(
    "Today, Instant Queue (Within 5 mins)",
    "Today, 07:30 PM - 08:00 PM",
    "Tomorrow, 10:00 AM - 10:30 AM",
    "Tomorrow, 06:00 PM - 06:30 PM",
    "Day After Tomorrow, 11:30 AM - 12:00 PM"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Confirm Consultation",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = RoyalNavy
            )
            Text(
              text = doctor.name,
              fontSize = 12.sp,
              color = RoyalTextSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("booking_back_button")) {
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
        .testTag("consultation_booking_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Step 2: Audio vs Video Consultation Selection
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "STEP 1: SELECT CONSULTATION MODE",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Video Selection Card
              ConsultationTypeOptionCard(
                icon = Icons.Default.Videocam,
                title = "Video Call",
                fee = "৳ ${doctor.videoFee.toInt()}",
                subtitle = "Face-to-face HD consultation",
                isSelected = consultationType == ConsultationType.VIDEO,
                onClick = { onConsultationTypeChange(ConsultationType.VIDEO) },
                modifier = Modifier.weight(1f)
              )

              // Audio Selection Card
              ConsultationTypeOptionCard(
                icon = Icons.Default.Call,
                title = "Audio Call",
                fee = "৳ ${doctor.audioFee.toInt()}",
                subtitle = "Direct encrypted voice call",
                isSelected = consultationType == ConsultationType.AUDIO,
                onClick = { onConsultationTypeChange(ConsultationType.AUDIO) },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }

      // Instant vs Scheduled Selection
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "STEP 2: CHOOSE CONSULTATION TIMING",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Instant Option
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (bookingType == BookingType.INSTANT) RoyalNavyDeep else Color.White,
                border = BorderStroke(
                  1.5.dp,
                  if (bookingType == BookingType.INSTANT) RoyalGold else RoyalBorderLight
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable(enabled = doctor.isOnline) {
                    onBookingTypeChange(BookingType.INSTANT)
                    onSlotChange("Today, Instant Queue (Within 5 mins)")
                  }
                  .testTag("booking_type_instant")
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.FlashOn,
                      contentDescription = null,
                      tint = if (bookingType == BookingType.INSTANT) RoyalGold else RoyalNavy,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Instant Call",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = if (bookingType == BookingType.INSTANT) Color.White else RoyalNavy
                    )
                  }
                  Text(
                    text = if (doctor.isOnline) "Doctor is online now (+৳${doctor.instantFeePremium.toInt()})" else "Doctor currently offline",
                    fontSize = 11.sp,
                    color = if (bookingType == BookingType.INSTANT) RoyalGold else RoyalTextSecondary
                  )
                }
              }

              // Scheduled Option
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (bookingType == BookingType.SCHEDULED) RoyalNavyDeep else Color.White,
                border = BorderStroke(
                  1.5.dp,
                  if (bookingType == BookingType.SCHEDULED) RoyalGold else RoyalBorderLight
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable {
                    onBookingTypeChange(BookingType.SCHEDULED)
                    onSlotChange("Tomorrow, 10:00 AM - 10:30 AM")
                  }
                  .testTag("booking_type_scheduled")
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Event,
                      contentDescription = null,
                      tint = if (bookingType == BookingType.SCHEDULED) RoyalGold else RoyalNavy,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Schedule Slot",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp,
                      color = if (bookingType == BookingType.SCHEDULED) Color.White else RoyalNavy
                    )
                  }
                  Text(
                    text = "Book preferred future time",
                    fontSize = 11.sp,
                    color = if (bookingType == BookingType.SCHEDULED) RoyalGold else RoyalTextSecondary
                  )
                }
              }
            }

            if (bookingType == BookingType.SCHEDULED) {
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "Select Appointment Slot",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalNavy
              )
              Spacer(modifier = Modifier.height(6.dp))

              availableSlots.drop(1).forEach { slot ->
                val isSlotSelected = selectedSlot == slot
                Surface(
                  color = if (isSlotSelected) RoyalNavyMedium else RoyalSurfaceVariant,
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSlotChange(slot) }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = slot,
                      fontSize = 12.5.sp,
                      fontWeight = if (isSlotSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSlotSelected) Color.White else RoyalNavy
                    )
                    if (isSlotSelected) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = RoyalGold,
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

      // Patient Details Form
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "STEP 3: PATIENT INFORMATION",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              letterSpacing = 1.sp,
              color = RoyalNavy
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = patientName,
              onValueChange = { patientName = it },
              label = { Text("Patient Full Name") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RoyalTeal) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("patient_name_input"),
              shape = RoundedCornerShape(12.dp),
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = RoyalTeal
              )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = patientPhone,
              onValueChange = { patientPhone = it },
              label = { Text("Mobile Number (bKash/Nagad/SMS)") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalTeal) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("patient_phone_input"),
              shape = RoundedCornerShape(12.dp),
              colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = RoyalTeal
              )
            )
          }
        }
      }

      // Step 3: Exact Consultation Fee Breakdown Card
      item {
        FeeBreakdownCard(
          doctor = doctor,
          consultationType = consultationType,
          bookingType = bookingType
        )
      }

      // Refund Policy & Agreement Notice
      item {
        Surface(
          color = Color(0xFFF8FAFC),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, RoyalBorderLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
          ) {
            Checkbox(
              checked = agreeTerms,
              onCheckedChange = { agreeTerms = it },
              colors = CheckboxDefaults.colors(checkedColor = RoyalNavy),
              modifier = Modifier.testTag("terms_checkbox")
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "100% Money-Back & Booking Guarantee",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = RoyalNavy
              )
              Text(
                text = "I agree to the Royal Care terms. If the doctor rejects the instant request, fails to connect, or is unavailable, a full automated refund will be credited back via the gateway.",
                fontSize = 11.sp,
                color = RoyalTextSecondary,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      // Primary Action Button
      item {
        Button(
          onClick = {
            if (agreeTerms && patientName.isNotBlank() && patientPhone.isNotBlank()) {
              onConfirmBooking(patientName, patientPhone)
            }
          },
          enabled = agreeTerms && patientName.isNotBlank() && patientPhone.isNotBlank(),
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("confirm_and_proceed_button"),
          colors = ButtonDefaults.buttonColors(
            containerColor = RoyalNavy,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(16.dp)
        ) {
          Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Confirm Booking & Proceed to Payment",
            fontWeight = FontWeight.Bold,
            fontSize = 14.5.sp
          )
        }
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
private fun ConsultationTypeOptionCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  fee: String,
  subtitle: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) RoyalNavyDeep else Color.White,
    border = BorderStroke(1.5.dp, if (isSelected) RoyalGold else RoyalBorderLight),
    modifier = modifier.clickable { onClick() }
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) RoyalGold else RoyalNavy,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = fee,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 13.sp,
          color = if (isSelected) RoyalGold else RoyalNavy
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = if (isSelected) Color.White else RoyalNavy
      )
      Text(
        text = subtitle,
        fontSize = 10.5.sp,
        color = if (isSelected) Color(0xFFCBD5E1) else RoyalTextSecondary
      )
    }
  }
}
