package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.Action
import com.example.CompatibilityStatus
import com.example.MainViewModel
import com.example.ui.theme.*

@Composable
fun AuroraCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val baseModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick)
    } else {
        modifier.clip(RoundedCornerShape(24.dp))
    }

    Box(
        modifier = baseModifier
            .background(DarkSurface)
            .border(1.dp, DividerColor, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun GlowingIconBox(
    icon: ImageVector,
    isActive: Boolean = false,
    color: Color = AuroraPrimary,
    boxSize: androidx.compose.ui.unit.Dp = 56.dp,
    iconSize: androidx.compose.ui.unit.Dp = 28.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp
) {
    val containerColor by animateColorAsState(
        targetValue = if (isActive) color.copy(alpha = 0.2f) else SurfaceVariantDark,
        animationSpec = tween(300)
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) color else TextSecondary,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .size(boxSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(iconSize))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val isEnabled by viewModel.isEnabled.collectAsStateWithLifecycle()
    val timeoutMs by viewModel.timeoutMs.collectAsStateWithLifecycle()
    val backPanelSensitivity by viewModel.backPanelSensitivity.collectAsStateWithLifecycle()
    val triggerLogs by viewModel.triggerLogs.collectAsStateWithLifecycle()
    
    val sh by viewModel.shakeEnabled.collectAsStateWithLifecycle()
    val prox by viewModel.proximityWaveEnabled.collectAsStateWithLifecycle()
    val flip by viewModel.flipPhoneEnabled.collectAsStateWithLifecycle()
    val bp by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    val activeCount = listOf(sh, prox, flip, bp).count { it }
    
    val context = LocalContext.current
    var hasAllPermissions by remember { mutableStateOf(false) }
    val autostartChecked by viewModel.autostartChecked.collectAsStateWithLifecycle()
    
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, autostartChecked) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val accessibility = viewModel.refreshCompatibility().isAccessibilityEnabled
                val battery = isIgnoringBatteryOptimizations(context)
                hasAllPermissions = accessibility && battery && autostartChecked
                if (!hasAllPermissions && viewModel.isEnabled.value) {
                    viewModel.setEnabled(false)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        val accessibility = viewModel.refreshCompatibility().isAccessibilityEnabled
        val battery = isIgnoringBatteryOptimizations(context)
        hasAllPermissions = accessibility && battery && autostartChecked
        if (!hasAllPermissions && viewModel.isEnabled.value) {
            viewModel.setEnabled(false)
        }
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TapTrigger",
                            style = MaterialTheme.typography.displayLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Hardware Gestures",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AuroraSecondary
                        )
                    }
                    IconButton(onClick = { navController.navigate("permissions") }) {
                        Icon(
                            if (hasAllPermissions) Icons.Rounded.VerifiedUser else Icons.Rounded.GppBad,
                            contentDescription = "Permissions",
                            tint = if (hasAllPermissions) AuroraSecondary else ErrorRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Master Switch
            item {
                val gradientBrush = Brush.horizontalGradient(
                    colors = listOf(AuroraPrimary, AuroraSecondary)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .let { 
                            if (isEnabled) it.background(gradientBrush) 
                            else it.background(DarkSurface).border(1.dp, DividerColor, RoundedCornerShape(32.dp))
                        }
                        .clickable {
                            if (!isEnabled && !hasAllPermissions) {
                                navController.navigate("permissions")
                            } else {
                                viewModel.setEnabled(!isEnabled)
                            }
                        }
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isEnabled) "Service Active" else "Service Paused",
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (isEnabled) Color.White else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEnabled) "Gestures are being monitored" else "Tap to enable gestures",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isEnabled) Color.White.copy(alpha = 0.8f) else TextSecondary
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AuroraPrimary,
                                checkedTrackColor = Color.White,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = SurfaceVariantDark,
                                uncheckedBorderColor = DividerColor
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = "CONFIGURATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp)
                )
            }

            // Trigger Methods
            item {
                AuroraCard(onClick = { navController.navigate("select_trigger") }) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Active Triggers",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Monitoring ${activeCount} gesture sensors",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (sh || prox || flip || bp) {
                                if (sh) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.Vibration, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (prox) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.PanTool, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (flip) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.ScreenRotation, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (bp) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.Smartphone, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                            } else {
                                GlowingIconBox(icon = Icons.Rounded.TouchApp, isActive = false, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                Text("No triggers selected", modifier = Modifier.align(Alignment.CenterVertically), color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }


            // Tap Style Selector
            item {
                AuroraCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlowingIconBox(icon = Icons.Rounded.TouchApp, isActive = false)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Back Tap Style",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = when(backPanelSensitivity) {
                                    0 -> "Hard Tap"
                                    2 -> "Smooth Tap"
                                    else -> "Balanced Tap"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = backPanelSensitivity == 0,
                            onClick = { viewModel.setBackPanelSensitivity(0) },
                            label = { Text("Hard") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                                selectedLabelColor = AuroraSecondary
                            )
                        )
                        FilterChip(
                            selected = backPanelSensitivity == 1,
                            onClick = { viewModel.setBackPanelSensitivity(1) },
                            label = { Text("Balance") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                                selectedLabelColor = AuroraSecondary
                            )
                        )
                        FilterChip(
                            selected = backPanelSensitivity == 2,
                            onClick = { viewModel.setBackPanelSensitivity(2) },
                            label = { Text("Smooth") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AuroraSecondary.copy(alpha = 0.2f),
                                selectedLabelColor = AuroraSecondary
                            )
                        )
                    }
                }
            }

            // Trigger History Log
            item {
                AuroraCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlowingIconBox(icon = Icons.Rounded.History, isActive = false, boxSize = 40.dp, iconSize = 20.dp, cornerRadius = 10.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Recent Trigger History",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Last successful double-taps",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (triggerLogs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No triggers recorded yet.\nTry testing a double-tap!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            triggerLogs.forEach { log ->
                                val dateStr = android.text.format.DateFormat.format("hh:mm:ss a, MMM dd", java.util.Date(log.timestamp)).toString()
                                val triggerDisplayName = when (log.sourceTrigger) {
                                    "SHAKE" -> "Shake Phone"
                                    "PROXIMITY_WAVE" -> "Proximity Wave"
                                    "FLIP_PHONE" -> "Flip Phone"
                                    "BACK_PANEL" -> "Back Tap"
                                    else -> log.sourceTrigger
                                }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceVariantDark)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(AuroraSecondary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (log.sourceTrigger) {
                                                    "SHAKE" -> Icons.Rounded.Vibration
                                                    "PROXIMITY_WAVE" -> Icons.Rounded.PanTool
                                                    "FLIP_PHONE" -> Icons.Rounded.ScreenRotation
                                                    "BACK_PANEL" -> Icons.Rounded.Smartphone
                                                    else -> Icons.Rounded.TouchApp
                                                },
                                                contentDescription = null,
                                                tint = AuroraSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = triggerDisplayName,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Action: ${log.actionName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AuroraPrimary
                                            )
                                        }
                                    }
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
