package org.akaii.kettles8.emulator.display

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*

class Display {
    companion object {
        const val DISPLAY_WIDTH = 64
        const val DISPLAY_HEIGHT = 32

        const val DISPLAY_WIDTH_DP = DISPLAY_WIDTH * 10
        const val DISPLAY_HEIGHT_DP = DISPLAY_HEIGHT * 10

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

    fun flip(row: Int, column: Int) {
        if (underlying[row][column].value == UByte.MIN_VALUE) underlying[row][column].value = UByte.MAX_VALUE
        else underlying[row][column].value = UByte.MIN_VALUE
    }

    @Composable
    fun render() {
        Canvas(
            modifier = Modifier.size(DISPLAY_WIDTH_DP.dp, DISPLAY_HEIGHT_DP.dp).background(Color.White),
        ) {
            val unitWidth = size.width / DISPLAY_WIDTH
            val unitHeight = size.height / DISPLAY_HEIGHT
            underlying.forEachIndexed { column, columns ->
                columns.forEachIndexed { row, cell ->
                    drawRect(
                        color = Color.Black.copy(alpha = cell.value.toFloat() / UByte.MAX_VALUE.toFloat()),
                        topLeft = Offset(column * unitWidth, row * unitHeight),
                        size = Size(unitWidth + 1, unitHeight + 1) // + 1 Removes gaps between cells
                    )
                }
            }
        }
    }

}