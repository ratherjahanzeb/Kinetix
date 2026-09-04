package com.example

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
        
    val shakeAction: StateFlow<Action> = settingsRepo.shakeAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.HOME)
        
    val proximityWaveAction: StateFlow<Action> = settingsRepo.proximityWaveAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.FLASHLIGHT)
        
    val flipPhoneAction: StateFlow<Action> = settingsRepo.flipPhoneAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.HOME)
        
    val backPanelAction: StateFlow<Action> = settingsRepo.backPanelAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.SCREENSHOT)
        
    val shakeAppPackage: StateFlow<String?> = settingsRepo.shakeAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val proximityWaveAppPackage: StateFlow<String?> = settingsRepo.proximityWaveAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val flipPhoneAppPackage: StateFlow<String?> = settingsRepo.flipPhoneAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val backPanelAppPackage: StateFlow<String?> = settingsRepo.backPanelAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val autostartChecked: StateFlow<Boolean> = settingsRepo.autostartChecked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val onboardingCompleted: StateFlow<Boolean?> = settingsRepo.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val shakeEnabled: StateFlow<Boolean> = settingsRepo.shakeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val proximityWaveEnabled: StateFlow<Boolean> = settingsRepo.proximityWaveEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val flipPhoneEnabled: StateFlow<Boolean> = settingsRepo.flipPhoneEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val backPanelEnabled: StateFlow<Boolean> = settingsRepo.backPanelEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val backPanelSensitivity: StateFlow<Int> = settingsRepo.backPanelSensitivity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val vibrateEnabled: StateFlow<Boolean> = settingsRepo.vibrateEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val materialYouEnabled: StateFlow<Boolean> = settingsRepo.materialYouEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val accentTheme: StateFlow<String> = settingsRepo.accentTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "AURORA")

    val triggerLogs: StateFlow<List<TriggerLogEntry>> = settingsRepo.triggerLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTrigger: StateFlow<TriggerMethod> = settingsRepo.activeTrigger
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TriggerMethod.SHAKE)

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
    fun setMaterialYouEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setMaterialYouEnabled(enabled) } }
    fun setAccentTheme(theme: String) { viewModelScope.launch { settingsRepo.setAccentTheme(theme) } }

    fun setBackPanelSensitivity(sensitivity: Int) {
        viewModelScope.launch { settingsRepo.setBackPanelSensitivity(sensitivity) }
    }

    fun setActiveTrigger(trigger: TriggerMethod) {
        viewModelScope.launch { settingsRepo.setActiveTrigger(trigger) }
    }

    fun refreshCompatibility(): CompatibilityStatus {
        compatibilityStatus = compatibilityChecker.checkCompatibility()
        return compatibilityStatus
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
