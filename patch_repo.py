import re

with open("app/src/main/java/com/example/SettingsRepository.kt", "r") as f:
    content = f.read()

# Add keys
keys = """        val SELECTED_ACTION = stringPreferencesKey("selected_action")
        
        val FINGERPRINT_ACTION = stringPreferencesKey("fingerprint_action")
        val POWER_BUTTON_ACTION = stringPreferencesKey("power_button_action")
        val BACK_PANEL_ACTION = stringPreferencesKey("back_panel_action")"""

content = re.sub(r'val SELECTED_ACTION = stringPreferencesKey\("selected_action"\)', keys, content)

# Add flows
flows = """    val selectedAction: Flow<Action> = context.dataStore.data.map { preferences ->
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
    }"""

content = re.sub(r'val selectedAction: Flow<Action> = [^\n]*\n[^\n]*\n[^\n]*\n[^\n]*\}', flows, content)

# Add setters
setters = """    suspend fun setSelectedAction(action: Action) { context.dataStore.edit { it[SELECTED_ACTION] = action.name } }
    
    suspend fun setFingerprintAction(action: Action) { context.dataStore.edit { it[FINGERPRINT_ACTION] = action.name } }
    suspend fun setPowerButtonAction(action: Action) { context.dataStore.edit { it[POWER_BUTTON_ACTION] = action.name } }
    suspend fun setBackPanelAction(action: Action) { context.dataStore.edit { it[BACK_PANEL_ACTION] = action.name } }"""

content = re.sub(r'suspend fun setSelectedAction[^\n]*\n', setters + "\n", content)

with open("app/src/main/java/com/example/SettingsRepository.kt", "w") as f:
    f.write(content)
