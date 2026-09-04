new_code = """package com.example.ui.screens

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
        title = { Text(title, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = DarkGreyText,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun TriggerSelectionScreen(viewModel: MainViewModel, navController: NavController) {
    val fingerprintEnabled by viewModel.fingerprintEnabled.collectAsStateWithLifecycle()
    val powerButtonEnabled by viewModel.powerButtonEnabled.collectAsStateWithLifecycle()
    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    
    val compatibility = viewModel.compatibilityStatus
    
    Scaffold(
        topBar = { SimpleTopBar("Select Triggers", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SectionHeader("AVAILABLE METHODS")
            
            TriggerItem(
                trigger = TriggerMethod.FINGERPRINT,
                icon = Icons.Rounded.Fingerprint,
                isChecked = fingerprintEnabled,
                isSupported = compatibility.hasHardware && compatibility.hasFingerprintInputDevice,
                statusText = if (compatibility.hasHardware && compatibility.hasFingerprintInputDevice) "Supported" else "Unsupported (No accessible fingerprint sensor)",
                onCheckedChange = { viewModel.setFingerprintEnabled(it) }
            )
            
            TriggerItem(
                trigger = TriggerMethod.POWER_BUTTON,
                icon = Icons.Rounded.PowerSettingsNew,
                isChecked = powerButtonEnabled,
                isSupported = true,
                statusText = "Restricted (May not work due to Android OEM limitations)",
                onCheckedChange = { viewModel.setPowerButtonEnabled(it) }
            )
            
            TriggerItem(
                trigger = TriggerMethod.BACK_PANEL,
                icon = Icons.Rounded.TapAndPlay,
                isChecked = backPanelEnabled,
                isSupported = compatibility.hasAccelerometer,
                statusText = if (compatibility.hasAccelerometer) "Supported (Uses motion sensors)",
                onCheckedChange = { viewModel.setBackPanelEnabled(it) }
            )
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
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isSupported, onClick = { onCheckedChange(!isChecked) })
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .let { if (!isSupported) it.background(Color.Transparent) else it },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSupported) SurfaceVariantDark else DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSupported) Color.White else DarkGreyText)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trigger.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSupported) Color.White else DarkGreyText
            )
            Text(
                text = trigger.description,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGreyText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSupported) (if (statusText.startsWith("Restricted")) Color(0xFFFFB300) else NeonGreen) else ErrorRed
            )
        }
        if (isSupported) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = NeonGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun ActionSelectionScreen(viewModel: MainViewModel, navController: NavController) {
    val selectedAction by viewModel.selectedAction.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { SimpleTopBar("Select Action", navController) },
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
                    onClick = { viewModel.setSelectedAction(action) }
                )
            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceVariantDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = NeonGreen)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            if (action.description.isNotEmpty()) {
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGreyText
                )
            }
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = NeonGreen,
                unselectedColor = DarkGreyText
            )
        )
    }
}

@Composable
fun CompatibilityScreen(viewModel: MainViewModel, navController: NavController) {
    val compatibility = viewModel.refreshCompatibility()
    Scaffold(
        topBar = { SimpleTopBar("Compatibility Details", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text("Hardware Support: ${compatibility.hasHardware}", color = Color.White)
            Text("Fingerprint Input: ${compatibility.hasFingerprintInputDevice}", color = Color.White)
            Text("Accessibility Enabled: ${compatibility.isAccessibilityEnabled}", color = Color.White)
            Text("Accelerometer: ${compatibility.hasAccelerometer}", color = Color.White)
        }
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
            Text("More settings coming soon...", color = DarkGreyText)
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
            Text("Double tap the physical sensor now.", color = Color.White)
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "w") as f:
    f.write(new_code)
