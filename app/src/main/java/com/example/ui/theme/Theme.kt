package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DankColorScheme = darkColorScheme(
    primary = AccentBlue,
    secondary = NeonCyan,
    tertiary = GoldCert,
    background = DeepNavyBg,
    surface = DarkSlateSurface,
    surfaceVariant = DarkSlateActive,
    outline = CustomOutline,
    onBackground = PlatinumText,
    onSurface = TitleWhite,
    onPrimary = Color.White,
    onSecondary = DeepNavyBg
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DankColorScheme,
        typography = Typography,
        content = content
    )
}
