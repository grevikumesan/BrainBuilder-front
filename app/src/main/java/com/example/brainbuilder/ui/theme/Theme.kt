package com.example.brainbuilder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = ElectricBlueOnContainer,
    secondary = LimePop,
    onSecondary = Color.White,
    secondaryContainer = LimeContainer,
    onSecondaryContainer = LimeOnContainer,
    tertiary = SparkOrange,
    onTertiary = Color.White,
    tertiaryContainer = SparkOrangeContainer,
    onTertiaryContainer = SparkOrangeOnContainer,
    background = CanvasLight,
    onBackground = InkLight,
    surface = SurfaceLight,
    onSurface = InkLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = SlateLight,
    outline = OutlineLight,
    outlineVariant = SurfaceVariantLight,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueOnDark,
    onPrimary = ElectricBlueOnContainer,
    primaryContainer = ElectricBlueContainerDark,
    onPrimaryContainer = ElectricBlueContainer,
    secondary = LimeContainer,
    onSecondary = LimeOnContainer,
    secondaryContainer = LimeContainerDark,
    onSecondaryContainer = LimeContainer,
    tertiary = SparkOrangeDark,
    onTertiary = SparkOrangeOnContainer,
    tertiaryContainer = SparkOrangeOnContainer,
    onTertiaryContainer = SparkOrangeContainer,
    background = CanvasDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = SlateDark,
    outline = OutlineDark,
    outlineVariant = SurfaceVariantDark,
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0A0A)
)

@Composable
fun BrainBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color is intentionally off (MD3 allows static schemes): a learning brand
    // needs one consistent, recognizable identity, not the device wallpaper palette.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
