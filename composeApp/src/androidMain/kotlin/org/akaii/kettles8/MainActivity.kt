package org.akaii.kettles8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.akaii.kettles8.emulator.beep.NoBeep
import org.akaii.kettles8.ui.DisplayPanel

class MainActivity : ComponentActivity() {
    private val app = Application(beep = NoBeep())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.start(Application.AppMode.SPRITE_TEST)

        setContent {
            DisplayPanel(app.emulator.display, app.config)
        }
    }

    override fun onDestroy() {
        app.emulator.cleanup()
        super.onDestroy()
    }
}
