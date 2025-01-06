package org.akaii.kettles8.emulator.interpreter

import org.akaii.kettles8.emulator.CPU
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.memory.Memory

interface Interpreter {
    fun cycle(cpu: CPU, memory: Memory, display: Display)
}

