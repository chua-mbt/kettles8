package org.akaii.kettles8.emulator.interpreter

import org.akaii.kettles8.emulator.CPU
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.instructions.Instruction
import org.akaii.kettles8.emulator.memory.Memory

class Chip8RunLoop : Interpreter {
    override tailrec fun cycle(cpu: CPU, memory: Memory, display: Display, keypad: Keypad) {
        val instruction = fetchInstruction(cpu, memory)
        instruction.describe()
        cpu.advanceProgram()
        instruction.execute(cpu, memory, display, keypad)
        cycle(cpu, memory, display, keypad)
    }

    fun fetchInstruction(cpu: CPU, memory: Memory): Instruction =
        Instruction.Companion.decode(memory[cpu.programCounter])
}