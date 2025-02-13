package org.akaii.kettles8.emulator.interpreter

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.shouldBeNull
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.beep.Beep
import org.akaii.kettles8.emulator.cpu.Cpu
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.memory.Address
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register

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

    test("Beep runs until sound timer expires") {
        var beeping = false
        val beepTestCpu = Cpu(object : Beep {
            override fun start() {
                beeping = true
            }

            override fun stop() {
                beeping  = false
            }

            override fun cleanup() {}
        })
        val emulator = Emulator(cpu = beepTestCpu)
        emulator.memory[Address.ROM_START.toUInt()] = 0x6Au
        emulator.memory[Address.ROM_START+1u] = 0x0Au
        emulator.memory[Address.ROM_START+2u] = 0xFAu
        emulator.memory[Address.ROM_START+3u] = 0x18u

        beeping shouldBe false
        emulator.cpu.cycles shouldBe(0)

        val runLoop = emulator.interpreter as Chip8RunLoop
        emulator.cpu.running = true
        runLoop.cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[Register.VA] shouldBe 10u

        emulator.cpu.soundTimer shouldBe 0u
        runLoop.cpuTick(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.soundTimer shouldBe 10u

        (0..<10).forEach {
            emulator.cpu.updateTimers()
            emulator.cpu.soundTimer shouldBe (10u - it.toUInt() - 1u).toUByte()
            beeping shouldBe true
        }

        emulator.cpu.updateTimers()
        beeping shouldBe false
    }
})