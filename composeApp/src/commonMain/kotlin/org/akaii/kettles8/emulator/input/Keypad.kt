package org.akaii.kettles8.emulator.input

import androidx.compose.runtime.*

class Keypad {
    companion object {
        enum class Config {
            CLASSIC, QWERT
        }

        enum class Key(val value: UByte, val classic: String, val qwert: String) {
            K0(0u, "0", "X"), K1(1u, "1", "1"), K2(2u, "2", "2"), K3(3u, "3", "3"),
            K4(4u, "4", "Q"), K5(5u, "5", "W"), K6(6u, "6", "E"), K7(7u, "7", "A"),
            K8(8u, "8", "S"), K9(9u, "9", "D"), KA(10u, "A", "Z"), KB(11u, "B", "C"),
            KC(12u, "C", "4"), KD(13u, "D", "R"), KE(14u, "E", "F"), KF(15u, "F", "V");

            companion object {
                fun ofValue(value: UByte): Key =
                    Key.entries[value.toInt()]
            }

            fun text(config: Config): String = when (config) {
                Config.CLASSIC -> classic
                Config.QWERT -> qwert
            }
        }
    }

    private val config: MutableState<Config> = mutableStateOf(Config.CLASSIC)
    private val underlying: BooleanArray = BooleanArray(Key.entries.size)
    private val previous: BooleanArray = BooleanArray(Key.entries.size)

    val configState: State<Config> get() = config
    fun getConfig(): Config = config.value
    fun setConfig(config: Config) {
        this.config.value = config
    }
    val futureInput: FutureInput = FutureInput()

    operator fun get(key: Key): Boolean = underlying[key.value.toInt()]
    operator fun set(key: Key, value: Boolean) {
        previous[key.value.toInt()] = underlying[key.value.toInt()]
        underlying[key.value.toInt()] = value
    }

    operator fun get(key: UByte): Boolean = get(Key.ofValue(key))
    operator fun set(key: UByte, value: Boolean) {
        set(Key.ofValue(key), value)
    }

    fun onDown(key: Key) {
        set(key, true)
    }

    fun onUp(key: Key) {
        set(key, false)
    }

    fun keyReleased(): Key? {
        val justReleased = underlying.zip(previous).zip(underlying.indices).find {
            (pair, _) ->
                val (currently, previously) = pair
                !currently && previously
        }?.second
        return justReleased?.let { Key.ofValue(it.toUByte()) }
    }

    fun reset() {
        underlying.fill(false)
        previous.fill(false)
    }

    fun clearPrevious() {
        for (key in Key.entries) {
            previous[key.value.toInt()] = false
        }
    }

}