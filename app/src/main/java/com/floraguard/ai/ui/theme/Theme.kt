package com.floraguard.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGreenScheme = lightColorScheme(
    primary = Color(0xFF2F8F4E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC6F2D2),
    onPrimaryContainer = Color(0xFF0B2E1A),
    secondary = Color(0xFF3B7F57),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFBFE7CD),
    onSecondaryContainer = Color(0xFF0C2C1B),
    tertiary = Color(0xFF1F6E4A),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4FAF5),
    onBackground = Color(0xFF0E1B12),
    surface = Color(0xFFF4FAF5),
    onSurface = Color(0xFF0E1B12),
    surfaceVariant = Color(0xFFDCE9DE),
    onSurfaceVariant = Color(0xFF2F3E35),
    outline = Color(0xFF6F8277)
)

private val DarkGreenScheme = darkColorScheme(
    primary = Color(0xFF70D39A),
    onPrimary = Color(0xFF0B2B1A),
    primaryContainer = Color(0xFF145C37),
    onPrimaryContainer = Color(0xFFCFF8DD),
    secondary = Color(0xFF64C38F),
    onSecondary = Color(0xFF0A2819),
    secondaryContainer = Color(0xFF1D4A33),
    onSecondaryContainer = Color(0xFFBFEFD2),
    tertiary = Color(0xFF59B884),
    onTertiary = Color(0xFF0A2618),
    background = Color(0xFF0B1410),
    onBackground = Color(0xFFE1F4E7),
    surface = Color(0xFF0B1410),
    onSurface = Color(0xFFE1F4E7),
    surfaceVariant = Color(0xFF1E2A23),
    onSurfaceVariant = Color(0xFFB8C9BE),
    outline = Color(0xFF6E7F75)
)

@Composable
fun FloraGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkGreenScheme else LightGreenScheme,
        content = content
    )
}
