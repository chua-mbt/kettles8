package org.akaii.kettles8.emulator.input

class Keypad {
    companion object {
        enum class Key(val value: UByte, val text: String) {
            K0(0u, "0"), K1(1u, "1"), K2(2u, "2"), K3(3u, "3"),
            K4(4u, "4"), K5(5u, "5"), K6(6u, "6"), K7(7u, "7"),
            K8(8u, "8"), K9(9u, "9"), KA(10u, "A"), KB(11u, "B"),
            KC(12u, "C"), KD(13u, "D"), KE(14u, "E"), KF(15u, "F");

            companion object {
                fun ofValue(value: UByte): Key =
                    Key.entries[value.toInt()]
            }
        }
    }

    private val underlying: BooleanArray = BooleanArray(Key.entries.size)

    operator fun get(key: Key): Boolean = underlying[key.value.toInt()]
    operator fun set(key: Key, value: Boolean) {
        underlying[key.value.toInt()] = value
    }

    operator fun get(key: UByte): Boolean = get(Key.ofValue(key))
    operator fun set(key: UByte, value: Boolean) {
        set(Key.ofValue(key), value)
    }
}