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
    
    // Sensitivity: 0=Low, 1=Medium, 2=High
    var sensitivityLevel: Int = 1 

    private var lastShakeTime: Long = 0
    private var shakeCount: Int = 0
    private var lastSpikeTime: Long = 0

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            shakeCount = 0
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

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        val threshold = when (sensitivityLevel) {
            0 -> 2.7f // Low (hard shake)
            2 -> 1.8f // High (light shake)
            else -> 2.2f // Medium
        }

        val now = System.currentTimeMillis()

        if (gForce > threshold) {
            if (now - lastSpikeTime > 100) {
                lastSpikeTime = now
                shakeCount++
                
                if (shakeCount >= 3) { // Require 3 spikes for a shake
                    if (now - lastShakeTime > 1000) {
                        lastShakeTime = now
                        onShake()
                    }
                    shakeCount = 0
                }
            }
        } else {
            // Reset shake count if too much time has passed without a spike
            if (now - lastSpikeTime > 500) {
                shakeCount = 0
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
