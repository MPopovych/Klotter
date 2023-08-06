package com.makki.klotter.builder

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


class PlotNavBuilder(private var visible: Int? = null) {

	private var direction = InitialScroll.FromStart
	private var allowVerticalZoom: Boolean = true
	private var allowHorizontalZoom: Boolean = true
	private var allowSeek: Boolean = true
	private var verticalZoom: MutableState<Float> = mutableStateOf(1f)
	private var horizontalZoom: MutableState<Float> = mutableStateOf(1f)
	private var updateListener: MutableState<Int> = mutableStateOf(1)
	private var offsetState: MutableState<Float> = mutableStateOf(0f)

	fun interactive(enabled: Boolean) = this.also {
		allowSeek = enabled
		allowHorizontalZoom = enabled
		allowVerticalZoom = enabled
	}

	fun horizontalZoom(enabled: Boolean) = this.also {
		allowHorizontalZoom = enabled
	}

	fun verticalZoom(enabled: Boolean) = this.also {
		allowVerticalZoom = enabled
	}

	fun seek(enabled: Boolean) = this.also {
		allowSeek = enabled
	}

	fun visible(count: Int?): PlotNavBuilder {
		visible = count
		return this
	}

	fun separateUpdates(): PlotNavBuilder {
		updateListener = mutableStateOf(1)
		return this
	}

	fun separateVZoom(z: Float = 1f): PlotNavBuilder {
		verticalZoom = mutableStateOf(z)
		return this
	}

	fun separateHZoom(z: Float = 1f): PlotNavBuilder {
		horizontalZoom = mutableStateOf(z)
		return this
	}

	fun fromStart(): PlotNavBuilder {
		direction = InitialScroll.FromStart
		return this
	}

	fun fromEnd(): PlotNavBuilder {
		direction = InitialScroll.FromEnd
		return this
	}

	fun buildFor(data: PlotData): PlotNavigation {
		val finalVisibility = visible ?: data.rows.maxOfOrNull { it.value.count() } ?: data.idList.size
		offsetState.value = when (direction) {
			InitialScroll.FromStart -> 0f
			InitialScroll.FromEnd -> (data.count() - finalVisibility).toFloat()
		}
		return PlotNavigation(
			visible = finalVisibility,
			allowVerticalZoom = allowVerticalZoom,
			allowHorizontalZoom = allowHorizontalZoom,
			allowSeek = allowSeek,
			verticalZoom = verticalZoom,
			horizontalZoom = horizontalZoom,
			itemOffset = offsetState,
			direction = direction
		)
	}

}

class PlotNavigation internal constructor(
	val visible: Int,
	val allowVerticalZoom: Boolean,
	val allowHorizontalZoom: Boolean,
	val allowSeek: Boolean,
	val verticalZoom: MutableState<Float>,
	val horizontalZoom: MutableState<Float>,
	val itemOffset: MutableState<Float>,
	val direction: InitialScroll,
) {

	companion object {
		fun default(plotData: PlotData): PlotNavigation {
			return PlotNavBuilder().fromStart().visible(64).separateVZoom().buildFor(plotData)
		}

		fun disabledScroll(plotData: PlotData): PlotNavigation {
			return PlotNavBuilder().fromStart().visible(64).interactive(false).separateVZoom().buildFor(plotData)
		}

		fun builder() = PlotNavBuilder()
	}
}