package com.makki.klotter.utils

import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect

object TextMeasureUtils {

	fun textRect(text: String, font: Font, paint: Paint): Rect {
		return font.measureText(text, paint).let {
			return@let it.offset(-it.left, -it.top)
		}
	}

}