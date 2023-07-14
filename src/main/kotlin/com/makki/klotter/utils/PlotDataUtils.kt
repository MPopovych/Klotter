package com.makki.klotter.utils

import com.makki.klotter.builder.PlotData

object PlotDataUtils {
	fun getTopOfVisibleValue(ids: List<String>, plotData: PlotData): Float? {
		val focusable = plotData.meta.filter { it.value.focus }
		return plotData.rows.filter { it.key in focusable }.values.mapNotNull {
			it.topForIds(ids)
		}.maxOrNull()
	}

	fun getBotOfVisibleValue(ids: List<String>, plotData: PlotData): Float? {
		val focusable = plotData.meta.filter { it.value.focus }
		return plotData.rows.filter { it.key in focusable }.values.mapNotNull {
			it.botForIds(ids)
		}.minOrNull()
	}
}