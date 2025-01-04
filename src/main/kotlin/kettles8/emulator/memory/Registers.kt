package kettles8.emulator.memory

class Registers {

    companion object {
        enum class Register(val value: Int) {
            V0(0), V1(1), V2(2), V3(3), V4(4), V5(5), V6(6), V7(7),
            V8(8), V9(9), VA(10), VB(11), VC(12), VD(13), VE(14), VF(15);

            companion object {
                private val map = entries.associateBy(Register::value)
                fun fromInt(type: Int) = map[type]
            }
        }
    }

    private val underlying: UByteArray = UByteArray(Register.entries.size)

    operator fun get(register: Register): UByte = underlying[register.value]
    operator fun set(register: Register, value: UByte) {
        underlying[register.value] = value
    }
}