#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/SettingsRepository.kt
package com.example

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val IS_ENABLED = booleanPreferencesKey("is_enabled")
        val TIMEOUT_MS = intPreferencesKey("timeout_ms")
        val SELECTED_ACTION = stringPreferencesKey("selected_action")
        
        val ACTIVE_TRIGGER = stringPreferencesKey("active_trigger")
        val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
        
        // Power Button Settings
        val POWER_BUTTON_TIMEOUT_MS = intPreferencesKey("power_button_timeout_ms")
        
        // Back Panel Settings
        val BACK_PANEL_SENSITIVITY = intPreferencesKey("back_panel_sensitivity") // 0=Low, 1=Medium, 2=High
        val BACK_PANEL_TIMEOUT_MS = intPreferencesKey("back_panel_timeout_ms")
        
        // Feedback
        val VIBRATE_ENABLED = booleanPreferencesKey("vibrate_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }

    val isEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_ENABLED] ?: false
    }

    val timeoutMs: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[TIMEOUT_MS] ?: 300
    }

    val selectedAction: Flow<Action> = context.dataStore.data.map { preferences ->
        val actionName = preferences[SELECTED_ACTION] ?: Action.NONE.name
        try { Action.valueOf(actionName) } catch (e: Exception) { Action.NONE }
    }
    
    val activeTrigger: Flow<TriggerMethod> = context.dataStore.data.map { preferences ->
        val triggerName = preferences[ACTIVE_TRIGGER] ?: TriggerMethod.FINGERPRINT.name
        try { TriggerMethod.valueOf(triggerName) } catch (e: Exception) { TriggerMethod.FINGERPRINT }
    }
    
    val startOnBoot: Flow<Boolean> = context.dataStore.data.map { it[START_ON_BOOT] ?: false }
    
    val powerButtonTimeoutMs: Flow<Int> = context.dataStore.data.map { it[POWER_BUTTON_TIMEOUT_MS] ?: 300 }
    
    val backPanelSensitivity: Flow<Int> = context.dataStore.data.map { it[BACK_PANEL_SENSITIVITY] ?: 1 }
    val backPanelTimeoutMs: Flow<Int> = context.dataStore.data.map { it[BACK_PANEL_TIMEOUT_MS] ?: 300 }
    
    val vibrateEnabled: Flow<Boolean> = context.dataStore.data.map { it[VIBRATE_ENABLED] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: false }

    suspend fun setEnabled(enabled: Boolean) { context.dataStore.edit { it[IS_ENABLED] = enabled } }
    suspend fun setTimeoutMs(timeout: Int) { context.dataStore.edit { it[TIMEOUT_MS] = timeout } }
    suspend fun setSelectedAction(action: Action) { context.dataStore.edit { it[SELECTED_ACTION] = action.name } }
    suspend fun setActiveTrigger(trigger: TriggerMethod) { context.dataStore.edit { it[ACTIVE_TRIGGER] = trigger.name } }
    suspend fun setStartOnBoot(enabled: Boolean) { context.dataStore.edit { it[START_ON_BOOT] = enabled } }
    suspend fun setPowerButtonTimeoutMs(timeout: Int) { context.dataStore.edit { it[POWER_BUTTON_TIMEOUT_MS] = timeout } }
    suspend fun setBackPanelSensitivity(sensitivity: Int) { context.dataStore.edit { it[BACK_PANEL_SENSITIVITY] = sensitivity } }
    suspend fun setBackPanelTimeoutMs(timeout: Int) { context.dataStore.edit { it[BACK_PANEL_TIMEOUT_MS] = timeout } }
    suspend fun setVibrateEnabled(enabled: Boolean) { context.dataStore.edit { it[VIBRATE_ENABLED] = enabled } }
    suspend fun setSoundEnabled(enabled: Boolean) { context.dataStore.edit { it[SOUND_ENABLED] = enabled } }
}
INNER_EOF
bash update_settings.sh
