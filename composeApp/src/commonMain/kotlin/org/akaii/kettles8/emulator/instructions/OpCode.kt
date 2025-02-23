package org.akaii.kettles8.emulator.instructions

/**
 * Represents an opcode pattern in the CHIP-8 instruction set.
 *
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#3.1
 */
enum class OpCode(val pattern: UShort, val mask: UShort = 0XF000u) {
    CLS(0x00E0u, mask = 0xFFFFu),
    RET(0x00EEu, mask = 0xFFFFu),
    SYS(0x0000u),
    JP(0x1000u),
    CALL(0x2000u),
    SE_VX_BYTE(0x3000u),
    SNE_VX_BYTE(0x4000u),
    SE_VX_VY(0x5000u, mask = 0xF00Fu),
    LD_VX_BYTE(0x6000u),
    ADD_VX_BYTE(0x7000u),
    LD_VX_VY(0x8000u, mask = 0xF00Fu),
    OR_VX_VY(0x8001u, mask = 0xF00Fu),
    AND_VX_VY(0x8002u, mask = 0xF00Fu),
    XOR_VX_VY(0x8003u, mask = 0xF00Fu),
    ADD_VX_VY(0x8004u, mask = 0xF00Fu),
    SUB_VX_VY(0x8005u, mask = 0xF00Fu),
    SHR_VX(0x8006u, mask = 0xF00Fu),
    SUBN_VX_VY(0x8007u, mask = 0xF00Fu),
    SHL_VX(0x800Eu, mask = 0xF00Fu),
    SNE_VX_VY(0x9000u, mask = 0xF00Fu),
    LD_I(0xA000u),
    JP_V0(0xB000u),
    RND_VX(0xC000u),
    DRW_VX_VY(0xD000u),
    SKP_VX(0xE09Eu, mask = 0xF0FFu),
    SKNP_VX(0xE0A1u, mask = 0xF0FFu),
    LD_VX_DT(0xF007u, mask = 0xF0FFu),
    LD_VX_K(0xF00Au, mask = 0xF0FFu),
    LD_DT_VX(0xF015u, mask = 0xF0FFu),
    LD_ST_VX(0xF018u, mask = 0xF0FFu),
    ADD_I_VX(0xF01Eu, mask = 0xF0FFu),
    LD_F_VX(0xF029u, mask = 0xF0FFu),
    LD_B_VX(0xF033u, mask = 0xF0FFu),
    LD_I_VX(0xF055u, mask = 0xF0FFu),
    LD_VX_I(0xF065u, mask = 0xF0FFu);

    companion object {
        fun decode(value: UShort): OpCode =
            entries.first { (value and it.mask) == it.pattern }
    }
}