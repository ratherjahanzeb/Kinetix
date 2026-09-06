package com.jahanzeb.kinetix.ui.theme

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
    primary = Color(0xFF10B981),
    secondary = Color(0xFF34D399),
    tertiary = Color(0xFF6EE7B7),
    background = Color(0xFF090D16),
    surface = Color(0xFF131C2E),
    surfaceVariant = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF94A3B8),
    error = Color(0xFFEF4444),
    outline = Color(0xFF334155)
)

private val LightEmeraldColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    secondary = Color(0xFF10B981),
    tertiary = Color(0xFF34D399),
    background = Color(0xFFF0FDF4),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFDCFCE7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF064E3B),
    onSurface = Color(0xFF064E3B),
    onSurfaceVariant = Color(0xFF047857),
    error = Color(0xFFEF4444),
    outline = Color(0xFFBBF7D0)
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

private val LightSunsetColorScheme = lightColorScheme(
    primary = Color(0xFFEA580C),
    secondary = Color(0xFFF97316),
    tertiary = Color(0xFFFBBF24),
    background = Color(0xFFFFFBEB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFFEF3C7),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF78350F),
    onSurface = Color(0xFF78350F),
    onSurfaceVariant = Color(0xFFB45309),
    error = Color(0xFFEF4444),
    outline = Color(0xFFFDE68A)
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

private val LightPurpleColorScheme = lightColorScheme(
    primary = Color(0xFF7C3AED),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFFD946EF),
    background = Color(0xFFFAF5FF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF3E8FF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF3B0764),
    onSurface = Color(0xFF3B0764),
    onSurfaceVariant = Color(0xFF6D28D9),
    error = Color(0xFFEF4444),
    outline = Color(0xFFE9D5FF)
)

fun getStaticColorScheme(name: String, darkTheme: Boolean = true, amoled: Boolean = false) = when (name) {
    "EMERALD" -> if (darkTheme) DynamicEmeraldColorScheme else LightEmeraldColorScheme
    "SUNSET" -> if (darkTheme) SunsetColorScheme else LightSunsetColorScheme
    "PURPLE" -> if (darkTheme) PurpleColorScheme else LightPurpleColorScheme
    else -> if (darkTheme) DarkColorScheme else LightColorScheme
}.let { scheme ->
    if (darkTheme && amoled) {
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
                if (darkTheme && isAmoled) {
                    base.copy(background = Color.Black, surface = Color(0xFF0C0C0C), surfaceVariant = Color(0xFF161616))
                } else {
                    base
                }
            } catch (e: Exception) {
                getStaticColorScheme(accentTheme, darkTheme, isAmoled)
            }
        }
        else -> {
            getStaticColorScheme(accentTheme, darkTheme, isAmoled)
        }
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
