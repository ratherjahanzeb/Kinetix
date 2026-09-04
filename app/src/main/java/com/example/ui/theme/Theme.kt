package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C3AED),
    secondary = Color(0xFF06B6D4),
    tertiary = Color(0xFF3B82F6),
    background = Color(0xFF07090F),
    surface = Color(0xFF131624),
    surfaceVariant = Color(0xFF1B1E31),
    onPrimary = Color(0xFFF8FAFC),
    onSecondary = Color(0xFFF8FAFC),
    onTertiary = Color(0xFFF8FAFC),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Color(0xFFEF4444),
    outline = Color(0xFF272B40)
)

private val DynamicEmeraldColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF10B981),
    secondary = androidx.compose.ui.graphics.Color(0xFF34D399),
    tertiary = androidx.compose.ui.graphics.Color(0xFF6EE7B7),
    background = androidx.compose.ui.graphics.Color(0xFF090D16),
    surface = androidx.compose.ui.graphics.Color(0xFF131C2E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF1E293B),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF94A3B8),
    error = Color(0xFFEF4444),
    outline = androidx.compose.ui.graphics.Color(0xFF334155)
)

private val SunsetColorScheme = darkColorScheme(
    primary = Color(0xFFF97316),
    secondary = Color(0xFFFBBF24),
    tertiary = Color(0xFFFB7185),
    background = Color(0xFF0C0907),
    surface = Color(0xFF1C130D),
    surfaceVariant = Color(0xFF2C1D14),
    onPrimary = Color(0xFFF8FAFC),
    onSecondary = Color(0xFFF8FAFC),
    onTertiary = Color(0xFFF8FAFC),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Color(0xFFEF4444),
    outline = Color(0xFF3F2B1E)
)

private val PurpleColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFFEC4899),
    tertiary = Color(0xFFD946EF),
    background = Color(0xFF09070F),
    surface = Color(0xFF141021),
    surfaceVariant = Color(0xFF201833),
    onPrimary = Color(0xFFF8FAFC),
    onSecondary = Color(0xFFF8FAFC),
    onTertiary = Color(0xFFF8FAFC),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Color(0xFFEF4444),
    outline = Color(0xFF352651)
)

fun getStaticColorScheme(name: String) = when (name) {
    "EMERALD" -> DynamicEmeraldColorScheme
    "SUNSET" -> SunsetColorScheme
    "PURPLE" -> PurpleColorScheme
    else -> DarkColorScheme
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    accentTheme: String = "AURORA",
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            try {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } catch (e: Exception) {
                getStaticColorScheme(accentTheme)
            }
        }
        else -> getStaticColorScheme(accentTheme)
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme && !dynamicColor
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
