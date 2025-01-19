package org.akaii.kettles8.emulator

import org.akaii.kettles8.emulator.instructions.Instruction.Companion.INSTRUCTION_SIZE
import org.akaii.kettles8.emulator.memory.Registers

class CPU {
    companion object {
        private const val STACK_SIZE = 16
    }

    val stack: ArrayDeque<UShort> = ArrayDeque(STACK_SIZE)
    val registers: Registers = Registers()

    /** Program Counter (PC) */
    var programCounter: UShort = 0u

    /** Index Register (I) */
    var indexRegister: UShort = 0u

    /** Delay Timer (DT) */
    var delayTimer: UByte = 0u

    /** Sound Timer (ST) */
    var soundTimer: UByte = 0u

    fun advanceProgram() {
        programCounter = (programCounter + INSTRUCTION_SIZE).toUShort()
    }

    fun updateTimers() {
        if(delayTimer > 0u) delayTimer--
        if(soundTimer > 0u) soundTimer--
    }
}