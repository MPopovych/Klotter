package com.makki.klotter.handlers

import com.makki.klotter.elements.DrawContext

interface PlotDataHandler<T> {
	fun draw(context: DrawContext, scopeT: List<T?>)

	fun topFocus(data: T): Float
	fun botFocus(data: T): Float
}