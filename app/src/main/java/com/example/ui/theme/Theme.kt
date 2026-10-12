package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameBridgeColorScheme = darkColorScheme(
    primary = GamingBlue,
    onPrimary = Color.White,
    primaryContainer = GamingNavySurfaceVariant,
    onPrimaryContainer = GamingCyanGlow,
    secondary = GamingCyan,
    onSecondary = GamingNavyDark,
    secondaryContainer = GamingNavyBorder,
    onSecondaryContainer = TextPrimary,
    tertiary = GamingBlueHover,
    onTertiary = Color.White,
    background = GamingNavyDark,
    onBackground = TextPrimary,
    surface = GamingNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = GamingNavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GamingNavyBorder,
    outlineVariant = GamingBlue.copy(alpha = 0.3f),
    error = AccentRed,
    onError = Color.White
)

@Composable
fun GameBridgeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameBridgeColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GameBridgeTheme(content = content)
}
