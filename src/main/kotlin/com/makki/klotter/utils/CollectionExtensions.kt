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

fun Iterable<Float?>.safeMaxOrNull(): Float? {
	val iterator = iterator()
	if (!iterator.hasNext()) return null
	var max: Float?
	do {
		max = iterator.next()
	} while (max == null && iterator.hasNext())

	var safeMax: Float = max ?: return null
	while (iterator.hasNext()) {
		val e = iterator.next() ?: continue
		safeMax = maxOf(safeMax, e)
	}
	return safeMax
}

fun Iterable<Float?>.safeMinOrNull(): Float? {
	val iterator = iterator()
	if (!iterator.hasNext()) return null
	var min: Float?
	do {
		min = iterator.next()
	} while (min == null && iterator.hasNext())

	var safeMin: Float = min ?: return null
	while (iterator.hasNext()) {
		val e = iterator.next() ?: continue
		safeMin = minOf(safeMin, e)
	}
	return safeMin
}