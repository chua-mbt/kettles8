package org.akaii.kettles8.shaders

import android.graphics.*
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import org.akaii.kettles8.Config
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.shaders.KettlesShader.Companion.SHADER_PADDING

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
interface AgslShader : KettlesShader {
    fun arguments(displayWidth: Int, displayHeight: Int): FloatArray

    override fun render(drawScope: DrawScope, display: Display, config: Config) {
        val effect = RuntimeShader(raw)

        val unitWidth = drawScope.size.width / (Display.DISPLAY_WIDTH + SHADER_PADDING)
        val unitHeight = drawScope.size.height / (Display.DISPLAY_HEIGHT + SHADER_PADDING)

        val adaptedWidth = (Display.DISPLAY_WIDTH + SHADER_PADDING) * unitWidth
        val adaptedHeight = (Display.DISPLAY_HEIGHT + SHADER_PADDING) * unitHeight

        val uniforms = arguments(adaptedWidth.toInt(), adaptedHeight.toInt())

        val bitmap = ImageBitmap(adaptedWidth.toInt(), adaptedHeight.toInt())
        val canvas = Canvas(bitmap.asAndroidBitmap())

        val colorSet = config.getColorSet()

        display.renderState().forEachIndexed { column, columns ->
            columns.forEachIndexed { row, cell ->
                val color = if (Display.isOn(cell.value)) colorSet.pixel else colorSet.background
                val paint = Paint().apply { this.color = Color(color).toArgb() }

                canvas.drawRect(
                    (column + SHADER_PADDING / 2) * unitWidth,
                    (row + SHADER_PADDING / 2) * unitHeight,
                    (column + SHADER_PADDING / 2 + 1) * unitWidth + 1,
                    (row + SHADER_PADDING / 2 + 1) * unitHeight + 1,
                    paint
                )
            }
        }

        val imageShader = BitmapShader(bitmap.asAndroidBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        effect.setInputShader("inputImage", imageShader)
        effect.setFloatUniform("resolution", uniforms)

        val androidPaint = Paint().apply {
            shader = effect
        }

        drawScope.drawContext.canvas.nativeCanvas.drawRect(
            0f, 0f, adaptedWidth, adaptedHeight, androidPaint
        )
    }
}

