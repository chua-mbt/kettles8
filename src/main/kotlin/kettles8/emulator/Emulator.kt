package kettles8.emulator

import kettles8.emulator.memory.Memory

class Emulator {
    private val stackSize = 16
    private val displayWidth = 64
    private val displayHeight = 32

    private enum class VariableRegister(val value: UByte) {
        V0(0u), V1(1u), V2(2u), V3(3u), V4(4u), V5(5u), V6(6u), V7(7u),
        V8(8u), V9(9u), VA(10u), VB(11u), VC(12u), VD(13u), VE(14u), VF(15u)
    }

    private enum class InputKeys(val value: UByte) {
        K0(0u), K1(1u), K2(2u), K3(3u), K4(4u), K5(5u), K6(6u), K7(7u),
        K8(8u), K9(9u), KA(10u), KB(11u), KC(12u), KD(13u), KE(14u), KF(15u)
    }

    private val memory: Memory = Memory()

    private val display: Array<Array<Boolean>> = Array(displayWidth) { Array(displayHeight) { false } }

    private var programCounter: UShort = 0u

    private var indexRegister: UShort = 0u

    private val stack: ArrayDeque<UShort> = ArrayDeque(stackSize)

    private var delayTimer: UByte = 0u

    private var soundTimer: UByte = 0u

    private val registers: ByteArray = ByteArray(VariableRegister.entries.size)
}