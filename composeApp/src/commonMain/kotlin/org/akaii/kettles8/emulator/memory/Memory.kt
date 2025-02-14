package org.akaii.kettles8.emulator.memory

import org.akaii.kettles8.emulator.display.DefaultFont

class Memory {

    private val underlying: UByteArray = UByteArray(Address.TOTAL_SIZE)
    private val romCopy: UByteArray = UByteArray(Address.ROM_BLOCK_SIZE)

    init {
        clear()
    }

    fun clear() {
        underlying.fill(0u)
        DefaultFont.representation.copyInto(
            underlying,
            destinationOffset = Address.FONT_START.toInt(),
            startIndex = 0,
            endIndex = Address.FONT_BLOCK_SIZE
        )
    }

    fun isInitialized(): Boolean =
        romCopy.any { it != 0.toUByte() }

    fun reset() {
        clear()
        startROM()
    }

    private fun startROM() {
        romCopy.copyInto(
            underlying,
            destinationOffset = Address.ROM_START.toInt(),
            startIndex = 0,
            endIndex = Address.ROM_BLOCK_SIZE
        )
    }

    fun setFromROM(loadedROM: UByteArray) {
        loadedROM.copyInto(
            romCopy,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = minOf(Address.ROM_BLOCK_SIZE, loadedROM.size)
        )
        startROM()
    }

    fun slice(addressRange: IntRange): UByteArray =
        underlying.slice(addressRange).toUByteArray()

    fun getInstruction(byteAddress: UShort): UShort {
        val significantByteAddress = byteAddress
        val remainingByteAddress = significantByteAddress + 1u
        val significantByte = underlying[significantByteAddress.toInt()]
        val remainingByte = underlying[remainingByteAddress.toInt()]
        val fetched = (significantByte.toUInt() shl 8) or remainingByte.toUInt()
        return fetched.toUShort()
    }

    operator fun get(byteAddress: UInt): UByte = underlying[byteAddress.toInt()]
    operator fun get(byteAddress: UByte): UByte = underlying[byteAddress.toInt()]
    operator fun set(byteAddress: UInt, value: UByte) {
        underlying[byteAddress.toInt()] = value
    }
    operator fun set(byteAddress: UShort, value: UByte) {
        set(byteAddress.toUInt(), value)
    }
}