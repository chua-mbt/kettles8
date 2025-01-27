package org.akaii.kettles8.emulator.interpreter

import org.akaii.kettles8.emulator.Cpu
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.memory.Memory

interface Interpreter {
    fun start(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad)
    fun cleanup()
}

