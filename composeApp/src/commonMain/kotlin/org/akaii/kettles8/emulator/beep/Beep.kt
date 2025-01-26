package org.akaii.kettles8.emulator.beep

interface Beep {
    fun start()
    fun stop()
    fun cleanup()
}