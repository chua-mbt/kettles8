package kettles8.emulator.instructions

import kettles8.emulator.display.DefaultFont
import kettles8.emulator.Emulator
import kettles8.emulator.memory.Address
import kettles8.emulator.memory.Registers.Companion.Register

/**
 * Represents an instruction in the CHIP-8 instruction set.
 *
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#3.1
 */
sealed class Instruction(value: UShort) : Executable {
    companion object {
        const val INSTRUCTION_SIZE: UInt = 2u

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
            OpCode.LD_VX_VY -> LD_VX_VY(value)
            OpCode.OR_VX_VY -> OR_VX_VY(value)
            OpCode.AND_VX_VY -> AND_VX_VY(value)
            OpCode.XOR_VX_VY -> XOR_VX_VY(value)
            OpCode.ADD_VX_VY -> ADD_VX_VY(value)
            OpCode.SUB_VX_VY -> SUB_VX_VY(value)
            OpCode.SHR_VX -> SHR_VX(value)
            OpCode.SUBN_VX_VY -> SUBN_VX_VY(value)
            OpCode.SHL_VX -> SHL_VX(value)
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

    fun describe() = println(description())
}

interface AddressMask {
    private val addressMask: UShort
        get() = 0x0FFFu

    fun address(value: UShort): UShort {
        return (value and addressMask).toUShort()
    }
}

interface VXMask {
    private val vxMask: UShort
        get() = 0x0F00u

    fun vx(value: UShort): Register {
        return Register.fromInt((value and vxMask).toInt() shr 8)!!
    }
}

interface VYMask {
    private val vyMask: UShort
        get() = 0x00F0u

    fun vy(value: UShort): Register {
        return Register.fromInt((value and vyMask).toInt() shr 4)!!
    }
}

interface ByteMask {
    private val byteMask: UShort
        get() = 0x00FFu

    fun byteConst(value: UShort): UByte {
        return (value and byteMask).toUByte()
    }
}

interface Executable {
    fun description(): String = javaClass.simpleName

    fun execute(emulator: Emulator)
}

interface NotImplemented : Executable {
    override fun execute(emulator: Emulator) {
        TODO("Not implemented: ${description()}")
    }
}

/** Clear the display. */
object CLS : Instruction(0x00E0u) {
    override fun execute(emulator: Emulator) {
        emulator.display.clear()
    }
}

/**
 * Return from a subroutine.
 * The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
 */
object RET : Instruction(0x00EEu) {
    override fun execute(emulator: Emulator) {
        emulator.stack.removeLast()
    }
}

/**
 * This instruction is only used on the old computers on which Chip-8 was originally implemented.
 * It is ignored by modern interpreters.
 */
class SYS(val value: UShort) : Instruction(value), NotImplemented

/**
 * Jump to location nnn.
 * The interpreter sets the program counter to nnn.
 */
class JP(val value: UShort) : Instruction(value), AddressMask {
    override fun execute(emulator: Emulator) {
        emulator.programCounter = address(value)
    }
}

/**
 * Call subroutine at nnn.
 * The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
 */
class CALL(val value: UShort) : Instruction(value), AddressMask {
    override fun execute(emulator: Emulator) {
        emulator.stack.addLast(emulator.programCounter)
        emulator.programCounter = address(value)
    }
}

/**
 * Skip next instruction if Vx = kk.
 * The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
 */
class SE_VX_BYTE(val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(emulator: Emulator) {
        if (emulator.registers[vx(value)] == byteConst(value)) {
            emulator.programCounter = (emulator.programCounter + INSTRUCTION_SIZE).toUShort()
        }
    }
}

/**
 * Skip next instruction if Vx != kk.
 * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
 */
class SNE_VX_BYTE(val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(emulator: Emulator) {
        if (emulator.registers[vx(value)] != byteConst(value)) {
            emulator.programCounter = (emulator.programCounter + INSTRUCTION_SIZE).toUShort()
        }
    }
}

/**
 * Skip next instruction if Vx = Vy.
 * The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
 */
class SE_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        if (emulator.registers[vx(value)] == emulator.registers[vy(value)]) {
            emulator.programCounter = (emulator.programCounter + INSTRUCTION_SIZE).toUShort()
        }
    }
}

/**
 * Set Vx = kk.
 * The interpreter puts the value kk into register Vx.
 */
class LD_VX_BYTE(val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = byteConst(value)
    }
}

/**
 * Set Vx = Vx + kk.
 * Adds the value kk to the value of register Vx, then stores the result in Vx.
 */
class ADD_VX_BYTE(val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(emulator: Emulator) {
        val vX = vx(value)
        emulator.registers[vX] = (emulator.registers[vX] + byteConst(value)).toUByte()
    }
}

/**
 * Set Vx = Vy.
 * Stores the value of register Vy in register Vx.
 */
class LD_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = emulator.registers[vy(value)]
    }
}

/**
 * Set Vx = Vx OR Vy.
 * Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx.
 * A bitwise OR compares the corrseponding bits from two values, and if either bit is 1,
 * then the same bit in the result is also 1. Otherwise, it is 0.
 */
class OR_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = emulator.registers[vx(value)] or emulator.registers[vy(value)]
    }
}

/**
 * Set Vx = Vx AND Vy.
 * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx.
 * A bitwise AND compares the corrseponding bits from two values, and if both bits are 1,
 * then the same bit in the result is also 1. Otherwise, it is 0.
 */
class AND_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = emulator.registers[vx(value)] and emulator.registers[vy(value)]
    }
}

/**
 * Set Vx = Vx XOR Vy.
 * Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx.
 * An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then
 * the corresponding bit in the result is set to 1. Otherwise, it is 0.
 */
class XOR_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = emulator.registers[vx(value)] xor emulator.registers[vy(value)]
    }
}

/**
 * Set Vx = Vx + Vy, set VF = carry.
 * The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0.
 * Only the lowest 8 bits of the result are kept, and stored in Vx.
 */
class ADD_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        val vX = vx(value)
        val sum = emulator.registers[vX] + emulator.registers[vy(value)]
        if (sum > 255u) {
            emulator.registers[Register.VF] = 1u
        } else {
            emulator.registers[Register.VF] = 0u
        }
        emulator.registers[vX] = (sum and 0xFFu).toUByte()
    }
}

/**
 * Set Vx = Vx - Vy, set VF = NOT borrow.
 * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
 */
class SUB_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        val vX = vx(value)
        val vY = vy(value)
        if (emulator.registers[vX] > emulator.registers[vY]) {
            emulator.registers[Register.VF] = 1u
        } else {
            emulator.registers[Register.VF] = 0u
        }
        emulator.registers[vX] = (emulator.registers[vX] - emulator.registers[vY]).toUByte()
    }
}

/**
 * Set Vx = Vx SHR 1.
 * If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
 */
class SHR_VX(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        val vX = vx(value)
        emulator.registers[Register.VF] = (emulator.registers[vX] and 0x1u)
        emulator.registers[vX] = (emulator.registers[vX].toUInt() shr 1).toUByte()
    }
}

/**
 * Set Vx = Vy - Vx, set VF = NOT borrow.
 * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
 */
class SUBN_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        val vX = vx(value)
        val vY = vy(value)
        if (emulator.registers[vY] > emulator.registers[vX]) {
            emulator.registers[Register.VF] = 1u
        } else {
            emulator.registers[Register.VF] = 0u
        }
        emulator.registers[vX] = (emulator.registers[vY] - emulator.registers[vX]).toUByte()
    }
}

/**
 * Set Vx = Vx SHL 1.
 * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
 */
class SHL_VX(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        val vX = vx(value)
        val carry = (emulator.registers[vX] and 0x80u).toUInt() shr 7
        emulator.registers[Register.VF] = carry.toUByte()
        emulator.registers[vX] = (emulator.registers[vX].toUInt() shl 1).toUByte()
    }
}

/**
 * Skip next instruction if Vx != Vy.
 * The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
 */
class SNE_VX_VY(val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(emulator: Emulator) {
        if (emulator.registers[vx(value)] != emulator.registers[vy(value)]) {
            emulator.programCounter = (emulator.programCounter + INSTRUCTION_SIZE).toUShort()
        }
    }
}

/**
 * Set I = nnn.
 * The value of register I is set to nnn.
 */
class LD_I(val value: UShort) : Instruction(value), AddressMask {
    override fun execute(emulator: Emulator) {
        emulator.indexRegister = address(value)
    }
}

/**
 * Jump to location nnn + V0.
 * The program counter is set to nnn plus the value of V0.
 */
class JP_V0(val value: UShort) : Instruction(value), AddressMask {
    override fun execute(emulator: Emulator) {
        emulator.programCounter = (address(value) + emulator.registers[Register.V0]).toUShort()
    }
}

/**
 * Set Vx = random byte AND kk.
 * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
 * The results are stored in Vx.
 */
class RND_VX(val value: UShort) : Instruction(value), VXMask, ByteMask {
    val randomRange = 0..255

    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = (byteConst(value) and randomRange.random().toUByte())
    }
}

/**
 * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
 * The interpreter reads n bytes from memory, starting at the address stored in I.
 * These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
 * Sprites are XORed onto the existing screen. If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0.
 * If the sprite is positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the screen.
 * See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the Chip-8 screen and sprites.
 */
class DRW_VX_VY(val value: UShort) : Instruction(value), NotImplemented

/**
 * Skip next instruction if key with the value of Vx is pressed.
 * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2.
 */
class SKP_VX(val value: UShort) : Instruction(value), NotImplemented

/**
 * Skip next instruction if key with the value of Vx is not pressed.
 * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2.
 */
class SKNP_VX(val value: UShort) : Instruction(value), NotImplemented

/**
 * Set Vx = delay timer value.
 * The value of DT is placed into Vx.
 */
class LD_VX_DT(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        emulator.registers[vx(value)] = emulator.delayTimer
    }
}

/**
 * Wait for a key press, store the value of the key in Vx.
 * All execution stops until a key is pressed, then the value of that key is stored in Vx.
 */
class LD_VX_K(val value: UShort) : Instruction(value), NotImplemented

/**
 * Set delay timer = Vx.
 * DT is set equal to the value of Vx.
 */
class LD_DT_VX(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        emulator.delayTimer = emulator.registers[vx(value)]
    }
}

/**
 * Set sound timer = Vx.
 * ST is set equal to the value of Vx.
 */
class LD_ST_VX(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        emulator.soundTimer = emulator.registers[vx(value)]
    }
}

/**
 * Set I = I + Vx.
 * The values of I and Vx are added, and the results are stored in location I.
 */
class ADD_I_VX(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        emulator.indexRegister = (emulator.indexRegister + emulator.registers[vx(value)]).toUShort()
    }
}

/**
 * Set I = location of sprite for digit Vx.
 * The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx.
 */
class LD_F_VX(val value: UShort) : Instruction(value), VXMask {
    override fun execute(emulator: Emulator) {
        emulator.indexRegister = (Address.FONT_START + emulator.registers[vx(value)] * DefaultFont.BYTE_SIZE).toUShort()
    }
}

/**
 * Store BCD representation of Vx in memory locations I, I+1, and I+2.
 * The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
 * the tens digit at location I+1, and the ones digit at location I+2.
 */
class LD_B_VX(val value: UShort) : Instruction(value), NotImplemented

/**
 * Store registers V0 through Vx in memory starting at location I.
 * The interpreter copies the values of registers V0 through Vx into memory, starting at the address in location I.
 */
class LD_I_VX(val value: UShort) : Instruction(value), NotImplemented

/**
 * Read registers V0 through Vx from memory starting at location I.
 * The interpreter reads values from memory starting at location I into registers V0 through Vx.
 */
class LD_VX_I(val value: UShort) : Instruction(value), NotImplemented