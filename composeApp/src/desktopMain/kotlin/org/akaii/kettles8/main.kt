package org.akaii.kettles8

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.*
import org.akaii.kettles8.ui.WindowKeypad

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kettles8",
        resizable = false,
        state = rememberWindowState(
            width = Dp.Unspecified,
            height = Dp.Unspecified
        )
    ) {
        Row(modifier = Modifier.wrapContentSize()) {
            EmulatorApp.render(EmulatorApp.AppMode.SPRITE_TEST)
            WindowKeypad(onKeyPress = { key -> }).render()
        }
    }
}