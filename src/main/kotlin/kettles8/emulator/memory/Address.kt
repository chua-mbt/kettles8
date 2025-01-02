package kettles8.emulator.memory

/**
 * Address Space:
 *  0x000-0x1FF: Reserved
 *  0x050-0x0A0: 16 built-in characters (0 through F)
 *  0x200-0xFFF: ROM instructions
 *
 *  http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#2.1
 */
object Address {
    const val RESERVED_START: Int = 0x000
    const val RESERVED_END: Int = 0x1FF
    const val FONT_START: Int = 0x050
    const val FONT_END: Int = 0x09F
    const val ROM_START: Int = 0x200
    const val ROM_END: Int = 0xFFF

    val FONT_BLOCK: IntRange = FONT_START..FONT_END
    val ROM_BLOCK: IntRange = ROM_START..ROM_END

    const val FONT_BLOCK_SIZE: Int = 80
    const val ROM_BLOCK_SIZE: Int = 3584
    const val TOTAL_SIZE: Int = 4096
}
