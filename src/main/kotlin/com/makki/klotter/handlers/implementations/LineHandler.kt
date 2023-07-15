package com.makki.klotter.handlers.implementations

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler

class LineHandler(
	private val strokeWidth: Float,
	color: Int,
	stroke: Boolean = false,
	private val smooth: Boolean = false
) : PlotDataHandler<Number> {
	private val nativeColor = Color(color)
	private val effect: PathEffect? = if (stroke) {
		PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
	} else {
		null
	}

	override fun topFocus(data: Number): Float {
		return data.toFloat()
	}

	override fun botFocus(data: Number): Float {
		return data.toFloat()
	}

	override fun trackerValue(data: Number): Float {
		return data.toFloat()
	}

	override fun draw(context: DrawContext, scopeT: List<Number?>) {
		val xList = List(scopeT.size) { index -> context.getRecForIndex(index).center.x }
		val yList = scopeT.map { number -> context.getYForData(number?.toFloat() ?: return@map null) }
		val zip = xList.zip(yList)
		val p = Path()
		var lastX = 0f
		var lastY = 0f

		val firstPresent = zip.firstOrNull { it.second != null }
		if (firstPresent != null) {
			lastX = firstPresent.first
			lastY = firstPresent.second ?: 0f
		}
		p.moveTo(lastX, lastY)
		var preDeltaX = 0f
		var preDeltaY = 0f
		for (i in zip.indices) {
			val (x, y) = zip[i]
			y ?: continue
			val (nextX, nextY) = zip.getOrNull(i + 1) ?: zip[i]
			nextY ?: continue
			if (smooth && !context.fastMode) {
				val gradient = gradient(nextX, nextY, x, y)
				val deltaX = (nextX - x) * -0.3f
				val deltaY = deltaX * gradient * 0.4f
				p.cubicTo(
					lastX - preDeltaX, lastY - preDeltaY,
					x + deltaX, y + deltaY,
					x, y
				)

				lastX = x
				lastY = y
				preDeltaX = deltaX
				preDeltaY = deltaY
			} else {
				p.lineTo(x, y)
			}
		}

		context.canvas.drawPath(
			p, nativeColor, style = Stroke(
				width = strokeWidth,
				pathEffect = if (context.fastMode) null else effect
			)
		)
	}

	private fun gradient(x: Float, y: Float, nX: Float, nY: Float): Float {
		val dX = nX - x
		val dY = nY - y
		if (dX != 0.0f) {
			return dY / dX
		}
		return 0f
	}
}