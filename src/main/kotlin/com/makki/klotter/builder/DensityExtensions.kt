package com.makki.klotter.builder

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.unit.Density


fun ProvidableCompositionLocal<Density>.default(): ProvidedValue<Density> {
	return this.providesDefault(value = Density(1f, 1f))
}