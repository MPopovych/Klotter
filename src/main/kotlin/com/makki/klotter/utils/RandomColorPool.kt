package com.makki.klotter.utils

import androidx.compose.ui.graphics.Color
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

object RandomColorPool {

	private val cache = ConcurrentHashMap<String, Color>()

	fun cachedRandomColor(name: String, alpha: Float): Color {
		return cache.getOrPut(name) {
			randomColor(alpha)
		}
	}

	private fun randomColor(alpha: Float = 1f): Color {
		val alphaInt = (max(min(alpha, 1f), 0f) * 255).roundToInt()
		return Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256), alphaInt)
	}




}