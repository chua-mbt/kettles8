package org.akaii.kettles8.emulator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT_DP
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_WIDTH
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_WIDTH_DP


@Composable
fun DisplayPanel(display: Display) {
    Canvas(
        modifier = Modifier.size(DISPLAY_WIDTH_DP.dp, DISPLAY_HEIGHT_DP.dp).background(Color.White),
    ) {
        val unitWidth = size.width / DISPLAY_WIDTH
        val unitHeight = size.height / DISPLAY_HEIGHT
        display.renderState().forEachIndexed { column, columns ->
            columns.forEachIndexed { row, cell ->
                drawRect(
                    color = Color.Black.copy(alpha = cell.value.toFloat() / UByte.MAX_VALUE.toFloat()),
                    topLeft = Offset(column * unitWidth, row * unitHeight),
                    size = Size(unitWidth + 1, unitHeight + 1) // + 1 Removes gaps between cells
                )
            }
        }
    }
}
