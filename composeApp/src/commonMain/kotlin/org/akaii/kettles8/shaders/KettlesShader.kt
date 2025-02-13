package org.akaii.kettles8.shaders

import androidx.compose.ui.graphics.drawscope.DrawScope
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.ui.Config

interface KettlesShader {
    val raw: String

    fun render(drawScope: DrawScope, display: Display, config: Config)

    companion object {
        const val SHADER_PADDING: Int = 2
    }
}