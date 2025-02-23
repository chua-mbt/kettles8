package org.akaii.kettles8.ui

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
import org.akaii.kettles8.Config

@Composable
fun MenuItem(label: () -> String, config: Config, onClick: () -> Unit, closeMenu: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(config.colorState.value.background))
            .clickable { onClick(); closeMenu() }
    ) {
        Text(
            text = label(),
            style = MaterialTheme.typography.body1,
            color = Color(config.colorState.value.pixel)
        )
    }
}

@Composable
fun MenuItem(label: String, config: Config, onClick: () -> Unit, closeMenu: () -> Unit) =
    MenuItem({ label }, config, onClick, closeMenu)