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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_HEIGHT_DP
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_WIDTH
import org.akaii.kettles8.emulator.display.Display.Companion.DISPLAY_WIDTH_DP
import org.akaii.kettles8.shaders.KettlesShader
import org.akaii.kettles8.shaders.KettlesShader.Companion.SHADER_PADDING_DP
import org.jetbrains.skia.*

@Composable
fun DisplayPanel(display: Display, config: Config) {
    Canvas(
        modifier = Modifier.size((DISPLAY_WIDTH_DP + 20).dp, (DISPLAY_HEIGHT_DP + 20).dp).background(Color.Black),
    ) {
        config.getShader()?.let { withShader(it, display, config) } ?: noShader(display, config)
    }
}

fun DrawScope.withShader(shader: KettlesShader, display: Display, config: Config) {
    val effect = RuntimeEffect.makeForShader(shader.raw)
    val uniforms = shader.uniforms(DISPLAY_WIDTH_DP + SHADER_PADDING_DP, DISPLAY_HEIGHT_DP + SHADER_PADDING_DP)

    val surface = Surface.makeRasterN32Premul(DISPLAY_WIDTH_DP + SHADER_PADDING_DP, DISPLAY_HEIGHT_DP + SHADER_PADDING_DP)
    val canvas = surface.canvas

    val colorSet = config.getColorSet()
    val unitWidth = (DISPLAY_WIDTH_DP / DISPLAY_WIDTH).toFloat()
    val unitHeight = (DISPLAY_HEIGHT_DP / DISPLAY_HEIGHT).toFloat()

    display.renderState().forEachIndexed { column, columns ->
        columns.forEachIndexed { row, cell ->
            val col = if (Display.isOn(cell.value)) colorSet.pixel else colorSet.background
            canvas.drawRect(
                Rect.makeXYWH(
                    column * unitWidth + SHADER_PADDING_DP / 2,
                    row * unitHeight + SHADER_PADDING_DP / 2,
                    unitWidth + 1,
                    unitHeight + 1
                ),
                Paint().apply { this.color = col }
            )
        }
    }

    val image = surface.makeImageSnapshot()
    val shader = effect.makeShader(uniforms, arrayOf(image.makeShader()), null)
    val skPaint = Paint().apply { this.shader = shader }

    drawContext.canvas.nativeCanvas.drawRect(
        Rect.makeXYWH(
            0f,
            0f,
            (DISPLAY_WIDTH_DP + SHADER_PADDING_DP).toFloat(),
            (DISPLAY_HEIGHT_DP + SHADER_PADDING_DP).toFloat()
        ), skPaint
    )
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