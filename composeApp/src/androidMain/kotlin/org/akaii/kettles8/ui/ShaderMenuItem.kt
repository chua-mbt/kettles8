package org.akaii.kettles8.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import org.akaii.kettles8.Config
import org.akaii.kettles8.shaders.CrtAgsl

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderMenuItem(config: Config, closeMenu: () -> Unit) =
    MenuItem(
        label = when(config.getShader()) {
            null -> "Turn CRT Effects On"
            else -> "Turn CRT Effects Off"
        },
        config = config,
        onClick = {
            when(config.getShader()) {
                null -> config.setShader(CrtAgsl)
                else -> config.setShader(null)
            }
            closeMenu()
        },
        closeMenu = closeMenu
    )
