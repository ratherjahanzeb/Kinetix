#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/MainViewModel.kt
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
    private val settingsRepository: SettingsRepository,
    private val compatibilityChecker: CompatibilityChecker
) : ViewModel() {

    val isEnabled: StateFlow<Boolean> = settingsRepository.isEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val timeoutMs: StateFlow<Int> = settingsRepository.timeoutMs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 300)

    val selectedAction: StateFlow<Action> = settingsRepository.selectedAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.NONE)
        
    val activeTrigger: StateFlow<TriggerMethod> = settingsRepository.activeTrigger
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TriggerMethod.FINGERPRINT)

    var compatibilityStatus: CompatibilityStatus = compatibilityChecker.checkCompatibility()
        private set

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setEnabled(enabled) }
    }

    fun setTimeoutMs(timeout: Int) {
        viewModelScope.launch { settingsRepository.setTimeoutMs(timeout) }
    }

    fun setSelectedAction(action: Action) {
        viewModelScope.launch { settingsRepository.setSelectedAction(action) }
    }
    
    fun setActiveTrigger(trigger: TriggerMethod) {
        viewModelScope.launch { settingsRepository.setActiveTrigger(trigger) }
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
INNER_EOF
bash update_viewmodel.sh
