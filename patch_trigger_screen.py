import re

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "r") as f:
    content = f.read()

# I want to update TriggerItem to show the selected action and navigate to ActionSelectionScreen.
old_trigger_item = """@Composable
fun TriggerItem(
    trigger: TriggerMethod,
    icon: ImageVector,
    isChecked: Boolean,
    isSupported: Boolean,
    statusText: String,
    onCheckedChange: (Boolean) -> Unit
)"""

new_trigger_item = """@Composable
fun TriggerItem(
    trigger: TriggerMethod,
    icon: ImageVector,
    isChecked: Boolean,
    isSupported: Boolean,
    statusText: String,
    selectedAction: Action,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
)"""

content = content.replace(old_trigger_item, new_trigger_item)

old_row = """    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = isSupported, onClick = { onCheckedChange(!isChecked) })
            .let { if (!isSupported) it.background(Color.Transparent) else it.background(containerColor) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {"""

new_row = """    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = isSupported, onClick = onClick)
            .let { if (!isSupported) it.background(Color.Transparent) else it.background(containerColor) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {"""

content = content.replace(old_row, new_row)

old_desc = """            Text(
                text = trigger.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )"""

new_desc = """            Text(
                text = trigger.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Action: ${selectedAction.displayName}",
                style = MaterialTheme.typography.labelLarge,
                color = if (isSupported) AuroraPrimary else TextSecondary
            )"""

content = content.replace(old_desc, new_desc)

old_trigger_screen = """@Composable
fun TriggerSelectionScreen(viewModel: MainViewModel, navController: NavController) {
    val fingerprintEnabled by viewModel.fingerprintEnabled.collectAsStateWithLifecycle()
    val powerButtonEnabled by viewModel.powerButtonEnabled.collectAsStateWithLifecycle()
    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    
    val compatibility = viewModel.compatibilityStatus"""

new_trigger_screen = """@Composable
fun TriggerSelectionScreen(viewModel: MainViewModel, navController: NavController) {
    val fingerprintEnabled by viewModel.fingerprintEnabled.collectAsStateWithLifecycle()
    val powerButtonEnabled by viewModel.powerButtonEnabled.collectAsStateWithLifecycle()
    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    
    val fpAction by viewModel.fingerprintAction.collectAsStateWithLifecycle()
    val pbAction by viewModel.powerButtonAction.collectAsStateWithLifecycle()
    val bpAction by viewModel.backPanelAction.collectAsStateWithLifecycle()
    
    val compatibility = viewModel.compatibilityStatus"""

content = content.replace(old_trigger_screen, new_trigger_screen)

old_fp = """            TriggerItem(
                trigger = TriggerMethod.FINGERPRINT,
                icon = Icons.Rounded.Fingerprint,
                isChecked = fingerprintEnabled,
                isSupported = compatibility.hasHardware && compatibility.hasFingerprintInputDevice,
                statusText = if (compatibility.hasHardware && compatibility.hasFingerprintInputDevice) "Supported" else "Unsupported (No accessible fingerprint sensor)",
                onCheckedChange = { viewModel.setFingerprintEnabled(it) }
            )"""

new_fp = """            TriggerItem(
                trigger = TriggerMethod.FINGERPRINT,
                icon = Icons.Rounded.Fingerprint,
                isChecked = fingerprintEnabled,
                isSupported = compatibility.hasHardware && compatibility.hasFingerprintInputDevice,
                statusText = if (compatibility.hasHardware && compatibility.hasFingerprintInputDevice) "Supported" else "Unsupported (No accessible fingerprint sensor)",
                selectedAction = fpAction,
                onCheckedChange = { viewModel.setFingerprintEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.FINGERPRINT.name}") }
            )"""

content = content.replace(old_fp, new_fp)

old_pb = """            TriggerItem(
                trigger = TriggerMethod.POWER_BUTTON,
                icon = Icons.Rounded.PowerSettingsNew,
                isChecked = powerButtonEnabled,
                isSupported = true,
                statusText = "Restricted (May not work due to Android OEM limitations)",
                onCheckedChange = { viewModel.setPowerButtonEnabled(it) }
            )"""

new_pb = """            TriggerItem(
                trigger = TriggerMethod.POWER_BUTTON,
                icon = Icons.Rounded.PowerSettingsNew,
                isChecked = powerButtonEnabled,
                isSupported = true,
                statusText = "Restricted (May not work due to Android OEM limitations)",
                selectedAction = pbAction,
                onCheckedChange = { viewModel.setPowerButtonEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.POWER_BUTTON.name}") }
            )"""

content = content.replace(old_pb, new_pb)

old_bp = """            TriggerItem(
                trigger = TriggerMethod.BACK_PANEL,
                icon = Icons.Rounded.TapAndPlay,
                isChecked = backPanelEnabled,
                isSupported = compatibility.hasAccelerometer,
                statusText = if (compatibility.hasAccelerometer) "Supported (Uses motion sensors)" else "Unsupported (No accelerometer)",
                onCheckedChange = { viewModel.setBackPanelEnabled(it) }
            )"""

new_bp = """            TriggerItem(
                trigger = TriggerMethod.BACK_PANEL,
                icon = Icons.Rounded.TapAndPlay,
                isChecked = backPanelEnabled,
                isSupported = compatibility.hasAccelerometer,
                statusText = if (compatibility.hasAccelerometer) "Supported (Uses motion sensors)" else "Unsupported (No accelerometer)",
                selectedAction = bpAction,
                onCheckedChange = { viewModel.setBackPanelEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.BACK_PANEL.name}") }
            )"""

content = content.replace(old_bp, new_bp)

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "w") as f:
    f.write(content)

