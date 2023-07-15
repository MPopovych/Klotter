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

	fun trackForId(index: Int): Float? {
		return handler.trackerValue(cachedData?.getOrNull(index) ?: return null)
	}

	fun topForId(index: Int): Float? {
		return handler.topFocus(cachedData?.getOrNull(index) ?: return null)
	}

	fun botForId(index: Int): Float? {
		return handler.botFocus(cachedData?.getOrNull(index) ?: return null)
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
