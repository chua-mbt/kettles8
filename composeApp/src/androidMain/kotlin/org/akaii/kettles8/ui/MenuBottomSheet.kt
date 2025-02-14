package org.akaii.kettles8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MenuBottomSheet(
    config: Config,
    pickROM: () -> Unit,
    reset: () -> Unit,
    pickColor: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val sheetScaffoldState = rememberBottomSheetScaffoldState()
    val peekHeight = 20.dp

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color(config.colorState.value.background)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    tint = Color(config.colorState.value.pixel),
                    contentDescription = "Expand"
                )
                MenuItem("ROM", config, pickROM)
                MenuItem("Reset", config, reset)
                MenuItem("Theme", config, pickColor)
            }
        },
        sheetPeekHeight = peekHeight,
        content = content
    )
}

@Composable
fun MenuItem(label: String, config: Config,onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(config.colorState.value.background))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            color = Color(config.colorState.value.pixel)
        )
    }
}