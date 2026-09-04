package com.example

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.FingerprintGestureController
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DoubleTapAccessibilityService : AccessibilityService() {
    private var lastTapTime = 0L
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var settingsRepo: SettingsRepository
    private var isFlashlightOn = false
    private var backTapDetector: BackTapDetector? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        settingsRepo = SettingsRepository(this)
        
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                isFlashlightOn = enabled
            }
        }, null)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        backTapDetector = BackTapDetector(sensorManager) {
            handlePotentialTap(TriggerMethod.BACK_PANEL)
        }

        scope.launch {
            settingsRepo.isEnabled.collect { isEnabled ->
                updateForegroundState(isEnabled)
            }
        }

        scope.launch {
            combine(settingsRepo.isEnabled, settingsRepo.backPanelEnabled) { isEnabled, backPanel ->
                Pair(isEnabled, backPanel)
            }.collect { (isEnabled, backPanel) ->
                if (isEnabled && backPanel) {
                    backTapDetector?.start()
                } else {
                    backTapDetector?.stop()
                }
            }
        }

        // Setup FingerprintGestureController callback just in case the device supports swipe gestures natively.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val fpController = fingerprintGestureController
            if (fpController.isGestureDetectionAvailable) {
                fpController.registerFingerprintGestureCallback(object : FingerprintGestureController.FingerprintGestureCallback() {
                    override fun onGestureDetectionAvailabilityChanged(available: Boolean) {}
                    override fun onGestureDetected(gesture: Int) {
                        handlePotentialTap(TriggerMethod.FINGERPRINT)
                    }
                }, null)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used for hardware tap detection
    }

    override fun onInterrupt() {
        // Required override
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode
            
            // Check for Power Button
            if (keyCode == KeyEvent.KEYCODE_POWER) {
                handlePotentialTap(TriggerMethod.POWER_BUTTON)
                return false // Don't consume power button to avoid breaking screen on/off
            }

            // Check for Fingerprint
            val device = event.device
            var isFingerprint = false
            if (device != null) {
                val name = device.name.lowercase()
                if (name.contains("fingerprint") || name.contains("goodix") || name.contains("fpc") || name.contains("uinput-fpc")) {
                    isFingerprint = true
                }
            }
            if (keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN ||
                keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP ||
                keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT ||
                keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT) {
                isFingerprint = true
            }

            if (isFingerprint) {
                handlePotentialTap(TriggerMethod.FINGERPRINT)
                return true // Consume event
            }
        }
        return super.onKeyEvent(event)
    }

    private fun handlePotentialTap(sourceTrigger: TriggerMethod) {
        scope.launch {
            val isEnabled = settingsRepo.isEnabled.first()
            if (!isEnabled) return@launch
            
            val isSourceEnabled = when (sourceTrigger) {
                TriggerMethod.FINGERPRINT -> settingsRepo.fingerprintEnabled.first()
                TriggerMethod.POWER_BUTTON -> settingsRepo.powerButtonEnabled.first()
                TriggerMethod.BACK_PANEL -> settingsRepo.backPanelEnabled.first()
            }
            if (!isSourceEnabled) return@launch

            val timeoutMs = settingsRepo.timeoutMs.first()
            val currentTime = System.currentTimeMillis()
            val diff = currentTime - lastTapTime

            if (diff in 50..timeoutMs) { // 50ms debounce
                lastTapTime = 0L // Reset to prevent triple-tap firing twice
                val vibrateEnabled = settingsRepo.vibrateEnabled.first()
                if (vibrateEnabled) {
                    vibrate()
                }
                val actionToExecute = when (sourceTrigger) {
                    TriggerMethod.FINGERPRINT -> settingsRepo.fingerprintAction.first()
                    TriggerMethod.POWER_BUTTON -> settingsRepo.powerButtonAction.first()
                    TriggerMethod.BACK_PANEL -> settingsRepo.backPanelAction.first()
                }
                settingsRepo.addTriggerLog(
                    TriggerLogEntry(
                        sourceTrigger = sourceTrigger.name,
                        actionName = actionToExecute.name,
                        timestamp = System.currentTimeMillis()
                    )
                )
                executeAction(actionToExecute, sourceTrigger)
            } else {
                lastTapTime = currentTime
            }
        }
    }

    private suspend fun executeAction(action: Action, sourceTrigger: TriggerMethod) {
        when (action) {
            Action.HOME -> performGlobalAction(GLOBAL_ACTION_HOME)
            Action.RECENTS -> performGlobalAction(GLOBAL_ACTION_RECENTS)
            Action.NOTIFICATIONS -> performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
            Action.QUICK_SETTINGS -> performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
            Action.SCREENSHOT -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
                }
            }
            Action.LOCK_SCREEN -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                }
            }
            Action.FLASHLIGHT -> toggleFlashlight()
            Action.OPEN_APP -> openApp(sourceTrigger)
            Action.NONE -> {}
        }
    }

    private suspend fun openApp(sourceTrigger: TriggerMethod) {
        val packageName = when (sourceTrigger) {
            TriggerMethod.FINGERPRINT -> settingsRepo.fingerprintAppPackage.first()
            TriggerMethod.POWER_BUTTON -> settingsRepo.powerButtonAppPackage.first()
            TriggerMethod.BACK_PANEL -> settingsRepo.backPanelAppPackage.first()
        }
        if (!packageName.isNullOrEmpty()) {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
            } else {
                Log.e("DoubleTap", "App not found: $packageName")
            }
        }
    }

    private fun toggleFlashlight() {
        try {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, !isFlashlightOn)
            }
        } catch (e: Exception) {
            Log.e("DoubleTap", "Failed to toggle flashlight", e)
        }
    }

    private fun vibrate() {
        try {
            val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(50, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {
            Log.e("DoubleTap", "Failed to vibrate", e)
        }
    }

    private fun updateForegroundState(isEnabled: Boolean) {
        val channelId = "taptrigger_service_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "TapTrigger Active Service",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps TapTrigger active in the background"
            }
            notificationManager.createNotificationChannel(channel)
        }

        if (isEnabled) {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = android.app.PendingIntent.getActivity(
                this, 0, notificationIntent,
                android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setContentTitle("TapTrigger is Active")
                .setContentText("Listening for double-tap gestures...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()

            try {
                notificationManager.notify(1, notification)
            } catch (e: Exception) {
                Log.e("DoubleTap", "Failed to show notification", e)
            }
        } else {
            try {
                notificationManager.cancel(1)
            } catch (e: Exception) {
                Log.e("DoubleTap", "Failed to cancel notification", e)
            }
        }
    }
}
