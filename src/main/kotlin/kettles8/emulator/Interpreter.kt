package kettles8.emulator

import kettles8.emulator.instructions.Instruction
import kettles8.emulator.instructions.Instruction.Companion.INSTRUCTION_SIZE

class Interpreter {
    tailrec fun cycle(emulator: Emulator) {
        val instruction = fetchInstruction(emulator)
        instruction.describe()
        advanceProgram(emulator)
        instruction.execute(emulator)
        cycle(emulator)
    }

    fun fetchInstruction(emulator: Emulator): Instruction =
        Instruction.Companion.decode(emulator.memory[emulator.programCounter])

    fun advanceProgram(emulator: Emulator) {
        emulator.programCounter = (emulator.programCounter + INSTRUCTION_SIZE).toUShort()
    }
}