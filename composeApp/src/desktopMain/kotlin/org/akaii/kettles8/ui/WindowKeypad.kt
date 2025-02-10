package org.akaii.kettles8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

object WindowKeypad {
    val KEYS = arrayOf(
        arrayOf(Key.K1, Key.K2, Key.K3, Key.KC),
        arrayOf(Key.K4, Key.K5, Key.K6, Key.KD),
        arrayOf(Key.K7, Key.K8, Key.K9, Key.KE),
        arrayOf(Key.KA, Key.K0, Key.KB, Key.KF)
    )

    @Composable
    operator fun invoke(config: Config, onDown: (Key) -> Unit, onUp: (Key) -> Unit) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val boxSize = (maxWidth / KEYS.size).coerceAtMost(maxHeight / KEYS.size)

            Column(Modifier.fillMaxSize().background(Color(config.getColorSet().pixel))) {
                for (row in KEYS) {
                    Row {
                        for (key in row) {
                            Box(Modifier.size(boxSize)) {
                                KeyButton(config, key, onDown, onUp)
                            }
                        }
                    }
                }
            }
        }
    }
}
