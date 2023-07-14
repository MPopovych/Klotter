package com.makki.klotter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.makki.klotter.elements.Plot
import com.makki.klotter.builder.PlotDataBuilder
import com.makki.klotter.builder.PlotNavBuilder
import com.makki.klotter.builder.PlotTitleBuilder
import com.makki.klotter.handlers.models.KLineDrawing
import com.makki.klotter.utils.SimpleWindow
import kotlin.random.Random

fun main() = application {
	val map1 = (0 until 1000).associate { Pair(it.toString(), Random.nextFloat() * 5 - 1) }

	val map2 = (0 until 1000).associate { Pair(it.toString(), Random.nextFloat() * 5 - 1) }
	val map3 = (0 until 1000).associate { Pair(it.toString(), Random.nextFloat() * 5 - 1) }

	val mapCandle = (0 until 1000).associate {
		val point1 = Random.nextFloat() * 8 - 1
		val point2 = Random.nextFloat() * 12 - 1
		val point3 = Random.nextFloat() * 5 - 1
		val point4 = Random.nextFloat() * 8 - 1
		val list = listOf(point1, point2, point3, point4).sortedDescending()
		val b = Random.nextBoolean()
		val a = if (b) 1 else 2
		val c = if (b) 2 else 1
		val candle = KLineDrawing(list[a], list[c], list[0], list[3])
		Pair(it.toString(), candle)
	}

	val data1 = PlotDataBuilder(map1.keys)
		.title("Volumetric data")
		.titleData(PlotTitleBuilder().topRight().build())
		.addLineData("line1", map1, color = Color.Yellow, stroke = true, smooth = true)
		.addLineData("line2", map2, color = Color.Red, stroke = false, smooth = true)
		.addPointData("line3", map3, color = Color.White)
		.build()

	val data2 = PlotDataBuilder(map1.keys)
		.title("Kline data")
		.titleData(PlotTitleBuilder().topRight().build())
		.addCandleData("candle", mapCandle)
		.build()
	val builder = PlotNavBuilder(40).separateHZoom().fromEnd()
	val navigation1 = builder.buildFor(data1)
	val navigation2 = builder.separateVZoom().buildFor(data2)

	SimpleWindow(DpSize(1000.dp, 600.dp)) {
		Column {
			Box(Modifier.weight(1f)) {
				Plot(data2, navigation2)
			}
			Box(Modifier.weight(0.5f)) {
				Plot(data1, navigation1)
			}
		}

	}
}