package org.akaii.kettles8.emulator.cpu

import org.akaii.kettles8.emulator.beep.Beep
import org.akaii.kettles8.emulator.beep.NoBeep
import org.akaii.kettles8.emulator.instructions.Instruction
import org.akaii.kettles8.emulator.memory.Address
import org.akaii.kettles8.emulator.memory.Registers

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
    var instruction: Instruction? = null

    fun advanceProgram() {
        programCounter = (programCounter + Instruction.Companion.INSTRUCTION_SIZE).toUShort()
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
        instruction = null
        beep.stop()
    }

    fun cleanup() {
        beep.cleanup()
    }
}