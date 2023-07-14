package com.makki.klotter.elements

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


open class PlotNavContext(
	val offset: Float = 0f,
	val zoom: Float = 1f,
) {
	open val updateListener: MutableState<Int> = mutableStateOf(1)
	open val offsetItemsState: MutableState<Float> = mutableStateOf(offset)
	open val zoomTimesState: MutableState<Float> = mutableStateOf(zoom)

	fun update() {
		updateListener.value = updateListener.value + 1
	}

	fun cloneWithNewZoom(): PlotNavContext {
		return NewZoomClone(this)
	}

	fun cloneWithOldUpdates(): PlotNavContext {
		return OnlyUpdateClone(this)
	}

	class NewZoomClone(
		private val original: PlotNavContext
	) : PlotNavContext(
		original.offset,
		original.zoom,
	) {
		override val updateListener: MutableState<Int>
			get() = original.updateListener

		override val offsetItemsState: MutableState<Float>
			get() = original.offsetItemsState
	}

	class OnlyUpdateClone(
		private val original: PlotNavContext
	) : PlotNavContext(
		original.offset,
		original.zoom,
	) {
		override val updateListener: MutableState<Int>
			get() = original.updateListener
	}
}