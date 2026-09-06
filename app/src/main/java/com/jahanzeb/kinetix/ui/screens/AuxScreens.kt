package com.jahanzeb.kinetix.ui.screens

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
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.browser.customtabs.CustomTabsIntent
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.jahanzeb.kinetix.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jahanzeb.kinetix.Action
import com.jahanzeb.kinetix.MainViewModel
import com.jahanzeb.kinetix.AppInfo
import com.jahanzeb.kinetix.TriggerMethod
import com.jahanzeb.kinetix.clickWithVibration
import com.jahanzeb.kinetix.ui.theme.*
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
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
    val context = LocalContext.current
    val moveLeftEnabled by viewModel.moveLeftEnabled.collectAsStateWithLifecycle()
    val moveBackwardEnabled by viewModel.moveBackwardEnabled.collectAsStateWithLifecycle()
    val flipPhoneEnabled by viewModel.flipPhoneEnabled.collectAsStateWithLifecycle()
    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    
    val shAction by viewModel.moveLeftAction.collectAsStateWithLifecycle()
    val proxAction by viewModel.moveBackwardAction.collectAsStateWithLifecycle()
    val moveRightAction by viewModel.flipPhoneAction.collectAsStateWithLifecycle()
    val bpAction by viewModel.backPanelAction.collectAsStateWithLifecycle()

    val moveLeftPkg by viewModel.moveLeftAppPackage.collectAsStateWithLifecycle()
    val moveBackwardPkg by viewModel.moveBackwardAppPackage.collectAsStateWithLifecycle()
    val flipPhonePkg by viewModel.flipPhoneAppPackage.collectAsStateWithLifecycle()
    val backPanelPkg by viewModel.backPanelAppPackage.collectAsStateWithLifecycle()

    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    fun getAppLabel(packageName: String?): String? {
        if (packageName == null || installedApps == null) return null
        return installedApps?.find { it.packageName == packageName }?.name
    }
    
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
                trigger = TriggerMethod.MOVE_RIGHT_PHONE,
                icon = Icons.Rounded.ArrowForward,
                isChecked = flipPhoneEnabled,
                selectedAction = moveRightAction,
                appLabel = getAppLabel(flipPhonePkg),
                onCheckedChange = { checked ->
                    clickWithVibration(context, viewModel) {
                        viewModel.setFlipPhoneEnabled(checked)
                    }
                },
                onClick = {
                    clickWithVibration(context, viewModel) {
                        navController.navigate("select_action/${TriggerMethod.MOVE_RIGHT_PHONE.name}")
                    }
                }
            )
            
            TriggerItem(
                trigger = TriggerMethod.MOVE_LEFT,
                icon = Icons.Rounded.ArrowBack,
                isChecked = moveLeftEnabled,
                selectedAction = shAction,
                appLabel = getAppLabel(moveLeftPkg),
                onCheckedChange = { checked ->
                    clickWithVibration(context, viewModel) {
                        viewModel.setShakeEnabled(checked)
                    }
                },
                onClick = {
                    clickWithVibration(context, viewModel) {
                        navController.navigate("select_action/${TriggerMethod.MOVE_LEFT.name}")
                    }
                }
            )
            
            TriggerItem(
                trigger = TriggerMethod.BACK_PANEL,
                icon = Icons.Rounded.ArrowUpward,
                isChecked = backPanelEnabled,
                selectedAction = bpAction,
                appLabel = getAppLabel(backPanelPkg),
                onCheckedChange = { checked ->
                    clickWithVibration(context, viewModel) {
                        viewModel.setBackPanelEnabled(checked)
                    }
                },
                onClick = {
                    clickWithVibration(context, viewModel) {
                        navController.navigate("select_action/${TriggerMethod.BACK_PANEL.name}")
                    }
                }
            )
            
            TriggerItem(
                trigger = TriggerMethod.MOVE_BACKWARD,
                icon = Icons.Rounded.ArrowDownward,
                isChecked = moveBackwardEnabled,
                selectedAction = proxAction,
                appLabel = getAppLabel(moveBackwardPkg),
                onCheckedChange = { checked ->
                    clickWithVibration(context, viewModel) {
                        viewModel.setProximityWaveEnabled(checked)
                    }
                },
                onClick = {
                    clickWithVibration(context, viewModel) {
                        navController.navigate("select_action/${TriggerMethod.MOVE_BACKWARD.name}")
                    }
                }
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
    selectedAction: Action,
    appLabel: String? = null,
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
            .clickable(onClick = onClick)
            .background(containerColor)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isChecked) AuroraSecondary else DarkSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isChecked) Color.White else TextPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = trigger.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = trigger.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            val actionDisplay = if (selectedAction == Action.OPEN_APP && !appLabel.isNullOrEmpty()) {
                "Action: Open $appLabel"
            } else {
                "Action: ${selectedAction.displayName}"
            }
            Text(
                text = actionDisplay,
                style = MaterialTheme.typography.labelLarge,
                color = AuroraPrimary
            )
        }
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

@Composable
fun ActionSelectionScreen(viewModel: MainViewModel, navController: NavController, triggerMethod: TriggerMethod) {
    val context = LocalContext.current
    val selectedAction by when (triggerMethod) {
        TriggerMethod.MOVE_LEFT -> viewModel.moveLeftAction.collectAsStateWithLifecycle()
        TriggerMethod.MOVE_BACKWARD -> viewModel.moveBackwardAction.collectAsStateWithLifecycle()
        TriggerMethod.MOVE_RIGHT_PHONE -> viewModel.flipPhoneAction.collectAsStateWithLifecycle()
        TriggerMethod.BACK_PANEL -> viewModel.backPanelAction.collectAsStateWithLifecycle()
    }

    val selectedPkg by when (triggerMethod) {
        TriggerMethod.MOVE_LEFT -> viewModel.moveLeftAppPackage.collectAsStateWithLifecycle()
        TriggerMethod.MOVE_BACKWARD -> viewModel.moveBackwardAppPackage.collectAsStateWithLifecycle()
        TriggerMethod.MOVE_RIGHT_PHONE -> viewModel.flipPhoneAppPackage.collectAsStateWithLifecycle()
        TriggerMethod.BACK_PANEL -> viewModel.backPanelAppPackage.collectAsStateWithLifecycle()
    }

    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    val selectedAppName = remember(selectedPkg, installedApps) {
        installedApps?.find { it.packageName == selectedPkg }?.name
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
            val categories = listOf(
                "GENERAL" to listOf(Action.NONE, Action.OPEN_APP),
                "SYSTEM & NAVIGATION" to listOf(Action.HOME, Action.BACK, Action.RECENTS, Action.NOTIFICATIONS, Action.QUICK_SETTINGS, Action.SPLIT_SCREEN, Action.SCREENSHOT, Action.LOCK_SCREEN, Action.POWER_DIALOG, Action.FLASHLIGHT),
                "MEDIA & PLAYBACK" to listOf(Action.MEDIA_PLAY_PAUSE, Action.NEXT_TRACK, Action.PREV_TRACK),
                "VOLUME & AUDIO" to listOf(Action.VOLUME_UP, Action.VOLUME_DOWN, Action.MUTE_AUDIO, Action.RINGER_NORMAL, Action.RINGER_VIBRATE, Action.RINGER_SILENT),
                "DISPLAY & BRIGHTNESS" to listOf(Action.BRIGHTNESS_UP, Action.BRIGHTNESS_DOWN)
            )

            categories.forEach { (categoryName, actions) ->
                SectionHeader(categoryName)
                actions.forEach { action ->
                    val icon = when (action) {
                        Action.HOME -> Icons.Rounded.Home
                        Action.RECENTS -> Icons.Rounded.ViewAgenda
                        Action.NOTIFICATIONS -> Icons.Rounded.Notifications
                        Action.QUICK_SETTINGS -> Icons.Rounded.Settings
                        Action.SCREENSHOT -> Icons.Rounded.Screenshot
                        Action.LOCK_SCREEN -> Icons.Rounded.Lock
                        Action.SPLIT_SCREEN -> Icons.Rounded.GridView
                        Action.POWER_DIALOG -> Icons.Rounded.PowerSettingsNew
                        Action.VOLUME_UP -> Icons.Rounded.VolumeUp
                        Action.VOLUME_DOWN -> Icons.Rounded.VolumeDown
                        Action.MEDIA_PLAY_PAUSE -> Icons.Rounded.PlayArrow
                        Action.BACK -> Icons.Rounded.ArrowBack
                        Action.NEXT_TRACK -> Icons.Rounded.SkipNext
                        Action.PREV_TRACK -> Icons.Rounded.SkipPrevious
                        Action.MUTE_AUDIO -> Icons.Rounded.VolumeOff
                        Action.RINGER_SILENT -> Icons.Rounded.VolumeMute
                        Action.RINGER_NORMAL -> Icons.Rounded.VolumeUp
                        Action.RINGER_VIBRATE -> Icons.Rounded.Vibration
                        Action.BRIGHTNESS_DOWN -> Icons.Rounded.BrightnessLow
                        Action.BRIGHTNESS_UP -> Icons.Rounded.BrightnessHigh
                        Action.FLASHLIGHT -> Icons.Rounded.FlashlightOn
                        Action.OPEN_APP -> Icons.Rounded.Apps
                        Action.NONE -> Icons.Rounded.Block
                    }
                    
                    val description = if (action == Action.OPEN_APP && !selectedAppName.isNullOrEmpty()) {
                        "Selected: $selectedAppName"
                    } else {
                        action.description
                    }

                    ActionItem(
                        action = action,
                        icon = icon,
                        isSelected = selectedAction == action,
                        descriptionOverride = description,
                        onClick = { 
                            clickWithVibration(context, viewModel) {
                                when (triggerMethod) {
                                    TriggerMethod.MOVE_LEFT -> viewModel.setShakeAction(action)
                                    TriggerMethod.MOVE_BACKWARD -> viewModel.setProximityWaveAction(action)
                                    TriggerMethod.MOVE_RIGHT_PHONE -> viewModel.setFlipPhoneAction(action)
                                    TriggerMethod.BACK_PANEL -> viewModel.setBackPanelAction(action)
                                }
                                if (action == Action.OPEN_APP) {
                                    navController.navigate("select_app/${triggerMethod.name}")
                                }
                            }
                        }
                    )
                }
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
    descriptionOverride: String? = null,
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
            val description = descriptionOverride ?: action.description
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
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
    val context = LocalContext.current
    var compatibility by remember { mutableStateOf(viewModel.refreshCompatibility()) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                compatibility = viewModel.refreshCompatibility()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = { SimpleTopBar("Device Compatibility", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompatibilityItemCard(
                icon = Icons.Rounded.Sensors,
                title = "Accelerometer Sensor",
                subtitle = "Detects physical motion, shakes, and orientation changes",
                isAvailable = compatibility.hasAccelerometer,
                actionText = if (compatibility.hasAccelerometer) "Ready" else "Missing",
                onClick = {}
            )

            CompatibilityItemCard(
                icon = Icons.Rounded.Vibration,
                title = "Vibrator / Taptic Engine",
                subtitle = "Provides haptic feedback upon successful gesture triggers",
                isAvailable = compatibility.hasVibrator,
                actionText = if (compatibility.hasVibrator) "Ready" else "Missing",
                onClick = {}
            )

            CompatibilityItemCard(
                icon = Icons.Rounded.Accessibility,
                title = "Accessibility Service Permission",
                subtitle = "Required to execute global actions like Home, Back & Recents",
                isAvailable = compatibility.isAccessibilityEnabled,
                actionText = if (compatibility.isAccessibilityEnabled) "Enabled" else "Enable",
                onClick = {
                    if (!compatibility.isAccessibilityEnabled) {
                        clickWithVibration(context, viewModel) {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                }
            )

            CompatibilityItemCard(
                icon = Icons.Rounded.Settings,
                title = "Modify System Settings",
                subtitle = "Required to adjust screen brightness directly without opening Quick Settings",
                isAvailable = compatibility.canWriteSettings,
                actionText = if (compatibility.canWriteSettings) "Allowed" else "Allow",
                onClick = {
                    if (!compatibility.canWriteSettings) {
                        clickWithVibration(context, viewModel) {
                            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CompatibilityItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isAvailable: Boolean,
    actionText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceVariantDark)
            .clickable(enabled = actionText == "Enable") { onClick() }
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            GlowingIconBox(
                icon = icon,
                isActive = isAvailable,
                color = if (isAvailable) AuroraSecondary else ErrorRed,
                boxSize = 44.dp,
                iconSize = 22.dp,
                cornerRadius = 12.dp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isAvailable) AuroraSecondary.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
        ) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                color = if (isAvailable) AuroraSecondary else ErrorRed,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun SettingsScreen(viewModel: MainViewModel, navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val vibrateEnabled by viewModel.vibrateEnabled.collectAsStateWithLifecycle()
    val hapticIntensity by viewModel.hapticIntensity.collectAsStateWithLifecycle()
    val materialYouEnabled by viewModel.materialYouEnabled.collectAsStateWithLifecycle()
    val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()
    val sensorSensitivity by viewModel.sensorSensitivity.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { SimpleTopBar("Settings", navController) },
        bottomBar = { AppBottomBar(navController, viewModel) },
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
                    .clickable {
                        clickWithVibration(context, viewModel) {
                            navController.navigate("compatibility")
                        }
                    }
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
                    .clickable {
                        clickWithVibration(context, viewModel) {
                            navController.navigate("gesture_test")
                        }
                    }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Sensors, contentDescription = null, tint = AuroraSecondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Gesture Calibration & Test", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceVariantDark)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Vibration, contentDescription = null, tint = AuroraSecondary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Vibration Feedback", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Vibrate on successful action", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = vibrateEnabled,
                        onCheckedChange = { checked ->
                            clickWithVibration(context, viewModel) {
                                viewModel.setVibrateEnabled(checked)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AuroraSecondary,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = DarkSurface,
                            uncheckedBorderColor = DividerColor
                        )
                    )
                }

                if (vibrateEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Haptic Intensity",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Slider(
                        value = hapticIntensity,
                        onValueChange = { viewModel.setHapticIntensity(it) },
                        onValueChangeFinished = { 
                            // Provide sample feedback when user finishes sliding
                            clickWithVibration(context, viewModel) {}
                        },
                        valueRange = 0f..1f,
                        steps = 9,
                        colors = androidx.compose.material3.SliderDefaults.colors(
                            thumbColor = AuroraSecondary,
                            activeTrackColor = AuroraSecondary,
                            inactiveTrackColor = DividerColor
                        )
                    )
                }
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
                        onClick = {
                            clickWithVibration(context, viewModel) {
                                viewModel.setThemeMode("SYSTEM")
                            }
                        },
                        label = { Text("System") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                    FilterChip(
                        selected = themeMode == "LIGHT",
                        onClick = {
                            clickWithVibration(context, viewModel) {
                                viewModel.setThemeMode("LIGHT")
                            }
                        },
                        label = { Text("Light") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                    FilterChip(
                        selected = themeMode == "DARK",
                        onClick = {
                            clickWithVibration(context, viewModel) {
                                viewModel.setThemeMode("DARK")
                            }
                        },
                        label = { Text("Dark") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                            selectedLabelColor = AuroraSecondary
                        )
                    )
                    FilterChip(
                        selected = themeMode == "AMOLED",
                        onClick = {
                            clickWithVibration(context, viewModel) {
                                viewModel.setThemeMode("AMOLED")
                            }
                        },
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
                        Text("Keep Kinetix Moving", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
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
                
                // Instagram & GitHub Logos Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Instagram
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark)
                            .clickable { openInstagramUrl(context, "https://www.instagram.com/rather_jahanzeb") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_instagram),
                            contentDescription = "Instagram",
                            tint = AuroraSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    // GitHub
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariantDark)
                            .clickable { openBrowserUrl(context, "https://github.com/ratherjahanzeb") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_github),
                            contentDescription = "GitHub",
                            tint = AuroraSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
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
        topBar = { SimpleTopBar("Keep Kinetix Moving", navController) },
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
                        text = "Fuel future updates and keep Kinetix completely free and ad-free.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Scan with any UPI App (GPay, PhonePe, Paytm)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AuroraSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AppSelectionScreen(viewModel: MainViewModel, navController: NavController, triggerMethod: TriggerMethod) {
    val context = LocalContext.current
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    val apps = installedApps ?: emptyList()
    val isLoading = installedApps == null

    val filteredApps = remember(apps, searchQuery) {
        if (searchQuery.isBlank()) apps
        else apps.filter { it.name.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
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
                                TriggerMethod.MOVE_LEFT -> viewModel.setShakeAppPackage(app.packageName)
                                TriggerMethod.MOVE_BACKWARD -> viewModel.setProximityWaveAppPackage(app.packageName)
                                TriggerMethod.MOVE_RIGHT_PHONE -> viewModel.setFlipPhoneAppPackage(app.packageName)
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

@Composable
fun GestureTestScreen(viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    val sensorSensitivity by viewModel.sensorSensitivity.collectAsStateWithLifecycle()
    val triggerLogs by viewModel.triggerLogs.collectAsStateWithLifecycle()

    var accelX by remember { mutableStateOf(0f) }
    var accelY by remember { mutableStateOf(0f) }
    var accelZ by remember { mutableStateOf(0f) }
    var proximityValue by remember { mutableStateOf(-1f) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
        val accelerometer = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
        val proximity = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY)

        val listener = object : android.hardware.SensorEventListener {
            override fun onSensorChanged(event: android.hardware.SensorEvent?) {
                if (event == null) return
                if (event.sensor.type == android.hardware.Sensor.TYPE_ACCELEROMETER) {
                    accelX = event.values[0]
                    accelY = event.values[1]
                    accelZ = event.values[2]
                } else if (event.sensor.type == android.hardware.Sensor.TYPE_PROXIMITY) {
                    proximityValue = event.values[0]
                }
            }
            override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, android.hardware.SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, proximity, android.hardware.SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Scaffold(
        topBar = { SimpleTopBar("Gesture Calibration & Test", navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader("LIVE SENSOR TELEMETRY")

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Accelerometer Values", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("X: %.2f m/s²".format(accelX), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("Y: %.2f m/s²".format(accelY), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text("Z: %.2f m/s²".format(accelZ), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Proximity Sensor", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("Distance: ${if (proximityValue >= 0) "$proximityValue cm" else "N/A"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            SectionHeader("SENSOR SENSITIVITY CALIBRATION")

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Sensitivity Threshold", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Adjust how sensitive motion triggers are to your movements.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = sensorSensitivity,
                        onValueChange = { viewModel.setSensorSensitivity(it) },
                        valueRange = 0.1f..1f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = AuroraSecondary,
                            activeTrackColor = AuroraSecondary,
                            inactiveTrackColor = DividerColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { clickWithVibration(context, viewModel) {} },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraSecondary)
                    ) {
                        Text("Test Haptic Vibration", color = Color.Black)
                    }
                }
            }

            SectionHeader("RECENT TRIGGER LOGS")

            if (triggerLogs.isEmpty()) {
                Text("No triggers recorded yet. Perform gestures to test!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            } else {
                triggerLogs.forEach { log ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.sourceTrigger, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("Action: ${log.actionName}", style = MaterialTheme.typography.bodySmall, color = AuroraSecondary)
                            }
                            Text(
                                text = android.text.format.DateFormat.format("hh:mm:ss a", log.timestamp).toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

