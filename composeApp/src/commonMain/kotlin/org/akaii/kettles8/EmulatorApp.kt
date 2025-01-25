package org.akaii.kettles8

import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.instructions.Instruction
import org.akaii.kettles8.emulator.memory.Address
import org.akaii.kettles8.emulator.memory.Registers

object EmulatorApp {
    val emulator = Emulator()

    enum class AppMode {
        FONT_TEST, FONT_WRAP_TEST, SPRITE_TEST, EMULATE
    }

    fun start(mode: AppMode) {
        when (mode) {
            AppMode.FONT_TEST -> {
                emulator.cpu.indexRegister = (Address.FONT_START + 30u).toUShort()
                emulator.cpu.registers[Registers.Companion.Register.V0] = 0x0u
                emulator.cpu.registers[Registers.Companion.Register.V1] = 0x0u
                Instruction.decode(0xD015u)
                    .execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
            }

            AppMode.FONT_WRAP_TEST -> {
                emulator.cpu.indexRegister = (Address.FONT_START + 50u).toUShort()
                emulator.cpu.registers[Registers.Companion.Register.V0] = 0x3Eu
                emulator.cpu.registers[Registers.Companion.Register.V1] = 0x3Eu
                Instruction.decode(0xD015u)
                    .execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
            }

            AppMode.SPRITE_TEST -> {
                emulator.memory[0x200u] = 0b11100000u
                emulator.memory[0x201u] = 0b10100000u
                emulator.memory[0x202u] = 0b11100000u

                emulator.cpu.registers[Registers.Companion.Register.V1] = 0u // X position
                emulator.cpu.registers[Registers.Companion.Register.V2] = 5u  // Y position

                emulator.cpu.indexRegister = 0x200u
                Instruction.decode(0xD123u)
                    .execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
            }

            AppMode.EMULATE ->
                emulator.start(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        }
    }
}
