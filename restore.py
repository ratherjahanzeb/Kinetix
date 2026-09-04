aux_kt = """package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.Action
import com.example.MainViewModel
import com.example.TriggerMethod
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(title: String, navController: NavController) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary
        )
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun TriggerSelectionScreen(viewModel: MainViewModel, navController: NavController) {
    val fingerprintEnabled by viewModel.fingerprintEnabled.collectAsStateWithLifecycle()
    val powerButtonEnabled by viewModel.powerButtonEnabled.collectAsStateWithLifecycle()
    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    
    val fpAction by viewModel.fingerprintAction.collectAsStateWithLifecycle()
    val pbAction by viewModel.powerButtonAction.collectAsStateWithLifecycle()
    val bpAction by viewModel.backPanelAction.collectAsStateWithLifecycle()
    
    val compatibility = viewModel.compatibilityStatus
    
    Scaffold(
        topBar = { SimpleTopBar("Trigger Methods", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader("AVAILABLE SENSORS")
            
            TriggerItem(
                trigger = TriggerMethod.FINGERPRINT,
                icon = Icons.Rounded.Fingerprint,
                isChecked = fingerprintEnabled,
                isSupported = compatibility.hasHardware && compatibility.hasFingerprintInputDevice,
                statusText = if (compatibility.hasHardware && compatibility.hasFingerprintInputDevice) "Supported" else "Unsupported (No accessible fingerprint sensor)",
                selectedAction = fpAction,
                onCheckedChange = { viewModel.setFingerprintEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.FINGERPRINT.name}") }
            )
            
            TriggerItem(
                trigger = TriggerMethod.POWER_BUTTON,
                icon = Icons.Rounded.PowerSettingsNew,
                isChecked = powerButtonEnabled,
                isSupported = true,
                statusText = "Restricted (May not work due to Android OEM limitations)",
                selectedAction = pbAction,
                onCheckedChange = { viewModel.setPowerButtonEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.POWER_BUTTON.name}") }
            )
            
            TriggerItem(
                trigger = TriggerMethod.BACK_PANEL,
                icon = Icons.Rounded.TapAndPlay,
                isChecked = backPanelEnabled,
                isSupported = compatibility.hasAccelerometer,
                statusText = if (compatibility.hasAccelerometer) "Supported (Uses motion sensors)" else "Unsupported (No accelerometer)",
                selectedAction = bpAction,
                onCheckedChange = { viewModel.setBackPanelEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.BACK_PANEL.name}") }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TriggerItem(
    trigger: TriggerMethod,
    icon: ImageVector,
    isChecked: Boolean,
    isSupported: Boolean,
    statusText: String,
    selectedAction: Action,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isChecked) AuroraSecondary.copy(alpha = 0.15f) else SurfaceVariantDark,
        animationSpec = tween(300)
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = isSupported, onClick = onClick)
            .let { if (!isSupported) it.background(Color.Transparent) else it.background(containerColor) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSupported) (if (isChecked) AuroraSecondary else DarkSurface) else DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSupported) (if (isChecked) Color.White else TextPrimary) else TextSecondary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trigger.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSupported) TextPrimary else TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = trigger.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Action: ${selectedAction.displayName}",
                style = MaterialTheme.typography.labelLarge,
                color = if (isSupported) AuroraPrimary else TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSupported) (if (statusText.startsWith("Restricted")) Color(0xFFFFB300) else AuroraSecondary) else ErrorRed
            )
        }
        if (isSupported) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AuroraSecondary,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = DarkSurface,
                    uncheckedBorderColor = DividerColor
                )
            )
        }
    }
}

@Composable
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
}

@Composable
fun ActionItem(
    action: Action,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) AuroraPrimary.copy(alpha = 0.15f) else SurfaceVariantDark,
        animationSpec = tween(300)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) AuroraPrimary else DarkSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else AuroraPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            if (action.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = AuroraPrimary,
                unselectedColor = TextSecondary
            )
        )
    }
}

@Composable
fun CompatibilityScreen(viewModel: MainViewModel, navController: NavController) {
    val compatibility = viewModel.refreshCompatibility()
    Scaffold(
        topBar = { SimpleTopBar("Device Compatibility", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard("Hardware Support", compatibility.hasHardware.toString())
            InfoCard("Fingerprint Sensor", compatibility.hasFingerprintInputDevice.toString())
            InfoCard("Accessibility Enabled", compatibility.isAccessibilityEnabled.toString())
            InfoCard("Accelerometer", compatibility.hasAccelerometer.toString())
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariantDark)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        Text(value.uppercase(), style = MaterialTheme.typography.labelMedium, color = if (value == "true") AuroraSecondary else ErrorRed)
    }
}

@Composable
fun SettingsScreen(viewModel: MainViewModel, navController: NavController) {
    Scaffold(
        topBar = { SimpleTopBar("Settings", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .clickable { navController.navigate("compatibility") }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Memory, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Device Compatibility", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
        }
    }
}

@Composable
fun TestGestureScreen(viewModel: MainViewModel, navController: NavController) {
    Scaffold(
        topBar = { SimpleTopBar("Test Gesture", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Rounded.Sensors, 
                    contentDescription = null, 
                    tint = AuroraSecondary, 
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Awaiting Hardware Gesture", 
                    style = MaterialTheme.typography.headlineMedium, 
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Double tap the physical sensor now.", 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = TextSecondary
                )
            }
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "w") as f:
    f.write(aux_kt)
