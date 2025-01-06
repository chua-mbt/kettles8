package org.akaii.kettles8.emulator.memory

class Registers {

    companion object {
        sealed class Register(val value: Int) {

            object V0 : Register(0)
            object V1 : Register(1)
            object V2 : Register(2)
            object V3 : Register(3)
            object V4 : Register(4)
            object V5 : Register(5)
            object V6 : Register(6)
            object V7 : Register(7)
            object V8 : Register(8)
            object V9 : Register(9)
            object VA : Register(10)
            object VB : Register(11)
            object VC : Register(12)
            object VD : Register(13)
            object VE : Register(14)
            object VF : Register(15)

            companion object {
                val entries = listOf(V0, V1, V2, V3, V4, V5, V6, V7, V8, V9, VA, VB, VC, VD, VE, VF)

                // Map of value to corresponding Register object
                private val map = entries.associateBy(Register::value)

                // Function to get the corresponding Register from an integer value
                fun fromInt(value: Int): Register? = map[value]
            }

            // Function to create a range from this register to another
            operator fun rangeTo(register: Register): Iterable<Register> =
                object : Iterable<Register> {
                    override fun iterator(): Iterator<Register> {
                        val startIdx = this@Register.value
                        val endIdx = register.value
                        return (startIdx..endIdx).mapNotNull { fromInt(it) }.iterator()
                    }
                }
        }
    }

    private val underlying: UByteArray = UByteArray(Register.entries.size)

    operator fun get(register: Register): UByte = underlying[register.value]
    operator fun set(register: Register, value: UByte) {
        underlying[register.value] = value
    }

    fun toList(): List<UByte> = underlying.toList()
}