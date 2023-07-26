package com.makki.klotter.handlers.implementations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler
import com.makki.klotter.handlers.models.DotDrawing

class MultiDotHandler : PlotDataHandler<List<DotDrawing>> {
	override fun topFocus(data: List<DotDrawing>): Float {
		return data.maxBy { it.value.toFloat() }.value.toFloat()
	}

	override fun botFocus(data: List<DotDrawing>): Float {
		return data.minBy { it.value.toFloat() }.value.toFloat()
	}

	override fun trackerValue(data: List<DotDrawing>): Float {
		return data.map { it.value.toFloat() }.average().toFloat()
	}

	override fun draw(context: DrawContext, scopeT: List<List<DotDrawing>?>) {
		scopeT.forEachIndexed { i, n ->
			n ?: return@forEachIndexed
			for (dot in n) {
				val p = Offset(context.getRecForIndex(i).center.x, context.getYForData(dot.value.toFloat()))
				context.canvas.drawCircle(dot.color, dot.radius, p)
			}
		}
	}
}