package org.akaii.kettles8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.akaii.kettles8.Config
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key.*

object KeyPanel {
    val KEYS = arrayOf(
        arrayOf(K1, K2, K3, KC),
        arrayOf(K4, K5, K6, KD),
        arrayOf(K7, K8, K9, KE),
        arrayOf(KA, K0, KB, KF)
    )

    @Composable
    operator fun invoke(config: Config, onDown: (Keypad.Companion.Key) -> Unit, onUp: (Keypad.Companion.Key) -> Unit) {
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(Color(config.getColorSet().pixel))
        ) {
            for (row in KEYS) {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (key in row) {
                        Box(
                            modifier = Modifier.Companion
                                .fillMaxHeight()
                                .weight(1f)
                        ) {
                            KeyButton(config, key, onDown, onUp)
                        }
                    }
                }
            }
        }
    }
}