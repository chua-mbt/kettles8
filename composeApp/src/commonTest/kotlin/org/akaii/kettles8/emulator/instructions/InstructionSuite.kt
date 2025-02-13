package org.akaii.kettles8.emulator.instructions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.doubles.*
import io.kotest.matchers.string.*
import io.kotest.matchers.types.*
import io.kotest.property.checkAll
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.input.Keypad.Companion.Key
import org.akaii.kettles8.emulator.memory.*
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register.*
import kotlin.math.ln

class InstructionSuite : FunSpec({
    test("AddressMask") {
        val withAddressMask = object : AddressMask {
            override val value: UShort
                get() = 0xF123u
        }

        withAddressMask.target shouldBe 0x123u
    }

    test("VXMask") {
        for (register in Register.entries) {
            val withVXMask = object : VXMask {
                override val value: UShort
                    get() = (0xF023u or (register.value.toUInt() shl 8)).toUShort()
            }
            withVXMask.vX shouldBe register
        }
    }

    test("VYMask") {
        for (register in Register.entries) {
            val withVYMask = object : VYMask {
                override val value: UShort
                    get() = (0xF203u or (register.value.toUInt() shl 4)).toUShort()
            }
            withVYMask.vY shouldBe register
        }
    }

    test("NMask") {
        val withNMask = object : NMask {
            override val value: UShort
                get() = 0xF123u
        }

        withNMask.nC shouldBe 0x3u
    }

    test("ByteMask") {
        val withByteMask = object : ByteMask {
            override val value: UShort
                get() = 0xF123u
        }

        withByteMask.byteC shouldBe 0x23u
    }


    test("CLS") {
        val instruction = Instruction.decode(0x00E0u)
        instruction shouldBe CLS

        val emulator = Emulator()
        emulator.display.fill()
        emulator.display.flattened().shouldContainOnly(UByte.MAX_VALUE)

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.display.flattened().shouldContainOnly(UByte.MIN_VALUE)
    }

    test("RET") {
        val instruction = Instruction.decode(0x00EEu)
        instruction shouldBe RET
        val expectedAddress: UShort = 0x0400u

        val emulator = Emulator()
        emulator.cpu.stack.addLast(expectedAddress)
        emulator.cpu.programCounter shouldNotBe expectedAddress
        emulator.cpu.stack.shouldNotBeEmpty()

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.stack.shouldBeEmpty()
        emulator.cpu.programCounter shouldBe expectedAddress
    }

    test("JP") {
        val instruction = Instruction.decode(0x1FF1u)
        val expectedAddress: UShort = 0x0FF1u
        instruction.shouldBeTypeOf<JP>()
        instruction.address(instruction.value) shouldBe expectedAddress

        val emulator = Emulator()
        emulator.cpu.programCounter shouldNotBe expectedAddress

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe expectedAddress

        instruction.description() shouldEndWith "JP 0x0FF1"
    }

    test("CALL") {
        val instruction = Instruction.decode(0x2FF1u)
        val expectedAddress: UShort = 0x0FF1u
        instruction.shouldBeTypeOf<CALL>()
        instruction.address(instruction.value) shouldBe expectedAddress

        val emulator = Emulator()
        val originalAddress: UShort = 0x0002u
        emulator.cpu.programCounter = originalAddress
        emulator.cpu.stack.shouldBeEmpty()
        emulator.cpu.programCounter shouldNotBe expectedAddress

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe expectedAddress
        emulator.cpu.stack.shouldContain(originalAddress)
        emulator.cpu.stack.size shouldBe 1

        instruction.description() shouldEndWith "CALL 0x0FF1"
    }

    test("SE_VX_BYTE") {
        val instruction = Instruction.decode(0x3A21u)
        instruction.shouldBeTypeOf<SE_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.programCounter = 0x0002u

        emulator.cpu.registers[VA] = 0x20u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.cpu.registers[VA] = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        instruction.description() shouldEndWith "SE_VX_BYTE VA, 0x21"
    }

    test("SNE_VX_BYTE") {
        val instruction = Instruction.decode(0x4A21u)
        instruction.shouldBeTypeOf<SNE_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.programCounter = 0x0002u

        emulator.cpu.registers[VA] = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.cpu.registers[VA] = 0x20u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        instruction.description() shouldEndWith "SNE_VX_BYTE VA, 0x21"
    }

    test("SE_VX_VY") {
        val instruction = Instruction.decode(0x5AB0u)
        instruction.shouldBeTypeOf<SE_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.registers[VB] = 0x20u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.registers[VB] = 0x21u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        instruction.description() shouldEndWith "SE_VX_VY VA, VB"
    }

    test("LD_VX_BYTE") {
        val instruction = Instruction.decode(0x6A21u)
        instruction.shouldBeTypeOf<LD_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x21u

        instruction.description() shouldEndWith "LD_VX_BYTE VA, 0x21"
    }

    test("ADD_VX_BYTE") {
        val instruction = Instruction.decode(0x7A21u)
        instruction.shouldBeTypeOf<ADD_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x21u

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x42u

        instruction.description() shouldEndWith "ADD_VX_BYTE VA, 0x21"
    }

    test("LD_VX_VY") {
        val instruction = Instruction.decode(0x8AB0u)
        instruction.shouldBeTypeOf<LD_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        emulator.cpu.registers[VB] = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x21u

        instruction.description() shouldEndWith "LD_VX_VY VA, VB"
    }

    test("OR_VX_VY") {
        val instruction = Instruction.decode(0x8AB1u)
        instruction.shouldBeTypeOf<OR_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        emulator.cpu.registers[VB] = 0b1100u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b1110u

        instruction.description() shouldEndWith "OR_VX_VY VA, VB"
    }

    test("AND_VX_VY") {
        val instruction = Instruction.decode(0x8AB2u)
        instruction.shouldBeTypeOf<AND_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        emulator.cpu.registers[VB] = 0b1100u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b1000u

        instruction.description() shouldEndWith "AND_VX_VY VA, VB"
    }

    test("XOR_VX_VY") {
        val instruction = Instruction.decode(0x8AB3u)
        instruction.shouldBeTypeOf<XOR_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        emulator.cpu.registers[VB] = 0b1100u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b0110u

        instruction.description() shouldEndWith "XOR_VX_VY VA, VB"
    }

    test("ADD_VX_VY") {
        val instruction = Instruction.decode(0x8AB4u)
        instruction.shouldBeTypeOf<ADD_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0xFFu
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x00u
        emulator.cpu.registers[VF] shouldBe 0x01u // Overflow

        emulator.cpu.registers[VA] = 0x01u
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x02u
        emulator.cpu.registers[VF] shouldBe 0x00u // No overflow

        instruction.description() shouldEndWith "ADD_VX_VY VA, VB"
    }

    test("SUB_VX_VY") {
        val instruction = Instruction.decode(0x8AB5u)
        instruction.shouldBeTypeOf<SUB_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x02u
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x01u
        emulator.cpu.registers[VF] shouldBe 0x01u // Borrow

        emulator.cpu.registers[VA] = 0x01u
        emulator.cpu.registers[VB] = 0x02u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0xFFu
        emulator.cpu.registers[VF] shouldBe 0x00u // No borrow

        instruction.description() shouldEndWith "SUB_VX_VY VA, VB"
    }

    test("SHR_VX") {
        val instruction = Instruction.decode(0x8AB6u)
        instruction.shouldBeTypeOf<SHR_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b0101u
        emulator.cpu.registers[VF] shouldBe 0u // No carry from LSB

        emulator.cpu.registers[VA] = 0b0101u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b0010u
        emulator.cpu.registers[VF] shouldBe 1u // Carry from LSB

        instruction.description() shouldEndWith "SHR_VX VA"
    }

    test("SUBN_VX_VY") {
        val instruction = Instruction.decode(0x8AB7u)
        instruction.shouldBeTypeOf<SUBN_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x01u
        emulator.cpu.registers[VB] = 0x02u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x01u
        emulator.cpu.registers[VF] shouldBe 0x01u // Borrow

        emulator.cpu.registers[VA] = 0x02u
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0xFFu
        emulator.cpu.registers[VF] shouldBe 0x00u // No borrow

        instruction.description() shouldEndWith "SUBN_VX_VY VA, VB"
    }

    test("SHL_VX") {
        val instruction = Instruction.decode(0x8ABEu)
        instruction.shouldBeTypeOf<SHL_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b01010000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b10100000u
        emulator.cpu.registers[VF] shouldBe 0u // No carry from MSB

        emulator.cpu.registers[VA] = 0b10100000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0b01000000u
        emulator.cpu.registers[VF] shouldBe 1u // Carry from MSB

        instruction.description() shouldEndWith "SHL_VX VA"
    }

    test("SNE_VX_VY") {
        val instruction = Instruction.decode(0x9AB0u)
        instruction.shouldBeTypeOf<SNE_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.registers[VB] = 0x20u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.registers[VB] = 0x21u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        instruction.description() shouldEndWith "SNE_VX_VY VA, VB"
    }

    test("LD_I") {
        val instruction = Instruction.decode(0xABCDu)
        instruction.shouldBeTypeOf<LD_I>()
        instruction.address(instruction.value) shouldBe 0xBCDu

        val emulator = Emulator()
        emulator.cpu.indexRegister shouldBe 0x0000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.indexRegister shouldBe 0x0BCDu

        instruction.description() shouldEndWith "LD_I 0x0BCD"
    }

    test("JP_V0") {
        val instruction = Instruction.decode(0xBBCDu)
        instruction.shouldBeTypeOf<JP_V0>()
        instruction.address(instruction.value) shouldBe 0x0BCDu

        val emulator = Emulator()
        emulator.cpu.registers[V0] = 0x01u
        emulator.cpu.programCounter shouldBe Address.ROM_START
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0BCEu // 0xBCDu + 0x001u

        instruction.description() shouldEndWith "JP_V0 0x0BCD"
    }

    test("RND_VX") {
        val instruction = Instruction.decode(0xCABCu)
        instruction.shouldBeTypeOf<RND_VX>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0xBCu

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldNotBe 0x00u

        instruction.description() shouldEndWith "RND_VX VA, 0xBC"
    }

    test("RND_VX - Value Diversity") {
        val emulator = Emulator()
        val instruction = Instruction.decode(0xCAFFu)

        checkAll<Unit>(10) { _ ->
            val uniqueOutputs = mutableSetOf<UByte>()
            repeat(1000){
                instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
                uniqueOutputs.add(emulator.cpu.registers[VA])
            }
            (uniqueOutputs.size / 255.0) shouldBeGreaterThan 0.90 // High diversity indicates good randomness
        }
    }

    test("RND_VX - Shannon Entropy") {
        val instruction = Instruction.decode(0xCAFFu)
        val emulator = Emulator()
        checkAll<Unit>(100) { _ ->
            val outputs = mutableListOf<UByte>()
            repeat(100){
                instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
                outputs.add(emulator.cpu.registers[VA])
            }
            // Shannon entropy calculation
            val frequencies = outputs.groupingBy { it }.eachCount()
            val probabilities = frequencies.mapValues { it.value.toDouble() / outputs.size }
            val entropy = -probabilities.values.sumOf { p -> p * ln(p) }
            entropy.shouldBeGreaterThan(4.0) // High entropy indicates good randomness
        }
    }

    test("RND_VX - Uniform Distribution") {
        val instruction = Instruction.decode(0xCAFFu)
        val emulator = Emulator()
        val totalSamples = 10_000
        val counts = IntArray(256)

        repeat(totalSamples) {
            instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
            val output = emulator.cpu.registers[VA].toInt()
            counts[output]++
        }

        val expectedCount = totalSamples / 256.0
        val chiSquare = counts.sumOf { observed ->
            val diff = observed - expectedCount
            (diff * diff) / expectedCount
        }

        val criticalValue = 323.00 // Critical value for 95% confidence level and 255 degrees of freedom
        chiSquare.shouldBeLessThan(criticalValue)
    }

    test("DRW_VX_VY") {
        val instruction = Instruction.decode(0xD123u)
        instruction.shouldBeTypeOf<DRW_VX_VY>()

        val emulator = Emulator()

        emulator.memory[0x200u] = 0b11100000u
        emulator.memory[0x201u] = 0b10100000u
        emulator.memory[0x202u] = 0b11100000u

        emulator.cpu.registers[V1] = 5u
        emulator.cpu.registers[V2] = 10u
        emulator.cpu.indexRegister = 0x200u

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)

        emulator.display[5u, 10u] shouldBe UByte.MAX_VALUE
        emulator.display[5u, 11u] shouldBe UByte.MAX_VALUE
        emulator.display[5u, 12u] shouldBe UByte.MAX_VALUE
        emulator.display[6u, 10u] shouldBe UByte.MAX_VALUE
        emulator.display[6u, 11u] shouldBe UByte.MIN_VALUE
        emulator.display[6u, 12u] shouldBe UByte.MAX_VALUE
        emulator.display[7u, 10u] shouldBe UByte.MAX_VALUE
        emulator.display[7u, 11u] shouldBe UByte.MAX_VALUE
        emulator.display[7u, 12u] shouldBe UByte.MAX_VALUE

        emulator.cpu.registers[VF] shouldBe 0u

        instruction.description() shouldEndWith "DRW_VX_VY V1, V2, 0x03"
    }

    test("DRW_VX_VY - Wrapping") {
        val instruction = Instruction.decode(0xD123u)
        instruction.shouldBeTypeOf<DRW_VX_VY>()

        val emulator = Emulator()

        emulator.memory[0x200u] = 0b11100000u
        emulator.memory[0x201u] = 0b10100000u
        emulator.memory[0x202u] = 0b11100000u

        emulator.cpu.registers[V1] = 62u
        emulator.cpu.registers[V2] = 30u
        emulator.cpu.indexRegister = 0x200u

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)

        emulator.display[62u, 30u] shouldBe UByte.MAX_VALUE
        emulator.display[62u, 31u] shouldBe UByte.MAX_VALUE
        emulator.display[62u, 0u] shouldBe UByte.MAX_VALUE
        emulator.display[63u, 30u] shouldBe UByte.MAX_VALUE
        emulator.display[63u, 31u] shouldBe UByte.MIN_VALUE
        emulator.display[63u, 0u] shouldBe UByte.MAX_VALUE
        emulator.display[0u, 30u] shouldBe UByte.MAX_VALUE
        emulator.display[0u, 31u] shouldBe UByte.MAX_VALUE
        emulator.display[0u, 0u] shouldBe UByte.MAX_VALUE

        emulator.cpu.registers[VF] shouldBe 0u

        instruction.description() shouldEndWith "DRW_VX_VY V1, V2, 0x03"
    }

    test("DRW_VX_VY - Erasure") {
        val instruction = Instruction.decode(0xD123u)
        instruction.shouldBeTypeOf<DRW_VX_VY>()

        val emulator = Emulator()

        emulator.memory[0x200u] = 0b11100000u
        emulator.memory[0x201u] = 0b10100000u
        emulator.memory[0x202u] = 0b11100000u

        emulator.cpu.registers[V1] = 62u
        emulator.cpu.registers[V2] = 30u
        emulator.cpu.indexRegister = 0x200u

        emulator.display[62u, 30u] = UByte.MAX_VALUE
        emulator.display[62u, 31u] = UByte.MAX_VALUE
        emulator.display[62u, 0u] = UByte.MAX_VALUE
        emulator.display[63u, 30u] = UByte.MAX_VALUE
        emulator.display[63u, 31u] = UByte.MIN_VALUE
        emulator.display[63u, 0u] = UByte.MAX_VALUE
        emulator.display[0u, 30u] = UByte.MAX_VALUE
        emulator.display[0u, 31u] = UByte.MAX_VALUE
        emulator.display[0u, 0u] = UByte.MAX_VALUE

        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)

        emulator.display[62u, 30u] shouldBe UByte.MIN_VALUE
        emulator.display[62u, 31u] shouldBe UByte.MIN_VALUE
        emulator.display[62u, 0u] shouldBe UByte.MIN_VALUE
        emulator.display[63u, 30u] shouldBe UByte.MIN_VALUE
        emulator.display[63u, 31u] shouldBe UByte.MIN_VALUE
        emulator.display[63u, 0u] shouldBe UByte.MIN_VALUE
        emulator.display[0u, 30u] shouldBe UByte.MIN_VALUE
        emulator.display[0u, 31u] shouldBe UByte.MIN_VALUE
        emulator.display[0u, 0u] shouldBe UByte.MIN_VALUE

        emulator.cpu.registers[VF] shouldBe 1u

        instruction.description() shouldEndWith "DRW_VX_VY V1, V2, 0x03"
    }

    test("SKP_VX") {
        val instruction = Instruction.decode(0xEA9Eu)
        instruction.shouldBeTypeOf<SKP_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x0Bu
        emulator.cpu.programCounter = 0x0002u

        emulator.keypad[Key.KB] = false
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.keypad[Key.KB] = true
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        instruction.description() shouldEndWith "SKP_VX VA"
    }

    test("SKNP_VX") {
        val instruction = Instruction.decode(0xEAA1u)
        instruction.shouldBeTypeOf<SKNP_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x0Bu
        emulator.cpu.programCounter = 0x0002u

        emulator.keypad[Key.KB] = true
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.keypad[Key.KB] = false
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        instruction.description() shouldEndWith "SKNP_VX VA"
    }

    test("LD_VX_DT") {
        val instruction = Instruction.decode(0xFA07u)
        instruction.shouldBeTypeOf<LD_VX_DT>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        emulator.cpu.delayTimer = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers[VA] shouldBe 0x21u

        instruction.description() shouldEndWith "LD_VX_DT VA"
    }

    test("LD_VX_K") {
        val instruction = Instruction.decode(0xFA0Au)
        instruction.shouldBeTypeOf<LD_VX_K>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0u
        emulator.keypad.futureInput.isPending() shouldBe false
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.keypad.futureInput.isPending() shouldBe true

        emulator.keypad.futureInput.checkInput(emulator.keypad)
        emulator.keypad.futureInput.isPending() shouldBe true
        emulator.cpu.registers[VA] shouldBe 0u

        emulator.keypad[Key.KF] = true
        emulator.keypad.futureInput.checkInput(emulator.keypad)
        emulator.keypad.futureInput.isPending() shouldBe true
        emulator.cpu.registers[VA] shouldBe 0u

        emulator.keypad[Key.KF] = false
        emulator.keypad.futureInput.checkInput(emulator.keypad)
        emulator.keypad.futureInput.isPending() shouldBe false
        emulator.cpu.registers[VA] shouldBe Key.KF.value

        emulator.keypad[Key.K0] = true
        emulator.keypad.futureInput.checkInput(emulator.keypad)
        emulator.keypad.futureInput.isPending() shouldBe false
        emulator.cpu.registers[VA] shouldBe Key.KF.value

        instruction.description() shouldEndWith "LD_VX_K VA"
    }

    test("LD_DT_VX") {
        val instruction = Instruction.decode(0xFA15u)
        instruction.shouldBeTypeOf<LD_DT_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.delayTimer shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.delayTimer shouldBe 0x21u

        instruction.description() shouldEndWith "LD_DT_VX VA"
    }

    test("LD_ST_VX") {
        val instruction = Instruction.decode(0xFA18u)
        instruction.shouldBeTypeOf<LD_ST_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.soundTimer shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.soundTimer shouldBe 0x21u

        instruction.description() shouldEndWith "LD_ST_VX VA"
    }

    test("ADD_I_VX") {
        val instruction = Instruction.decode(0xFA1Eu)
        instruction.shouldBeTypeOf<ADD_I_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.indexRegister = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.indexRegister shouldBe 0x42u

        instruction.description() shouldEndWith "ADD_I_VX VA"
    }

    test("LD_F_VX") {
        val instruction = Instruction.decode(0xFA29u)
        instruction.shouldBeTypeOf<LD_F_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x02u
        emulator.cpu.indexRegister shouldBe 0x0000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        val expectedFontAddress = Address.FONT_START + 0x000Au // 2x5 = 10
        emulator.cpu.indexRegister shouldBe expectedFontAddress.toUShort()

        instruction.description() shouldEndWith "LD_F_VX VA"
    }

    test("LD_B_VX") {
        val instruction = Instruction.decode(0xFA33u)
        instruction.shouldBeTypeOf<LD_B_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 123u
        emulator.cpu.indexRegister = 0x000Au
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.memory[10u.toUByte()] shouldBe 1u
        emulator.memory[11u.toUByte()] shouldBe 2u
        emulator.memory[12u.toUByte()] shouldBe 3u

        instruction.description() shouldEndWith "LD_B_VX VA"
    }

    test("LD_I_VX") {
        val instruction = Instruction.decode(0xFA55u)
        instruction.shouldBeTypeOf<LD_I_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        for (vx in V0..VA) {
            emulator.cpu.registers[vx] = vx.value.toUByte()
        }
        emulator.cpu.indexRegister = 0x000Au
        emulator.memory.slice(10..25) shouldContainOnly (listOf(0u))
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.memory.slice(10..25) shouldBe emulator.cpu.registers.toList().toUByteArray()

        instruction.description() shouldEndWith "LD_I_VX VA"
    }

    test("LD_VX_I") {
        val instruction = Instruction.decode(0xFA65u)
        instruction.shouldBeTypeOf<LD_VX_I>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        for (vx in V0..VA) {
            emulator.memory[511u + vx.value.toUInt()] = vx.value.toUByte()
        }
        emulator.cpu.indexRegister = 0x01FFu // 511 in hex
        emulator.cpu.registers.toList() shouldContainOnly (listOf(0u))
        instruction.execute(emulator.cpu, emulator.memory, emulator.display, emulator.keypad)
        emulator.cpu.registers.toList().toUByteArray() shouldBe emulator.memory.slice(511..526)

        instruction.description() shouldEndWith "LD_VX_I VA"
    }

})