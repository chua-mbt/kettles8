package org.akaii.kettles8.emulator.format

object Hex {
    private fun format(len: Int): HexFormat = HexFormat {
        upperCase = true
        number {
            prefix = "0x"
            minLength = len
        }
    }

    val UI16_FORMAT: HexFormat = format(4)
    val UI8_FORMAT: HexFormat = format(2)
}