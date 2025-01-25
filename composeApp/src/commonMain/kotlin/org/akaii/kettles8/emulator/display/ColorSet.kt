package org.akaii.kettles8.emulator.display

import androidx.compose.ui.graphics.Color

data class ColorSet(val name: String, val background: Color, val pixel: Color) {
    companion object {
        val Grays = ColorSet("Grays", Color(0xFFD3D3D3), Color(0xFF444444))
        val Reds = ColorSet("Reds", Color(0xFFFFCCCC), Color(0xFF330000))
        val Blues = ColorSet("Blues", Color(0xFFCCE5FF), Color(0xFF00264D))
        val Greens = ColorSet("Greens", Color(0xFFCCFFCC), Color(0xFF003300))
        val Yellows = ColorSet("Yellows", Color(0xFFFFFFCC), Color(0xFF4D4D00))
        val Purples = ColorSet("Purples", Color(0xFFE5CCFF), Color(0xFF2D004D))
        val Oranges = ColorSet("Oranges", Color(0xFFFFE5CC), Color(0xFF4D2600))

        val values = listOf(Grays, Reds, Blues, Greens, Yellows, Purples, Oranges)
    }
}