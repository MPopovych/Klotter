package com.makki.klotter.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Typeface


class PlotAxisBuilder {
	private var gridRows: Boolean = true
	private var labels: Boolean = false
	private var labelLambda: (String, Int) -> String = { s, _ -> s }
	private var gridColor: Int = Color.White.copy(alpha = 0.2f).toArgb()
	private var gridColumns: Boolean = true
	private var gridColumnGap: Float = 24f
	private var gridNumbersEnabled: Boolean = true
	private var gridNumbersSize: HorizontalSide = HorizontalSide.Left
	private var gridNumbersFontSize: Float = 12f
	private var gridNumbersTypeface: Typeface = Typeface.makeDefault()

	fun grid(enabled: Boolean): PlotAxisBuilder {
		gridRows = enabled
		gridColumns = enabled
		gridNumbersEnabled = enabled
		return this
	}

	fun gridRows(enabled: Boolean): PlotAxisBuilder {
		gridRows = enabled
		return this
	}

	fun labels(enabled: Boolean) = this.also { labels = enabled }

	fun gridIdMap(block: (String, Int) -> String) = this.also {
		labelLambda = block
		labels = true
	}

	fun gridColor(color: Int): PlotAxisBuilder {
		gridColor = color
		return this
	}

	fun gridColumnsGap(gap: Float): PlotAxisBuilder {
		gridColumnGap = gap
		return this
	}

	fun gridColumns(enabled: Boolean): PlotAxisBuilder {
		gridColumns = enabled
		return this
	}

	fun gridNumbers(enabled: Boolean): PlotAxisBuilder {
		gridNumbersEnabled = enabled
		return this
	}

	fun gridNumbersOnLeft(): PlotAxisBuilder {
		gridNumbersSize = HorizontalSide.Left
		return this
	}

	fun gridNumbersOnMiddle(): PlotAxisBuilder {
		gridNumbersSize = HorizontalSide.Center
		return this
	}

	fun gridNumbersOnRight(): PlotAxisBuilder {
		gridNumbersSize = HorizontalSide.Right
		return this
	}

	fun fontSize(size: Float): PlotAxisBuilder {
		gridNumbersFontSize = size
		return this
	}

	fun fontTypeFace(typeface: Typeface): PlotAxisBuilder {
		gridNumbersTypeface = typeface
		return this
	}

	fun build(): PlotAxisData {
		return PlotAxisData(
			gridRows,
			labels,
			labelLambda,
			gridColor,
			gridColumns,
			gridColumnGap,
			gridNumbersEnabled,
			gridNumbersSize,
			gridNumbersFontSize,
			gridNumbersTypeface
		)
	}
}

class PlotAxisData(
	val gridRows: Boolean,
	val gridLabels: Boolean,
	val gridLabelMap: (String, Int) -> String,
	val gridColorInt: Int,
	val gridColumns: Boolean,
	val gridColumnGap: Float,
	val gridNumbers: Boolean,
	val gridNumbersSide: HorizontalSide,
	val gridNumbersFontSize: Float,
	val gridNumbersTypeface: Typeface,
) {

	companion object {
		fun default(): PlotAxisData {
			return PlotAxisBuilder().build()
		}

		fun builder(): PlotAxisBuilder {
			return PlotAxisBuilder()
		}

		fun disabled(): PlotAxisData {
			return PlotAxisBuilder().grid(false).labels(false).build()
		}
	}

	val gridColor = Color(gridColorInt)
	val gridPaint = Paint().also {
		it.color = gridColorInt
	}
}