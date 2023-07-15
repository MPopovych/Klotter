package com.makki.klotter.elements

import com.makki.klotter.builder.PlotData
import com.makki.klotter.utils.safeMaxOrNull
import com.makki.klotter.utils.safeMinOrNull
import com.makki.klotter.utils.safeSub

class PlotDataCache(plotData: PlotData) {

	private val recordHighDataByItem: List<Float?>
	private val recordLowDataByItem: List<Float?>

	init {
		val focusable = plotData.meta.filter { it.value.focus }
		val rows = plotData.rows.filter { it.key in focusable }

		recordHighDataByItem = plotData.idList.indices.map { index ->
			return@map rows.mapNotNull { it.value.topForId(index) }.maxOrNull()
		}
		recordLowDataByItem = plotData.idList.indices.map { index ->
			return@map rows.mapNotNull { it.value.botForId(index) }.minOrNull()
		}
	}

	fun getHighForRange(range: IntRange): Float? {
		return recordHighDataByItem.safeSub(range.first, range.last).safeMaxOrNull()
	}

	fun getLowForRange(range: IntRange): Float? {
		return recordLowDataByItem.safeSub(range.first, range.last).safeMinOrNull()
	}

}