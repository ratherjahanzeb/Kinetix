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
        
    val fingerprintAction: StateFlow<Action> = settingsRepo.fingerprintAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.HOME)
        
    val powerButtonAction: StateFlow<Action> = settingsRepo.powerButtonAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.FLASHLIGHT)
        
    val backPanelAction: StateFlow<Action> = settingsRepo.backPanelAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.SCREENSHOT)
        
    val fingerprintAppPackage: StateFlow<String?> = settingsRepo.fingerprintAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val powerButtonAppPackage: StateFlow<String?> = settingsRepo.powerButtonAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val backPanelAppPackage: StateFlow<String?> = settingsRepo.backPanelAppPackage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val autostartChecked: StateFlow<Boolean> = settingsRepo.autostartChecked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val onboardingCompleted: StateFlow<Boolean?> = settingsRepo.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fingerprintEnabled: StateFlow<Boolean> = settingsRepo.fingerprintEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val powerButtonEnabled: StateFlow<Boolean> = settingsRepo.powerButtonEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val backPanelEnabled: StateFlow<Boolean> = settingsRepo.backPanelEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val vibrateEnabled: StateFlow<Boolean> = settingsRepo.vibrateEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val materialYouEnabled: StateFlow<Boolean> = settingsRepo.materialYouEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val accentTheme: StateFlow<String> = settingsRepo.accentTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "AURORA")

    val triggerLogs: StateFlow<List<TriggerLogEntry>> = settingsRepo.triggerLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTrigger: StateFlow<TriggerMethod> = settingsRepo.activeTrigger
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TriggerMethod.FINGERPRINT)

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
    
    fun setFingerprintAction(action: Action) {
        viewModelScope.launch { settingsRepo.setFingerprintAction(action) }
    }
    
    fun setPowerButtonAction(action: Action) {
        viewModelScope.launch { settingsRepo.setPowerButtonAction(action) }
    }
    
    fun setBackPanelAction(action: Action) {
        viewModelScope.launch { settingsRepo.setBackPanelAction(action) }
    }
    
    fun setFingerprintAppPackage(pkg: String) {
        viewModelScope.launch { settingsRepo.setFingerprintAppPackage(pkg) }
    }
    
    fun setPowerButtonAppPackage(pkg: String) {
        viewModelScope.launch { settingsRepo.setPowerButtonAppPackage(pkg) }
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

    fun setFingerprintEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setFingerprintEnabled(enabled) } }
    fun setPowerButtonEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setPowerButtonEnabled(enabled) } }
    fun setBackPanelEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setBackPanelEnabled(enabled) } }
    fun setVibrateEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setVibrateEnabled(enabled) } }
    fun setMaterialYouEnabled(enabled: Boolean) { viewModelScope.launch { settingsRepo.setMaterialYouEnabled(enabled) } }
    fun setAccentTheme(theme: String) { viewModelScope.launch { settingsRepo.setAccentTheme(theme) } }

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
