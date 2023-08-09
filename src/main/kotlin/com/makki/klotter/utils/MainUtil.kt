package com.makki.klotter.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import kotlin.system.exitProcess

@Composable
fun SimpleWindow(
	size: DpSize?,
	name: String = "Untitled",
	icon: ImageVector? = null,
	exit: (() -> Unit)? = null,
	content: @Composable FrameWindowScope.() -> Unit,
) {
	Window(
		onCloseRequest = {
			exit?.invoke() ?: exitProcess(0)
		},
		title = name,
		icon = icon?.let { rememberVectorPainter(icon) },
		state = WindowState(size = size ?: DpSize(600.dp, 300.dp)),
	) {
		content()
	}
}