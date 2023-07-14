package com.makki.klotter.builder

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Typeface


class PlotAxisBuilder {
	var gridRows: Boolean = true
	var gridColumns: Boolean = true
	var gridColumnGap: Float = 24.dp.value
	var gridColumnRowAlpha: Float = 0.2f
	var gridNumbersEnabled: Boolean = true
	var gridNumbersSize: HorizontalSide = HorizontalSide.Right
	var gridNumbersFontSize: Float = 24.sp.value
	var gridNumbersTypeface: Typeface = Typeface.makeDefault()

	fun grid(enabled: Boolean): PlotAxisBuilder {
		gridRows = enabled
		gridColumns = enabled
		gridNumbersEnabled = enabled
		return this
	}

	fun gridAlpha(alpha: Float): PlotAxisBuilder {
		gridColumnRowAlpha = alpha
		return this
	}

	fun gridRows(enabled: Boolean): PlotAxisBuilder {
		gridRows = enabled
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
			gridColumns,
			gridColumnGap,
			gridColumnRowAlpha,
			gridNumbersEnabled,
			gridNumbersSize,
			gridNumbersFontSize,
			gridNumbersTypeface
		)
	}
}

class PlotAxisData(
	val gridRows: Boolean,
	val gridColumns: Boolean,
	val gridColumnGap: Float,
	val gridColumnRowAlpha: Float,
	val gridNumbers: Boolean,
	val gridNumbersSize: HorizontalSide,
	val gridNumbersFontSize: Float,
	val gridNumbersTypeface: Typeface,
) {
	companion object {
		fun default(): PlotAxisData {
			return PlotAxisBuilder().build()
		}
	}
}