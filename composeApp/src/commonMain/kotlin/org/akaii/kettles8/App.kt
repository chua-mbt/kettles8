package org.akaii.kettles8

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.display.Display
import org.akaii.kettles8.emulator.instructions.Instruction
import org.akaii.kettles8.emulator.memory.Address
import org.akaii.kettles8.emulator.memory.Registers
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

object App {
    enum class AppMode {
        FONT_TEST, FONT_WRAP_TEST, FLIP_TEST, SPRITE_TEST
    }

    @Composable
    @Preview
    fun render(mode: AppMode) {
        MaterialTheme {
            val emulator = Emulator()
            emulator.display.render()
            when (mode) {
                AppMode.FONT_TEST -> {
                    emulator.cpu.indexRegister = (Address.FONT_START + 50u).toUShort()
                    emulator.cpu.registers[Registers.Companion.Register.V0] = 0x5u
                    emulator.cpu.registers[Registers.Companion.Register.V1] = 0x5u
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

                AppMode.FLIP_TEST -> {
                    LaunchedEffect(Unit) {
                        while (true) {
                            emulator.display.flip(
                                Random.nextInt(Display.DISPLAY_WIDTH),
                                Random.nextInt(Display.DISPLAY_HEIGHT)
                            )
                            delay(1000)
                        }
                    }
                }

                AppMode.SPRITE_TEST -> {
                    emulator.memory[0x200u.toUByte()] = 0b11100000u
                    emulator.memory[0x201u.toUByte()] = 0b10100000u
                    emulator.memory[0x202u.toUByte()] = 0b11100000u

                    emulator.cpu.registers[Registers.Companion.Register.V1] = 0u // X position
                    emulator.cpu.registers[Registers.Companion.Register.V2] = 5u  // Y position

                    emulator.cpu.indexRegister = 0x200u
                    Instruction.decode(0xD123u)
                        .execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
                }
            }
        }

    }
}
