package org.akaii.kettles8

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.shaders.KettlesShader
import org.akaii.kettles8.ui.ColorSet

class Config {
    private val colorSet: MutableState<ColorSet> = mutableStateOf(ColorSet.Companion.Grays)
    val colorState: State<ColorSet> get() = colorSet
    fun getColorSet(): ColorSet = colorSet.value
    fun setColorSet(colorSet: ColorSet) {
        this.colorSet.value = colorSet
    }

    private val keyConfig: MutableState<Keypad.Companion.KeyConfig> = mutableStateOf(Keypad.Companion.KeyConfig.CLASSIC)
    val keyConfigState: State<Keypad.Companion.KeyConfig> get() = keyConfig
    fun getKeyConfig(): Keypad.Companion.KeyConfig = keyConfig.value
    fun setKeyConfig(config: Keypad.Companion.KeyConfig) {
        this.keyConfig.value = config
    }

    private val shader: MutableState<KettlesShader?> = mutableStateOf(null)
    val shaderState: State<KettlesShader?> get() = shader
    fun getShader(): KettlesShader? = shader.value
    fun setShader(shader: KettlesShader?) {
        this.shader.value = shader
    }

    private val maxQuirkCompatibility: MutableState<Boolean> = mutableStateOf(false)
    val maxQuirkCompatibilityState: State<Boolean> get() = maxQuirkCompatibility
    fun getMaxQuirkCompatibility(): Boolean = maxQuirkCompatibility.value
    fun setMaxQuirkCompatibility(value: Boolean) {
        this.maxQuirkCompatibility.value = value
    }
}