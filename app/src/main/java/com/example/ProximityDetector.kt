package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ProximityDetector(
    private val sensorManager: SensorManager,
    private val onWave: () -> Unit
) : SensorEventListener {
    private var proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    private var isListening = false
    
    private var lastNearTime: Long = 0
    private var wasNear = false

    fun start() {
        if (!isListening && proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            isListening = true
        }
    }

    fun stop() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_PROXIMITY) return

        val distance = event.values[0]
        val maxRange = proximitySensor?.maximumRange ?: 5f
        
        // Treat as "near" if distance is less than max range (or typical 5cm threshold)
        val isNear = distance < maxRange && distance < 5f

        val now = System.currentTimeMillis()
        if (isNear && !wasNear) {
            lastNearTime = now
            wasNear = true
        } else if (!isNear && wasNear) {
            wasNear = false
            // If the hand was removed within 1 second, count it as a wave
            if (now - lastNearTime < 1000) {
                onWave()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
