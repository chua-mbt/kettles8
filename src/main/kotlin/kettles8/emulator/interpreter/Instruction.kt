package kettles8.emulator.interpreter

/**
 * Represents an instruction in the CHIP-8 instruction set.
 *
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#3.1
 */
sealed class Instruction(value: UShort) {
    companion object {
        fun decode(value: UShort): Instruction = when (OpCode.decode(value)) {
            OpCode.CLS -> CLS
            OpCode.RET -> RET
            OpCode.SYS -> SYS(value)
            OpCode.JP -> JP(value)
            OpCode.CALL -> CALL(value)
            OpCode.SE_VX_BYTE -> SE_VX_BYTE(value)
            OpCode.SNE_VX_BYTE -> SNE_VX_BYTE(value)
            OpCode.SE_VX_VY -> SE_VX_VY(value)
            OpCode.LD_VX_BYTE -> LD_VX_BYTE(value)
            OpCode.ADD_VX_BYTE -> ADD_VX_BYTE(value)
            OpCode.OR_VX_VY -> OR_VX_VY(value)
            OpCode.AND_VX_VY -> AND_VX_VY(value)
            OpCode.XOR_VX_VY -> XOR_VX_VY(value)
            OpCode.ADD_VX_VY -> ADD_VX_VY(value)
            OpCode.SUB_VX_VY -> SUB_VX_VY(value)
            OpCode.SHR_VX_VY -> SHR_VX_VY(value)
            OpCode.SUBN_VX_VY -> SUBN_VX_VY(value)
            OpCode.SHL_VX_VY -> SHL_VX_VY(value)
            OpCode.LD_VX_VY -> LD_VX_VY(value)
            OpCode.SNE_VX_VY -> SNE_VX_VY(value)
            OpCode.LD_I -> LD_I(value)
            OpCode.JP_V0 -> JP_V0(value)
            OpCode.RND_VX -> RND_VX(value)
            OpCode.DRW_VX_VY -> DRW_VX_VY(value)
            OpCode.SKP_VX -> SKP_VX(value)
            OpCode.SKNP_VX -> SKNP_VX(value)
            OpCode.LD_VX_DT -> LD_VX_DT(value)
            OpCode.LD_VX_K -> LD_VX_K(value)
            OpCode.LD_DT_VX -> LD_DT_VX(value)
            OpCode.LD_ST_VX -> LD_ST_VX(value)
            OpCode.ADD_I_VX -> ADD_I_VX(value)
            OpCode.LD_F_VX -> LD_F_VX(value)
            OpCode.LD_B_VX -> LD_B_VX(value)
            OpCode.LD_I_VX -> LD_I_VX(value)
            OpCode.LD_VX_I -> LD_VX_I(value)
        }
    }

    fun describe(): Unit =
        println(javaClass.simpleName)

    fun execute(): Unit {
        TODO()
    }
}

object CLS : Instruction(0x00E0u)
object RET : Instruction(0x00EEu)
class SYS(val value: UShort) : Instruction(value)
class JP(val value: UShort) : Instruction(value)
class CALL(val value: UShort) : Instruction(value)
class SE_VX_BYTE(val value: UShort) : Instruction(value)
class SNE_VX_BYTE(val value: UShort) : Instruction(value)
class SE_VX_VY(val value: UShort) : Instruction(value)
class LD_VX_BYTE(val value: UShort) : Instruction(value)
class ADD_VX_BYTE(val value: UShort) : Instruction(value)
class OR_VX_VY(val value: UShort) : Instruction(value)
class AND_VX_VY(val value: UShort) : Instruction(value)
class XOR_VX_VY(val value: UShort) : Instruction(value)
class ADD_VX_VY(val value: UShort) : Instruction(value)
class SUB_VX_VY(val value: UShort) : Instruction(value)
class SHR_VX_VY(val value: UShort) : Instruction(value)
class SUBN_VX_VY(val value: UShort) : Instruction(value)
class SHL_VX_VY(val value: UShort) : Instruction(value)
class LD_VX_VY(val value: UShort) : Instruction(value)
class SNE_VX_VY(val value: UShort) : Instruction(value)
class LD_I(val value: UShort) : Instruction(value)
class JP_V0(val value: UShort) : Instruction(value)
class RND_VX(val value: UShort) : Instruction(value)
class DRW_VX_VY(val value: UShort) : Instruction(value)
class SKP_VX(val value: UShort) : Instruction(value)
class SKNP_VX(val value: UShort) : Instruction(value)
class LD_VX_DT(val value: UShort) : Instruction(value)
class LD_VX_K(val value: UShort) : Instruction(value)
class LD_DT_VX(val value: UShort) : Instruction(value)
class LD_ST_VX(val value: UShort) : Instruction(value)
class ADD_I_VX(val value: UShort) : Instruction(value)
class LD_F_VX(val value: UShort) : Instruction(value)
class LD_B_VX(val value: UShort) : Instruction(value)
class LD_I_VX(val value: UShort) : Instruction(value)
class LD_VX_I(val value: UShort) : Instruction(value)