package org.akaii.kettles8.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.shaders.CrtAgsl

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderMenuItem(config: Config, closeMenu: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(config.colorState.value.background))
            .clickable {
                when(config.getShader()) {
                    null -> config.setShader(CrtAgsl)
                    else -> config.setShader(null)
                }
                closeMenu()
            }
    ) {
        Text(
            text = when(config.getShader()) {
                null -> "Turn CRT On"
                else -> "Turn CRT Off"
            },
            style = MaterialTheme.typography.body1,
            color = Color(config.colorState.value.pixel)
        )
    }
}