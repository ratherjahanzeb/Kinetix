package com.example

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.view.InputDevice
import android.view.accessibility.AccessibilityManager

data class CompatibilityStatus(
    val hasHardware: Boolean,
    val hasFingerprintInputDevice: Boolean,
    val inputDeviceNames: List<String>,
    val isAccessibilityEnabled: Boolean,
    val hasAccelerometer: Boolean,
    val hasProximitySensor: Boolean
) {
    val level: StatusLevel
        get() = when {
            hasAccelerometer && hasProximitySensor -> StatusLevel.SUPPORTED
            hasAccelerometer || hasProximitySensor -> StatusLevel.PARTIALLY_SUPPORTED
            else -> StatusLevel.UNSUPPORTED
        }

    enum class StatusLevel {
        SUPPORTED,
        PARTIALLY_SUPPORTED,
        UNSUPPORTED
    }
}

class CompatibilityChecker(private val context: Context) {

    fun checkCompatibility(): CompatibilityStatus {
        val pm = context.packageManager
        val hasHardware = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)

        val deviceIds = InputDevice.getDeviceIds()
        val inputDeviceNames = mutableListOf<String>()
        var hasFingerprintInputDevice = false

        for (id in deviceIds) {
            val device = InputDevice.getDevice(id)
            if (device != null) {
                val name = device.name.lowercase()
                if (name.contains("fingerprint") || name.contains("goodix") || name.contains("fpc") || name.contains("uinput-fpc")) {
                    hasFingerprintInputDevice = true
                }
                inputDeviceNames.add(device.name)
            }
        }

        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val isAccessibilityEnabled = am.isEnabled
        
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val hasAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null

        return CompatibilityStatus(
            hasHardware = hasHardware,
            hasFingerprintInputDevice = hasFingerprintInputDevice,
            inputDeviceNames = inputDeviceNames,
            isAccessibilityEnabled = isAccessibilityEnabled,
            hasAccelerometer = hasAccelerometer,
            hasProximitySensor = hasProximitySensor
        )
    }
}
