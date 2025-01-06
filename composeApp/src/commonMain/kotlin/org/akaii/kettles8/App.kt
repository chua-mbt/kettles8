package org.akaii.kettles8

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.display.Display
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
@Preview
fun App() {
    MaterialTheme {
        val emulator = Emulator()
        emulator.display.fill()
        emulator.display.render()
        LaunchedEffect(Unit) {
            while(true) {
                emulator.display.flip(Random.nextInt(Display.DISPLAY_WIDTH), Random.nextInt(Display.DISPLAY_HEIGHT))
                delay(1000)
            }
        }
    }
}