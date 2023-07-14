package com.makki.klotter.builder

import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler
import com.makki.klotter.utils.safeSub


class RowData<T>(
	val handler: PlotDataHandler<T>,
	val values: HashMap<String, T>,
) {

	var cachedData: List<T?>? = null

	internal fun bakeCache(ids: Collection<String>) {
		cachedData = ids.map { values[it] }
	}

	fun count() = values.size

	fun drawFastForIds(context: DrawContext, range: IntRange) {
		handler.draw(context, getCachedForRange(range))
	}

	fun topForIds(ids: IntRange): Float? {
		return getCachedForRange(ids).mapNotNull { it }.maxOfOrNull { handler.topFocus(it) }
	}

	fun botForIds(ids: IntRange): Float? {
		return getCachedForRange(ids).mapNotNull { it }.minOfOrNull { handler.botFocus(it) }
	}

	private fun getCachedForRange(ids: IntRange): List<T?> {
		return cachedData?.safeSub(ids.first, ids.last) ?: throw IllegalStateException()
	}
}
