package com.makki.klotter.handlers.implementations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler
import com.makki.klotter.handlers.models.KLineDrawing
import kotlin.math.max
import kotlin.math.min


class KLineHandler(
	dropColor: Int = Color(227, 85, 97).toArgb(),
	raiseColor: Int = Color(93, 200, 153).toArgb()
) : PlotDataHandler<KLineDrawing> {

	private val dropColor: Color = Color(dropColor)
	private val raiseColor: Color = Color(raiseColor)

	override fun topFocus(data: KLineDrawing): Float {
		return data.high.toFloat()
	}

	override fun botFocus(data: KLineDrawing): Float {
		return data.low.toFloat()
	}

	override fun draw(context: DrawContext, scopeT: List<KLineDrawing?>) {
		val rectList = List(scopeT.size) { index -> context.getRecForIndex(index) }

		scopeT.forEachIndexed { i, k: KLineDrawing? ->
			k ?: return@forEachIndexed

			val rect = rectList[i]
			val highY = context.getYForData(k.high.toFloat())
			val lowY = context.getYForData(k.low.toFloat())
			val close = k.close.toFloat()
			val open = k.open.toFloat()
			val topOCY = context.getYForData(max(open, close))
			val botOCY = context.getYForData(min(open, close))

			val color = if (close >= open) {
				raiseColor
			} else {
				dropColor
			}

			val bodyHeight = max(botOCY - topOCY, 1f)
			val bodyWidth = max(rect.width - 1f, 0f)
			context.canvas.drawLine(color, Offset(rect.center.x, highY), Offset(rect.center.x, lowY))
			context.canvas.drawRect(color = color, Offset(rect.left, topOCY), Size(bodyWidth, bodyHeight))
		}
	}
}