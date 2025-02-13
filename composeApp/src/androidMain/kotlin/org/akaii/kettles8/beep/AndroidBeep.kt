package org.akaii.kettles8.beep

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import org.akaii.kettles8.emulator.beep.Beep
import kotlin.concurrent.thread

class AndroidBeep : Beep {
    private var audioTrack: AudioTrack? = null
    private var playing = false
    private val beepData: ByteArray

    init {
        val sampleRate = 44100f
        val frequency = 2000.0
        val lengthS = 16.0 / 1000.0

        val samples = (sampleRate * lengthS).toInt()
        beepData = ByteArray(samples) { i ->
            val amplitude = 4
            val halfPeriod = (sampleRate / frequency).toInt()
            val value = if (i % halfPeriod < halfPeriod / 2) amplitude else -amplitude
            value.toByte()
        }

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate.toInt(),
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT
        )

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate.toInt(),
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT,
            minBufferSize,
            AudioTrack.MODE_STREAM
        )
    }

    override fun start() {
        if (playing) return
        audioTrack?.play()
        playing = true
        thread {
            while (playing) {
                audioTrack?.write(beepData, 0, beepData.size)
            }
        }
    }

    override fun stop() {
        playing = false
        audioTrack?.stop()
    }

    override fun cleanup() {
        stop()
        audioTrack?.release()
    }
}
