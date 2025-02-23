package org.akaii.kettles8.emulator.display

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class Display {
    companion object {
        const val DISPLAY_WIDTH = 64
        const val DISPLAY_HEIGHT = 32
        const val ASPECT_RATIO: Float = (DISPLAY_WIDTH / DISPLAY_HEIGHT).toFloat()

        fun normalize(pixel: UByte): UByte =
            if (pixel == UByte.MIN_VALUE) 0u else UByte.MAX_VALUE

        fun isOn(pixel: UByte): Boolean =
            pixel == UByte.MAX_VALUE
    }

    private val underlying: Array<Array<MutableState<UByte>>> =
        Array(DISPLAY_WIDTH) { Array(DISPLAY_HEIGHT) { mutableStateOf(0u) } }


    fun fill() {
        underlying.forEach { it.forEach { cell -> cell.value = UByte.MAX_VALUE } }
    }

    fun clear() {
        underlying.forEach { it.forEach { cell -> cell.value = UByte.MIN_VALUE } }
    }

    fun flattened(): List<UByte> = underlying.flatten().map { it.value }

    operator fun get(row: UByte, column: UByte): UByte = underlying[row.toInt()][column.toInt()].value
    operator fun set(row: UByte, column: UByte, value: UByte) {
        underlying[row.toInt()][column.toInt()].value = normalize(value)
    }

    fun renderState(): Array<Array<State<UByte>>> =
        underlying.map { it.map { it as State<UByte> }.toTypedArray() }.toTypedArray()
}