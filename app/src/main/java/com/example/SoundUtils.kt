package com.example

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlin.random.Random

fun playShoooSound() {
    Thread {
        try {
            val sampleRate = 22050
            val durationSec = 0.5f
            val numSamples = (sampleRate * durationSec).toInt()
            val buffer = ShortArray(numSamples)

            // Generate a filtered noise whoosh ("shooo") with volume envelope
            var lastValue = 0.0f
            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                // Envelope: quick attack, smooth exponential decay
                val envelope = kotlin.math.exp(-t * 5.0f) * (t * 4.0f).coerceAtMost(1.0f)
                
                // White noise
                val white = (Random.nextFloat() * 2f - 1f)
                
                // Simple low-pass filter to make it a soft "sh" sound rather than harsh static
                lastValue = lastValue * 0.7f + white * 0.3f
                
                // Modulate frequency/pitch slightly downwards for "whoosh" effect
                val sampleVal = (lastValue * envelope * 12000.toInt()).toInt().coerceIn(-32768, 32767)
                buffer[i] = sampleVal.toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, numSamples)
            audioTrack.play()

            Thread.sleep(600)
            audioTrack.release()
        } catch (e: Exception) {
            Log.e("SoundUtils", "Failed to play sound", e)
        }
    }.start()
}
