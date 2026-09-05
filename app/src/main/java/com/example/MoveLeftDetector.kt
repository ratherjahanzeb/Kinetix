package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MoveLeftDetector(
    private val sensorManager: SensorManager,
    private val onMoveLeft: () -> Unit
) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false
    
    var sensitivityLevel: Int = 1 
    var sensorSensitivity: Float = 0.5f
    
    private var gravity = FloatArray(3)
    private val alpha = 0.8f
    
    private var moveLeftCount: Int = 0
    private var lastMoveTime: Long = 0
    private var isCoolingDown = false

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            moveLeftCount = 0
            gravity = floatArrayOf(0f, 0f, 0f)
            isCoolingDown = false
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

        val x = event.values[0] - gravity[0]
        
        val threshold = 3.5f * (1.5f - sensorSensitivity.coerceIn(0.1f, 1.0f))
        val isMovingLeft = x < -threshold
        
        val now = System.currentTimeMillis()
        
        if (isMovingLeft) {
            if (!isCoolingDown) {
                moveLeftCount++
                lastMoveTime = now
                isCoolingDown = true
                
                if (moveLeftCount >= 2) {
                    onMoveLeft()
                    moveLeftCount = 0
                    lastMoveTime = now
                }
            }
        } else {
            if (isCoolingDown && x > -1.5f) {
                if (now - lastMoveTime > 300) { 
                    isCoolingDown = false
                }
            }
        }
        
        if (moveLeftCount > 0 && now - lastMoveTime > 1500) {
            moveLeftCount = 0
            isCoolingDown = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
