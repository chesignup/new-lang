package com.newlang.french.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Scheme = lightColorScheme(
    primary = Burgundy,
    onPrimary = Color.White,
    primaryContainer = Blush,
    onPrimaryContainer = BurgundyDark,
    secondary = Gold,
    onSecondary = Ink,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = Ink,
    tertiary = Sage,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Blush,
    onSurfaceVariant = InkMuted,
    outline = Gold.copy(alpha = 0.55f),
    error = Danger
)

@Composable
fun NewLangTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        typography = NewLangTypography,
        content = content
    )
}
