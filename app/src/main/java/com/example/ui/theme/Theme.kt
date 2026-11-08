package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = GoldSun,
  secondary = TerracottaTezontle,
  tertiary = JadeTeal,
  background = ObsidianDark,
  surface = ObsidianCard,
  onPrimary = ObsidianDark,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = SandBeige,
  onSurface = SandBeige
)

private val LightColorScheme = lightColorScheme(
  primary = TerracottaTezontle,
  secondary = GoldSun,
  tertiary = JadeTeal,
  background = SandBeige,
  surface = Color.White,
  onPrimary = Color.White,
  onSecondary = ObsidianDark,
  onTertiary = Color.White,
  onBackground = ObsidianDark,
  onSurface = ObsidianDark
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val currentDensity = LocalDensity.current
  // Global font size multiplier to ensure clear, legible, enlarged text across all app screens
  val enlargedDensity = Density(
    density = currentDensity.density,
    fontScale = currentDensity.fontScale * 1.20f
  )

  CompositionLocalProvider(LocalDensity provides enlargedDensity) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}
