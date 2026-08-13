package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TerminalColorScheme = darkColorScheme(
    primary = TerminalAmber,
    onPrimary = Color.Black,
    primaryContainer = TerminalSurfaceVariant,
    onPrimaryContainer = TerminalAmberLight,
    secondary = TerminalCyan,
    onSecondary = Color.Black,
    tertiary = TerminalGold,
    onTertiary = Color.Black,
    background = TerminalBlack,
    onBackground = TerminalTextPrimary,
    surface = TerminalSurface,
    onSurface = TerminalTextPrimary,
    surfaceVariant = TerminalSurfaceVariant,
    onSurfaceVariant = TerminalTextSecondary,
    outline = TerminalBorder,
    outlineVariant = TerminalTextMuted,
    error = TerminalRed,
    onError = Color.White
)

@Composable
fun BloombergTheme(
    darkTheme: Boolean = true, // Force terminal dark mode by default
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TerminalColorScheme,
        typography = Typography,
        content = content
    )
}
