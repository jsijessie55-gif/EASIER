package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Emerald400,
    onPrimary = Navy900,
    primaryContainer = Teal600,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = Teal500,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = Navy900,
    surface = Navy800,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Purple600,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Purple100,
    onPrimaryContainer = Purple900,
    secondary = Purple500,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = SurfaceLight,
    surface = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
