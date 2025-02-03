package org.akaii.kettles8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
        Column(
            modifier = Modifier.wrapContentSize().background(Color(config.getColorSet().pixel)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in KEYS) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (key in row) {
                        KeyButton(config, key, onDown, onUp)
                    }
                }
            }
        }
    }
}
