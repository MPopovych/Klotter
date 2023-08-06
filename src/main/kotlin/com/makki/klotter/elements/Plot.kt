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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import com.makki.klotter.builder.PlotData
import com.makki.klotter.builder.PlotNavigation
import com.makki.klotter.utils.PlotDataUtils
import com.makki.klotter.utils.isNanDebug
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

	val pR = 0.09f // 7.5% * 2 ~ 15%
	val defaultVisibility = plotNavigation.visible
	var lastItemWidth = 1f

	Canvas(modifier = modifier
		.fillMaxSize()
		.background(Color(23, 26, 30)),
		onDraw = {
			clipRect {
				val plotRect = getPlotRect(pR)
				val axisRect = Rect(getLeftPadding(pR), getTopPadding(pR), getRightPadding(pR), getBotPadding(pR))
				val localWidth = plotRect.width
				val localHeight = plotRect.height

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

				val visibleRange = IntRange(startId, endId)

				var dataTop = PlotDataUtils.getTopOfVisibleValue(visibleRange, plotData) ?: 1f
				var dataBot = PlotDataUtils.getBotOfVisibleValue(visibleRange, plotData) ?: -1f
				if (dataTop - dataBot == 0.0f) {
					dataTop *= 1.05f
					dataBot /= 1.05f
				}
				val mid = (dataTop + dataBot) / 2f
				if (mid.isNanDebug()) throw IllegalStateException()
				val dataHeightWZoom = (dataTop - dataBot) * vZoomCoeff
				if (!dataHeightWZoom.isFinite()) return@Canvas
				val dataTopWZoom = mid + dataHeightWZoom / 2f

				var leftOffset = 0f
				if (itemOffset > 0) {
					leftOffset = -adjustRatio * itemWidth
				} else if (itemOffset < 0) {
					leftOffset = -itemOffset * itemWidth
				}
				if (leftOffset.isNanDebug()) throw IllegalStateException()

				val drawContext = DrawContext(
					this,
					plotData.idList,
					plotRect,
					axisRect,
					dataHeightWZoom,
					dataTopWZoom,
					leftOffset,
					visibleItemsZoomed
				)
				val lastValidCount = min(plotData.count(), endId)
//				val lastValidStart = min(lastValidCount, startId)
//				val realItemCount = max(min(visibleRange.count(), lastValidCount - lastValidStart), 0)

				drawGrid(plotData.axisData, drawContext, startId, endId, lastValidCount)
				drawTracks(plotData, drawContext, lastValidCount - 1)

				plotData.rows.values.forEach { u ->
					u.drawFastForIds(drawContext, IntRange(startId, endId))
				}

				plotData.title?.also { title ->
					drawTitle(plotData.titleData, title, size)
				}
			}
		})

	// user control components
	Row(modifier.fillMaxSize()
		.let {
			if (plotNavigation.allowSeek) {
				it.draggable(
					orientation = Orientation.Horizontal,
					state = rememberDraggableState { delta ->
						itemOffset -= delta / lastItemWidth
					}
				)
			} else {
				it
			}
		}) {
		Box(modifier = modifier
			.weight(1f)
			.fillMaxSize()
			.let {
				if (plotNavigation.allowHorizontalZoom) {
					it.scrollable(orientation = Orientation.Vertical,
						state = rememberScrollableState { delta ->
							hZoom += delta
							return@rememberScrollableState delta
						}
					)
				} else {
					it
				}
			}) {

		}
		Box(modifier = modifier
			.weight(1f)
			.fillMaxSize()
			.let {
				if (plotNavigation.allowVerticalZoom) {
					it.scrollable(orientation = Orientation.Vertical,
						state = rememberScrollableState { delta ->
							vZoom += delta
							return@rememberScrollableState delta
						}
					)
				} else {
					it
				}
			}
		) {
		}
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
