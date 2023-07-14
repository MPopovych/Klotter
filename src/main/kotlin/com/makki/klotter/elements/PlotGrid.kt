package com.makki.klotter.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.makki.klotter.utils.findClosestPower
import kotlin.math.*


fun DrawScope.drawGrid(
	c: DrawContext,
	dataCount: Int,
	startId: Int,
) {
	val dataHeightSafe = if (c.dataHeight == 0f) 1f else c.dataHeight
	val visibleCountSafe = max(c.canFit, 1f)

	val height = c.plotRect.height
	val width = c.plotRect.width

	val columnMultiplier = ceil(12.dp.value / (width / c.canFit)).roundToInt()

	val gridWidth = width / visibleCountSafe
	var process = c.leftOffset - 1f
	var count = 0
	while (process < width && count <= dataCount) {
		if ((startId + count) % columnMultiplier == 0) {
			drawLine(
				Color.White,
				Offset(c.plotRect.left + process, c.plotRect.top),
				Offset(c.plotRect.left + process, c.plotRect.top + height),
				alpha = 0.5f
			)
		}
		process += gridWidth
		count += 1
	}

	val pair = findClosestPower(dataHeightSafe)
	val topBound = c.plotRect.top
	val botBound = topBound + height
	val power = pair.first
	var highestPoint = ceil(c.highestDataPoint / power).roundToInt() * power
	val lowestPoint = (floor(c.highestDataPoint - c.dataHeight) / power).roundToInt() * power
	val leftPoint = c.plotRect.left + c.leftOffset - 1f
	while (highestPoint >= lowestPoint) {
		val y = min(max(c.getYForData(highestPoint), topBound), botBound)
		drawLine(Color.White, Offset(leftPoint, y), Offset(leftPoint + width, y), alpha = 0.3f)
		highestPoint -= power
	}
}