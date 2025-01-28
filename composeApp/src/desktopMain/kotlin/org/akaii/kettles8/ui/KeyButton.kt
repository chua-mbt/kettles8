package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad.Companion.Config
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

object KeyButton {
    val KEYPAD_SIZE_DP = Display.Companion.DISPLAY_HEIGHT_DP / WindowKeypad.KEYS.size

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
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
        ) {
            Text(text = key.text(config), color = Color.White)
        }
    }
}
