package com.makki.klotter.handlers.implementations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler
import com.makki.klotter.models.KLineDrawing
import kotlin.math.max
import kotlin.math.min


class KLineHandler(
	val dropColor: Color = Color(227, 85, 97),
	val raiseColor: Color = Color(93, 200, 153)
) : PlotDataHandler<KLineDrawing> {
	override fun topFocus(data: KLineDrawing): Float {
		return data.high.toFloat()
	}

	override fun botFocus(data: KLineDrawing): Float {
		return data.low.toFloat()
	}

	override fun draw(context: DrawContext, scopeT: List<KLineDrawing?>) {
		val rectList = List(scopeT.size) { index -> context.getRecForIndex(index) }

		scopeT.forEachIndexed { i, k ->
			k ?: return@forEachIndexed

			val rect = rectList[i]
			val highY = context.getYForData(k.high.toFloat())
			val lowY = context.getYForData(k.low.toFloat())
			val close = k.close.toFloat()
			val open = k.open.toFloat()
			val topOCY = context.getYForData(max(open, close))
			var botOCY = context.getYForData(min(open, close))
			if (topOCY - botOCY == 0f) {
				botOCY = topOCY - 20f
			}
			val color = if (close >= open) {
				raiseColor
			} else {
				dropColor
			}

			context.canvas.drawLine(color, Offset(rect.center.x, highY), Offset(rect.center.x, lowY))
			context.canvas.drawRect(color = color, Offset(rect.left, topOCY), Size(rect.width, botOCY - topOCY))
		}
	}
}