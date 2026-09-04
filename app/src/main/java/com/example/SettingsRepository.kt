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
        
        val FINGERPRINT_ACTION = stringPreferencesKey("fingerprint_action")
        val POWER_BUTTON_ACTION = stringPreferencesKey("power_button_action")
        val BACK_PANEL_ACTION = stringPreferencesKey("back_panel_action")
        
        val FINGERPRINT_APP_PACKAGE = stringPreferencesKey("fingerprint_app_package")
        val POWER_BUTTON_APP_PACKAGE = stringPreferencesKey("power_button_app_package")
        val BACK_PANEL_APP_PACKAGE = stringPreferencesKey("back_panel_app_package")
        
        val ACTIVE_TRIGGER = stringPreferencesKey("active_trigger")
        val FINGERPRINT_ENABLED = booleanPreferencesKey("fingerprint_enabled")
        val POWER_BUTTON_ENABLED = booleanPreferencesKey("power_button_enabled")
        val BACK_PANEL_ENABLED = booleanPreferencesKey("back_panel_enabled")
        val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val AUTOSTART_CHECKED = booleanPreferencesKey("autostart_checked")
        
        // Power Button Settings
        val POWER_BUTTON_TIMEOUT_MS = intPreferencesKey("power_button_timeout_ms")
        
        // Back Panel Settings
        val BACK_PANEL_SENSITIVITY = intPreferencesKey("back_panel_sensitivity") // 0=Low, 1=Medium, 2=High
        val BACK_PANEL_TIMEOUT_MS = intPreferencesKey("back_panel_timeout_ms")
        
        // Feedback
        val VIBRATE_ENABLED = booleanPreferencesKey("vibrate_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val TRIGGER_LOGS = stringPreferencesKey("trigger_logs")
        val MATERIAL_YOU_ENABLED = booleanPreferencesKey("material_you_enabled")
        val ACCENT_THEME = stringPreferencesKey("accent_theme")
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
    
    val fingerprintAction: Flow<Action> = context.dataStore.data.map { preferences ->
        val actionName = preferences[FINGERPRINT_ACTION] ?: Action.NONE.name
        try { Action.valueOf(actionName) } catch (e: Exception) { Action.NONE }
    }
    
    val powerButtonAction: Flow<Action> = context.dataStore.data.map { preferences ->
        val actionName = preferences[POWER_BUTTON_ACTION] ?: Action.NONE.name
        try { Action.valueOf(actionName) } catch (e: Exception) { Action.NONE }
    }
    
    val backPanelAction: Flow<Action> = context.dataStore.data.map { preferences ->
        val actionName = preferences[BACK_PANEL_ACTION] ?: Action.NONE.name
        try { Action.valueOf(actionName) } catch (e: Exception) { Action.NONE }
    }
    
    val fingerprintAppPackage: Flow<String?> = context.dataStore.data.map { it[FINGERPRINT_APP_PACKAGE] }
    val powerButtonAppPackage: Flow<String?> = context.dataStore.data.map { it[POWER_BUTTON_APP_PACKAGE] }
    val backPanelAppPackage: Flow<String?> = context.dataStore.data.map { it[BACK_PANEL_APP_PACKAGE] }
    
    val fingerprintEnabled: Flow<Boolean> = context.dataStore.data.map { it[FINGERPRINT_ENABLED] ?: true }
    val powerButtonEnabled: Flow<Boolean> = context.dataStore.data.map { it[POWER_BUTTON_ENABLED] ?: true }
    val backPanelEnabled: Flow<Boolean> = context.dataStore.data.map { it[BACK_PANEL_ENABLED] ?: true }

    val activeTrigger: Flow<TriggerMethod> = context.dataStore.data.map { preferences ->
        val triggerName = preferences[ACTIVE_TRIGGER] ?: TriggerMethod.FINGERPRINT.name
        try { TriggerMethod.valueOf(triggerName) } catch (e: Exception) { TriggerMethod.FINGERPRINT }
    }
    
    val startOnBoot: Flow<Boolean> = context.dataStore.data.map { it[START_ON_BOOT] ?: false }
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    val autostartChecked: Flow<Boolean> = context.dataStore.data.map { it[AUTOSTART_CHECKED] ?: false }
    
    val powerButtonTimeoutMs: Flow<Int> = context.dataStore.data.map { it[POWER_BUTTON_TIMEOUT_MS] ?: 300 }
    
    val backPanelSensitivity: Flow<Int> = context.dataStore.data.map { it[BACK_PANEL_SENSITIVITY] ?: 1 }
    val backPanelTimeoutMs: Flow<Int> = context.dataStore.data.map { it[BACK_PANEL_TIMEOUT_MS] ?: 300 }
    
    val vibrateEnabled: Flow<Boolean> = context.dataStore.data.map { it[VIBRATE_ENABLED] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUND_ENABLED] ?: false }
    val materialYouEnabled: Flow<Boolean> = context.dataStore.data.map { it[MATERIAL_YOU_ENABLED] ?: false }
    val accentTheme: Flow<String> = context.dataStore.data.map { it[ACCENT_THEME] ?: "AURORA" }

    val triggerLogs: Flow<List<TriggerLogEntry>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[TRIGGER_LOGS] ?: "[]"
        try {
            val array = org.json.JSONArray(jsonStr)
            val list = mutableListOf<TriggerLogEntry>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TriggerLogEntry(
                        sourceTrigger = obj.optString("sourceTrigger", "UNKNOWN"),
                        actionName = obj.optString("actionName", "NONE"),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun setEnabled(enabled: Boolean) { context.dataStore.edit { it[IS_ENABLED] = enabled } }
    suspend fun setTimeoutMs(timeout: Int) { context.dataStore.edit { it[TIMEOUT_MS] = timeout } }
        suspend fun setSelectedAction(action: Action) { context.dataStore.edit { it[SELECTED_ACTION] = action.name } }
    
    suspend fun setFingerprintAction(action: Action) { context.dataStore.edit { it[FINGERPRINT_ACTION] = action.name } }
    suspend fun setPowerButtonAction(action: Action) { context.dataStore.edit { it[POWER_BUTTON_ACTION] = action.name } }
    suspend fun setBackPanelAction(action: Action) { context.dataStore.edit { it[BACK_PANEL_ACTION] = action.name } }
    suspend fun setFingerprintAppPackage(pkg: String) { context.dataStore.edit { it[FINGERPRINT_APP_PACKAGE] = pkg } }
    suspend fun setPowerButtonAppPackage(pkg: String) { context.dataStore.edit { it[POWER_BUTTON_APP_PACKAGE] = pkg } }
    suspend fun setBackPanelAppPackage(pkg: String) { context.dataStore.edit { it[BACK_PANEL_APP_PACKAGE] = pkg } }
    suspend fun setFingerprintEnabled(enabled: Boolean) { context.dataStore.edit { it[FINGERPRINT_ENABLED] = enabled } }
    suspend fun setPowerButtonEnabled(enabled: Boolean) { context.dataStore.edit { it[POWER_BUTTON_ENABLED] = enabled } }
    suspend fun setBackPanelEnabled(enabled: Boolean) { context.dataStore.edit { it[BACK_PANEL_ENABLED] = enabled } }

    suspend fun setActiveTrigger(trigger: TriggerMethod) { context.dataStore.edit { it[ACTIVE_TRIGGER] = trigger.name } }
    suspend fun setStartOnBoot(enabled: Boolean) { context.dataStore.edit { it[START_ON_BOOT] = enabled } }
    suspend fun setOnboardingCompleted(completed: Boolean) { context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed } }
    suspend fun setAutostartChecked(checked: Boolean) { context.dataStore.edit { it[AUTOSTART_CHECKED] = checked } }
    suspend fun setPowerButtonTimeoutMs(timeout: Int) { context.dataStore.edit { it[POWER_BUTTON_TIMEOUT_MS] = timeout } }
    suspend fun setBackPanelSensitivity(sensitivity: Int) { context.dataStore.edit { it[BACK_PANEL_SENSITIVITY] = sensitivity } }
    suspend fun setBackPanelTimeoutMs(timeout: Int) { context.dataStore.edit { it[BACK_PANEL_TIMEOUT_MS] = timeout } }
    suspend fun setVibrateEnabled(enabled: Boolean) { context.dataStore.edit { it[VIBRATE_ENABLED] = enabled } }
    suspend fun setSoundEnabled(enabled: Boolean) { context.dataStore.edit { it[SOUND_ENABLED] = enabled } }
    suspend fun setMaterialYouEnabled(enabled: Boolean) { context.dataStore.edit { it[MATERIAL_YOU_ENABLED] = enabled } }
    suspend fun setAccentTheme(theme: String) { context.dataStore.edit { it[ACCENT_THEME] = theme } }

    suspend fun addTriggerLog(entry: TriggerLogEntry) {
        context.dataStore.edit { preferences ->
            val jsonStr = preferences[TRIGGER_LOGS] ?: "[]"
            val array = try { org.json.JSONArray(jsonStr) } catch (e: Exception) { org.json.JSONArray() }
            
            val newObj = org.json.JSONObject().apply {
                put("sourceTrigger", entry.sourceTrigger)
                put("actionName", entry.actionName)
                put("timestamp", entry.timestamp)
            }
            
            val newArray = org.json.JSONArray()
            newArray.put(newObj)
            for (i in 0 until array.length()) {
                if (newArray.length() < 10) {
                    newArray.put(array.getJSONObject(i))
                }
            }
            preferences[TRIGGER_LOGS] = newArray.toString()
        }
    }
}

data class TriggerLogEntry(
    val sourceTrigger: String,
    val actionName: String,
    val timestamp: Long
)
