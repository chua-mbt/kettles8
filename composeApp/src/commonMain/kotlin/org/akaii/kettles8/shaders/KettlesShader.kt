package org.akaii.kettles8.shaders

import androidx.compose.ui.graphics.drawscope.DrawScope
import org.akaii.kettles8.Config
import org.akaii.kettles8.emulator.display.Display

interface KettlesShader {
    val raw: String

    fun render(drawScope: DrawScope, display: Display, config: Config)

    companion object {
        const val SHADER_PADDING: Int = 2
    }
}