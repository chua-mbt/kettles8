package org.akaii.kettles8

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kettles8",
    ) {
        App.render(App.AppMode.SPRITE_TEST)
    }
}