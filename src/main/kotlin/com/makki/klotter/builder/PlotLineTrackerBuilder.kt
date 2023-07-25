package com.makki.klotter.builder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Typeface

class PlotLineTrackerBuilder {

	private val tracks = HashMap<String, PlotTrackMeta>()

	fun trackLine(
		name: String,
		textColor: Color = Color.White,
		textSize: Float = 24.sp.value,
		backgroundColor: Color = Color(227, 85, 97),
		side: HorizontalSide = HorizontalSide.Right,
	): PlotLineTrackerBuilder {
		tracks[name] = PlotTrackMeta(name, textColor, textSize, backgroundColor, side)
		return this
	}

	fun build(): PlotLineTrackData {
		return PlotLineTrackData(
			HashMap(tracks) // copy
		)
	}
}

class PlotTrackMeta(
	val rowName: String,
	val textColor: Color,
	val textSize: Float,
	val backgroundColor: Color,
	val side: HorizontalSide,
) {
	val font = Font(Typeface.makeDefault(), textSize)
	val fontPaint = Paint().also {
		it.color = textColor.toArgb()
	}
}

class PlotLineTrackData(
	val tracks: HashMap<String, PlotTrackMeta>,
) {
	companion object {
		fun default(): PlotLineTrackData {
			return PlotLineTrackerBuilder().build()
		}

		fun builder() = PlotLineTrackerBuilder()
	}
}