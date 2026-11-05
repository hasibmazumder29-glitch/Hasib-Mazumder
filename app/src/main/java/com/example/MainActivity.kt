package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.VideoChat
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.VideoChat
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppointmentStatus
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.IncomingCallModal
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ConsultationBookingScreen
import com.example.ui.screens.DoctorListScreen
import com.example.ui.screens.GatewayConfigScreen
import com.example.ui.screens.PatientHistoryScreen
import com.example.ui.screens.PaymentCheckoutScreen
import com.example.ui.screens.PaymentSuccessReceiptScreen
import com.example.ui.screens.TelemedicineRoomScreen
import com.example.ui.theme.RoyalCareTheme
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalTeal

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      RoyalCareTheme {
        RoyalCareApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun RoyalCareApp(viewModel: MainViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val doctors by viewModel.doctors.collectAsStateWithLifecycle()
  val orders by viewModel.orders.collectAsStateWithLifecycle()
  val appointments by viewModel.appointments.collectAsStateWithLifecycle()
  val gatewayConfig by viewModel.gatewayConfig.collectAsStateWithLifecycle()
  val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
  val refunds by viewModel.refunds.collectAsStateWithLifecycle()

  val selectedDoctor by viewModel.selectedDoctor.collectAsStateWithLifecycle()
  val selectedConsultationType by viewModel.selectedConsultationType.collectAsStateWithLifecycle()
  val selectedBookingType by viewModel.selectedBookingType.collectAsStateWithLifecycle()
  val selectedSlot by viewModel.selectedSlot.collectAsStateWithLifecycle()

  val currentOrder by viewModel.currentOrder.collectAsStateWithLifecycle()
  val activeAppointment by viewModel.activeAppointment.collectAsStateWithLifecycle()
  val isPaymentProcessing by viewModel.isPaymentProcessing.collectAsStateWithLifecycle()
  val paymentErrorMessage by viewModel.paymentErrorMessage.collectAsStateWithLifecycle()
  val incomingCallAppointment by viewModel.incomingCallAppointment.collectAsStateWithLifecycle()

  // Handle system back navigation
  BackHandler(enabled = currentScreen != Screen.Doctors) {
    when (currentScreen) {
      Screen.BookingConfirm -> viewModel.navigateTo(Screen.Doctors)
      Screen.Checkout -> viewModel.navigateTo(Screen.BookingConfirm)
      Screen.Receipt -> viewModel.navigateTo(Screen.Doctors)
      Screen.TelemedicineRoom -> viewModel.navigateTo(Screen.Doctors)
      Screen.GatewayConfigView -> viewModel.navigateTo(Screen.AdminDashboard)
      Screen.PatientHistory -> viewModel.navigateTo(Screen.Doctors)
      Screen.AdminDashboard -> viewModel.navigateTo(Screen.Doctors)
      else -> viewModel.navigateTo(Screen.Doctors)
    }
  }

  val showBottomNav = currentScreen == Screen.Doctors ||
      currentScreen == Screen.PatientHistory ||
      currentScreen == Screen.AdminDashboard

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (showBottomNav) {
        NavigationBar(
          containerColor = Color.White,
          contentColor = RoyalNavy
        ) {
          NavigationBarItem(
            selected = currentScreen == Screen.Doctors,
            onClick = { viewModel.navigateTo(Screen.Doctors) },
            icon = {
              Icon(
                imageVector = if (currentScreen == Screen.Doctors) Icons.Filled.LocalHospital else Icons.Outlined.LocalHospital,
                contentDescription = "Doctors"
              )
            },
            label = { Text("Doctors", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoyalGold,
              selectedTextColor = RoyalNavy,
              indicatorColor = RoyalNavy
            ),
            modifier = Modifier.testTag("nav_doctors_tab")
          )

          NavigationBarItem(
            selected = currentScreen == Screen.PatientHistory,
            onClick = { viewModel.navigateTo(Screen.PatientHistory) },
            icon = {
              Icon(
                imageVector = if (currentScreen == Screen.PatientHistory) Icons.Filled.History else Icons.Outlined.History,
                contentDescription = "Payment History"
              )
            },
            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoyalGold,
              selectedTextColor = RoyalNavy,
              indicatorColor = RoyalNavy
            ),
            modifier = Modifier.testTag("nav_history_tab")
          )

          NavigationBarItem(
            selected = currentScreen == Screen.AdminDashboard,
            onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
            icon = {
              Icon(
                imageVector = if (currentScreen == Screen.AdminDashboard) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                contentDescription = "Admin Dashboard"
              )
            },
            label = { Text("Admin Portal", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = RoyalGold,
              selectedTextColor = RoyalNavy,
              indicatorColor = RoyalNavy
            ),
            modifier = Modifier.testTag("nav_admin_tab")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(if (showBottomNav) innerPadding else androidx.compose.foundation.layout.PaddingValues())
    ) {
      when (currentScreen) {
        Screen.Doctors -> {
          DoctorListScreen(
            doctors = doctors,
            isSandbox = gatewayConfig?.isSandbox ?: true,
            onSelectDoctor = { doc, consultationType, bookingType ->
              viewModel.selectDoctorForBooking(doc, consultationType, bookingType)
            },
            onOpenGatewayConfig = {
              viewModel.navigateTo(Screen.GatewayConfigView)
            }
          )
        }

        Screen.BookingConfirm -> {
          selectedDoctor?.let { doc ->
            ConsultationBookingScreen(
              doctor = doc,
              consultationType = selectedConsultationType,
              bookingType = selectedBookingType,
              selectedSlot = selectedSlot,
              onConsultationTypeChange = { viewModel.setConsultationType(it) },
              onBookingTypeChange = { viewModel.setBookingType(it) },
              onSlotChange = { viewModel.setSlot(it) },
              onConfirmBooking = { name, phone ->
                viewModel.confirmBookingAndProceedToCheckout(name, phone)
              },
              onBack = { viewModel.navigateTo(Screen.Doctors) }
            )
          } ?: run {
            viewModel.navigateTo(Screen.Doctors)
          }
        }

        Screen.Checkout -> {
          currentOrder?.let { order ->
            PaymentCheckoutScreen(
              order = order,
              gatewayConfig = gatewayConfig,
              isProcessing = isPaymentProcessing,
              errorMessage = paymentErrorMessage,
              onPay = { method, simulateFailure, failureReason ->
                viewModel.processPayment(method, simulateFailure, failureReason)
              },
              onBack = { viewModel.navigateTo(Screen.BookingConfirm) }
            )
          } ?: run {
            viewModel.navigateTo(Screen.Doctors)
          }
        }

        Screen.Receipt -> {
          currentOrder?.let { order ->
            PaymentSuccessReceiptScreen(
              order = order,
              appointment = activeAppointment,
              onJoinConsultation = {
                activeAppointment?.let { viewModel.openConsultationRoom(it) }
              },
              onGoHome = { viewModel.navigateTo(Screen.Doctors) }
            )
          } ?: run {
            viewModel.navigateTo(Screen.Doctors)
          }
        }

        Screen.TelemedicineRoom -> {
          activeAppointment?.let { apt ->
            TelemedicineRoomScreen(
              appointment = apt,
              onEndCall = { prescription ->
                viewModel.completeConsultation(apt.appointmentId, prescription)
                viewModel.navigateTo(Screen.Doctors)
              }
            )
          } ?: run {
            viewModel.navigateTo(Screen.Doctors)
          }
        }

        Screen.PatientHistory -> {
          PatientHistoryScreen(
            orders = orders,
            onViewReceipt = { order ->
              viewModel.openReceiptForOrder(order)
            }
          )
        }

        Screen.AdminDashboard -> {
          AdminDashboardScreen(
            orders = orders,
            doctors = doctors,
            auditLogs = auditLogs,
            refunds = refunds,
            onInitiateRefund = { orderId, amount, reason ->
              viewModel.executeAdminRefund(orderId, amount, reason)
            },
            onUpdateDoctorFee = { docId, video, audio, instant, sched, dur, hours, online ->
              viewModel.updateDoctorFeeConfig(docId, video, audio, instant, sched, dur, hours, online)
            },
            onOpenGatewaySettings = {
              viewModel.navigateTo(Screen.GatewayConfigView)
            }
          )
        }

        Screen.GatewayConfigView -> {
          GatewayConfigScreen(
            currentConfig = gatewayConfig,
            onSaveConfig = { updated ->
              viewModel.updateGatewayConfiguration(updated)
            },
            onBack = { viewModel.navigateTo(Screen.AdminDashboard) }
          )
        }
      }
    }
  }

  // Doctor Incoming Consultation Request Modal
  incomingCallAppointment?.let { incomingCall ->
    IncomingCallModal(
      appointment = incomingCall,
      onAccept = {
        viewModel.respondToIncomingCall(incomingCall.appointmentId, accept = true)
      },
      onReject = {
        viewModel.respondToIncomingCall(incomingCall.appointmentId, accept = false)
      }
    )
  }
}
