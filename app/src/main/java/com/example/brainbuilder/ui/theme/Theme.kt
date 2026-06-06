package com.example.brainbuilder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = BrandBlueLight,
    onPrimaryContainer = BrandBlueDark,
    secondary = AccentTeal,
    onSecondary = Color.White,
    secondaryContainer = AccentTealLight,
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = AccentAmber,
    onTertiary = Color.White,
    background = NeutralCloud,
    onBackground = NeutralInk,
    surface = NeutralSurface,
    onSurface = NeutralInk,
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = NeutralSlate,
    outline = NeutralOutline,
    error = NeutralError,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlueOnDark,
    onPrimary = Color(0xFF0B1020),
    primaryContainer = BrandBlueDark,
    onPrimaryContainer = BrandBlueLight,
    secondary = AccentTeal,
    onSecondary = Color(0xFF02281D),
    secondaryContainer = Color(0xFF0B3D2E),
    onSecondaryContainer = AccentTealLight,
    tertiary = AccentAmber,
    onTertiary = Color(0xFF3A1A00),
    background = DarkBg,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkSlate,
    outline = DarkOutline,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0A0A)
)

@Composable
fun BrainBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color is intentionally off: a learning brand needs a consistent,
    // recognizable identity rather than the device wallpaper palette.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
