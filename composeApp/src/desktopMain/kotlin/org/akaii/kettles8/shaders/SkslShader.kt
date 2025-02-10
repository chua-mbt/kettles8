package org.akaii.kettles8.shaders

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.shaders.KettlesShader.Companion.SHADER_PADDING
import org.akaii.kettles8.ui.Config
import org.jetbrains.skia.*

interface SkslShader : KettlesShader {
    fun uniforms(displayWidth: Int, displayHeight: Int): Data

    override fun render(drawScope: DrawScope, display: Display, config: Config) {
        val effect = RuntimeEffect.Companion.makeForShader(raw)

        val unitWidth = (drawScope.size.width / (Display.Companion.DISPLAY_WIDTH + SHADER_PADDING)).toFloat()
        val unitHeight = (drawScope.size.height / (Display.Companion.DISPLAY_HEIGHT + SHADER_PADDING)).toFloat()
        val adaptedWidth = (Display.Companion.DISPLAY_WIDTH + SHADER_PADDING) * unitWidth
        val adaptedHeight = (Display.Companion.DISPLAY_HEIGHT + SHADER_PADDING) * unitHeight

        val uniforms = uniforms(adaptedWidth.toInt(), adaptedHeight.toInt())

        val surface = Surface.Companion.makeRasterN32Premul(adaptedWidth.toInt(), adaptedHeight.toInt())
        val canvas = surface.canvas

        val colorSet = config.getColorSet()

        display.renderState().forEachIndexed { column, columns ->
            columns.forEachIndexed { row, cell ->
                val col = if (Display.Companion.isOn(cell.value)) colorSet.pixel else colorSet.background
                canvas.drawRect(
                    Rect.Companion.makeXYWH(
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
            Rect.Companion.makeXYWH(0f, 0f, adaptedWidth, adaptedHeight), skPaint
        )
    }
}