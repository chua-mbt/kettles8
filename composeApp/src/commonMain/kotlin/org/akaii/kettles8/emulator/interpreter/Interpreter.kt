package org.akaii.kettles8.emulator.interpreter

import org.akaii.kettles8.emulator.CPU
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.memory.Memory

interface Interpreter {
    fun start(cpu: CPU, memory: Memory, display: Display, keypad: Keypad)
    fun stop()
}

