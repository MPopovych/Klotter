package com.makki.klotter.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.makki.klotter.builder.HorizontalSide
import com.makki.klotter.builder.PlotAxisData
import com.makki.klotter.builder.VerticalSide
import com.makki.klotter.utils.TextMeasureUtils
import com.makki.klotter.utils.isNanDebug
import java.math.RoundingMode
import kotlin.math.*


fun DrawScope.drawGrid(
	axisData: PlotAxisData,
	c: DrawContext,
	dataCount: Int,
	startId: Int,
) {
	drawColumns(axisData, c, dataCount, startId)
	drawRowsAndNumbers(axisData, c)
}

private fun DrawScope.drawColumns(
	axisData: PlotAxisData,
	c: DrawContext,
	realItemCount: Int,
	startId: Int,
) {
	if (!axisData.gridColumns) return

	val gridWidth = c.plotRect.width / max(c.canFit, 1f)
	if (gridWidth.isNanDebug()) throw IllegalStateException()
	val columnMultiplier = ceil(axisData.gridColumnGap / gridWidth).roundToInt()

	var process = c.leftOffset - 1f
	var count = 0
	while (process < c.plotRect.width && count <= realItemCount) {
		if ((startId + count) % columnMultiplier == 0) {
			drawLine(
				axisData.gridColor,
				Offset(c.plotRect.left + process, c.plotRect.top),
				Offset(c.plotRect.left + process, c.plotRect.bottom)
			)
		}
		process += gridWidth
		count++
	}
}

private fun DrawScope.drawRowsAndNumbers(
	axisData: PlotAxisData,
	c: DrawContext,
) {
	val dataHeightSafe = if (c.dataHeight == 0f) 1f else c.dataHeight

	val pair = findClosestPower(dataHeightSafe)
	val topBound = c.plotRect.top
	val botBound = topBound + c.plotRect.height
	val power = pair.first
	val decimal = pair.second
	if (power.isNanDebug() || power == 0f) return
	var highestPoint = (ceil(c.highestDataPoint / power).roundToInt() * power).round(decimal)

	val lowestPoint = (c.highestDataPoint - c.dataHeight).round(decimal)
	val leftPoint = c.plotRect.left + c.leftOffset - 1f
	var iter = 0
	while (highestPoint >= lowestPoint && iter < 30) {
		iter++
		val pureY = c.getYForData(highestPoint)
		val y = min(max(pureY, topBound), botBound)
		if (axisData.gridRows) {
			drawLine(
				axisData.gridColor,
				Offset(leftPoint, y),
				Offset(leftPoint + c.plotRect.width, y),
			)
		}
		if (axisData.gridNumbers && pureY in (topBound .. botBound)) {
			drawNumber(highestPoint, y, axisData, c)
		}
		highestPoint = (highestPoint - power).round(decimal)
	}
}

fun DrawScope.drawNumber(
	number: Float,
	y: Float?,
	axisData: PlotAxisData,
	c: DrawContext
) {
	val ySafe = y ?: c.getYForData(number)
	val text = "$number"
	val measure = TextMeasureUtils.textRect(text, axisData.font, axisData.gridPaint)
	val x = when(axisData.gridNumbersSide) {
		HorizontalSide.Left -> c.leftPaddingRect.right - measure.width - c.itemWidth
		HorizontalSide.Center -> c.leftPaddingRect.right + c.plotRect.width / 2 - measure.width / 2
		HorizontalSide.Right -> c.rightPaddingRect.left
	}

	drawIntoCanvas {// sub to updates
		it.nativeCanvas.drawString(
			text,
			x,
			ySafe,
			axisData.font,
			axisData.gridPaint
		)
	}
}

private fun Float.round(precision: Int): Float {
	return this.toBigDecimal().setScale(precision, RoundingMode.HALF_EVEN).toFloat()
}

private fun findClosestPower(number: Float): Pair<Float, Int> {
	var start = 100000000.0f
	var decimal = -8
	while (number < start && decimal < 10) {
		start /= 10
		decimal++
	}
	return Pair(start, decimal)
}
