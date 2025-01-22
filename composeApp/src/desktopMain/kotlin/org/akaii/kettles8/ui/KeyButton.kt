package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

class KeyButton(private val key: Key, private val onDown: (Key) -> Unit, private val onUp: (Key) -> Unit) {
    companion object {
        val KEYPAD_SIZE_DP = Display.Companion.DISPLAY_HEIGHT_DP / WindowKeypad.Companion.KEYS.size
    }

    @Composable
    fun render() {
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
            Text(text = key.text, color = Color.White)
        }
    }
}
