package com.makki.klotter.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import kotlin.system.exitProcess

@Composable
fun SimpleWindow(size: DpSize?, name: String = "Untitled", exit: (() -> Unit)? = null,  content: @Composable FrameWindowScope.() -> Unit) {
    Window(
        onCloseRequest = {
            exit?.invoke() ?: exitProcess(0)
        },
        title = name,
        state = WindowState(size = size ?: DpSize(600.dp, 300.dp)),
    ) {
        content()
    }
}