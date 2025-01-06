package org.akaii.kettles8.emulator

import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.interpreter.Chip8RunLoop
import org.akaii.kettles8.emulator.interpreter.Interpreter
import org.akaii.kettles8.emulator.memory.Memory

class Emulator(
    rom: UByteArray = UByteArray(0),
    val cpu: CPU = CPU(),
    val memory: Memory = Memory(),
    val display: Display = Display(),
    val interpreter: Interpreter = Chip8RunLoop()
) : Interpreter by interpreter {
    companion object {
        enum class InputKeys(val value: UByte) {
            K0(0u), K1(1u), K2(2u), K3(3u), K4(4u), K5(5u), K6(6u), K7(7u),
            K8(8u), K9(9u), KA(10u), KB(11u), KC(12u), KD(13u), KE(14u), KF(15u)
        }
    }

    init {
        memory.setFromROM(rom)
    }
}