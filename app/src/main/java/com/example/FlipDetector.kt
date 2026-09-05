package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class FlipDetector(
    private val sensorManager: SensorManager,
    private val onFlip: () -> Unit
) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false
    
    private var state = 0 // 0=Waiting for Face Down, 1=Waiting for Face Up
    private var lastFaceDownTime: Long = 0

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            isListening = true
            state = 0
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

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        
        // Device must be relatively stable (not violently shaking or falling)
        if (Math.abs(gForce - SensorManager.GRAVITY_EARTH) > 3.0f) {
            return 
        }

        val isFlat = Math.abs(x / SensorManager.GRAVITY_EARTH) < 0.3f && Math.abs(y / SensorManager.GRAVITY_EARTH) < 0.3f
        val zNormalized = z / SensorManager.GRAVITY_EARTH

        val isFaceUp = isFlat && zNormalized > 0.75f
        val isFaceDown = isFlat && zNormalized < -0.75f

        val now = System.currentTimeMillis()

        if (isFaceDown && state == 0) {
            state = 1
            lastFaceDownTime = now
        } else if (isFaceUp && state == 1) {
            // Must complete flip (face up -> face down -> face up) within 2.5 seconds
            if (now - lastFaceDownTime < 2500) { 
                onFlip()
            }
            state = 0
        } else if (isFaceUp) {
            state = 0 // Reset baseline
        }

        if (state == 1 && now - lastFaceDownTime > 2500) {
            state = 0 // Timeout waiting for face up
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
