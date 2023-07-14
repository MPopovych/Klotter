package com.makki.klotter.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.makki.klotter.builder.PlotAxisData
import kotlin.math.*


fun DrawScope.drawGrid(
	axisData: PlotAxisData,
	c: DrawContext,
	dataCount: Int,
	startId: Int,
) {
	drawColumns(axisData, c, dataCount, startId)
	drawRows(axisData, c)
}

private fun DrawScope.drawColumns(
	axisData: PlotAxisData,
	c: DrawContext,
	realItemCount: Int,
	startId: Int,
) {
	if (!axisData.gridColumns) return

	val gridWidth = c.plotRect.width / max(c.canFit, 1f)
	val columnMultiplier = ceil(axisData.gridColumnGap / gridWidth).roundToInt()

	var process = c.leftOffset - 1f
	var count = 0
	while (process < c.plotRect.width && count <= realItemCount) {
		if ((startId + count) % columnMultiplier == 0) {
			drawLine(
				Color.White,
				Offset(c.plotRect.left + process, c.plotRect.top),
				Offset(c.plotRect.left + process, c.plotRect.bottom),
				alpha = axisData.gridColumnRowAlpha
			)
		}
		process += gridWidth
		count++
	}
}

private fun DrawScope.drawRows(
	axisData: PlotAxisData,
	c: DrawContext,
) {
	if (!axisData.gridRows) return

	val dataHeightSafe = if (c.dataHeight == 0f) 1f else c.dataHeight

	val pair = findClosestPower(dataHeightSafe)
	val topBound = c.plotRect.top
	val botBound = topBound + c.plotRect.height
	val power = pair.first
	var highestPoint = ceil(c.highestDataPoint / power).roundToInt() * power
	val lowestPoint = c.highestDataPoint - c.dataHeight
	val leftPoint = c.plotRect.left + c.leftOffset - 1f
	while (highestPoint >= lowestPoint) {
		val y = min(max(c.getYForData(highestPoint), topBound), botBound)
		drawLine(
			Color.White,
			Offset(leftPoint, y),
			Offset(leftPoint + c.plotRect.width, y),
			alpha = axisData.gridColumnRowAlpha
		)
		highestPoint -= power
	}
}

private fun findClosestPower(number: Float): Pair<Float, Int> {
	var start = 100000000.0f
	var decimal = -8
	while (number < start) {
		start /= 10
		decimal++
	}
	return Pair(start, decimal)
}
