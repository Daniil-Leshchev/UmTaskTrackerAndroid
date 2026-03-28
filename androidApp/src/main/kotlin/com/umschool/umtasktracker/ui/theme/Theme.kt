package com.umschool.umtasktracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = UmOrange,
    onPrimary = White,
    primaryContainer = UmOrange,
    onPrimaryContainer = White,
    error = ErrorRed,
    background = White,
    surface = White
)

@Composable
fun UmTaskTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
