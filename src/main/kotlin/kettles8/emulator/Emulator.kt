package kettles8.emulator

import kettles8.emulator.display.Display
import kettles8.emulator.memory.Memory
import kettles8.emulator.memory.Registers

class Emulator(rom: UByteArray = UByteArray(0)) {
    companion object {
        private const val STACK_SIZE = 16

        enum class InputKeys(val value: UByte) {
            K0(0u), K1(1u), K2(2u), K3(3u), K4(4u), K5(5u), K6(6u), K7(7u),
            K8(8u), K9(9u), KA(10u), KB(11u), KC(12u), KD(13u), KE(14u), KF(15u)
        }
    }

    val memory: Memory = Memory()
    val registers: Registers = Registers()
    val display: Display = Display()

    val stack: ArrayDeque<UShort> = ArrayDeque(STACK_SIZE)

    /** Program Counter (PC) */
    var programCounter: UShort = 0u

    /** Index Register (I) */
    var indexRegister: UShort = 0u

    /** Delay Timer (DT) */
    var delayTimer: UByte = 0u

    /** Sound Timer (ST) */
    var soundTimer: UByte = 0u

    init {
        memory.setFromROM(rom)
    }
}