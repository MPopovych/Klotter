package com.makki.klotter.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.makki.klotter.elements.PlotDataCache
import com.makki.klotter.handlers.implementations.DotHandler
import com.makki.klotter.handlers.implementations.KLineHandler
import com.makki.klotter.handlers.implementations.LineHandler
import com.makki.klotter.handlers.implementations.MultiDotHandler
import com.makki.klotter.handlers.models.DotDrawing
import com.makki.klotter.handlers.models.KLineDrawing


class PlotDataBuilder(private val ids: Collection<String>) {
	private val meta = LinkedHashMap<String, MetaData>()
	private val data = LinkedHashMap<String, RowData<*>>()
	private var axisData = PlotAxisData.default()
	private var trackData = PlotLineTrackData.default()
	private var titleData = PlotTitleData.default()
	private var title: String? = null
	private var useCache: Boolean = true

	fun caching(enabled: Boolean): PlotDataBuilder {
		useCache = enabled
		return this
	}

	fun title(text: String?): PlotDataBuilder {
		title = text
		return this
	}

	fun titleData(data: PlotTitleData): PlotDataBuilder {
		titleData = data
		return this
	}

	fun trackData(data: PlotLineTrackData): PlotDataBuilder {
		trackData = data
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

	fun addPointData(
		name: String,
		values: Map<String, List<DotDrawing>>,
		focus: Boolean = true,
	): PlotDataBuilder {
		val cast = if (values is HashMap) values else HashMap(values)
		val rowData = RowData(MultiDotHandler(), cast)
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
			trackData = trackData,
			titleData = titleData,
			title = title,
			useCache = useCache,
		)
	}
}

class PlotData(
	ids: LinkedHashSet<String>,
	val rows: Map<String, RowData<*>>,
	val meta: Map<String, MetaData>,
	val axisData: PlotAxisData,
	val trackData: PlotLineTrackData,
	val titleData: PlotTitleData,
	val title: String?,
	useCache: Boolean,
) {
	val idList = ids.toList()
	val plotDataCache: PlotDataCache?

	init {
		cacheRowValues()
		plotDataCache = if (useCache) PlotDataCache(this) else null
	}

	fun count() = idList.size

	private fun cacheRowValues() {
		for (r in rows.values) {
			r.bakeCache(idList)
		}
	}
}

class MetaData(
	val focus: Boolean,
)

