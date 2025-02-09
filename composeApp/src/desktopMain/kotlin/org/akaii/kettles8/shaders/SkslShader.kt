package org.akaii.kettles8.shaders

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.ui.Config
import org.jetbrains.skia.Data
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.Surface

interface SkslShader : KettlesShader {
    fun uniforms(displayWidth: Int, displayHeight: Int): Data

    override fun render(drawScope: DrawScope, display: Display, config: Config) {
        val effect = RuntimeEffect.Companion.makeForShader(raw)
        val uniforms = uniforms(Display.Companion.DISPLAY_WIDTH_DP + KettlesShader.Companion.SHADER_PADDING_DP, Display.Companion.DISPLAY_HEIGHT_DP + KettlesShader.Companion.SHADER_PADDING_DP)

        val surface =
            Surface.Companion.makeRasterN32Premul(Display.Companion.DISPLAY_WIDTH_DP + KettlesShader.Companion.SHADER_PADDING_DP, Display.Companion.DISPLAY_HEIGHT_DP + KettlesShader.Companion.SHADER_PADDING_DP)
        val canvas = surface.canvas

        val colorSet = config.getColorSet()
        val unitWidth = (Display.Companion.DISPLAY_WIDTH_DP / Display.Companion.DISPLAY_WIDTH).toFloat()
        val unitHeight = (Display.Companion.DISPLAY_HEIGHT_DP / Display.Companion.DISPLAY_HEIGHT).toFloat()

        display.renderState().forEachIndexed { column, columns ->
            columns.forEachIndexed { row, cell ->
                val col = if (Display.Companion.isOn(cell.value)) colorSet.pixel else colorSet.background
                canvas.drawRect(
                    Rect.Companion.makeXYWH(
                        column * unitWidth + KettlesShader.Companion.SHADER_PADDING_DP / 2,
                        row * unitHeight + KettlesShader.Companion.SHADER_PADDING_DP / 2,
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

        drawScope.drawContext.canvas.nativeCanvas.drawRect(
            Rect.Companion.makeXYWH(
                0f,
                0f,
                (Display.Companion.DISPLAY_WIDTH_DP + KettlesShader.Companion.SHADER_PADDING_DP).toFloat(),
                (Display.Companion.DISPLAY_HEIGHT_DP + KettlesShader.Companion.SHADER_PADDING_DP).toFloat()
            ), skPaint
        )
    }
}