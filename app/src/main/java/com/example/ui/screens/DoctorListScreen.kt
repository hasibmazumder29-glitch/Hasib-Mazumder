package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.Doctor
import com.example.ui.components.DoctorCard
import com.example.ui.components.SandboxWarningBanner
import com.example.ui.theme.RoyalBorderLight
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.RoyalTextPrimary
import com.example.ui.theme.RoyalTextSecondary

@Composable
fun DoctorListScreen(
  doctors: List<Doctor>,
  isSandbox: Boolean,
  onSelectDoctor: (Doctor, ConsultationType, BookingType) -> Unit,
  onOpenGatewayConfig: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedSpecialty by remember { mutableStateOf("All") }
  var onlyOnline by remember { mutableStateOf(false) }

  val specialties = listOf("All", "Cardiology", "Pediatrics", "Gynecology", "Dermatology", "Psychiatry")

  val filteredDoctors = doctors.filter { doc ->
    val matchesSearch = doc.name.contains(searchQuery, ignoreCase = true) ||
        doc.specialty.contains(searchQuery, ignoreCase = true) ||
        doc.hospital.contains(searchQuery, ignoreCase = true)

    val matchesSpecialty = if (selectedSpecialty == "All") true else doc.specialty.contains(selectedSpecialty, ignoreCase = true)
    val matchesOnline = if (onlyOnline) doc.isOnline else true

    matchesSearch && matchesSpecialty && matchesOnline
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("doctor_list_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Hero Banner: Royal Care Telemedicine
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = RoyalNavyDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Image(
                painter = painterResource(id = R.drawable.img_royal_care_logo),
                contentDescription = "Royal Care Logo",
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape),
                contentScale = ContentScale.Crop
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "ROYAL CARE",
                  color = RoyalGold,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "Telemedicine Consultation & Payments",
                  color = Color(0xFFCFD6E4),
                  fontSize = 11.sp
                )
              }
            }

            IconButton(
              onClick = onOpenGatewayConfig,
              modifier = Modifier.testTag("open_gateway_config_header_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Gateway Settings",
                tint = RoyalGold
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Expert Specialist Care,\nDirect to Your Home",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Instant audio/video consultations with BMDC-verified doctors. Secure payments via bKash, Nagad, Rocket & Cards with 100% money-back guarantee.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }
      }
    }

    // Sandbox Environment Warning
    item {
      SandboxWarningBanner(isSandbox = isSandbox)
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search doctor, specialty, or hospital...") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = RoyalTeal)
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White,
          focusedIndicatorColor = RoyalTeal,
          unfocusedIndicatorColor = RoyalBorderLight
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("doctor_search_field")
      )
    }

    // Specialty Filter Chips
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        specialties.forEach { specialty ->
          val isSelected = selectedSpecialty == specialty
          FilterChip(
            selected = isSelected,
            onClick = { selectedSpecialty = specialty },
            label = { Text(specialty, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalNavy,
              selectedLabelColor = RoyalGold,
              containerColor = Color.White,
              labelColor = RoyalNavy
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("chip_$specialty")
          )
        }

        // Online Only Chip
        FilterChip(
          selected = onlyOnline,
          onClick = { onlyOnline = !onlyOnline },
          label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = if (onlyOnline) RoyalGold else RoyalTeal
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Online Now")
            }
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RoyalTeal,
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = RoyalNavy
          ),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.testTag("chip_online_now")
        )
      }
    }

    // Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Available Specialists (${filteredDoctors.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = RoyalNavy
        )
        Text(
          text = "Exact fees displayed",
          fontSize = 11.5.sp,
          color = RoyalTextSecondary
        )
      }
    }

    // Doctor List
    if (filteredDoctors.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
        ) {
          Column(
            modifier = Modifier
              .padding(32.dp)
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = null,
              tint = RoyalTextSecondary,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No doctors found matching filters",
              fontWeight = FontWeight.SemiBold,
              color = RoyalNavy
            )
            Text(
              text = "Try clearing the search query or online filter",
              fontSize = 12.sp,
              color = RoyalTextSecondary
            )
          }
        }
      }
    } else {
      items(filteredDoctors, key = { it.id }) { doctor ->
        DoctorCard(
          doctor = doctor,
          onBookInstant = {
            onSelectDoctor(doctor, ConsultationType.VIDEO, BookingType.INSTANT)
          },
          onBookScheduled = {
            onSelectDoctor(doctor, ConsultationType.VIDEO, BookingType.SCHEDULED)
          }
        )
      }
    }
  }
}
