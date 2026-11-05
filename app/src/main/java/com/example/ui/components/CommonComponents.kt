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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.ui.theme.BkashPink
import com.example.ui.theme.CardNavy
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketViolet
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDeep
import com.example.ui.theme.RoyalNavyMedium
import com.example.ui.theme.RoyalTeal
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusFailedBg
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusRefunded
import com.example.ui.theme.StatusRefundedBg
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusSuccessBg
import com.example.ui.theme.UpayNavy

@Composable
fun StatusBadge(status: PaymentStatus, modifier: Modifier = Modifier) {
  val (bgColor, textColor, icon) = when (status) {
    PaymentStatus.SUCCESSFUL -> Triple(StatusSuccessBg, StatusSuccess, Icons.Default.CheckCircle)
    PaymentStatus.PENDING, PaymentStatus.INITIATED -> Triple(StatusPendingBg, StatusPending, Icons.Default.Info)
    PaymentStatus.FAILED -> Triple(StatusFailedBg, StatusFailed, Icons.Default.Warning)
    PaymentStatus.REFUNDED, PaymentStatus.PARTIALLY_REFUNDED -> Triple(StatusRefundedBg, StatusRefunded, Icons.Default.Info)
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(20.dp),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = status.label,
        color = textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun PaymentMethodBadge(method: PaymentMethod?, modifier: Modifier = Modifier) {
  if (method == null) return
  val (brandColor, label) = when (method) {
    PaymentMethod.BKASH -> Pair(BkashPink, "bKash")
    PaymentMethod.NAGAD -> Pair(NagadOrange, "Nagad")
    PaymentMethod.ROCKET -> Pair(RocketViolet, "Rocket")
    PaymentMethod.UPAY -> Pair(UpayNavy, "Upay")
    PaymentMethod.CARD -> Pair(CardNavy, "Card")
  }

  Surface(
    color = brandColor.copy(alpha = 0.12f),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, brandColor.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Text(
      text = label,
      color = brandColor,
      fontWeight = FontWeight.ExtraBold,
      fontSize = 12.sp,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun SandboxWarningBanner(isSandbox: Boolean, modifier: Modifier = Modifier) {
  if (isSandbox) {
    Surface(
      color = Color(0xFFFFFBEB),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
      shape = RoundedCornerShape(12.dp),
      modifier = modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = Color(0xFFD97706),
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "SANDBOX TEST ENVIRONMENT ACTIVE",
            color = Color(0xFF92400E),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = "All bKash, Nagad & Card payment transactions are simulated securely. No real money will be deducted from your account.",
            color = Color(0xFFB45309),
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        }
      }
    }
  } else {
    Surface(
      color = Color(0xFFECFDF5),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
      shape = RoundedCornerShape(12.dp),
      modifier = modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = StatusSuccess,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "LIVE MERCHANT GATEWAY ACTIVE",
            color = Color(0xFF065F46),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
          Text(
            text = "Connected to verified merchant accounts with end-to-end cryptographic signature validation.",
            color = Color(0xFF047857),
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun SecurityGuaranteesCard(modifier: Modifier = Modifier) {
  Surface(
    color = RoyalNavyDeep,
    shape = RoundedCornerShape(16.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = RoyalGold,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "ROYAL CARE ENTERPRISE SECURITY",
          color = RoyalGold,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          letterSpacing = 1.sp
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      SecurityBullet(text = "Zero-PIN Retention: We NEVER store your bKash or Nagad PIN / OTP on any server.")
      SecurityBullet(text = "HMAC-SHA256 Signature Verification on every transaction to prevent tampering.")
      SecurityBullet(text = "Cryptographic Idempotency token prevents accidental double billing.")
      SecurityBullet(text = "100% Guaranteed Refund if doctor is unavailable for instant consultation.")
    }
  }
}

@Composable
private fun SecurityBullet(text: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .padding(top = 6.dp)
        .size(5.dp)
        .clip(CircleShape)
        .background(RoyalTeal)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      color = Color(0xFFCFD6E4),
      fontSize = 11.5.sp,
      lineHeight = 16.sp
    )
  }
}
