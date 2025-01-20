package org.akaii.kettles8.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.oshai.kotlinlogging.KotlinLogging
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

class WindowKeypad(private val onKeyPress: (Key) -> Unit) {
    companion object {
        val KEYS = arrayOf(
            arrayOf(Key.K1, Key.K2, Key.K3, Key.KC),
            arrayOf(Key.K4, Key.K5, Key.K6, Key.KD),
            arrayOf(Key.K7, Key.K8, Key.K9, Key.KE),
            arrayOf(Key.KA, Key.K0, Key.KB, Key.KF)
        )
    }

    private val logger = KotlinLogging.logger {}

    @Composable
    fun render() {
        Column(
            modifier = Modifier.wrapContentSize().background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in KEYS) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (key in row) {
                        KeyButton(key = key, onClick = {
                            logger.debug { "Key pressed: $key" }
                            onKeyPress(key)
                        }).render()
                    }
                }
            }
        }
    }
}
