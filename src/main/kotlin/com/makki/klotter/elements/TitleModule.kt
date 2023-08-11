package com.makki.klotter.elements

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.sp
import com.makki.klotter.builder.HorizontalSide
import com.makki.klotter.builder.PlotTitleData
import com.makki.klotter.builder.VerticalSide
import com.makki.klotter.utils.TextMeasureUtils
import org.jetbrains.skia.Font

fun DrawScope.drawTitle(data: PlotTitleData, title: String, frame: Size) {
	val font = Font(data.titleTypeface, data.titleFontSize.sp.toPx())
	val measure = TextMeasureUtils.textRect(title, font, data.fontPaint)

	val x = when (data.titleHSide) {
		HorizontalSide.Left -> 0f
		HorizontalSide.Center -> frame.center.x - measure.width / 2
		HorizontalSide.Right -> size.width - measure.width
	}

	val y = when (data.titleVSide) {
		VerticalSide.Top -> measure.height
		VerticalSide.Center -> frame.center.y - measure.height / 2
		VerticalSide.Bottom -> size.height - measure.height
	}

	drawIntoCanvas {// sub to updates
		it.nativeCanvas.drawString(
			title,
			x,
			y,
			font,
			data.fontPaint
		)
	}
}