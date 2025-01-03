package kettles8.emulator.interpreter

import kettles8.emulator.memory.Memory

class Interpreter {
    private var programCounter: UShort = 0u

    tailrec fun cycle(memory: Memory): Unit {
        val opCode = memory.fetch(this.programCounter)
        advanceProgram()
        val instruction = Instruction.decode(opCode)
        instruction.describe()
        // instruction.execute()
        cycle(memory)
    }

    private fun advanceProgram(): Unit {
        this.programCounter = (this.programCounter + 2u).toUShort()
    }
}

