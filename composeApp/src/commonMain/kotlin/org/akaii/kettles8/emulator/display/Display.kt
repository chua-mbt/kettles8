package org.akaii.kettles8.emulator.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

class Display {
    companion object {
        const val DISPLAY_WIDTH = 64
        const val DISPLAY_HEIGHT = 32
    }

    private val underlying: Array<Array<MutableState<Boolean>>> =
        Array(DISPLAY_WIDTH) { Array(DISPLAY_HEIGHT) { mutableStateOf(false) } }

    fun fill() {
        underlying.forEach { it.forEach { cell -> cell.value = true } }
    }

    fun clear() {
        underlying.forEach { it.forEach { cell -> cell.value = false } }
    }

    fun flattened(): List<Boolean> = underlying.flatten().map { it.value }

    operator fun get(row: Int, column: Int): Boolean = underlying[row][column].value
    operator fun set(row: Int, column: Int, value: Boolean) {
        underlying[row][column].value = value
    }

    fun flip(row: Int, column: Int) {
        underlying[row][column].value = !underlying[row][column].value
    }

    @Composable
    fun render() {
        Canvas(
            modifier = Modifier.fillMaxSize().background(Color.White),
        ) {
            val unitWidth = size.width / DISPLAY_WIDTH
            val unitHeight = size.height / DISPLAY_HEIGHT
            underlying.forEachIndexed { column, columns ->
                columns.forEachIndexed { row, cell ->
                    val color = if (cell.value) Color.Black else Color.Transparent
                    drawRect(
                        color = color,
                        topLeft = Offset(column * unitWidth, row * unitHeight),
                        size = Size(unitWidth + 1, unitHeight + 1)
                    )
                }
            }
        }
    }

}