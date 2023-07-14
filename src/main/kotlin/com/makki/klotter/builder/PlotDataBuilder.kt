package com.makki.klotter.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.makki.klotter.handlers.implementations.DotHandler
import com.makki.klotter.handlers.implementations.KLineHandler
import com.makki.klotter.handlers.implementations.LineHandler
import com.makki.klotter.handlers.models.KLineDrawing


class PlotDataBuilder(private val ids: Collection<String>) {
	private val meta = LinkedHashMap<String, MetaData>()
	private val data = LinkedHashMap<String, RowData<*>>()
	private var axisData = PlotAxisData.default()
	private var titleData = PlotTitleData.default()
	private var title: String? = null

	fun title(text: String?): PlotDataBuilder {
		title = text
		return this
	}

	fun titleData(data: PlotTitleData): PlotDataBuilder {
		titleData = data
		return this
	}

	fun axisData(data: PlotAxisData): PlotDataBuilder {
		axisData = data
		return this
	}

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
			axisData = axisData,
			titleData = titleData,
			title = title,
		)
	}
}

class PlotData(
	ids: LinkedHashSet<String>,
	val rows: Map<String, RowData<*>>,
	val meta: Map<String, MetaData>,
	val axisData: PlotAxisData,
	val titleData: PlotTitleData,
	val title: String?
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

