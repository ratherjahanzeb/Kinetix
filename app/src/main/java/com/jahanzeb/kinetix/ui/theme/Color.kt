package com.jahanzeb.kinetix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkBackground: Color
    @Composable get() = MaterialTheme.colorScheme.background

val DarkSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val SurfaceVariantDark: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant

val DividerColor: Color
    @Composable get() = MaterialTheme.colorScheme.outline

val AuroraPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val AuroraSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.secondary

val AuroraTertiary: Color
    @Composable get() = MaterialTheme.colorScheme.tertiary

val NeonGreen: Color
    @Composable get() = MaterialTheme.colorScheme.secondary

val DeepPurple: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val ErrorRed: Color
    @Composable get() = MaterialTheme.colorScheme.error

val TextPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

val TextSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val DarkGreyText: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val AuroraGradient: List<Color>
    @Composable get() = listOf(AuroraPrimary, AuroraSecondary)

val AccentSuccess: Color
    @Composable get() = Color(0xFF10B981)
