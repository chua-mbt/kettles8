package org.akaii.kettles8.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT_DP
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_WIDTH
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_WIDTH_DP
import org.akaii.kettles8.shaders.KettlesShader.Companion.SHADER_PADDING_DP

@Composable
fun DisplayPanel(display: Display, config: Config) {
    Canvas(
        modifier = Modifier
            .size(
                (DISPLAY_WIDTH_DP + SHADER_PADDING_DP).dp,
                (DISPLAY_HEIGHT_DP + SHADER_PADDING_DP).dp
            )
            .background(Color.Black),
    ) {
        config.getShader()?.render(this, display, config) ?: noShader(display, config)
    }
}

fun DrawScope.noShader(display: Display, config: Config) {
    val colorSet = config.getColorSet()
    val unitWidth = size.width / DISPLAY_WIDTH
    val unitHeight = size.height / DISPLAY_HEIGHT
    display.renderState().forEachIndexed { column, columns ->
        columns.forEachIndexed { row, cell ->
            val color = if (Display.isOn(cell.value)) colorSet.pixel else colorSet.background
            drawRect(
                color = Color(color),
                topLeft = Offset(column * unitWidth, row * unitHeight),
                size = Size(unitWidth + 1, unitHeight + 1)
            )
        }
    }
}