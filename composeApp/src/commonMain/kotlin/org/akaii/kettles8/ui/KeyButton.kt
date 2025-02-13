package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import org.akaii.kettles8.emulator.input.Keypad

object KeyButton {
    @Composable
    operator fun invoke(config: Config, key: Keypad.Companion.Key, onDown: (Keypad.Companion.Key) -> Unit, onUp: (Keypad.Companion.Key) -> Unit) {
        Surface(
            onClick = {},
            modifier = Modifier.Companion
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
            color = Color(config.getColorSet().pixel),
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {
            BoxWithConstraints(Modifier.Companion.fillMaxSize(), contentAlignment = Alignment.Companion.Center) {
                Text(
                    text = key.text(config.getKeyConfig()),
                    color = Color(config.getColorSet().background),
                    fontSize = ((maxWidth.value + maxHeight.value) / 4f).sp
                )
            }
        }
    }
}