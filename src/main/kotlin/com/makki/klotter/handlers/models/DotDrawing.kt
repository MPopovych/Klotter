package com.makki.klotter.handlers.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class DotDrawing(
	val value: Number,
	val color: Color,
	val radius: Float = 5.dp.value
)