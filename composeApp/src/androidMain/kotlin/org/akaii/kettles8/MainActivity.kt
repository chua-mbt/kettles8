package org.akaii.kettles8

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.beep.AndroidBeep
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.rom.AndroidROM
import org.akaii.kettles8.shaders.CrtAgsl
import org.akaii.kettles8.ui.*

class MainActivity : ComponentActivity() {
    private val app = Application(beep = AndroidBeep())

    private lateinit var pickROM: ActivityResultLauncher<Array<String>>

    private val useShader = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.start(Application.AppMode.EMULATE)
        if(useShader) { app.config.setShader(CrtAgsl) }

        pickROM = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                val rom = AndroidROM(it).load(contentResolver)
                app.emulator.loadRom(rom)
            }
        }

        setContent {
            var showThemePicker = remember { mutableStateOf(false) }
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MenuBottomSheet(
                        config = app.config,
                        pickROM = { pickROM.launch(arrayOf("*/*")) },
                        reset = { app.emulator.reset() },
                        pickColor = { showThemePicker.value = true }
                    ) { padding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .consumeWindowInsets(padding)
                        ) {
                            val isLandscape =
                                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

                            if (isLandscape) {
                                Row(
                                    modifier = Modifier.fillMaxSize()
                                        .background(Color(app.config.colorState.value.pixel))
                                ) {
                                    Box(
                                        modifier = Modifier.weight(0.6f).aspectRatio(Display.ASPECT_RATIO)
                                            .align(Alignment.CenterVertically)
                                            .border(1.dp, Color(app.config.colorState.value.background))
                                    ) {
                                        DisplayPanel(app.emulator.display, app.config, useShader)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(0.4f)
                                            .aspectRatio(1f)
                                            .align(Alignment.CenterVertically)
                                    ) {
                                        KeyPanel(
                                            app.config,
                                            app.emulator.keypad::onDown,
                                            app.emulator.keypad::onUp
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                        .background(Color(app.config.colorState.value.pixel))
                                ) {
                                    Box(
                                        modifier = Modifier.weight(0.5f).aspectRatio(Display.ASPECT_RATIO)
                                            .align(Alignment.CenterHorizontally)
                                            .border(1.dp, Color(app.config.colorState.value.background))
                                    ) {
                                        DisplayPanel(app.emulator.display, app.config, useShader)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(0.5f)
                                            .aspectRatio(1f)
                                            .align(Alignment.CenterHorizontally)
                                    ) {
                                        KeyPanel(
                                            app.config,
                                            app.emulator.keypad::onDown,
                                            app.emulator.keypad::onUp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (showThemePicker.value) {
                    ThemePicker(
                        config = app.config,
                        onSelect = app.config::setColorSet,
                        onDismiss = { showThemePicker.value = false }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        app.emulator.cleanup()
        super.onDestroy()
    }
}

