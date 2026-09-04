import re

with open("app/src/main/java/com/example/MainViewModel.kt", "r") as f:
    content = f.read()

# Add state flows
state_flows = """    val selectedAction: StateFlow<Action> = settingsRepo.selectedAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.NONE)
        
    val fingerprintAction: StateFlow<Action> = settingsRepo.fingerprintAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.HOME)
        
    val powerButtonAction: StateFlow<Action> = settingsRepo.powerButtonAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.FLASHLIGHT)
        
    val backPanelAction: StateFlow<Action> = settingsRepo.backPanelAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Action.SCREENSHOT)"""

content = re.sub(r'val selectedAction[^\n]*\n[^\n]*\n', state_flows + "\n", content)

# Add setters
setters = """    fun setSelectedAction(action: Action) {
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
    }"""

content = re.sub(r'fun setSelectedAction[^\n]*\n[^\n]*\n[^\n]*\n', setters + "\n", content)

with open("app/src/main/java/com/example/MainViewModel.kt", "w") as f:
    f.write(content)
