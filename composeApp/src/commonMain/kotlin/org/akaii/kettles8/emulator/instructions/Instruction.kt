package org.akaii.kettles8.emulator.instructions

import org.akaii.kettles8.emulator.cpu.Cpu
import org.akaii.kettles8.emulator.display.*
import org.akaii.kettles8.emulator.format.Hex.UI16_FORMAT
import org.akaii.kettles8.emulator.format.Hex.UI8_FORMAT
import org.akaii.kettles8.emulator.input.Keypad
import org.akaii.kettles8.emulator.memory.*
import org.akaii.kettles8.emulator.memory.Registers.Companion.Register

/**
 * Represents an instruction in the CHIP-8 instruction set.
 *
 * http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#3.1
 */
sealed class Instruction(override val value: UShort) : HasValue, Executable {

    override fun description(): String = "[${value.toHexString(UI16_FORMAT)}] : ${super.description()}"

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
}

interface HasValue {
    val value: UShort
}

interface AddressMask : HasValue {
    private val addressMask: UShort
        get() = 0x0FFFu

    fun address(value: UShort): UShort {
        return (value and addressMask).toUShort()
    }

    val target: UShort
        get() = address(value)
}

interface VXMask : HasValue {
    private val vxMask: UShort
        get() = 0x0F00u

    fun vx(value: UShort): Register {
        return Register.fromInt((value and vxMask).toInt() shr 8)!!
    }

    val vX: Register
        get() = vx(value)
}

interface VYMask : HasValue {
    private val vyMask: UShort
        get() = 0x00F0u

    fun vy(value: UShort): Register {
        return Register.fromInt((value and vyMask).toInt() shr 4)!!
    }

    val vY: Register
        get() = vy(value)
}

interface NMask : HasValue {
    private val nMask: UShort
        get() = 0x000Fu

    fun n(value: UShort): UByte {
        return (value and nMask).toUByte()
    }

    val nC: UByte
        get() = n(value)
}

interface ByteMask : HasValue {
    private val byteMask: UShort
        get() = 0x00FFu

    fun byteConst(value: UShort): UByte {
        return (value and byteMask).toUByte()
    }

    val byteC: UByte
        get() = byteConst(value)
}

interface Executable {
    fun description(): String = javaClass.simpleName

    fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad)
}

interface NotImplemented : Executable {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        TODO("Not implemented: ${description()}")
    }
}

/** Clear the display. */
object CLS : Instruction(0x00E0u) {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        display.clear()
    }
}

/**
 * Return from a subroutine.
 * The interpreter sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
 */
object RET : Instruction(0x00EEu) {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.programCounter = cpu.stack.removeLast()
    }
}

/**
 * This instruction is only used on the old computers on which Chip-8 was originally implemented.
 * It is ignored by modern interpreters.
 */
class SYS(override val value: UShort) : Instruction(value), NotImplemented

/**
 * Jump to location nnn.
 * The interpreter sets the program counter to nnn.
 */
class JP(override val value: UShort) : Instruction(value), AddressMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.programCounter = target
    }

    override fun description(): String =
        "${super.description()} ${target.toHexString(UI16_FORMAT)}"
}

/**
 * Call subroutine at nnn.
 * The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
 */
class CALL(override val value: UShort) : Instruction(value), AddressMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.stack.addLast(cpu.programCounter)
        cpu.programCounter = address(value)
    }

    override fun description(): String =
        "${super.description()} ${target.toHexString(UI16_FORMAT)}"
}

/**
 * Skip next instruction if Vx = kk.
 * The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2.
 */
class SE_VX_BYTE(override val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (cpu.registers[vX] == byteC) {
            cpu.advanceProgram()
        }
    }

    override fun description(): String =
        "${super.description()} $vX, ${byteC.toHexString(UI8_FORMAT)}"
}

/**
 * Skip next instruction if Vx != kk.
 * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
 */
class SNE_VX_BYTE(override val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (cpu.registers[vX] != byteC) {
            cpu.advanceProgram()
        }
    }

    override fun description(): String =
        "${super.description()} $vX, ${byteC.toHexString(UI8_FORMAT)}"
}

/**
 * Skip next instruction if Vx = Vy.
 * The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
 */
class SE_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (cpu.registers[vX] == cpu.registers[vY]) {
            cpu.advanceProgram()
        }
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = kk.
 * The interpreter puts the value kk into register Vx.
 */
class LD_VX_BYTE(override val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = byteC
    }

    override fun description(): String =
        "${super.description()} $vX, ${byteC.toHexString(UI8_FORMAT)}"
}

/**
 * Set Vx = Vx + kk.
 * Adds the value kk to the value of register Vx, then stores the result in Vx.
 */
class ADD_VX_BYTE(override val value: UShort) : Instruction(value), VXMask, ByteMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = (cpu.registers[vX] + byteC).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX, ${byteC.toHexString(UI8_FORMAT)}"
}

/**
 * Set Vx = Vy.
 * Stores the value of register Vy in register Vx.
 */
class LD_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = cpu.registers[vY]
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx OR Vy.
 * Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx.
 * A bitwise OR compares the corresponding bits from two values, and if either bit is 1,
 * then the same bit in the result is also 1. Otherwise, it is 0.
 */
class OR_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vx(value)] = cpu.registers[vx(value)] or cpu.registers[vy(value)]
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx AND Vy.
 * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx.
 * A bitwise AND compares the corrseponding bits from two values, and if both bits are 1,
 * then the same bit in the result is also 1. Otherwise, it is 0.
 */
class AND_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = cpu.registers[vX] and cpu.registers[vY]
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx XOR Vy.
 * Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx.
 * An exclusive OR compares the corrseponding bits from two values, and if the bits are not both the same, then
 * the corresponding bit in the result is set to 1. Otherwise, it is 0.
 */
class XOR_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = cpu.registers[vX] xor cpu.registers[vY]
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx + Vy, set VF = carry.
 * The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0.
 * Only the lowest 8 bits of the result are kept, and stored in Vx.
 */
class ADD_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        val sum = cpu.registers[vX] + cpu.registers[vY]
        if (sum > 255u) {
            cpu.registers[Register.VF] = 1u
        } else {
            cpu.registers[Register.VF] = 0u
        }
        cpu.registers[vX] = (sum and 0xFFu).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx - Vy, set VF = NOT borrow.
 * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
 */
class SUB_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (cpu.registers[vX] > cpu.registers[vY]) {
            cpu.registers[Register.VF] = 1u
        } else {
            cpu.registers[Register.VF] = 0u
        }
        cpu.registers[vX] = (cpu.registers[vX] - cpu.registers[vY]).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx SHR 1.
 * If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
 */
class SHR_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[Register.VF] = (cpu.registers[vX] and 0x1u)
        cpu.registers[vX] = (cpu.registers[vX].toUInt() shr 1).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Set Vx = Vy - Vx, set VF = NOT borrow.
 * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
 */
class SUBN_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (cpu.registers[vY] > cpu.registers[vX]) {
            cpu.registers[Register.VF] = 1u
        } else {
            cpu.registers[Register.VF] = 0u
        }
        cpu.registers[vX] = (cpu.registers[vY] - cpu.registers[vX]).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set Vx = Vx SHL 1.
 * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
 */
class SHL_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        val vX = vx(value)
        val carry = (cpu.registers[vX] and 0x80u).toUInt() shr 7
        cpu.registers[Register.VF] = carry.toUByte()
        cpu.registers[vX] = (cpu.registers[vX].toUInt() shl 1).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Skip next instruction if Vx != Vy.
 * The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
 */
class SNE_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (cpu.registers[vX] != cpu.registers[vY]) {
            cpu.advanceProgram()
        }
    }

    override fun description(): String =
        "${super.description()} $vX, $vY"
}

/**
 * Set I = nnn.
 * The value of register I is set to nnn.
 */
class LD_I(override val value: UShort) : Instruction(value), AddressMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.indexRegister = target
    }

    override fun description(): String =
        "${super.description()} ${target.toHexString(UI16_FORMAT)}"
}

/**
 * Jump to location nnn + V0.
 * The program counter is set to nnn plus the value of V0.
 */
class JP_V0(override val value: UShort) : Instruction(value), AddressMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.programCounter = (target + cpu.registers[Register.V0]).toUShort()
    }

    override fun description(): String =
        "${super.description()} ${target.toHexString(UI16_FORMAT)}"
}

/**
 * Set Vx = random byte AND kk.
 * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
 * The results are stored in Vx.
 */
class RND_VX(override val value: UShort) : Instruction(value), VXMask, ByteMask {
    val randomRange = 0..255

    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = (byteC and randomRange.random().toUByte())
    }

    override fun description(): String =
        "${super.description()} $vX, ${byteC.toHexString(UI8_FORMAT)}"
}

/**
 * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
 * The interpreter reads n bytes from memory, starting at the address stored in I.
 * These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
 * Sprites are XORed onto the existing screen. If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0.
 * If the sprite is positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the screen.
 * See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the Chip-8 screen and sprites.
 */
class DRW_VX_VY(override val value: UShort) : Instruction(value), VXMask, VYMask, NMask {
    fun screenWrap(coordinate: UInt, border: Int): UInt = coordinate.mod(border.toUInt())

    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        val startX = cpu.registers[vX]
        val startY = cpu.registers[vY]
        val memStart = cpu.indexRegister.toUInt()
        val memEnd = memStart + nC
        for (spriteByteAddress in memStart..<memEnd) {
            val offSetY = spriteByteAddress - memStart
            val displayY = screenWrap(startY + offSetY, Display.DISPLAY_HEIGHT)
            val spriteByte = memory[spriteByteAddress]
            for (offsetX in 0..<8) {
                val displayX = screenWrap(startX + offsetX.toUInt(), Display.DISPLAY_WIDTH)
                val spriteBit = Display.normalize(spriteByte and (0x80u shr offsetX).toUByte())
                val displayPixel = display[displayX.toUByte(), displayY.toUByte()]
                display[displayX.toUByte(), displayY.toUByte()] = displayPixel xor spriteBit
                if (Display.isOn(displayPixel)) cpu.registers[Register.VF] = 1u
            }
        }
    }

    override fun description(): String =
        "${super.description()} $vX, $vY, ${nC.toHexString(UI8_FORMAT)}"
}

/**
 * Skip next instruction if key with the value of Vx is pressed.
 * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position, PC is increased by 2.
 */
class SKP_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (keypad[cpu.registers[vX]]) {
            cpu.advanceProgram()
        }
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Skip next instruction if key with the value of Vx is not pressed.
 * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position, PC is increased by 2.
 */
class SKNP_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        if (!keypad[cpu.registers[vX]]) {
            cpu.advanceProgram()
        }
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Set Vx = delay timer value.
 * The value of DT is placed into Vx.
 */
class LD_VX_DT(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.registers[vX] = cpu.delayTimer
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Wait for a key press, store the value of the key in Vx.
 * All execution stops until a key is pressed, then the value of that key is stored in Vx.
 */
class LD_VX_K(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        keypad.futureInput.onNextKeyReady { cpu.registers[vX] = it.value }
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Set delay timer = Vx.
 * DT is set equal to the value of Vx.
 */
class LD_DT_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.delayTimer = cpu.registers[vX]
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Set sound timer = Vx.
 * ST is set equal to the value of Vx.
 */
class LD_ST_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.soundTimer = cpu.registers[vX]
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Set I = I + Vx.
 * The values of I and Vx are added, and the results are stored in location I.
 */
class ADD_I_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.indexRegister = (cpu.indexRegister + cpu.registers[vX]).toUShort()
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Set I = location of sprite for digit Vx.
 * The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx.
 */
class LD_F_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        cpu.indexRegister = (Address.FONT_START + cpu.registers[vX] * DefaultFont.BYTE_SIZE).toUShort()
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Store BCD representation of Vx in memory locations I, I+1, and I+2.
 * The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
 * the tens digit at location I+1, and the ones digit at location I+2.
 */
class LD_B_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        val vxContents = cpu.registers[vX]
        memory[cpu.indexRegister + 2u] = (vxContents.mod(10u)).toUByte()
        memory[cpu.indexRegister + 1u] = (vxContents.div(10u).mod(10u)).toUByte()
        memory[cpu.indexRegister] = (vxContents.div(100u).mod(10u)).toUByte()
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Store registers V0 through Vx in memory starting at location I.
 * The interpreter copies the values of registers V0 through Vx into memory, starting at the address in location I.
 */
class LD_I_VX(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        val registersToVX = Register.V0..vX
        for (register in registersToVX) {
            val memoryAddress = cpu.indexRegister + register.value.toUShort()
            memory[memoryAddress] = cpu.registers[register]
        }
    }

    override fun description(): String =
        "${super.description()} $vX"
}

/**
 * Read registers V0 through Vx from memory starting at location I.
 * The interpreter reads values from memory starting at location I into registers V0 through Vx.
 */
class LD_VX_I(override val value: UShort) : Instruction(value), VXMask {
    override fun execute(cpu: Cpu, memory: Memory, display: Display, keypad: Keypad) {
        val registersToVX = Register.V0..vX
        for (register in registersToVX) {
            val memoryAddress = cpu.indexRegister + register.value.toUShort()
            cpu.registers[register] = memory[memoryAddress]
        }
    }

    override fun description(): String =
        "${super.description()} $vX"
}