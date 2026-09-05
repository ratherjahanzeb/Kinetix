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
    private var lastBackTapTime = 0L
    private var lastShakeTriggerTime = 0L
    private var lastProximityTriggerTime = 0L
    private var lastFlipTriggerTime = 0L
    private var lastGlobalActionTime = 0L
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var settingsRepo: SettingsRepository
    private var isFlashlightOn = false
    private var backTapDetector: BackTapDetector? = null
    private var shakeDetector: MoveLeftDetector? = null
    private var proximityDetector: ProximityDetector? = null
    private var flipDetector: MoveRightDetector? = null

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
        shakeDetector = MoveLeftDetector(sensorManager) {
            handlePotentialTap(TriggerMethod.MOVE_LEFT)
        }
        backTapDetector = BackTapDetector(sensorManager) {
            handlePotentialTap(TriggerMethod.BACK_PANEL)
        }
        proximityDetector = ProximityDetector(sensorManager) {
            handlePotentialTap(TriggerMethod.MOVE_BACKWARD)
        }
        flipDetector = MoveRightDetector(sensorManager) {
            handlePotentialTap(TriggerMethod.MOVE_RIGHT_PHONE)
        }

        scope.launch {
            settingsRepo.isEnabled.collect { isEnabled ->
                updateForegroundState(isEnabled)
            }
        }

        scope.launch {
            combine(settingsRepo.isEnabled, settingsRepo.moveLeftEnabled) { isEnabled, shake ->
                Pair(isEnabled, shake)
            }.collect { (isEnabled, shake) ->
                if (isEnabled && shake) {
                    shakeDetector?.start()
                } else {
                    shakeDetector?.stop()
                }
            }
        }

        scope.launch {
            combine(settingsRepo.isEnabled, settingsRepo.moveBackwardEnabled) { isEnabled, proximity ->
                Pair(isEnabled, proximity)
            }.collect { (isEnabled, proximity) ->
                if (isEnabled && proximity) {
                    proximityDetector?.start()
                } else {
                    proximityDetector?.stop()
                }
            }
        }

        scope.launch {
            combine(settingsRepo.isEnabled, settingsRepo.flipPhoneEnabled) { isEnabled, flip ->
                Pair(isEnabled, flip)
            }.collect { (isEnabled, flip) ->
                if (isEnabled && flip) {
                    flipDetector?.start()
                } else {
                    flipDetector?.stop()
                }
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

        scope.launch {
            settingsRepo.backPanelSensitivity.collect { sensitivity ->
                backTapDetector?.sensitivityLevel = sensitivity
            }
        }

        scope.launch {
            settingsRepo.timeoutMs.collect { timeout ->
                backTapDetector?.timeoutMs = timeout
            }
        }

        scope.launch {
            settingsRepo.sensorSensitivity.collect { sens ->
                backTapDetector?.sensorSensitivity = sens
                shakeDetector?.sensorSensitivity = sens
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
        return super.onKeyEvent(event)
    }

    private fun handlePotentialTap(sourceTrigger: TriggerMethod) {
        scope.launch {
            val isEnabled = settingsRepo.isEnabled.first()
            if (!isEnabled) return@launch
            
            val isSourceEnabled = when (sourceTrigger) {
                TriggerMethod.MOVE_LEFT -> settingsRepo.moveLeftEnabled.first()
                TriggerMethod.MOVE_BACKWARD -> settingsRepo.moveBackwardEnabled.first()
                TriggerMethod.MOVE_RIGHT_PHONE -> settingsRepo.flipPhoneEnabled.first()
                TriggerMethod.BACK_PANEL -> settingsRepo.backPanelEnabled.first()
            }
            if (!isSourceEnabled) return@launch

            executeTriggerAction(sourceTrigger)
        }
    }

    private suspend fun executeTriggerAction(sourceTrigger: TriggerMethod) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastGlobalActionTime < 1000) {
            return // Ignore if another gesture action was executed within the last second
        }
        lastGlobalActionTime = currentTime

        val vibrateEnabled = settingsRepo.vibrateEnabled.first()
        if (vibrateEnabled) {
            vibrate()
        }
        val actionToExecute = when (sourceTrigger) {
            TriggerMethod.MOVE_LEFT -> settingsRepo.moveLeftAction.first()
            TriggerMethod.MOVE_BACKWARD -> settingsRepo.moveBackwardAction.first()
            TriggerMethod.MOVE_RIGHT_PHONE -> settingsRepo.flipPhoneAction.first()
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
            Action.SPLIT_SCREEN -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
                }
            }
            Action.POWER_DIALOG -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
                }
            }
            Action.VOLUME_UP, Action.VOLUME_DOWN -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                val direction = if (action == Action.VOLUME_UP) android.media.AudioManager.ADJUST_RAISE else android.media.AudioManager.ADJUST_LOWER
                audioManager.adjustStreamVolume(android.media.AudioManager.STREAM_MUSIC, direction, android.media.AudioManager.FLAG_SHOW_UI)
            }
            Action.MEDIA_PLAY_PAUSE -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                val eventTime = System.currentTimeMillis()
                val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0)
                val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0)
                try {
                    audioManager.dispatchMediaKeyEvent(downEvent)
                    audioManager.dispatchMediaKeyEvent(upEvent)
                } catch (e: Exception) {
                    Log.e("AccessibilityService", "Error dispatching media key", e)
                }
            }
            Action.BACK -> performGlobalAction(GLOBAL_ACTION_BACK)
            Action.NEXT_TRACK, Action.PREV_TRACK -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                val keyCode = if (action == Action.NEXT_TRACK) KeyEvent.KEYCODE_MEDIA_NEXT else KeyEvent.KEYCODE_MEDIA_PREVIOUS
                val eventTime = System.currentTimeMillis()
                val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0)
                val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0)
                try {
                    audioManager.dispatchMediaKeyEvent(downEvent)
                    audioManager.dispatchMediaKeyEvent(upEvent)
                } catch (e: Exception) {
                    Log.e("AccessibilityService", "Error dispatching media key", e)
                }
            }
            Action.MUTE_AUDIO -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                audioManager.adjustStreamVolume(android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.ADJUST_MUTE, android.media.AudioManager.FLAG_SHOW_UI)
            }
            Action.RINGER_SILENT, Action.RINGER_NORMAL, Action.RINGER_VIBRATE -> {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                val mode = when (action) {
                    Action.RINGER_SILENT -> android.media.AudioManager.RINGER_MODE_SILENT
                    Action.RINGER_NORMAL -> android.media.AudioManager.RINGER_MODE_NORMAL
                    else -> android.media.AudioManager.RINGER_MODE_VIBRATE
                }
                try {
                    audioManager.ringerMode = mode
                } catch (e: Exception) {
                    Log.e("AccessibilityService", "Error setting ringer mode", e)
                }
            }
            Action.BRIGHTNESS_UP, Action.BRIGHTNESS_DOWN -> {
                try {
                    if (android.provider.Settings.System.canWrite(this)) {
                        val cr = contentResolver
                        var currentBrightness = android.provider.Settings.System.getInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS)
                        val step = 25
                        currentBrightness = if (action == Action.BRIGHTNESS_UP) {
                            (currentBrightness + step).coerceAtMost(255)
                        } else {
                            (currentBrightness - step).coerceAtLeast(10)
                        }
                        android.provider.Settings.System.putInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS, currentBrightness)
                    } else {
                        performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
                    }
                } catch (e: Exception) {
                    Log.e("AccessibilityService", "Error adjusting brightness", e)
                }
            }
            Action.FLASHLIGHT -> toggleFlashlight()
            Action.OPEN_APP -> openApp(sourceTrigger)
            Action.NONE -> {}
        }
    }

    private suspend fun openApp(sourceTrigger: TriggerMethod) {
        val packageName = when (sourceTrigger) {
            TriggerMethod.MOVE_LEFT -> settingsRepo.moveLeftAppPackage.first()
            TriggerMethod.MOVE_BACKWARD -> settingsRepo.moveBackwardAppPackage.first()
            TriggerMethod.MOVE_RIGHT_PHONE -> settingsRepo.flipPhoneAppPackage.first()
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
        val channelId = "kinetix_service_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Kinetix Active Service",
                android.app.NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps Kinetix active in the background"
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
                .setContentTitle("Kinetix is Active")
                .setContentText("Listening for gestures...")
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
