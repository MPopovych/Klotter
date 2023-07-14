package com.makki.klotter.utils

import kotlin.math.max
import kotlin.math.min


fun <T> List<T>.safeSub(from: Int, to: Int): List<T> {
	val safeFrom = max(min(this.size, from), 0)
	val safeTo = max(min(this.size, to), 0)

	val max = max(safeTo, safeFrom)
	val min = min(safeTo, safeFrom)
	return this.subList(min, max)
}