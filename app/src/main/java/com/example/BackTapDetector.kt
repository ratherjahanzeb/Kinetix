package com.example

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class BackTapDetector(
    private val sensorManager: SensorManager,
    private val onDoubleTap: () -> Unit
) : SensorEventListener {
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var isListening = false
    
    var sensitivityLevel: Int = 1 // 0=Low, 1=Medium, 2=High
    var timeoutMs: Int = 300
    var sensorSensitivity: Float = 0.5f
    
    private var gravity = FloatArray(3)
    private var tapCount = 0
    private var lastTapTime: Long = 0
    private var isInPeak = false
    private val alpha = 0.8f

    fun start() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            isListening = true
            tapCount = 0
            isInPeak = false
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

        // High-pass filter to isolate linear acceleration (removes gravity and tilt)
        gravity[0] = alpha * gravity[0] + (1f - alpha) * event.values[0]
        gravity[1] = alpha * gravity[1] + (1f - alpha) * event.values[1]
        gravity[2] = alpha * gravity[2] + (1f - alpha) * event.values[2]

        val x = event.values[0] - gravity[0]
        val y = event.values[1] - gravity[1]
        val z = event.values[2] - gravity[2]
        
        val baseThreshold = when (sensitivityLevel) {
            0 -> 9.0f // Low sensitivity (needs hard tap)
            2 -> 3.5f // High sensitivity (light tap)
            else -> 6.0f // Medium
        }
        val threshold = baseThreshold * (1.5f - sensorSensitivity.coerceIn(0.1f, 1.0f))
        
        val absX = Math.abs(x)
        val absY = Math.abs(y)
        val absZ = Math.abs(z)

        // A tap primarily affects the Z axis.
        // Pushing the phone forward creates negative Z acceleration and positive Y acceleration
        if ((z < -threshold || y > threshold) && absZ > absX * 1.5f && absZ > absY * 0.5f) {
            if (!isInPeak) {
                isInPeak = true
                val now = System.currentTimeMillis()
                
                // Debounce a single tap (accelerometer rings for ~100-150ms after a tap)
                if (now - lastTapTime > 150) {
                    tapCount++
                    if (tapCount == 1) {
                        lastTapTime = now
                    } else if (tapCount == 2) {
                        val diff = now - lastTapTime
                        if (diff <= timeoutMs) {
                            onDoubleTap()
                        }
                        tapCount = 0
                        lastTapTime = 0
                    }
                }
            }
        } else if (z > threshold * 0.5f || y < -threshold * 0.5f) {
            isInPeak = false
        }

        // Timeout sequence if second tap doesn't arrive in time
        if (tapCount == 1 && System.currentTimeMillis() - lastTapTime > timeoutMs) {
            tapCount = 0
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
