package kettles8.emulator.memory

import kettles8.emulator.DefaultFont

class Memory {

    private val underlying: UByteArray = UByteArray(Address.TOTAL_SIZE)

    init {
        DefaultFont.representation.copyInto(
            underlying,
            destinationOffset = Address.FONT_START.toInt(),
            startIndex = 0,
            endIndex = Address.FONT_BLOCK_SIZE
        )
    }

    fun setFromROM(loadedROM: UByteArray) {
        loadedROM.copyInto(
            underlying,
            destinationOffset = Address.ROM_START.toInt(),
            startIndex = 0,
            endIndex = minOf(Address.ROM_BLOCK_SIZE, loadedROM.size)
        )
    }

    fun slice(addressRange: IntRange): UByteArray =
        underlying.slice(addressRange).toUByteArray()

    fun fetch(byteAddress: UShort): UShort {
        val significantByteAddress = byteAddress
        val remainingByteAddress = significantByteAddress + 1u
        val significantByte = underlying[significantByteAddress.toInt()]
        val remainingByte = underlying[remainingByteAddress.toInt()]
        val fetched = (significantByte.toUInt() shl 8) or remainingByte.toUInt()
        return fetched.toUShort()
    }
}