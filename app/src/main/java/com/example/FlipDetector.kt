package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class FlipDetector(
    private val sensorManager: SensorManager,
    private val onFlip: () -> Unit
) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false
    
    private var wasFaceUp = false
    private var lastFlipTime: Long = 0

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
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
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val z = event.values[2] / SensorManager.GRAVITY_EARTH // Roughly 1.0 when face up, -1.0 face down

        val isFaceUp = z > 0.7f
        val isFaceDown = z < -0.7f

        val now = System.currentTimeMillis()
        
        if (isFaceUp) {
            wasFaceUp = true
        } else if (isFaceDown && wasFaceUp) {
            wasFaceUp = false
            if (now - lastFlipTime > 1500) { // Debounce flips
                lastFlipTime = now
                onFlip()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
