package com.makki.klotter.builder

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Color
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Typeface


class PlotTitleBuilder {
	var titleHSide: HorizontalSide = HorizontalSide.Center
	var titleVSide: VerticalSide = VerticalSide.Top
	var titleFontSize: Float = 14f
	var titleTypeface: Typeface = Typeface.makeDefault()
	var titleColor: Int = Color.WHITE

	fun color(color: Int): PlotTitleBuilder {
		titleColor = color
		return this
	}

	fun topMiddle(): PlotTitleBuilder {
		titleVSide = VerticalSide.Top
		titleHSide = HorizontalSide.Center
		return this
	}

	fun topLeft(): PlotTitleBuilder {
		titleVSide = VerticalSide.Top
		titleHSide = HorizontalSide.Left
		return this
	}

	fun topRight(): PlotTitleBuilder {
		titleVSide = VerticalSide.Top
		titleHSide = HorizontalSide.Right
		return this
	}

	fun bottomRight(): PlotTitleBuilder {
		titleVSide = VerticalSide.Bottom
		titleHSide = HorizontalSide.Right
		return this
	}

	fun bottomMiddle(): PlotTitleBuilder {
		titleVSide = VerticalSide.Bottom
		titleHSide = HorizontalSide.Center
		return this
	}

	fun middle(): PlotTitleBuilder {
		titleVSide = VerticalSide.Center
		titleHSide = HorizontalSide.Center
		return this
	}

	fun bottomLeft(): PlotTitleBuilder {
		titleVSide = VerticalSide.Bottom
		titleHSide = HorizontalSide.Left
		return this
	}

	fun fontSize(size: Float): PlotTitleBuilder {
		titleFontSize = size
		return this
	}

	fun fontTypeFace(typeface: Typeface): PlotTitleBuilder {
		titleTypeface = typeface
		return this
	}

	fun build(): PlotTitleData {
		return PlotTitleData(
			titleColor,
			titleFontSize,
			titleTypeface,
			titleVSide,
			titleHSide
		)
	}
}

class PlotTitleData(
	val titleColor: Int,
	val titleFontSize: Float,
	val titleTypeface: Typeface,
	val titleVSide: VerticalSide,
	val titleHSide: HorizontalSide,
) {

	companion object {
		fun default(): PlotTitleData {
			return PlotTitleBuilder().build()
		}
	}

	val fontPaint = Paint().also {
		it.color = titleColor
	}
}