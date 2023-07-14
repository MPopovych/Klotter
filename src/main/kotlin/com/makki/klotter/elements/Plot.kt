package com.makki.klotter.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makki.klotter.builder.PlotData
import com.makki.klotter.builder.PlotNavigation
import com.makki.klotter.utils.PlotDataUtils
import com.makki.klotter.utils.drawText
import com.makki.klotter.utils.findClosestPower
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Typeface
import kotlin.math.*


@Composable
fun Plot(
	plotData: PlotData,
	plotNavigation: PlotNavigation,
	modifier: Modifier = Modifier,
) {

	var itemOffset by remember { plotNavigation.itemOffset }
	var hZoom by remember { plotNavigation.horizontalZoom }
	var vZoom by remember { plotNavigation.verticalZoom }

	val font = Font(Typeface.makeDefault(), 24.sp.value)
	val fontPaint = Paint().also {
		it.color = 0xFF_FF_FF_00.toInt()
	}

	val pR = 0.09f // 7.5% * 2 ~ 15%
	val defaultVisibility = plotNavigation.visible
	var lastItemWidth = 1f

	Canvas(modifier = modifier
		.fillMaxSize()
		.background(Color(23, 26, 30)),
		onDraw = {
			clipRect {
				val plotRect = getPlotRect(pR)
				val paddingRect = Rect(getLeftPadding(pR), getTopPadding(pR), getRightPadding(pR), getBotPadding(pR))
				val localWidth = size.width - paddingRect.left - paddingRect.right
				val localHeight = size.height - paddingRect.top - paddingRect.bottom

				var hZoomCoeff = 1f + abs(hZoom) / localWidth
				if (hZoom < 0) {
					hZoomCoeff = 1f / hZoomCoeff
				}
				var vZoomCoeff = 1f + abs(vZoom) / localHeight
				if (vZoom < 0) {
					vZoomCoeff = 1f / vZoomCoeff
				}

				val visibleItemsZoomed = defaultVisibility / hZoomCoeff
				val itemWidth = localWidth / visibleItemsZoomed
				lastItemWidth = itemWidth

				val adjustRatio: Float = abs(itemOffset) % 1f * sign(itemOffset)
				val startId: Int = floor(itemOffset).roundToInt()
				val endId: Int = startId + visibleItemsZoomed.roundToInt()

				val visibleIds = plotData.idList.safeSub(startId, endId)

				val dataTop = PlotDataUtils.getTopOfVisibleValue(visibleIds, plotData) ?: 1f
				val dataBot = PlotDataUtils.getBotOfVisibleValue(visibleIds, plotData) ?: -1f
				val mid = (dataTop + dataBot) / 2f
				val dataHeightWZoom = (dataTop - dataBot) * vZoomCoeff
				val dataTopWZoom = mid + dataHeightWZoom / 2f

				var leftOffset = 0f
				if (itemOffset > 0) {
					leftOffset = -adjustRatio * itemWidth
				} else if (itemOffset < 0) {
					leftOffset = -itemOffset * itemWidth
				}

				val drawContext = DrawContext(
					this,
					plotRect,
					dataHeightWZoom,
					dataTopWZoom,
					leftOffset,
					visibleItemsZoomed
				)

				drawGrid(drawContext, visibleIds.size, startId)

				plotData.rows.values.forEach { u ->
					u.drawFastForIds(drawContext, IntRange(startId, endId))
				}

				drawText(
					"c:${plotNavigation.updateListener.value} Preview", font, fontPaint
				)
			}
		})

	Row(modifier.fillMaxSize()
		.draggable(
			orientation = Orientation.Horizontal,
			state = rememberDraggableState { delta ->
				itemOffset -= delta / lastItemWidth
			}
		)) {
		Box(modifier = modifier
			.weight(1f)
			.fillMaxSize()
			.scrollable(orientation = Orientation.Vertical,
				state = rememberScrollableState { delta ->
					hZoom += delta
					return@rememberScrollableState delta
				}
			)) {

		}
		Box(modifier = modifier
			.weight(1f)
			.fillMaxSize()
			.scrollable(orientation = Orientation.Vertical,
				state = rememberScrollableState { delta ->
					vZoom += delta
					return@rememberScrollableState delta
				}
			)) {
		}
	}
}

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

fun DrawScope.getTopPadding(percent: Float): Float {
	return this.size.height * percent
}

fun DrawScope.getBotPadding(percent: Float): Float {
	return this.size.height * percent
}

fun DrawScope.getLeftPadding(percent: Float): Float {
	return this.size.width * percent
}

fun DrawScope.getRightPadding(percent: Float): Float {
	return this.size.width * percent
}

fun DrawScope.getPlotRect(fraction: Float): Rect {
	return getPlotRect(fraction, fraction, fraction, fraction)
}

fun DrawScope.getPlotRect(lFraction: Float, tFraction: Float, rFraction: Float, bFraction: Float): Rect {
	return Rect(
		getLeftPadding(lFraction),
		getTopPadding(tFraction),
		size.width - getRightPadding(rFraction),
		size.height - getBotPadding(bFraction)
	)
}

fun <T> List<T>.safeSub(from: Int, to: Int): List<T> {
	val safeFrom = max(min(this.size, from), 0)
	val safeTo = max(min(this.size, to), 0)

	val max = max(safeTo, safeFrom)
	val min = min(safeTo, safeFrom)
	return this.subList(min, max)
}