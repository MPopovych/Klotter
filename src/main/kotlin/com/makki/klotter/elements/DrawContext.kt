package com.makki.klotter.elements

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.max

class DrawContext(
	val canvas: DrawScope,
	val plotRect: Rect,
	val axisRect: Rect,
	val dataHeight: Float,
	val highestDataPoint: Float,
	val leftOffset: Float,
	val canFit: Float,
) {

	val itemWidth = plotRect.width / max(canFit, 1f)
	val leftPaddingRect = Rect(0f, axisRect.top, axisRect.left, axisRect.bottom)
	val rightPaddingRect = Rect(plotRect.right, axisRect.top, plotRect.right + axisRect.right, axisRect.bottom)
	val fastMode = itemWidth < 1.5f

	fun getRecForIndex(i: Int): Rect {
		val leftStart = leftOffset + itemWidth * i
		val pxLeft = plotRect.left + leftStart
		return Rect(plotRect.left + leftStart, plotRect.top, pxLeft + itemWidth, plotRect.bottom)
	}

	fun getYForData(dataPoint: Float): Float {
		if (dataHeight == 0f) return 0f
		val dataDeltaFromTop = highestDataPoint - dataPoint
		return plotRect.top + plotRect.height * dataDeltaFromTop / dataHeight
	}
}