package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class BackTapDetector(
    private val sensorManager: SensorManager,
    private val onTap: () -> Unit
) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private var isListening = false
    
    // Configurable
    var sensitivityLevel: Int = 1 // 0=Low, 1=Medium, 2=High
    
    // State
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f
    private var lastEventTime: Long = 0

    fun start() {
        if (!isListening && accelerometer != null) {
            // SENSOR_DELAY_GAME is ~20ms, good for tap detection
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
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

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        
        // Skip first reading
        if (lastZ == 0f && lastX == 0f && lastY == 0f) {
            lastX = x
            lastY = y
            lastZ = z
            return
        }

        val deltaX = Math.abs(lastX - x)
        val deltaY = Math.abs(lastY - y)
        val deltaZ = Math.abs(lastZ - z)
        
        lastX = x
        lastY = y
        lastZ = z
        
        val threshold = when (sensitivityLevel) {
            0 -> 16.0f // Low sensitivity (needs hard tap)
            2 -> 8.0f  // High sensitivity (light tap)
            else -> 12.0f // Medium
        }
        
        // A tap should primarily affect the Z axis.
        // If X or Y change is too large relative to Z, it's likely a shake or flip.
        if (deltaZ > threshold && deltaZ > deltaX * 1.5f && deltaZ > deltaY * 1.5f) {
            val now = System.currentTimeMillis()
            // 150ms debounce for the accelerometer oscillation of a single tap
            if (now - lastEventTime > 150) { 
                lastEventTime = now
                onTap()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
