package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    private val sensorManager: SensorManager,
    private val onShake: () -> Unit
) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false
    
    var sensitivityLevel: Int = 1 
    var sensorSensitivity: Float = 0.5f
    
    private var gravity = FloatArray(3)
    private val alpha = 0.8f
    
    private var shakeCount: Int = 0
    private var lastShakeTime: Long = 0

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            shakeCount = 0
            gravity = floatArrayOf(0f, 0f, 0f)
        }
    }

    fun stop() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // High-pass filter to remove gravity
        gravity[0] = alpha * gravity[0] + (1f - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1f - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1f - alpha) * event.values[2]

        val x = event.values[0] - gravity[0]
        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]

        val gForce = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val baseThreshold = when (sensitivityLevel) {
            0 -> 14.0f // Low (hard shake)
            2 -> 6.0f // High (light shake)
            else -> 10.0f // Medium
        }
        val threshold = baseThreshold * (1.5f - sensorSensitivity.coerceIn(0.1f, 1.0f))

        val now = System.currentTimeMillis()

        if (gForce > threshold) {
            if (now - lastShakeTime > 150) { // Slop time to ignore same-stroke peaks
                lastShakeTime = now
                shakeCount++
                
                if (shakeCount >= 4) { // Require 4 distinct directional shifts
                    onShake()
                    shakeCount = 0
                }
            }
        } else {
            // Reset if no shakes for 1 second
            if (now - lastShakeTime > 1000 && shakeCount > 0) {
                shakeCount = 0
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
