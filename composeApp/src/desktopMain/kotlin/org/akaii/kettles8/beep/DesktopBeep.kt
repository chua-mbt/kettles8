package org.akaii.kettles8.beep

import org.akaii.kettles8.emulator.beep.Beep
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine
import kotlin.concurrent.thread
import kotlin.math.sin

class DesktopBeep : Beep {
    private var line: SourceDataLine? = null
    private var playing = false
    private val beepData: ByteArray

    init {
        val sampleRate = 44100f
        val frequency = 2000.0
        val lengthS = 16.0 / 1000.0

        val samples = (sampleRate * lengthS).toInt()
        beepData = ByteArray(samples) { i ->
            val amplitude = 4
            //val fullPeriod = 2.0 * Math.PI
            //(amplitude * sin(fullPeriod * frequency * i / sampleRate)).toInt().toByte()
            val halfPeriod = (sampleRate / frequency).toInt()
            val value = if (i % halfPeriod < halfPeriod / 2) amplitude else -amplitude
            value.toByte()
        }

        val format = AudioFormat(sampleRate, 8, 1, true, false)
        line =
            AudioSystem.getLine(javax.sound.sampled.DataLine.Info(SourceDataLine::class.java, format)) as SourceDataLine
        line?.open(format, samples)
    }

    override fun start() {
        if (playing) return
        line?.start()
        playing = true
        thread {
            while (playing) {
                line?.write(beepData, 0, beepData.size)
            }
        }
    }

    override fun stop() {
        playing = false
        line?.stop()
    }

    override fun cleanup() {
        stop()
        line?.close()
    }
}