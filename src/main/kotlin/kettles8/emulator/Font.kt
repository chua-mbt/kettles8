package kettles8.emulator

/**
 * The default font for the Chip-8 emulator. Must be set into 0x050-0x09F in memory.
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#font
 */
object DefaultFont {
    val representation = byteArrayOf(
        0xF0.toByte(), 0x90.toByte(), 0x90.toByte(), 0x90.toByte(), 0xF0.toByte(), // 0
        0x20.toByte(), 0x60.toByte(), 0x20.toByte(), 0x20.toByte(), 0x70.toByte(), // 1
        0xF0.toByte(), 0x10.toByte(), 0xF0.toByte(), 0x80.toByte(), 0xF0.toByte(), // 2
        0xF0.toByte(), 0x10.toByte(), 0xF0.toByte(), 0x10.toByte(), 0xF0.toByte(), // 3
        0x90.toByte(), 0x90.toByte(), 0xF0.toByte(), 0x10.toByte(), 0x10.toByte(), // 4
        0xF0.toByte(), 0x80.toByte(), 0xF0.toByte(), 0x10.toByte(), 0xF0.toByte(), // 5
        0xF0.toByte(), 0x80.toByte(), 0xF0.toByte(), 0x90.toByte(), 0xF0.toByte(), // 6
        0xF0.toByte(), 0x10.toByte(), 0x20.toByte(), 0x40.toByte(), 0x40.toByte(), // 7
        0xF0.toByte(), 0x90.toByte(), 0xF0.toByte(), 0x90.toByte(), 0xF0.toByte(), // 8
        0xF0.toByte(), 0x90.toByte(), 0xF0.toByte(), 0x10.toByte(), 0xF0.toByte(), // 9
        0xF0.toByte(), 0x90.toByte(), 0xF0.toByte(), 0x90.toByte(), 0x90.toByte(), // A
        0xE0.toByte(), 0x90.toByte(), 0xE0.toByte(), 0x90.toByte(), 0xE0.toByte(), // B
        0xF0.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0xF0.toByte(), // C
        0xE0.toByte(), 0x90.toByte(), 0x90.toByte(), 0x90.toByte(), 0xE0.toByte(), // D
        0xF0.toByte(), 0x80.toByte(), 0xF0.toByte(), 0x80.toByte(), 0xF0.toByte(), // E
        0xF0.toByte(), 0x80.toByte(), 0xF0.toByte(), 0x80.toByte(), 0x80.toByte()  // F
    )
}