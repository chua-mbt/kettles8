package org.akaii.kettles8.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key

class KeyButton(private val key: Key, private val onClick: () -> Unit) {
    companion object {
        val KEYPAD_SIZE_DP = Display.Companion.DISPLAY_HEIGHT_DP / WindowKeypad.Companion.KEYS.size
    }

    @Composable
    fun render() {
        Button(
            onClick = onClick,
            modifier = Modifier.size(KEYPAD_SIZE_DP.dp).padding(4.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
        ) {
            Text(text = key.text, color = Color.White)
        }
    }
}
