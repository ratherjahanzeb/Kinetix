package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7C3AED),
    secondary = Color(0xFF06B6D4),
    tertiary = Color(0xFF3B82F6),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B),
    error = Color(0xFFEF4444),
    outline = Color(0xFFE2E8F0)
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

fun getStaticColorScheme(name: String, amoled: Boolean = false) = when (name) {
    "EMERALD" -> DynamicEmeraldColorScheme
    "SUNSET" -> SunsetColorScheme
    "PURPLE" -> PurpleColorScheme
    else -> DarkColorScheme
}.let { scheme ->
    if (amoled) {
        scheme.copy(
            background = Color.Black,
            surface = Color(0xFF0C0C0C),
            surfaceVariant = Color(0xFF161616)
        )
    } else {
        scheme
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    accentTheme: String = "AURORA",
    amoledDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isAmoled = amoledDarkMode && darkTheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            try {
                val base = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                if (isAmoled) {
                    base.copy(background = Color.Black, surface = Color(0xFF0C0C0C), surfaceVariant = Color(0xFF161616))
                } else {
                    base
                }
            } catch (e: Exception) {
                if (darkTheme) getStaticColorScheme(accentTheme, isAmoled) else LightColorScheme
            }
        }
        else -> {
            if (darkTheme) {
                getStaticColorScheme(accentTheme, isAmoled)
            } else {
                LightColorScheme
            }
        }
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
