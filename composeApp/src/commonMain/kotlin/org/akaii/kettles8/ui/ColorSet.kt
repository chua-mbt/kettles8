package org.akaii.kettles8.ui

data class ColorSet(val name: String, val background: Int, val pixel: Int) {
    companion object {
        val Grays = ColorSet("Grays", 0xFFD3D3D3.toInt(), 0xFF444444.toInt())
        val Reds = ColorSet("Reds", 0xFFFFCCCC.toInt(), 0xFF330000.toInt())
        val Blues = ColorSet("Blues", 0xFFCCE5FF.toInt(), 0xFF00264D.toInt())
        val Greens = ColorSet("Greens", 0xFFCCFFCC.toInt(), 0xFF003300.toInt())
        val Yellows = ColorSet("Yellows", 0xFFFFFFCC.toInt(), 0xFF4D4D00.toInt())
        val Purples = ColorSet("Purples", 0xFFE5CCFF.toInt(), 0xFF2D004D.toInt())
        val Oranges = ColorSet("Oranges", 0xFFFFE5CC.toInt(), 0xFF4D2600.toInt())

        val OrangeDarkRed = ColorSet("Orange & Red", 0xFF8B0000.toInt(), 0xFFFFA500.toInt())
        val WhiteTerm = ColorSet("White Term", 0xFF000000.toInt(), 0xFFFFFFFF.toInt())
        val GreenTerm = ColorSet("Green Term", 0xFF000000.toInt(), 0xFF00FF00.toInt())
        val AmberTerm = ColorSet("Amber Term", 0xFF000000.toInt(), 0xFFFFBF00.toInt())
        val CyanTerm = ColorSet("Cyan Term", 0xFF000000.toInt(), 0xFF00FFFF.toInt())
        val WinTerm = ColorSet("Windows Term", 0xFF00008B.toInt(), 0xFFADD8E6.toInt())

        val values = listOf(
            Grays, Reds, Blues, Greens, Yellows, Purples, Oranges,
            OrangeDarkRed, WhiteTerm, GreenTerm, AmberTerm, CyanTerm, WinTerm
        )
    }
}