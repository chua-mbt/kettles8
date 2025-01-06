package org.akaii.kettles8.emulator.instructions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.types.*
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register.*

class InstructionSuite : FunSpec({
    test("CLS") {
        val instruction = Instruction.decode(0x00E0u)
        instruction shouldBe CLS

        val emulator = Emulator()
        emulator.display.fill()
        emulator.display.flattened().shouldContainOnly(true)

        instruction.execute(emulator)
        emulator.display.flattened().shouldContainOnly(false)
    }

    test("RET") {
        val instruction = Instruction.decode(0x00EEu)
        instruction shouldBe RET

        val emulator = Emulator()
        emulator.stack.addLast(0x0200u)
        emulator.stack.shouldNotBeEmpty()

        instruction.execute(emulator)
        emulator.stack.shouldBeEmpty()
    }

    test("JP") {
        val instruction = Instruction.decode(0x1FF1u)
        val expectedAddress: UShort = 0x0FF1u
        instruction.shouldBeTypeOf<JP>()
        instruction.address(instruction.value) shouldBe expectedAddress

        val emulator = Emulator()
        emulator.programCounter shouldNotBe expectedAddress

        instruction.execute(emulator)
        emulator.programCounter shouldBe expectedAddress
    }

    test("CALL") {
        val instruction = Instruction.decode(0x2FF1u)
        val expectedAddress: UShort = 0x0FF1u
        instruction.shouldBeTypeOf<CALL>()
        instruction.address(instruction.value) shouldBe expectedAddress

        val emulator = Emulator()
        val originalAddress: UShort = 0x0002u
        emulator.programCounter = originalAddress
        emulator.stack.shouldBeEmpty()
        emulator.programCounter shouldNotBe expectedAddress

        instruction.execute(emulator)
        emulator.programCounter shouldBe expectedAddress
        emulator.stack.shouldContain(originalAddress)
        emulator.stack.size shouldBe 1
    }

    test("SE_VX_BYTE") {
        val instruction = Instruction.decode(0x3A21u)
        instruction.shouldBeTypeOf<SE_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.registers[VA] = 0x20u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0002u // No skip

        emulator.registers[VA] = 0x21u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0004u // Skip the next instruction
    }

    test("SNE_VX_BYTE") {
        val instruction = Instruction.decode(0x4A21u)
        instruction.shouldBeTypeOf<SNE_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.registers[VA] = 0x21u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0002u // No skip

        emulator.registers[VA] = 0x20u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0004u // Skip the next instruction
    }

    test("SE_VX_VY") {
        val instruction = Instruction.decode(0x5AB0u)
        instruction.shouldBeTypeOf<SE_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0x20u
        emulator.registers[VB] = 0x20u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0004u // Skip the next instruction

        emulator.registers[VA] = 0x20u
        emulator.registers[VB] = 0x21u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0002u // No skip
    }

    test("LD_VX_BYTE") {
        val instruction = Instruction.decode(0x6A21u)
        instruction.shouldBeTypeOf<LD_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.registers[VA] shouldBe 0x00u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x21u
    }

    test("ADD_VX_BYTE") {
        val instruction = Instruction.decode(0x7A21u)
        instruction.shouldBeTypeOf<ADD_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.registers[VA] shouldBe 0x00u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x21u

        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x42u
    }

    test("LD_VX_VY") {
        val instruction = Instruction.decode(0x8AB0u)
        instruction.shouldBeTypeOf<LD_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] shouldBe 0x00u
        emulator.registers[VB] = 0x21u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x21u
    }

    test("OR_VX_VY") {
        val instruction = Instruction.decode(0x8AB1u)
        instruction.shouldBeTypeOf<OR_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0b1010u
        emulator.registers[VB] = 0b1100u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b1110u
    }

    test("AND_VX_VY") {
        val instruction = Instruction.decode(0x8AB2u)
        instruction.shouldBeTypeOf<AND_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0b1010u
        emulator.registers[VB] = 0b1100u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b1000u
    }

    test("XOR_VX_VY") {
        val instruction = Instruction.decode(0x8AB3u)
        instruction.shouldBeTypeOf<XOR_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0b1010u
        emulator.registers[VB] = 0b1100u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b0110u
    }

    test("ADD_VX_VY") {
        val instruction = Instruction.decode(0x8AB4u)
        instruction.shouldBeTypeOf<ADD_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0xFFu
        emulator.registers[VB] = 0x01u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x00u
        emulator.registers[VF] shouldBe 0x01u // Overflow

        emulator.registers[VA] = 0x01u
        emulator.registers[VB] = 0x01u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x02u
        emulator.registers[VF] shouldBe 0x00u // No overflow
    }

    test("SUB_VX_VY") {
        val instruction = Instruction.decode(0x8AB5u)
        instruction.shouldBeTypeOf<SUB_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0x02u
        emulator.registers[VB] = 0x01u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x01u
        emulator.registers[VF] shouldBe 0x01u // Borrow

        emulator.registers[VA] = 0x01u
        emulator.registers[VB] = 0x02u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0xFFu
        emulator.registers[VF] shouldBe 0x00u // No borrow
    }

    test("SHR_VX") {
        val instruction = Instruction.decode(0x8AB6u)
        instruction.shouldBeTypeOf<SHR_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.registers[VA] = 0b1010u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b0101u
        emulator.registers[VF] shouldBe 0u // No carry from LSB

        emulator.registers[VA] = 0b0101u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b0010u
        emulator.registers[VF] shouldBe 1u // Carry from LSB
    }

    test("SUBN_VX_VY") {
        val instruction = Instruction.decode(0x8AB7u)
        instruction.shouldBeTypeOf<SUBN_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0x01u
        emulator.registers[VB] = 0x02u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x01u
        emulator.registers[VF] shouldBe 0x01u // Borrow

        emulator.registers[VA] = 0x02u
        emulator.registers[VB] = 0x01u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0xFFu
        emulator.registers[VF] shouldBe 0x00u // No borrow
    }

    test("SHL_VX") {
        val instruction = Instruction.decode(0x8ABEu)
        instruction.shouldBeTypeOf<SHL_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.registers[VA] = 0b01010000u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b10100000u
        emulator.registers[VF] shouldBe 0u // No carry from MSB

        emulator.registers[VA] = 0b10100000u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0b01000000u
        emulator.registers[VF] shouldBe 1u // Carry from MSB
    }

    test("SNE_VX_VY") {
        val instruction = Instruction.decode(0x9AB0u)
        instruction.shouldBeTypeOf<SNE_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.registers[VA] = 0x20u
        emulator.registers[VB] = 0x20u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0002u // No skip

        emulator.registers[VA] = 0x20u
        emulator.registers[VB] = 0x21u
        emulator.programCounter = 0x0002u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0x0004u // Skip the next instruction
    }

    test("LD_I") {
        val instruction = Instruction.decode(0xABCDu)
        instruction.shouldBeTypeOf<LD_I>()
        instruction.address(instruction.value) shouldBe 0xBCDu

        val emulator = Emulator()
        emulator.indexRegister shouldBe 0x0000u
        instruction.execute(emulator)
        emulator.indexRegister shouldBe 0xBCDu
    }

    test("JP_V0") {
        val instruction = Instruction.decode(0xBBCDu)
        instruction.shouldBeTypeOf<JP_V0>()
        instruction.address(instruction.value) shouldBe 0xBCDu

        val emulator = Emulator()
        emulator.registers[V0] = 0x01u
        emulator.programCounter shouldBe 0x0000u
        instruction.execute(emulator)
        emulator.programCounter shouldBe 0xBCEu // 0xBCDu + 0x001u
    }

    test("RND_VX") {
        val instruction = Instruction.decode(0xCABCu)
        instruction.shouldBeTypeOf<RND_VX>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0xBCu

        val emulator = Emulator()
        emulator.registers[VA] shouldBe 0x00u
        instruction.execute(emulator)
        emulator.registers[VA] shouldNotBe 0x00u
        // TODO: Properly test randomness?
    }

    test("DRW_VX_VY") {
        // TODO: Implement DRW_VX_VY
    }

    test("SKP_VX") {
        // TODO: Implement SKP_VX
    }

    test("SKNP_VX") {
        // TODO: Implement SKNP_VX
    }

    test("LD_VX_DT") {
        val instruction = Instruction.decode(0xFA07u)
        instruction.shouldBeTypeOf<LD_VX_DT>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.registers[VA] shouldBe 0x00u
        emulator.delayTimer = 0x21u
        instruction.execute(emulator)
        emulator.registers[VA] shouldBe 0x21u
    }

    test("LD_VX_K") {
        // TODO: Implement LD_VX_K
    }

    test("LD_DT_VX") {
        val instruction = Instruction.decode(0xFA15u)
        instruction.shouldBeTypeOf<LD_DT_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.registers[VA] = 0x21u
        emulator.delayTimer shouldBe 0x00u
        instruction.execute(emulator)
        emulator.delayTimer shouldBe 0x21u
    }

    test("LD_ST_VX") {
        val instruction = Instruction.decode(0xFA18u)
        instruction.shouldBeTypeOf<LD_ST_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.registers[VA] = 0x21u
        emulator.soundTimer shouldBe 0x00u
        instruction.execute(emulator)
        emulator.soundTimer shouldBe 0x21u
    }
})