import os

color_kt = """package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF07090F) // Deepest blue/black
val DarkSurface = Color(0xFF131624) // Slightly elevated
val SurfaceVariantDark = Color(0xFF1B1E31) // Higher elevation
val DividerColor = Color(0xFF272B40)

val AuroraPrimary = Color(0xFF7C3AED) // Violet
val AuroraSecondary = Color(0xFF06B6D4) // Cyan
val AuroraTertiary = Color(0xFF3B82F6) // Blue

val NeonGreen = Color(0xFF00E5FF) // Kept for compatibility but it's Cyan
val DeepPurple = Color(0xFF7C3AED) // Kept for compatibility
val ErrorRed = Color(0xFFEF4444)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val DarkGreyText = Color(0xFF94A3B8)

// Gradients
val AuroraGradient = listOf(AuroraPrimary, AuroraSecondary)
"""

with open("app/src/main/java/com/example/ui/theme/Color.kt", "w") as f:
    f.write(color_kt)

type_kt = """package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
"""

with open("app/src/main/java/com/example/ui/theme/Type.kt", "w") as f:
    f.write(type_kt)

theme_kt = """package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AuroraPrimary,
    secondary = AuroraSecondary,
    tertiary = AuroraTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = SurfaceVariantDark,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    outline = DividerColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
"""
with open("app/src/main/java/com/example/ui/theme/Theme.kt", "w") as f:
    f.write(theme_kt)
