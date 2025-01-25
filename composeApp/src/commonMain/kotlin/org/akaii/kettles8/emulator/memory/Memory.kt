package org.akaii.kettles8.emulator.memory

import org.akaii.kettles8.emulator.display.DefaultFont
import org.akaii.kettles8.emulator.format.Hex
import org.akaii.kettles8.emulator.format.StdoutColors

class Memory {

    private val underlying: UByteArray = UByteArray(Address.TOTAL_SIZE)

    init {
        reset()
    }

    fun reset() {
        underlying.fill(0u)
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

    override fun toString(): String {
        return buildString {
            append("\nMemory\n")
            append("--------\n")

            for (i in underlying.indices) {
                val value = underlying[i]
                val hexValue = value.toHexString(Hex.UI8_FORMAT)

                val colorCode = when (i) {
                    in Address.FONT_BLOCK -> StdoutColors.LIGHT_BLUE
                    in Address.ROM_BLOCK -> StdoutColors.LIGHT_GREEN
                    else -> StdoutColors.GRAY
                }

                append(colorCode)
                append(hexValue)
                append(StdoutColors.DEFAULT)

                if ((i + 1) % 32 == 0) append("\n")
                else append(" ")
            }
        }
    }
}