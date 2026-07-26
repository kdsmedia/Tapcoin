package com.altomedia.altotap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = darkColorScheme(
    primary = GameGoldPrimary,
    onPrimary = GameTextDark,
    primaryContainer = GameGoldDark,
    secondary = GameGreenLight,
    onSecondary = GameTextDark,
    background = GameGreenDark,
    surface = GameGreenCard,
    onSurface = GameTextDark,
    surfaceVariant = GameGreenCardDark,
    onSurfaceVariant = GameTextLight
)

@Composable
fun RupiahTapperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}

