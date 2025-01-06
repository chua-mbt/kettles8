package org.akaii.kettles8.emulator.memory

/**
 * Address Space:
 *  0x000-0x1FF: Reserved
 *  0x050-0x0A0: 16 built-in characters (0 through F)
 *  0x200-0xFFF: ROM instructions
 *
 *  http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#2.1
 */
object Address {
    const val RESERVED_START: UShort = 0x000u
    const val RESERVED_END: UShort = 0x1FFu
    const val FONT_START: UShort = 0x050u
    const val FONT_END: UShort = 0x09Fu
    const val ROM_START: UShort = 0x200u
    const val ROM_END: UShort = 0xFFFu

    val FONT_BLOCK: IntRange = FONT_START.toInt()..FONT_END.toInt()
    val ROM_BLOCK: IntRange = ROM_START.toInt()..ROM_END.toInt()

    const val FONT_BLOCK_SIZE: Int = 80
    const val ROM_BLOCK_SIZE: Int = 3584
    const val TOTAL_SIZE: Int = 4096
}
