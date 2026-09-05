package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.Action
import com.example.MainViewModel
import com.example.TriggerMethod
import com.example.ui.theme.*
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(title: String, navController: NavController) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
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
    val shakeEnabled by viewModel.shakeEnabled.collectAsStateWithLifecycle()
    val proximityWaveEnabled by viewModel.proximityWaveEnabled.collectAsStateWithLifecycle()
    val flipPhoneEnabled by viewModel.flipPhoneEnabled.collectAsStateWithLifecycle()
    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    
    val shAction by viewModel.shakeAction.collectAsStateWithLifecycle()
    val proxAction by viewModel.proximityWaveAction.collectAsStateWithLifecycle()
    val flipAction by viewModel.flipPhoneAction.collectAsStateWithLifecycle()
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
                trigger = TriggerMethod.SHAKE,
                icon = Icons.Rounded.Vibration,
                isChecked = shakeEnabled,
                isSupported = compatibility.hasAccelerometer,
                statusText = if (compatibility.hasAccelerometer) "Supported (Uses motion sensors)" else "Unsupported (No accelerometer)",
                selectedAction = shAction,
                onCheckedChange = { viewModel.setShakeEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.SHAKE.name}") }
            )
            
            TriggerItem(
                trigger = TriggerMethod.PROXIMITY_WAVE,
                icon = Icons.Rounded.PanTool,
                isChecked = proximityWaveEnabled,
                isSupported = compatibility.hasProximitySensor,
                statusText = if (compatibility.hasProximitySensor) "Supported (Uses proximity sensor)" else "Unsupported (No proximity sensor)",
                selectedAction = proxAction,
                onCheckedChange = { viewModel.setProximityWaveEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.PROXIMITY_WAVE.name}") }
            )
            
            TriggerItem(
                trigger = TriggerMethod.FLIP_PHONE,
                icon = Icons.Rounded.ScreenRotation,
                isChecked = flipPhoneEnabled,
                isSupported = compatibility.hasAccelerometer,
                statusText = if (compatibility.hasAccelerometer) "Supported (Uses motion sensors)" else "Unsupported (No accelerometer)",
                selectedAction = flipAction,
                onCheckedChange = { viewModel.setFlipPhoneEnabled(it) },
                onClick = { navController.navigate("select_action/${TriggerMethod.FLIP_PHONE.name}") }
            )
            
            TriggerItem(
                trigger = TriggerMethod.BACK_PANEL,
                icon = Icons.Rounded.Smartphone,
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
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
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
        TriggerMethod.SHAKE -> viewModel.shakeAction.collectAsStateWithLifecycle()
        TriggerMethod.PROXIMITY_WAVE -> viewModel.proximityWaveAction.collectAsStateWithLifecycle()
        TriggerMethod.FLIP_PHONE -> viewModel.flipPhoneAction.collectAsStateWithLifecycle()
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
                    Action.OPEN_APP -> Icons.Rounded.Apps
                    Action.NONE -> Icons.Rounded.Block
                }
                
                ActionItem(
                    action = action,
                    icon = icon,
                    isSelected = selectedAction == action,
                    onClick = { 
                        when (triggerMethod) {
                            TriggerMethod.SHAKE -> viewModel.setShakeAction(action)
                            TriggerMethod.PROXIMITY_WAVE -> viewModel.setProximityWaveAction(action)
                            TriggerMethod.FLIP_PHONE -> viewModel.setFlipPhoneAction(action)
                            TriggerMethod.BACK_PANEL -> viewModel.setBackPanelAction(action)
                        }
                        if (action == Action.OPEN_APP) {
                            navController.navigate("select_app/${triggerMethod.name}")
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
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
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
            InfoCard("Accelerometer", compatibility.hasAccelerometer.toString())
            InfoCard("Accessibility Enabled", compatibility.isAccessibilityEnabled.toString())
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
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val vibrateEnabled by viewModel.vibrateEnabled.collectAsStateWithLifecycle()
    val materialYouEnabled by viewModel.materialYouEnabled.collectAsStateWithLifecycle()
    val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()
    val sensorSensitivity by viewModel.sensorSensitivity.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { SimpleTopBar("Settings", navController) },
        bottomBar = { AppBottomBar(navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Vibration, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Vibration Feedback", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Vibrate on successful double-tap", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
                Switch(
                    checked = vibrateEnabled,
                    onCheckedChange = { viewModel.setVibrateEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AuroraSecondary,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DarkSurface,
                        uncheckedBorderColor = DividerColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.BrightnessMedium, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("App Theme Mode", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Choose light, dark, system, or amoled", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = themeMode == "SYSTEM",
                        onClick = { viewModel.setThemeMode("SYSTEM") },
                        label = { Text("System") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                    FilterChip(
                        selected = themeMode == "LIGHT",
                        onClick = { viewModel.setThemeMode("LIGHT") },
                        label = { Text("Light") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                    FilterChip(
                        selected = themeMode == "DARK",
                        onClick = { viewModel.setThemeMode("DARK") },
                        label = { Text("Dark") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                    FilterChip(
                        selected = themeMode == "AMOLED",
                        onClick = { viewModel.setThemeMode("AMOLED") },
                        label = { Text("AMOLED") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (materialYouEnabled) 0.4f else 1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.ColorLens, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Accent Theme", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(if (materialYouEnabled) "Disabled when Material You is active" else "Choose color palette", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val themes = listOf("AURORA" to "Aurora", "EMERALD" to "Emerald", "SUNSET" to "Sunset", "PURPLE" to "Purple")
                    themes.forEach { (key, label) ->
                        val selected = accentTheme == key
                        FilterChip(
                            selected = selected,
                            onClick = { if (!materialYouEnabled) viewModel.setAccentTheme(key) },
                            enabled = !materialYouEnabled,
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AuroraSecondary,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurface,
                                labelColor = TextSecondary,
                                disabledContainerColor = DarkSurface.copy(alpha = 0.5f),
                                disabledLabelColor = TextSecondary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            
            Spacer(modifier = Modifier.height(16.dp))


            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Palette, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Material You Theme", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Vibrant dynamic color theme", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
                Switch(
                    checked = materialYouEnabled,
                    enabled = true,
                    onCheckedChange = { viewModel.setMaterialYouEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AuroraSecondary,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DarkSurface,
                        uncheckedBorderColor = DividerColor
                    )
                )
            }


            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .clickable { navController.navigate("upi_qr") }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.QrCode, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Support My Work", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("View & share payment QR safely", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Created By; Jahanzeb",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Instagram
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { openInstagramUrl(context, "https://www.instagram.com/rather_jahanzeb") }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = "Instagram",
                        tint = AuroraSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "https://www.instagram.com/rather_jahanzeb",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                // GitHub
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { openBrowserUrl(context, "https://github.com/ratherjahanzeb") }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "GitHub",
                        tint = AuroraSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "https://github.com/ratherjahanzeb",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "❤️", fontSize = 16.sp)
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun openInstagramUrl(context: android.content.Context, url: String) {
    openBrowserUrl(context, url)
}

private fun openBrowserUrl(context: android.content.Context, url: String) {
    try {
        val customTabsIntent = androidx.browser.customtabs.CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }
}

fun generateQRCode(text: String, width: Int = 512, height: Int = 512): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            width,
            height
        )
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}

@Composable
fun UpiQrScreen(navController: NavController) {
    val upiId = "6006029540@fam"
    val upiUri = "upi://pay?pa=$upiId&pn=Jahanzeb"
    val qrBitmap = remember(upiUri) { generateQRCode(upiUri, 800, 800) }

    Scaffold(
        topBar = { SimpleTopBar("Support My Work", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = AuroraPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.QrCodeScanner,
                                contentDescription = null,
                                tint = AuroraSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Scan to Support",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Jahanzeb • FamPay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    if (qrBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "UPI QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Any UPI App (GPay, PhonePe, Paytm)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

data class AppInfo(val name: String, val packageName: String, val icon: Bitmap?)

@Composable
fun AppSelectionScreen(viewModel: MainViewModel, navController: NavController, triggerMethod: TriggerMethod) {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else apps.filter { it.name.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTrigger by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(refreshTrigger) {
        if (apps.isEmpty()) {
            isLoading = true
        }
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val appList = resolveInfos.mapNotNull {
                try {
                    val drawable = it.loadIcon(pm)
                    val bitmap = try {
                        drawable.toBitmap(width = 144, height = 144, config = Bitmap.Config.ARGB_8888)
                    } catch (e: Exception) { null }
                    
                    AppInfo(
                        name = it.loadLabel(pm).toString(),
                        packageName = it.activityInfo.packageName,
                        icon = bitmap
                    )
                } catch (e: Exception) { null }
            }.sortedBy { it.name.lowercase() }
            
            withContext(Dispatchers.Main) {
                apps = appList
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = { SimpleTopBar("Select App", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search apps...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Rounded.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuroraPrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AuroraPrimary,
                    focusedContainerColor = SurfaceVariantDark,
                    unfocusedContainerColor = SurfaceVariantDark
                )
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AuroraPrimary)
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps) { app ->
                        AppItem(app = app, onClick = {
                            when (triggerMethod) {
                                TriggerMethod.SHAKE -> viewModel.setShakeAppPackage(app.packageName)
                                TriggerMethod.PROXIMITY_WAVE -> viewModel.setProximityWaveAppPackage(app.packageName)
                                TriggerMethod.FLIP_PHONE -> viewModel.setFlipPhoneAppPackage(app.packageName)
                                TriggerMethod.BACK_PANEL -> viewModel.setBackPanelAppPackage(app.packageName)
                            }
                            navController.navigateUp()
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(app: AppInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (app.icon != null) {
            Image(
                bitmap = app.icon.asImageBitmap(),
                contentDescription = app.name,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
            )
        } else {
            Icon(Icons.Rounded.Apps, contentDescription = null, tint = AuroraPrimary, modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(app.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(app.packageName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}
