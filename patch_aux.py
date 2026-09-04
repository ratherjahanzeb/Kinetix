import re

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "r") as f:
    content = f.read()

replacement = """@Composable
fun ActionSelectionScreen(viewModel: MainViewModel, navController: NavController, triggerMethod: TriggerMethod) {
    val selectedAction by when (triggerMethod) {
        TriggerMethod.FINGERPRINT -> viewModel.fingerprintAction.collectAsStateWithLifecycle()
        TriggerMethod.POWER_BUTTON -> viewModel.powerButtonAction.collectAsStateWithLifecycle()
        TriggerMethod.BACK_PANEL -> viewModel.backPanelAction.collectAsStateWithLifecycle()
    }
    
    Scaffold(
        topBar = { SimpleTopBar("${triggerMethod.displayName} Action", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader("SYSTEM ACTIONS")
            
            Action.values().forEach { action ->
                val icon = when (action) {
                    Action.HOME -> Icons.Rounded.Home
                    Action.RECENTS -> Icons.Rounded.ViewAgenda
                    Action.NOTIFICATIONS -> Icons.Rounded.Notifications
                    Action.QUICK_SETTINGS -> Icons.Rounded.Settings
                    Action.SCREENSHOT -> Icons.Rounded.Screenshot
                    Action.LOCK_SCREEN -> Icons.Rounded.Lock
                    Action.FLASHLIGHT -> Icons.Rounded.FlashlightOn
                    Action.NONE -> Icons.Rounded.Block
                }
                
                ActionItem(
                    action = action,
                    icon = icon,
                    isSelected = selectedAction == action,
                    onClick = { 
                        when (triggerMethod) {
                            TriggerMethod.FINGERPRINT -> viewModel.setFingerprintAction(action)
                            TriggerMethod.POWER_BUTTON -> viewModel.setPowerButtonAction(action)
                            TriggerMethod.BACK_PANEL -> viewModel.setBackPanelAction(action)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}"""

content = re.sub(
    r'@Composable\nfun ActionSelectionScreen\(viewModel: MainViewModel, navController: NavController\) \{.*?(?=@Composable\nfun ActionItem)',
    replacement + "\n\n",
    content,
    flags=re.DOTALL
)

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "w") as f:
    f.write(content)
