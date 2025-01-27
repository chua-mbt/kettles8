package org.akaii.kettles8.emulator

import org.akaii.kettles8.emulator.cpu.Cpu
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.interpreter.Chip8RunLoop
import org.akaii.kettles8.emulator.interpreter.Interpreter
import org.akaii.kettles8.emulator.memory.Memory

class Emulator(
    val cpu: Cpu = Cpu(),
    val memory: Memory = Memory(),
    val display: Display = Display(),
    val keypad: Keypad = Keypad(),
    val interpreter: Interpreter = Chip8RunLoop()
) : Interpreter by interpreter {

    fun reset() {
        cpu.reset()
        memory.reset()
        display.clear()
        keypad.reset()
    }

    fun loadRom(rom: UByteArray) {
        reset()
        memory.setFromROM(rom)
        cpu.running = true
    }

    override fun cleanup() {
        interpreter.cleanup()
        cpu.cleanup()
    }
}