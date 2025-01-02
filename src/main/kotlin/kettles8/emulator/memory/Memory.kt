package kettles8.emulator.memory

import kettles8.emulator.DefaultFont

class Memory {

    private val underlying: ByteArray = ByteArray(Address.TOTAL_SIZE)

    init {
        DefaultFont.representation.copyInto(
            underlying,
            destinationOffset = Address.FONT_START,
            startIndex = 0,
            endIndex = Address.FONT_BLOCK_SIZE
        )
    }

    fun setFromROM(loadedROM: ByteArray) {
        loadedROM.copyInto(
            underlying,
            destinationOffset = Address.ROM_START,
            startIndex = 0,
            endIndex = minOf(Address.ROM_BLOCK_SIZE, loadedROM.size)
        )
    }

    fun slice(addressRange: IntRange): ByteArray =
        underlying.slice(addressRange).toByteArray()
}