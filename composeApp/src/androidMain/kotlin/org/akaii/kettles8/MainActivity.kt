package org.akaii.kettles8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmulatorApp.render(EmulatorApp.AppMode.FONT_WRAP_TEST)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    EmulatorApp.render(EmulatorApp.AppMode.FONT_WRAP_TEST)
}