package org.akaii.kettles8.emulator.instructions

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.types.*
import org.akaii.kettles8.emulator.Emulator
import org.akaii.kettles8.emulator.memory.Address
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register.*

class InstructionSuite : FunSpec({
    test("CLS") {
        val instruction = Instruction.decode(0x00E0u)
        instruction shouldBe CLS

        val emulator = Emulator()
        emulator.display.fill()
        emulator.display.flattened().shouldContainOnly(UByte.MAX_VALUE)

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.display.flattened().shouldContainOnly(UByte.MIN_VALUE)
    }

    test("RET") {
        val instruction = Instruction.decode(0x00EEu)
        instruction shouldBe RET

        val emulator = Emulator()
        emulator.cpu.stack.addLast(0x0200u)
        emulator.cpu.stack.shouldNotBeEmpty()

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.stack.shouldBeEmpty()
    }

    test("JP") {
        val instruction = Instruction.decode(0x1FF1u)
        val expectedAddress: UShort = 0x0FF1u
        instruction.shouldBeTypeOf<JP>()
        instruction.address(instruction.value) shouldBe expectedAddress

        val emulator = Emulator()
        emulator.cpu.programCounter shouldNotBe expectedAddress

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe expectedAddress
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

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe expectedAddress
        emulator.cpu.stack.shouldContain(originalAddress)
        emulator.cpu.stack.size shouldBe 1
    }

    test("SE_VX_BYTE") {
        val instruction = Instruction.decode(0x3A21u)
        instruction.shouldBeTypeOf<SE_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction
    }

    test("SNE_VX_BYTE") {
        val instruction = Instruction.decode(0x4A21u)
        instruction.shouldBeTypeOf<SNE_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction
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
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction

        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.registers[VB] = 0x21u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip
    }

    test("LD_VX_BYTE") {
        val instruction = Instruction.decode(0x6A21u)
        instruction.shouldBeTypeOf<LD_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x21u
    }

    test("ADD_VX_BYTE") {
        val instruction = Instruction.decode(0x7A21u)
        instruction.shouldBeTypeOf<ADD_VX_BYTE>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0x21u

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x21u

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x42u
    }

    test("LD_VX_VY") {
        val instruction = Instruction.decode(0x8AB0u)
        instruction.shouldBeTypeOf<LD_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        emulator.cpu.registers[VB] = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x21u
    }

    test("OR_VX_VY") {
        val instruction = Instruction.decode(0x8AB1u)
        instruction.shouldBeTypeOf<OR_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        emulator.cpu.registers[VB] = 0b1100u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b1110u
    }

    test("AND_VX_VY") {
        val instruction = Instruction.decode(0x8AB2u)
        instruction.shouldBeTypeOf<AND_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        emulator.cpu.registers[VB] = 0b1100u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b1000u
    }

    test("XOR_VX_VY") {
        val instruction = Instruction.decode(0x8AB3u)
        instruction.shouldBeTypeOf<XOR_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        emulator.cpu.registers[VB] = 0b1100u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b0110u
    }

    test("ADD_VX_VY") {
        val instruction = Instruction.decode(0x8AB4u)
        instruction.shouldBeTypeOf<ADD_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0xFFu
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x00u
        emulator.cpu.registers[VF] shouldBe 0x01u // Overflow

        emulator.cpu.registers[VA] = 0x01u
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x02u
        emulator.cpu.registers[VF] shouldBe 0x00u // No overflow
    }

    test("SUB_VX_VY") {
        val instruction = Instruction.decode(0x8AB5u)
        instruction.shouldBeTypeOf<SUB_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x02u
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x01u
        emulator.cpu.registers[VF] shouldBe 0x01u // Borrow

        emulator.cpu.registers[VA] = 0x01u
        emulator.cpu.registers[VB] = 0x02u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0xFFu
        emulator.cpu.registers[VF] shouldBe 0x00u // No borrow
    }

    test("SHR_VX") {
        val instruction = Instruction.decode(0x8AB6u)
        instruction.shouldBeTypeOf<SHR_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b1010u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b0101u
        emulator.cpu.registers[VF] shouldBe 0u // No carry from LSB

        emulator.cpu.registers[VA] = 0b0101u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b0010u
        emulator.cpu.registers[VF] shouldBe 1u // Carry from LSB
    }

    test("SUBN_VX_VY") {
        val instruction = Instruction.decode(0x8AB7u)
        instruction.shouldBeTypeOf<SUBN_VX_VY>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.vy(instruction.value) shouldBe VB

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x01u
        emulator.cpu.registers[VB] = 0x02u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x01u
        emulator.cpu.registers[VF] shouldBe 0x01u // Borrow

        emulator.cpu.registers[VA] = 0x02u
        emulator.cpu.registers[VB] = 0x01u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0xFFu
        emulator.cpu.registers[VF] shouldBe 0x00u // No borrow
    }

    test("SHL_VX") {
        val instruction = Instruction.decode(0x8ABEu)
        instruction.shouldBeTypeOf<SHL_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0b01010000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b10100000u
        emulator.cpu.registers[VF] shouldBe 0u // No carry from MSB

        emulator.cpu.registers[VA] = 0b10100000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0b01000000u
        emulator.cpu.registers[VF] shouldBe 1u // Carry from MSB
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
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0002u // No skip

        emulator.cpu.registers[VA] = 0x20u
        emulator.cpu.registers[VB] = 0x21u
        emulator.cpu.programCounter = 0x0002u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0004u // Skip the next instruction
    }

    test("LD_I") {
        val instruction = Instruction.decode(0xABCDu)
        instruction.shouldBeTypeOf<LD_I>()
        instruction.address(instruction.value) shouldBe 0xBCDu

        val emulator = Emulator()
        emulator.cpu.indexRegister shouldBe 0x0000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.indexRegister shouldBe 0x0BCDu
    }

    test("JP_V0") {
        val instruction = Instruction.decode(0xBBCDu)
        instruction.shouldBeTypeOf<JP_V0>()
        instruction.address(instruction.value) shouldBe 0x0BCDu

        val emulator = Emulator()
        emulator.cpu.registers[V0] = 0x01u
        emulator.cpu.programCounter shouldBe 0x0000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.programCounter shouldBe 0x0BCEu // 0xBCDu + 0x001u
    }

    test("RND_VX") {
        val instruction = Instruction.decode(0xCABCu)
        instruction.shouldBeTypeOf<RND_VX>()
        instruction.vx(instruction.value) shouldBe VA
        instruction.byteConst(instruction.value) shouldBe 0xBCu

        val emulator = Emulator()
        emulator.cpu.registers[VA] shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldNotBe 0x00u
        // TODO: Properly test randomness?
    }

    test("DRW_VX_VY") {
        val instruction = Instruction.decode(0xD123u)
        instruction.shouldBeTypeOf<DRW_VX_VY>()

        val emulator = Emulator()

        emulator.memory[0x200u.toUByte()] = 0b11100000u
        emulator.memory[0x201u.toUByte()] = 0b10100000u
        emulator.memory[0x202u.toUByte()] = 0b11100000u

        emulator.cpu.registers[V1] = 5u
        emulator.cpu.registers[V2] = 10u
        emulator.cpu.indexRegister = 0x200u

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)

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
    }

    test("DRW_VX_VY - Wrapping") {
        val instruction = Instruction.decode(0xD123u)
        instruction.shouldBeTypeOf<DRW_VX_VY>()

        val emulator = Emulator()

        emulator.memory[0x200u.toUByte()] = 0b11100000u
        emulator.memory[0x201u.toUByte()] = 0b10100000u
        emulator.memory[0x202u.toUByte()] = 0b11100000u

        emulator.cpu.registers[V1] = 62u
        emulator.cpu.registers[V2] = 30u
        emulator.cpu.indexRegister = 0x200u

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)

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
    }

    test("DRW_VX_VY - Erasure") {
        val instruction = Instruction.decode(0xD123u)
        instruction.shouldBeTypeOf<DRW_VX_VY>()

        val emulator = Emulator()

        emulator.memory[0x200u.toUByte()] = 0b11100000u
        emulator.memory[0x201u.toUByte()] = 0b10100000u
        emulator.memory[0x202u.toUByte()] = 0b11100000u

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

        instruction.execute(emulator.cpu, emulator.memory, emulator.display)

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
        emulator.cpu.registers[VA] shouldBe 0x00u
        emulator.cpu.delayTimer = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers[VA] shouldBe 0x21u
    }

    test("LD_VX_K") {
        // TODO: Implement LD_VX_K
    }

    test("LD_DT_VX") {
        val instruction = Instruction.decode(0xFA15u)
        instruction.shouldBeTypeOf<LD_DT_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.delayTimer shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.delayTimer shouldBe 0x21u
    }

    test("LD_ST_VX") {
        val instruction = Instruction.decode(0xFA18u)
        instruction.shouldBeTypeOf<LD_ST_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.soundTimer shouldBe 0x00u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.soundTimer shouldBe 0x21u
    }

    test("ADD_I_VX") {
        val instruction = Instruction.decode(0xFA1Eu)
        instruction.shouldBeTypeOf<ADD_I_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x21u
        emulator.cpu.indexRegister = 0x21u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.indexRegister shouldBe 0x42u
    }

    test("LD_F_VX") {
        val instruction = Instruction.decode(0xFA29u)
        instruction.shouldBeTypeOf<LD_F_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 0x02u
        emulator.cpu.indexRegister shouldBe 0x0000u
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        val expectedFontAddress = Address.FONT_START + 0x000Au // 2x5 = 10
        emulator.cpu.indexRegister shouldBe expectedFontAddress.toUShort()
    }

    test("LD_B_VX") {
        val instruction = Instruction.decode(0xFA33u)
        instruction.shouldBeTypeOf<LD_B_VX>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        emulator.cpu.registers[VA] = 123u
        emulator.cpu.indexRegister = 0x000Au
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.memory[10u.toUByte()] shouldBe 1u
        emulator.memory[11u.toUByte()] shouldBe 2u
        emulator.memory[12u.toUByte()] shouldBe 3u
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
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.memory.slice(10..25) shouldBe emulator.cpu.registers.toList().toUByteArray()
    }

    test("LD_VX_I") {
        val instruction = Instruction.decode(0xFA65u)
        instruction.shouldBeTypeOf<LD_VX_I>()
        instruction.vx(instruction.value) shouldBe VA

        val emulator = Emulator()
        for (vx in V0..VA) {
            emulator.memory[(10 + vx.value).toUByte()] = vx.value.toUByte()
        }
        emulator.cpu.indexRegister = 0x000Au
        emulator.cpu.registers.toList() shouldContainOnly (listOf(0u))
        instruction.execute(emulator.cpu, emulator.memory, emulator.display)
        emulator.cpu.registers.toList().toUByteArray() shouldBe emulator.memory.slice(10..25)
    }

})