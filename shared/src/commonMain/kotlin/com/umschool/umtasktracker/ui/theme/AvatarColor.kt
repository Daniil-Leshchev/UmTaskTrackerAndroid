package com.umschool.umtasktracker.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

fun avatarColor(seed: String): Color {
    var hash = 0
    for (c in seed) {
        hash = (hash shl 5) - hash + c.code
    }
    val hueSteps = 36
    val hue = (abs(hash) % hueSteps) * (360f / hueSteps)
    return Color.hsl(hue = hue, saturation = 0.60f, lightness = 0.55f)
}
