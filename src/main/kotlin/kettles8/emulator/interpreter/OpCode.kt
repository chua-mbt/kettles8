package kettles8.emulator.interpreter

/**
 * Represents an opcode pattern in the CHIP-8 instruction set.
 *
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#3.1
 */
enum class OpCode(val pattern: UShort, val isExact: Boolean = false) {
    CLS(0x00E0u, isExact = true),
    RET(0x00EEu, isExact = true),
    SYS(0x0000u),
    JP(0x1000u),
    CALL(0x2000u),
    SE_VX_BYTE(0x3000u),
    SNE_VX_BYTE(0x4000u),
    SE_VX_VY(0x5000u),
    LD_VX_BYTE(0x6000u),
    ADD_VX_BYTE(0x7000u),
    OR_VX_VY(0x8001u),
    AND_VX_VY(0x8002u),
    XOR_VX_VY(0x8003u),
    ADD_VX_VY(0x8004u),
    SUB_VX_VY(0x8005u),
    SHR_VX_VY(0x8006u),
    SUBN_VX_VY(0x8007u),
    SHL_VX_VY(0x800Eu),
    LD_VX_VY(0x8000u),
    SNE_VX_VY(0x9000u),
    LD_I(0xA000u),
    JP_V0(0xB000u),
    RND_VX(0xC000u),
    DRW_VX_VY(0xD000u),
    SKP_VX(0xE09Eu),
    SKNP_VX(0xE0A1u),
    LD_VX_DT(0xF007u),
    LD_VX_K(0xF00Au),
    LD_DT_VX(0xF015u),
    LD_ST_VX(0xF018u),
    ADD_I_VX(0xF01Eu),
    LD_F_VX(0xF029u),
    LD_B_VX(0xF033u),
    LD_I_VX(0xF055u),
    LD_VX_I(0xF065u);

    companion object {
        private val opCodeMask: UShort = 0xF000u

        fun decode(value: UShort): OpCode =
            entries.first {
                it.isExact && value == it.pattern || !it.isExact && value and opCodeMask == it.pattern
            }
    }
}