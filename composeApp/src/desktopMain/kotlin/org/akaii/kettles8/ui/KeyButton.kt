package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT_DP
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key
import org.akaii.kettles8.shaders.KettlesShader.Companion.SHADER_PADDING_DP

object KeyButton {
    val KEYPAD_SIZE_DP = (DISPLAY_HEIGHT_DP + SHADER_PADDING_DP)/ WindowKeypad.KEYS.size

    @Composable
    operator fun invoke(config: Config, key: Key, onDown: (Key) -> Unit, onUp: (Key) -> Unit) {
        Button(
            onClick = {},
            modifier = Modifier.size(KEYPAD_SIZE_DP.dp).padding(4.dp).pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()
                        if (change.pressed && !change.previousPressed) {
                            onDown(key)
                        }
                        if (!change.pressed && change.previousPressed) {
                            onUp(key)
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(config.getColorSet().pixel))
        ) {
            Text(text = key.text(config.getKeyConfig()), color = Color(config.getColorSet().background), fontSize = 30.sp)
        }
    }
}
