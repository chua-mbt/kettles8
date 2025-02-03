package org.akaii.kettles8.shaders

import org.jetbrains.skia.Data

interface KettlesShader {
    val raw: String
    fun uniforms(displayWidth: Int, displayHeight: Int): Data

    companion object {
        const val SHADER_PADDING_DP: Int = 20
    }
}