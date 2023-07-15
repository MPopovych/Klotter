package com.makki.klotter.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.makki.klotter.builder.HorizontalSide
import com.makki.klotter.builder.PlotAxisData
import com.makki.klotter.builder.PlotData
import com.makki.klotter.utils.TextMeasureUtils
import java.math.RoundingMode
import kotlin.math.*


fun DrawScope.drawTracks(
	plotData: PlotData,
	c: DrawContext,
	endId: Int,
) {
	// move to utils?
	val trackPairs = plotData.plotDataCache?.getTracksForIndex(endId) ?: return

	trackPairs.forEach { (meta, value) ->
		val text = "$value"
		val measure = TextMeasureUtils.textRect(text, meta.font, meta.fontPaint)
		val y = c.getYForData(value)
		val x = c.rightPaddingRect.left
		c.canvas.drawRect(
			meta.backgroundColor,
			Offset(x, y - (measure.height / 2) - 5f),
			Size(measure.width + 10f, measure.height + 15f)
		)
		drawIntoCanvas {// sub to updates
			it.nativeCanvas.drawString(
				text,
				x + 5f,
				y + measure.height / 2 + 2.5f,
				meta.font,
				meta.fontPaint
			)
		}
	}
}

private fun Float.round(precision: Int): Float {
	return this.toBigDecimal().setScale(precision, RoundingMode.HALF_EVEN).toFloat()
}

