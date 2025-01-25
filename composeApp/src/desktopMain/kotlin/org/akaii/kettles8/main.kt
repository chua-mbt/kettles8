package org.akaii.kettles8

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.akaii.kettles8.EmulatorApp.emulator
import org.akaii.kettles8.emulator.display.ColorSet
import org.akaii.kettles8.emulator.ui.*
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
            MaterialTheme(colors = darkColors()) {
                MenuBar {
                    Menu("File", mnemonic = 'F') {
                        Item("ROM") {
                            pickRom()
                        }
                    }
                    Menu("Themes", mnemonic = 'T') {
                        val selected: State<ColorSet> = remember { emulator.display.colorState() }
                        ColorSet.values.forEach { colorSet ->
                            Item(
                                text = colorSet.name,
                                onClick = { emulator.display.setColorSet(colorSet) },
                                icon = if (selected.value == colorSet) rememberVectorPainter(Icons.Default.Check) else null,
                            )
                        }
                    }
                }
                Column(modifier = Modifier.wrapContentSize()) {
                    Row(modifier = Modifier.wrapContentSize()) {
                        DisplayPanel(emulator.display)
                        WindowKeypad(emulator.keypad::onDown, emulator.keypad::onUp)
                    }
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

            logger.debug { "Picked file: $file" }

            file?.path?.let {
                val rom = ROMLoader(Path(it)).load()
                emulator.loadRom(rom)
            }
        }
    }
}
