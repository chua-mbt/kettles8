package org.akaii.kettles8.emulator

import org.akaii.kettles8.emulator.beep.*
import org.akaii.kettles8.emulator.format.Hex
import org.akaii.kettles8.emulator.instructions.Instruction.Companion.INSTRUCTION_SIZE
import org.akaii.kettles8.emulator.memory.*

class Cpu(private val beep: Beep = NoBeep()) {
    companion object {
        private const val STACK_SIZE = 16
    }

    val stack: ArrayDeque<UShort> = ArrayDeque(STACK_SIZE)
    val registers: Registers = Registers()

    /** Program Counter (PC) */
    var programCounter: UShort = Address.ROM_START

    /** Index Register (I) */
    var indexRegister: UShort = 0u

    /** Delay Timer (DT) */
    var delayTimer: UByte = 0u

    /** Sound Timer (ST) */
    var soundTimer: UByte = 0u

    var running: Boolean = false
    var cycles: Int = 0

    fun advanceProgram() {
        programCounter = (programCounter + INSTRUCTION_SIZE).toUShort()
    }

    fun updateTimers() {
        if (delayTimer > 0u) delayTimer--
        if (soundTimer > 0u) {
            soundTimer--
            beep.start()
        } else {
            beep.stop()
        }
    }

    fun reset() {
        stack.clear()
        registers.reset()
        programCounter = Address.ROM_START
        indexRegister = 0u
        delayTimer = 0u
        soundTimer = 0u
        running = false
        cycles = 0
        beep.stop()
    }

    fun cleanup() {
        beep.cleanup()
    }

    override fun toString(): String {
        return buildString {
            append("\nCPU\n")
            append("---------\n")

            append(String.format("%-15s: %-4s\n", "PC", programCounter.toHexString(Hex.UI16_FORMAT)))
            append(String.format("%-15s: %-4s\n", "I", indexRegister.toHexString(Hex.UI16_FORMAT)))
            append(String.format("%-15s: %-4s\n", "Delay Timer", delayTimer))
            append(String.format("%-15s: %-4s\n", "Sound Timer", soundTimer))

            // Stack: Displaying stack with the same formatting
            append(
                String.format(
                    "%-15s: %-4s\n",
                    "Stack",
                    stack.joinToString(", ") { it.toHexString(Hex.UI16_FORMAT) }
                )
            )
            append(registers.toString())
            append("\n")
        }
    }
}