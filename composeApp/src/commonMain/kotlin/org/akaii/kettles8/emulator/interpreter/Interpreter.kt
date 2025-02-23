package org.akaii.kettles8.emulator.interpreter

import org.akaii.kettles8.emulator.cpu.Cpu
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.memory.Memory

interface Interpreter {
    fun start(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad)
    fun cleanup()

    /**
     * The original COSMAC implementation of CHIP-8 had a few quirks that aren't part of the official design.
     * This setting chooses to maximize quirk compatibility according to widely used conventions
     * https://github.com/Timendus/chip8-test-suite?tab=readme-ov-file#quirks-test
     */
    fun toggleMaxQuirkCompatibility(enabled: Boolean)
}

