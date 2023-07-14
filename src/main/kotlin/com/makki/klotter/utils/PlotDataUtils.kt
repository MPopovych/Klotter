package com.makki.klotter.utils

import com.makki.klotter.builder.PlotData

object PlotDataUtils {
	fun getTopOfVisibleValue(ids: IntRange, plotData: PlotData): Float? {
		val focusable = plotData.meta.filter { it.value.focus }
		return plotData.rows.filter { it.key in focusable }.values.mapNotNull {
			it.topForIds(ids)
		}.maxOrNull()
	}

	fun getBotOfVisibleValue(ids: IntRange, plotData: PlotData): Float? {
		val focusable = plotData.meta.filter { it.value.focus }
		return plotData.rows.filter { it.key in focusable }.values.mapNotNull {
			it.botForIds(ids)
		}.minOrNull()
	}
}