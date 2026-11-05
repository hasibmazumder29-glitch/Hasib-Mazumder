package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = RoyalNavy,
  onPrimary = RoyalSurfaceLight,
  primaryContainer = RoyalNavyMedium,
  onPrimaryContainer = RoyalGoldLight,
  secondary = RoyalGold,
  onSecondary = RoyalNavyDeep,
  secondaryContainer = RoyalGoldLight,
  onSecondaryContainer = RoyalGoldDark,
  tertiary = RoyalTeal,
  onTertiary = RoyalSurfaceLight,
  tertiaryContainer = RoyalTealLight,
  onTertiaryContainer = RoyalTealDark,
  background = RoyalBgLight,
  onBackground = RoyalTextPrimary,
  surface = RoyalSurfaceLight,
  onSurface = RoyalTextPrimary,
  surfaceVariant = RoyalSurfaceVariant,
  onSurfaceVariant = RoyalTextSecondary,
  outline = RoyalBorderLight,
  outlineVariant = RoyalBorderLight
)

private val DarkColorScheme = darkColorScheme(
  primary = RoyalGold,
  onPrimary = RoyalNavyDeep,
  primaryContainer = RoyalNavyLight,
  onPrimaryContainer = RoyalGoldLight,
  secondary = RoyalTealLight,
  onSecondary = RoyalNavyDeep,
  secondaryContainer = RoyalNavyMedium,
  onSecondaryContainer = RoyalGoldLight,
  tertiary = RoyalTeal,
  onTertiary = RoyalSurfaceLight,
  background = RoyalNavyDeep,
  onBackground = RoyalSurfaceLight,
  surface = RoyalNavy,
  onSurface = RoyalSurfaceLight,
  surfaceVariant = RoyalNavyMedium,
  onSurfaceVariant = RoyalGoldLight,
  outline = RoyalNavyLight,
  outlineVariant = RoyalNavyLight
)

@Composable
fun RoyalCareTheme(
  darkTheme: Boolean = false, // Keep elegant Royal light mode by default for medical clarity
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
