package com.jahanzeb.kinetix

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ProximityDetector(
    private val sensorManager: SensorManager,
    private val onWave: () -> Unit
) : SensorEventListener {
    
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false
    
    private var backwardMoveCount = 0
    private var lastMoveTime = 0L
    private var isCoolingDown = false
    private var accelerationStartTime = 0L
    private var isAccelerating = false
    
    var sensorSensitivity: Float = 0.5f
    
    private var gravity = FloatArray(3)
    private val alpha = 0.8f

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            backwardMoveCount = 0
            gravity = floatArrayOf(0f, 0f, 0f)
            isCoolingDown = false
            isAccelerating = false
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

        gravity[0] = alpha * gravity[0] + (1f - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1f - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1f - alpha) * event.values[2]

        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]
        
        val threshold = 4.0f * (1.5f - sensorSensitivity.coerceIn(0.1f, 1.0f))
        val isMovingBackward = z > threshold || y < -threshold
        
        val now = System.currentTimeMillis()
        
        if (isMovingBackward) {
            if (!isAccelerating) {
                isAccelerating = true
                accelerationStartTime = now
            } else {
                if (now - accelerationStartTime >= 100 && !isCoolingDown) {
                    backwardMoveCount++
                    lastMoveTime = now
                    isCoolingDown = true
                    isAccelerating = false
                    
                    if (backwardMoveCount >= 2) {
                        onWave()
                        backwardMoveCount = 0
                        lastMoveTime = now
                    }
                }
            }
        } else {
            isAccelerating = false
            if (isCoolingDown && z < 1.5f && y > -1.5f) {
                if (now - lastMoveTime > 300) { 
                    isCoolingDown = false
                }
            }
        }
        
        if (backwardMoveCount > 0 && now - lastMoveTime > 1500) {
            backwardMoveCount = 0
            isCoolingDown = false
            isAccelerating = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
