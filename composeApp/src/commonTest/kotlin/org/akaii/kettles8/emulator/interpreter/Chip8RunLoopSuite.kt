package org.akaii.kettles8.emulator.interpreter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.shouldBeNull
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.memory.Address

class Chip8RunLoopSuite : FunSpec({
    test("Cycle when running") {
        val emulator = Emulator()
        emulator.memory[Address.ROM_START.toUInt()] = 0x00u
        emulator.memory[Address.ROM_START+1u] = 0xE0u

        emulator.display.fill()
        emulator.keypad[Keypad.Companion.Key.KB] = true
        emulator.keypad[Keypad.Companion.Key.KB] = false
        emulator.cpu.cycles shouldBe(0)
        emulator.keypad.keyReleased() shouldBe Keypad.Companion.Key.KB

        emulator.cpu.running = true
        (emulator.interpreter as Chip8RunLoop).cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.cycles shouldBe(1)
        emulator.cpu.programCounter shouldBe (Address.ROM_START + 2u).toUShort()
        emulator.display.flattened().shouldContainOnly(UByte.MIN_VALUE)
        emulator.keypad.keyReleased().shouldBeNull()
    }

    test("Don't cycle while waiting for input") {
        val emulator = Emulator()
        emulator.memory[Address.ROM_START.toUInt()] = 0x00u
        emulator.memory[Address.ROM_START+1u] = 0xE0u
        emulator.keypad.futureInput.onNextKeyReady { key -> }

        emulator.display.fill()
        emulator.cpu.cycles shouldBe(0)

        emulator.cpu.running = true
        emulator.keypad.futureInput.onNextKeyReady { key -> }
        (emulator.interpreter as Chip8RunLoop).cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.cycles shouldBe(0)
        emulator.cpu.programCounter shouldBe Address.ROM_START
        emulator.display.flattened().shouldContainOnly(UByte.MAX_VALUE)

        emulator.keypad[Keypad.Companion.Key.KB] = true
        emulator.keypad[Keypad.Companion.Key.KB] = false
        emulator.interpreter.cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.interpreter.cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.cycles shouldBe(1)
        emulator.cpu.programCounter shouldBe (Address.ROM_START + 2u).toUShort()
        emulator.display.flattened().shouldContainOnly(UByte.MIN_VALUE)
        emulator.keypad.keyReleased().shouldBeNull()
    }

    test("Don't cycle when not running") {
        val emulator = Emulator()
        emulator.memory[Address.ROM_START.toUInt()] = 0x00u
        emulator.memory[Address.ROM_START+1u] = 0xE0u
        emulator.keypad.futureInput.onNextKeyReady { key -> }

        emulator.display.fill()
        emulator.cpu.cycles shouldBe(0)

        (emulator.interpreter as Chip8RunLoop).cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.cycles shouldBe(0)
        emulator.cpu.programCounter shouldBe(Address.ROM_START)
        emulator.display.flattened().shouldContainOnly(UByte.MAX_VALUE)
    }
})