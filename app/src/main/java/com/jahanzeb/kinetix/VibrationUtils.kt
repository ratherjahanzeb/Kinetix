package com.jahanzeb.kinetix

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

fun vibrateDevice(context: Context, intensity: Float = 0.5f) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val duration = 15L + (70L * intensity).toLong()
        val amplitude = if (intensity < 0.3f) 50 else if (intensity < 0.7f) 100 else 255

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    } catch (e: Exception) {
        Log.e("VibrationUtils", "Failed to vibrate", e)
    }
}

fun clickWithVibration(context: Context, viewModel: MainViewModel?, action: () -> Unit) {
    if (viewModel?.vibrateEnabled?.value == true) {
        vibrateDevice(context, viewModel.hapticIntensity.value)
    }
    action()
}
