package com.makki.klotter.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makki.klotter.builder.HorizontalSide
import com.makki.klotter.builder.PlotAxisData
import com.makki.klotter.utils.TextMeasureUtils
import com.makki.klotter.utils.isNanDebug
import org.jetbrains.skia.Font
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


fun DrawScope.drawGrid(
	axisData: PlotAxisData,
	c: DrawContext,
	startId: Int,
	endId: Int,
	realEndId: Int,
) {
	drawColumns(axisData, c, startId, endId, realEndId)
	drawRowsAndNumbers(axisData, c)
}

private fun DrawScope.drawColumns(
	axisData: PlotAxisData,
	c: DrawContext,
	startId: Int,
	endId: Int,
	realEndId: Int,
) {
	if (!axisData.gridColumns && !axisData.gridLabels) return

	val gridWidth = c.plotRect.width / max(c.canFit, 1f)
	if (gridWidth.isNanDebug()) throw IllegalStateException()
	val columnMultiplier = ceil(axisData.gridColumnGap.dp.toPx() / gridWidth).roundToInt()

	val font = Font(axisData.gridNumbersTypeface, axisData.gridNumbersFontSize)

	var count = 0
	var lastLabelBorder = 0f
	val reduced = max(0, -startId)
	while (startId + count + reduced <= realEndId) {
		val current = startId + count
		if ((current + reduced) % columnMultiplier == 0 && count >= 0) {
			val x = c.getRecForIndex(count).left - 1f
			if (axisData.gridColumns) {
				drawLine(
					axisData.gridColor,
					Offset(x, c.plotRect.top),
					Offset(x, c.plotRect.bottom)
				)
			}
			if (axisData.gridLabels && x > lastLabelBorder + 3f) {
				c.ids.getOrNull(current + reduced)?.also {
					lastLabelBorder = drawId(
						it,
						current,
						x,
						axisData,
						c,
						font
					)
				}
			}
		}
		count++
	}
}

private fun DrawScope.drawRowsAndNumbers(
	axisData: PlotAxisData,
	c: DrawContext,
) {
	val dataHeightSafe = if (c.dataHeight == 0f) 1f else c.dataHeight

	val pair = findClosestPower(dataHeightSafe)
	val font = Font(axisData.gridNumbersTypeface, axisData.gridNumbersFontSize.sp.toPx())
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

		if (axisData.gridNumbers && pureY in (topBound..botBound)) {
			drawNumber(highestPoint, y, axisData, c, font)
		}
		highestPoint = (highestPoint - power).round(decimal)
	}
}

fun DrawScope.drawNumber(
	number: Float,
	y: Float?,
	axisData: PlotAxisData,
	c: DrawContext,
	font: Font
) {
	val ySafe = y ?: c.getYForData(number)
	val text = "$number"
	val measure = TextMeasureUtils.textRect(text, font, axisData.gridPaint)
	val x = when (axisData.gridNumbersSide) {
		HorizontalSide.Left -> c.leftPaddingRect.right - measure.width - c.itemWidth
		HorizontalSide.Center -> c.leftPaddingRect.right + c.plotRect.width / 2 - measure.width / 2
		HorizontalSide.Right -> c.rightPaddingRect.left
	}

	drawIntoCanvas {// sub to updates
		it.nativeCanvas.drawString(
			text,
			x,
			ySafe,
			font,
			axisData.gridPaint
		)
	}
}

/**
 * @return right border of label
 */
fun DrawScope.drawId(
	id: String,
	relativeIndex: Int,
	x: Float,
	axisData: PlotAxisData,
	c: DrawContext,
	font: Font
): Float {
	val text = axisData.gridLabelMap(id, relativeIndex)
	val measure = TextMeasureUtils.textRect(text, font, axisData.gridPaint)
	val y = c.axisRect.top + c.plotRect.height

	drawIntoCanvas {// sub to updates
		it.nativeCanvas.drawString(
			text,
			x,
			y + measure.height,
			font,
			axisData.gridPaint
		)
	}
	return x + measure.width
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
