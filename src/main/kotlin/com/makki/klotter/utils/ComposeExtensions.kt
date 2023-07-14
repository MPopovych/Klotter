package com.makki.klotter.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint

fun findClosestPower(number: Float): Pair<Float, Int> {
	var start = 100000000.0f
	var decimal = -8
	while (number < start) {
		start /= 10
		decimal++
	}
	return Pair(start, decimal)
}

@Composable
inline fun singleComposableJob(key: Any? = null, block: @Composable () -> Job): Job? {
	val pendingJob = if (key != null) {
		remember<Array<Job?>>(key1 = key) { arrayOf(null) }
	} else {
		remember<Array<Job?>> { arrayOf(null) }
	}
	pendingJob[0]?.cancel()
	pendingJob[0] = block()
	return pendingJob[0]
}

@Composable
inline fun launchSingletonJob(key: Any? = null, scope: CoroutineScope, crossinline block: suspend () -> Unit): Job? {
	return singleComposableJob(key) {
		return@singleComposableJob scope.launch {
			block()
		}
	}
}

@Composable
inline fun CoroutineScope.launchSingleton(
	key: Any? = null,
	crossinline block: suspend CoroutineScope.() -> Unit
): Job? {
	return singleComposableJob(key) {
		return@singleComposableJob launch {
			block()
		}
	}
}

@Composable
fun composeScope(block: @Composable () -> Unit) {
	block()
}

fun DrawScope.drawText(text: String, font: Font, paint: Paint, offset: Offset = Offset.Zero) {
	drawIntoCanvas {// sub to updates
		it.nativeCanvas.drawString(
			text,
			offset.x,
			offset.y + font.size,
			font,
			paint
		)
	}
}