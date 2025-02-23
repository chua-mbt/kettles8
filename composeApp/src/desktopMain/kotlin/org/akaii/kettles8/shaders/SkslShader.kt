package org.akaii.kettles8.shaders

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import org.akaii.kettles8.Config
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.shaders.KettlesShader.Companion.SHADER_PADDING
import org.jetbrains.skia.*

interface SkslShader : KettlesShader {
    fun uniforms(displayWidth: Int, displayHeight: Int): Data

    override fun render(drawScope: DrawScope, display: Display, config: Config) {
        val effect = RuntimeEffect.makeForShader(raw)

        val unitWidth = (drawScope.size.width / (Display.DISPLAY_WIDTH + SHADER_PADDING)).toFloat()
        val unitHeight = (drawScope.size.height / (Display.DISPLAY_HEIGHT + SHADER_PADDING)).toFloat()
        val adaptedWidth = (Display.DISPLAY_WIDTH + SHADER_PADDING) * unitWidth
        val adaptedHeight = (Display.DISPLAY_HEIGHT + SHADER_PADDING) * unitHeight

        val uniforms = uniforms(adaptedWidth.toInt(), adaptedHeight.toInt())

        val surface = Surface.makeRasterN32Premul(adaptedWidth.toInt(), adaptedHeight.toInt())
        val canvas = surface.canvas

        val colorSet = config.getColorSet()

        display.renderState().forEachIndexed { column, columns ->
            columns.forEachIndexed { row, cell ->
                val col = if (Display.isOn(cell.value)) colorSet.pixel else colorSet.background
                canvas.drawRect(
                    Rect.makeXYWH(
                        (column + SHADER_PADDING / 2) * unitWidth,
                        (row + SHADER_PADDING / 2) * unitHeight,
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
            Rect.makeXYWH(0f, 0f, adaptedWidth, adaptedHeight), skPaint
        )
    }
}