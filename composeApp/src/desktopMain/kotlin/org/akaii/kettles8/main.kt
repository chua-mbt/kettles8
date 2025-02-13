package org.akaii.kettles8

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.akaii.kettles8.beep.DesktopBeep
import org.akaii.kettles8.emulator.debug.Debug
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.rom.DesktopROM
import org.akaii.kettles8.shaders.CRT
import org.akaii.kettles8.shaders.KettlesShader
import org.akaii.kettles8.ui.*
import kotlin.io.path.Path

fun main() = DesktopApp().start()

class DesktopApp {

    private val app = Application(beep = DesktopBeep())
    private val logger = KotlinLogging.logger {}

    private val WINDOW_WIDTH = 960
    private val WINDOW_HEIGHT = 380

    fun start() = application {
        DisposableEffect(Unit) {
            app.start(Application.AppMode.EMULATE)
            onDispose {
                app.emulator.cleanup()
            }
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = Application.name,
            resizable = true,
            state = rememberWindowState(width = WINDOW_WIDTH.dp, height = WINDOW_HEIGHT.dp)
        ) {
            MaterialTheme(colors = darkColors()) {
                MenuBar {
                    Menu("File", mnemonic = 'F') {
                        Item("ROM") {
                            pickRom()
                        }
                    }
                    Menu("Keypad", mnemonic = 'K') {
                        val selected: State<Keypad.Companion.KeyConfig> = remember { app.config.keyConfigState }
                        Keypad.Companion.KeyConfig.entries.forEach { keypadConfig ->
                            Item(
                                text = keypadConfig.name,
                                onClick = { app.config.setKeyConfig(keypadConfig) },
                                icon = if (selected.value == keypadConfig) rememberVectorPainter(Icons.Default.Check) else null,
                            )
                        }
                    }
                    Menu("Themes", mnemonic = 'T') {
                        val selected: State<ColorSet> = remember { app.config.colorState }
                        ColorSet.values.forEach { colorSet ->
                            Item(
                                text = colorSet.name,
                                onClick = { app.config.setColorSet(colorSet) },
                                icon = if (selected.value == colorSet) rememberVectorPainter(Icons.Default.Check) else null,
                            )
                        }
                    }
                    Menu("Shaders", mnemonic = 'S') {
                        val selected: State<KettlesShader?> = remember { app.config.shaderState }
                        Item(
                            text = "CRT",
                            onClick = {
                                val newValue = if (selected.value == null) CRT else null
                                app.config.setShader(newValue)
                            },
                            icon = if (selected.value != null) rememberVectorPainter(Icons.Default.Check) else null,
                        )
                    }
                    Menu("Debug", mnemonic = 'D') {
                        Item(
                            text = "CPU",
                            onClick = {
                                Debug.flip(app.emulator.cpu)
                            },
                            icon = if (Debug.panelVisible.value) rememberVectorPainter(Icons.Default.Check) else null,
                        )
                    }
                }

                val focusRequester = remember { FocusRequester() }
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .focusable()
                        .onKeyEvent { keyEvent ->
                            when (keyEvent.type) {
                                KeyEventType.KeyUp -> app.emulator.keypad.onUp(keyEvent.key, app.config.getKeyConfig())
                                KeyEventType.KeyDown -> app.emulator.keypad.onDown(
                                    keyEvent.key,
                                    app.config.getKeyConfig()
                                )

                                else -> null
                            } == true
                        }) {

                    LaunchedEffect(Unit) { focusRequester.requestFocus() }
                    val displayWidth = maxWidth * 2 / 3
                    val keypadWidth = maxWidth / 3

                    Row(Modifier.fillMaxSize()) {
                        Box(Modifier.width(displayWidth).fillMaxHeight().background(Color.Black)) {
                            DisplayPanel(app.emulator.display, app.config)
                        }
                        Box(Modifier.width(keypadWidth).fillMaxHeight()) {
                            KeyPanel(
                                app.config,
                                app.emulator.keypad::onDown,
                                app.emulator.keypad::onUp
                            )
                        }
                    }

                    if (Debug.panelVisible.value) {
                        val debugFocusRequester = remember { FocusRequester() }

                        Window(onCloseRequest = { Debug.flip(app.emulator.cpu) }) {
                            Box(
                                Modifier.fillMaxSize()
                                    .focusRequester(debugFocusRequester)
                                    .focusable()
                                    .onKeyEvent { keyEvent -> Debug.step(keyEvent) }) {
                                LaunchedEffect(Unit) { debugFocusRequester.requestFocus() }
                                DebugPanel(app.emulator.cpu, app.emulator.keypad) { window.title = it }
                            }
                        }
                    }
                }
            }
        }
    }

    fun pickRom() {
        CoroutineScope(Dispatchers.Default).launch {
            val file = FileKit.pickFile(
                type = PickerType.File(listOf("ch8")),
                mode = PickerMode.Single
            )

            logger.debug { "Picked file: $file" }

            file?.path?.let {
                val rom = DesktopROM(Path(it)).load()
                app.emulator.loadRom(rom)
                Debug.reset()
            }
        }
    }
}
