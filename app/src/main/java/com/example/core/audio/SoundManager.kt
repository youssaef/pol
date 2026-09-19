package com.example.core.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isMuted = false

    private fun playTone(
        frequencies: List<Double>,
        durationsMs: List<Int>,
        sampleRate: Int = 22050,
        waveType: Int = 0 // 0: sine, 1: square
    ) {
        if (isMuted) return
        scope.launch {
            try {
                val totalDurationMs = durationsMs.sum()
                val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                var sampleOffset = 0
                for (i in frequencies.indices) {
                    val freq = frequencies[i]
                    val durationMs = durationsMs[i]
                    val samplesInSegment = (sampleRate * (durationMs / 1000.0)).toInt()

                    for (s in 0 until samplesInSegment) {
                        val t = s.toDouble() / sampleRate
                        val rawSample = if (waveType == 0) {
                            sin(2.0 * Math.PI * freq * t)
                        } else {
                            if (sin(2.0 * Math.PI * freq * t) >= 0) 0.8 else -0.8
                        }
                        // Gentle envelope decay to prevent clicking
                        val envelope = (1.0 - (s.toDouble() / samplesInSegment))
                        val finalSample = (rawSample * envelope * Short.MAX_VALUE * 0.4).toInt()
                        if (sampleOffset + s < buffer.size) {
                            buffer[sampleOffset + s] = finalSample.toShort()
                        }
                    }
                    sampleOffset += samplesInSegment
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Release after playback
                kotlinx.coroutines.delay(totalDurationMs.toLong() + 100)
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    fun playJump() {
        playTone(listOf(220.0, 330.0, 440.0), listOf(40, 50, 70))
    }

    fun playSlide() {
        playTone(listOf(350.0, 200.0, 140.0), listOf(60, 60, 60))
    }

    fun playCoin() {
        playTone(listOf(987.77, 1318.51), listOf(45, 90))
    }

    fun playCorrect() {
        // C - E - G - C major chord chime
        playTone(listOf(523.25, 659.25, 783.99, 1046.50), listOf(70, 70, 70, 150))
    }

    fun playWrong() {
        // Gentle soft buzz
        playTone(listOf(220.0, 196.0), listOf(120, 140))
    }

    fun playPowerUp() {
        playTone(listOf(440.0, 554.37, 659.25, 880.0), listOf(50, 50, 50, 120))
    }

    fun playLevelUp() {
        playTone(listOf(523.25, 659.25, 783.99, 1046.50, 1318.51), listOf(80, 80, 80, 100, 250))
    }

    fun playButton() {
        playTone(listOf(600.0, 800.0), listOf(20, 30))
    }

    fun playHeartLoss() {
        playTone(listOf(300.0, 200.0), listOf(80, 100))
    }
}
