package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppInfo(val name: String, val packageName: String, val icon: Bitmap?)

class MainViewModel(
    private val settingsRepo: SettingsRepository,
    private val compatibilityChecker: CompatibilityChecker
) : ViewModel() {

    val isEnabled: StateFlow<Boolean> = settingsRepo.isEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val timeoutMs: StateFlow<Int> = settingsRepo.timeoutMs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 300)

        val selectedAction: StateFlow<Action> = settingsRepo.selectedAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.NONE)
        
    val moveLeftAction: StateFlow<Action> = settingsRepo.moveLeftAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.HOME)
        
    val moveBackwardAction: StateFlow<Action> = settingsRepo.moveBackwardAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.FLASHLIGHT)
        
    val flipPhoneAction: StateFlow<Action> = settingsRepo.flipPhoneAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.HOME)
        
    val backPanelAction: StateFlow<Action> = settingsRepo.backPanelAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.SCREENSHOT)
        
    val moveLeftAppPackage: StateFlow<String?> = settingsRepo.moveLeftAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val moveBackwardAppPackage: StateFlow<String?> = settingsRepo.moveBackwardAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val flipPhoneAppPackage: StateFlow<String?> = settingsRepo.flipPhoneAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val backPanelAppPackage: StateFlow<String?> = settingsRepo.backPanelAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val autostartChecked: StateFlow<Boolean> = settingsRepo.autostartChecked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val onboardingCompleted: StateFlow<Boolean?> = settingsRepo.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val moveLeftEnabled: StateFlow<Boolean> = settingsRepo.moveLeftEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val moveBackwardEnabled: StateFlow<Boolean> = settingsRepo.moveBackwardEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val flipPhoneEnabled: StateFlow<Boolean> = settingsRepo.flipPhoneEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val backPanelEnabled: StateFlow<Boolean> = settingsRepo.backPanelEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val backPanelSensitivity: StateFlow<Int> = settingsRepo.backPanelSensitivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val sensorSensitivity: StateFlow<Float> = settingsRepo.sensorSensitivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)

    val vibrateEnabled: StateFlow<Boolean> = settingsRepo.vibrateEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val hapticIntensity: StateFlow<Float> = settingsRepo.hapticIntensity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)

    val materialYouEnabled: StateFlow<Boolean> = settingsRepo.materialYouEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val accentTheme: StateFlow<String> = settingsRepo.accentTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "AURORA")

    val themeMode: StateFlow<String> = settingsRepo.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val amoledDarkMode: StateFlow<Boolean> = settingsRepo.amoledDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val triggerLogs: StateFlow<List<TriggerLogEntry>> = settingsRepo.triggerLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTrigger: StateFlow<TriggerMethod> = settingsRepo.activeTrigger
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TriggerMethod.MOVE_LEFT)

    var compatibilityStatus: CompatibilityStatus = compatibilityChecker.checkCompatibility()
        private set

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setEnabled(enabled) }
    }

    fun setTimeoutMs(timeout: Int) {
        viewModelScope.launch { settingsRepo.setTimeoutMs(timeout) }
    }

        fun setSelectedAction(action: Action) {
        viewModelScope.launch { settingsRepo.setSelectedAction(action) }
    }
    
    fun setShakeAction(action: Action) {
        viewModelScope.launch { settingsRepo.setShakeAction(action) }
    }
    
    fun setProximityWaveAction(action: Action) {
        viewModelScope.launch { settingsRepo.setProximityWaveAction(action) }
    }
    
    fun setFlipPhoneAction(action: Action) {
        viewModelScope.launch { settingsRepo.setFlipPhoneAction(action) }
    }
    
    fun setBackPanelAction(action: Action) {
        viewModelScope.launch { settingsRepo.setBackPanelAction(action) }
    }
    
    fun setShakeAppPackage(pkg: String) {
        viewModelScope.launch { settingsRepo.setShakeAppPackage(pkg) }
    }
    
    fun setProximityWaveAppPackage(pkg: String) {
        viewModelScope.launch { settingsRepo.setProximityWaveAppPackage(pkg) }
    }
    
    fun setFlipPhoneAppPackage(pkg: String) {
        viewModelScope.launch { settingsRepo.setFlipPhoneAppPackage(pkg) }
    }
    
    fun setBackPanelAppPackage(pkg: String) {
        viewModelScope.launch { settingsRepo.setBackPanelAppPackage(pkg) }
    }
    
    fun setAutostartChecked() {
        viewModelScope.launch { settingsRepo.setAutostartChecked(true) }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch { settingsRepo.setOnboardingCompleted(true) }
    }

    fun setShakeEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setShakeEnabled(enabled) } }
    fun setProximityWaveEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setProximityWaveEnabled(enabled) } }
    fun setFlipPhoneEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setFlipPhoneEnabled(enabled) } }
    fun setBackPanelEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setBackPanelEnabled(enabled) } }
    fun setVibrateEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setVibrateEnabled(enabled) } }
    fun setHapticIntensity(intensity: Float) { viewModelScope.launch { settingsRepo.setHapticIntensity(intensity) } }
    fun setMaterialYouEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setMaterialYouEnabled(enabled) } }
    fun setAccentTheme(theme: String) { viewModelScope.launch { settingsRepo.setAccentTheme(theme) } }
    fun setThemeMode(mode: String) { viewModelScope.launch { settingsRepo.setThemeMode(mode) } }
    fun setAmoledDarkMode(enabled: Boolean) { viewModelScope.launch { settingsRepo.setAmoledDarkMode(enabled) } }

    fun setBackPanelSensitivity(sensitivity: Int) {
        viewModelScope.launch { settingsRepo.setBackPanelSensitivity(sensitivity) }
    }

    fun setSensorSensitivity(sensitivity: Float) {
        viewModelScope.launch { settingsRepo.setSensorSensitivity(sensitivity) }
    }

    fun setActiveTrigger(trigger: TriggerMethod) {
        viewModelScope.launch { settingsRepo.setActiveTrigger(trigger) }
    }

    fun refreshCompatibility(): CompatibilityStatus {
        compatibilityStatus = compatibilityChecker.checkCompatibility()
        return compatibilityStatus
    }

    private val _installedApps = MutableStateFlow<List<AppInfo>?>(null)
    val installedApps: StateFlow<List<AppInfo>?> = _installedApps.asStateFlow()

    fun loadInstalledApps(context: Context) {
        if (_installedApps.value != null) return
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val appList = resolveInfos.mapNotNull {
                try {
                    val drawable = it.loadIcon(pm)
                    val bitmap = try {
                        drawable.toBitmap(width = 144, height = 144, config = Bitmap.Config.ARGB_8888)
                    } catch (e: Exception) { null }
                    
                    AppInfo(
                        name = it.loadLabel(pm).toString(),
                        packageName = it.activityInfo.packageName,
                        icon = bitmap
                    )
                } catch (e: Exception) { null }
            }.sortedBy { it.name.lowercase() }
            
            _installedApps.value = appList
        }
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(SettingsRepository(context), CompatibilityChecker(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
