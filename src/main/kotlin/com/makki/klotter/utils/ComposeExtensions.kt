package com.makki.klotter.utils

fun Float.isNanDebug(): Boolean {
	if (this.isNaN()) {
		stacktraceHere()
		return true
	}
	return false
}

fun stacktraceHere() {
	try {
		throw Exception("DEBUG")
	} catch (e: Exception) {
		e.printStackTrace()
	}
}