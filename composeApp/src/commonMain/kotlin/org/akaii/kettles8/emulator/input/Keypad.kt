package org.akaii.kettles8.emulator.input

class Keypad {
    companion object {
        enum class Key(val value: UByte) {
            K0(0u), K1(1u), K2(2u), K3(3u), K4(4u), K5(5u), K6(6u), K7(7u),
            K8(8u), K9(9u), KA(10u), KB(11u), KC(12u), KD(13u), KE(14u), KF(15u);

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