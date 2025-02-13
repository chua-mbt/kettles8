package org.akaii.kettles8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.akaii.kettles8.beep.AndroidBeep
import org.akaii.kettles8.rom.AndroidROM
import org.akaii.kettles8.ui.*

class MainActivity : ComponentActivity() {
    private val app = Application(beep = AndroidBeep())

    private lateinit var pickROM: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.start(Application.AppMode.EMULATE)

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
                        pickColor = { showThemePicker.value = true }
                    ) { padding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .consumeWindowInsets(padding)
                        ) {
                            DisplayPanel(app.emulator.display, app.config)
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

