package com.hourlynotice.mobile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF6750A4)
private val LightPurple = Color(0xFFD0BCFF)
private val LightBackground = Color(0xFFFAF8FF)
private val DarkBackground = Color(0xFF121016)

@Composable
fun OisinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            darkColorScheme(primary = LightPurple, secondary = LightPurple, background = DarkBackground, surface = DarkBackground)
        } else {
            lightColorScheme(primary = Purple, secondary = Purple, background = LightBackground)
        },
        content = content,
    )
}
