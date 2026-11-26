package com.hourlynotice.wear

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightPurple = Color(0xFFD0BCFF)
private val WatchSurface = Color(0xFF19161D)

@Composable
fun OisinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = LightPurple,
            secondary = LightPurple,
            background = Color.Black,
            surface = WatchSurface,
        ),
        content = content,
    )
}
