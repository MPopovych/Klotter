package com.makki.klotter.handlers.implementations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler

class DotHandler(private val radius: Float, private val color: Color) : PlotDataHandler<Number> {
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
		scopeT.forEachIndexed { i, n ->
			n ?: return@forEachIndexed
			val p = Offset(context.getRecForIndex(i).center.x, context.getYForData(n.toFloat()))
			context.canvas.drawCircle(color, radius, p)
		}
	}
}