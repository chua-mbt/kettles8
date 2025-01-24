package org.akaii.kettles8

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.akaii.kettles8.EmulatorApp.emulator
import org.akaii.kettles8.rom.ROMLoader
import org.akaii.kettles8.ui.WindowKeypad
import kotlin.io.path.Path

fun main() = DesktopApp().start()

class DesktopApp {

    private val logger = KotlinLogging.logger {}

    fun start() = application {
        DisposableEffect(Unit) {
            EmulatorApp.start(EmulatorApp.AppMode.EMULATE)
            onDispose {
                emulator.stop()
            }
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "kettles8",
            resizable = false,
            state = rememberWindowState(
                width = Dp.Unspecified,
                height = Dp.Unspecified
            )
        ) {
            MaterialTheme {
                MenuBar {
                    Menu("File", mnemonic = 'F') {
                        Item("ROM") {
                            pickRom()
                        }
                    }
                }
                Row(modifier = Modifier.wrapContentSize()) {
                    emulator.display.render()
                    WindowKeypad(emulator.keypad::onDown, emulator.keypad::onUp)
                }
            }
        }
    }

    fun pickRom() {
        CoroutineScope(Dispatchers.Default).launch {
            val file = FileKit.pickFile(
                type = PickerType.File(listOf(ROMLoader.ROM_EXTENSION)),
                mode = PickerMode.Single
            )

            logger.debug{ "Picked file: $file" }

            file?.path?.let {
                val rom = ROMLoader(Path(it)).load()
                emulator.loadRom(rom)
            }
        }
    }
}
