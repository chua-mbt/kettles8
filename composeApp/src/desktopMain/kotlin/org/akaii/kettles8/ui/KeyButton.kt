package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

object KeyButton {
    @Composable
    operator fun invoke(config: Config, key: Key, onDown: (Key) -> Unit, onUp: (Key) -> Unit) {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .pointerInput(Unit) {
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
            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = key.text(config.getKeyConfig()),
                    color = Color(config.getColorSet().background),
                    fontSize = (maxWidth.value * 0.75f).sp
                )
            }
        }
    }
}
