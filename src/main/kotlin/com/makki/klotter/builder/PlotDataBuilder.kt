package com.makki.klotter.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.makki.klotter.elements.DrawContext
import com.makki.klotter.handlers.PlotDataHandler
import com.makki.klotter.handlers.implementations.DotHandler
import com.makki.klotter.handlers.implementations.KLineHandler
import com.makki.klotter.handlers.implementations.LineHandler
import com.makki.klotter.models.KLineDrawing
import com.makki.klotter.utils.safeSub

enum class HorizontalDirection {
	FromStart,
	FromEnd,
}

class PlotDataBuilder(private val ids: Collection<String>) {
	private val meta = LinkedHashMap<String, MetaData>()
	private val data = LinkedHashMap<String, RowData<*>>()

	fun addRowData(name: String, rowData: RowData<*>, metaData: MetaData): PlotDataBuilder {
		meta[name] = metaData
		data[name] = rowData
		return this
	}

	fun addLineData(
		name: String,
		values: Map<String, Number>,
		strokeWidth: Float = 3.dp.value,
		color: Color,
		stroke: Boolean = false,
		smooth: Boolean = false,
		focus: Boolean = true,
	): PlotDataBuilder {
		val cast = if (values is HashMap) values else HashMap(values)
		val rowData = RowData(LineHandler(strokeWidth, color.toArgb(), stroke = stroke, smooth = smooth), cast)
		return addRowData(name, rowData, MetaData(focus))
	}

	fun addCandleData(
		name: String,
		values: Map<String, KLineDrawing>,
		focus: Boolean = true,
	): PlotDataBuilder {
		val cast = if (values is HashMap) values else HashMap(values)
		val rowData = RowData(KLineHandler(), cast)
		return addRowData(name, rowData, MetaData(focus))
	}

	fun addPointData(
		name: String,
		values: Map<String, Number>,
		color: Color,
		radius: Float = 5.dp.value,
		focus: Boolean = true,
	): PlotDataBuilder {
		val cast = if (values is HashMap) values else HashMap(values)
		val rowData = RowData(DotHandler(radius, color), cast)
		return addRowData(name, rowData, MetaData(focus))
	}

	fun addLineDataColumn(
		name: String,
		id: String,
		value: Number,
	): PlotDataBuilder {
		val casted = data[name] as? RowData<Number> ?: throw IllegalStateException("wrong data line, type or missing")
		casted.values[id] = value
		return this
	}

	fun build(): PlotData {
		return PlotData(
			ids = LinkedHashSet(ids),
			rows = LinkedHashMap(data),
			meta = LinkedHashMap(meta),
		)
	}
}

class PlotData(
	ids: LinkedHashSet<String>,
	val rows: Map<String, RowData<*>>,
	val meta: Map<String, MetaData>,
) {
	val idList = ids.toList()
	fun count() = idList.size

	private fun onDataProcessed() {
		for (r in rows.values) {
			r.bakeCache(idList)
		}
	}

	init {
		onDataProcessed()
	}
}

class MetaData(
	val focus: Boolean,
)

class RowData<T>(
	val handler: PlotDataHandler<T>,
	val values: HashMap<String, T>
) {

	var cachedData: List<T?>? = null

	internal fun bakeCache(ids: Collection<String>) {
		cachedData = ids.map { values[it] }
	}

	fun count() = values.size

	fun drawForIds(context: DrawContext, ids: Collection<String>) {
		handler.draw(context, getForIds(ids))
	}

	fun drawFastForIds(context: DrawContext, range: IntRange) {
		handler.draw(context, getCachedForRange(range))
	}

	fun topForIds(ids: Collection<String>): Float? {
		return getForIds(ids).maxOfOrNull { handler.topFocus(it) }
	}

	fun botForIds(ids: Collection<String>): Float? {
		return getForIds(ids).minOfOrNull { handler.botFocus(it) }
	}

	private fun getForIds(ids: Collection<String>): List<T> {
		return ids.mapNotNull { values[it] }
	}

	private fun getCachedForRange(ids: IntRange): List<T?> {
		return cachedData?.safeSub(ids.first, ids.last) ?: throw IllegalStateException()
	}
}

