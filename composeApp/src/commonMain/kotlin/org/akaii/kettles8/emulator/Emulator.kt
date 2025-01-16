package org.akaii.kettles8.emulator

import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.interpreter.Chip8RunLoop
import org.akaii.kettles8.emulator.interpreter.Interpreter
import org.akaii.kettles8.emulator.memory.Memory

class Emulator(
    rom: UByteArray = UByteArray(0),
    val cpu: CPU = CPU(),
    val memory: Memory = Memory(),
    val display: Display = Display(),
    val keypad: Keypad = Keypad(),
    val interpreter: Interpreter = Chip8RunLoop()
) : Interpreter by interpreter {
    init {
        memory.setFromROM(rom)
    }
}