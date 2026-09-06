package com.jahanzeb.kinetix.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jahanzeb.kinetix.clickWithVibration
import com.jahanzeb.kinetix.CompatibilityStatus
import com.jahanzeb.kinetix.MainViewModel
import com.jahanzeb.kinetix.ui.theme.*

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
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) color else TextSecondary,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
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
    
    val ml by viewModel.moveLeftEnabled.collectAsStateWithLifecycle()
    val prox by viewModel.moveBackwardEnabled.collectAsStateWithLifecycle()
    val mr by viewModel.flipPhoneEnabled.collectAsStateWithLifecycle()
    val bp by viewModel.backPanelEnabled.collectAsStateWithLifecycle()
    val activeCount = listOf(ml, prox, mr, bp).count { it }
    
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
        bottomBar = { AppBottomBar(navController, viewModel) },
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
                            text = "Kinetix",
                            style = MaterialTheme.typography.displayLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Hardware Gestures",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AuroraSecondary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            clickWithVibration(context, viewModel) {
                                navController.navigate("permissions")
                            }
                        }) {
                            Icon(
                                if (hasAllPermissions) Icons.Rounded.VerifiedUser else Icons.Rounded.GppBad,
                                contentDescription = "Permissions",
                                tint = if (hasAllPermissions) AuroraSecondary else ErrorRed,
                                modifier = Modifier.size(32.dp)
                            )
                        }
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
                            clickWithVibration(context, viewModel) {
                                if (!isEnabled && !hasAllPermissions) {
                                    navController.navigate("permissions")
                                } else {
                                    viewModel.setEnabled(!isEnabled)
                                }
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
                            onCheckedChange = { checked ->
                                clickWithVibration(context, viewModel) {
                                    if (checked && !hasAllPermissions) {
                                        navController.navigate("permissions")
                                    } else {
                                        viewModel.setEnabled(checked)
                                    }
                                }
                            },
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
                AuroraCard(onClick = {
                    clickWithVibration(context, viewModel) {
                        navController.navigate("select_trigger")
                    }
                }) {
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
                            if (ml || prox || mr || bp) {
                                if (mr) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.ArrowForward, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (ml) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.ArrowBack, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (bp) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.ArrowUpward, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (prox) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.ArrowDownward, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
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

            // Sensor Sensitivity Calibration
            item {
                val sensorSensitivity by viewModel.sensorSensitivity.collectAsStateWithLifecycle()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceVariantDark)
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlowingIconBox(icon = Icons.Rounded.Tune, isActive = true, color = AuroraSecondary, boxSize = 40.dp, iconSize = 20.dp, cornerRadius = 10.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sensor Sensitivity Calibration", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Calibrate threshold (${(sensorSensitivity * 100).toInt()}% sensitivity)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = sensorSensitivity,
                        onValueChange = { newVal ->
                            val stepsList = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
                            val closest = stepsList.minByOrNull { kotlin.math.abs(it - newVal) } ?: newVal
                            viewModel.setSensorSensitivity(closest)
                        },
                        valueRange = 0.2f..1.0f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = AuroraSecondary,
                            activeTrackColor = AuroraSecondary,
                            inactiveTrackColor = DarkSurface
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("20%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("40%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("60%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("80%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("100%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
                            GlowingIconBox(icon = Icons.Rounded.History, isActive = true, color = AuroraSecondary, boxSize = 40.dp, iconSize = 20.dp, cornerRadius = 10.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Trigger History & Analytics",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Stats and last successful gestures",
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
                                text = "No triggers recorded yet.\nTry testing a gesture!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        val stats = triggerLogs.groupingBy { it.sourceTrigger }.eachCount()
                        
                        // Analytics Row
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            stats.forEach { (trigger, count) ->
                                val displayName = when (trigger) {
                                    "MOVE_LEFT" -> "Left"
                                    "MOVE_BACKWARD" -> "Backward"
                                    "MOVE_RIGHT_PHONE" -> "Right"
                                    "BACK_PANEL" -> "Forward"
                                    else -> trigger
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AuroraSecondary.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(displayName, style = MaterialTheme.typography.labelMedium, color = AuroraSecondary)
                                        Box(
                                            modifier = Modifier.clip(androidx.compose.foundation.shape.CircleShape).background(AuroraSecondary).padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(count.toString(), style = MaterialTheme.typography.labelSmall, color = DarkBackground)
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Show only the 5 most recent in the list
                            triggerLogs.take(5).forEach { log ->
                                val dateStr = android.text.format.DateFormat.format("hh:mm:ss a, MMM dd", java.util.Date(log.timestamp)).toString()
                                val triggerDisplayName = when (log.sourceTrigger) {
                                    "MOVE_LEFT" -> "Move Left"
                                    "MOVE_BACKWARD" -> "Move Backward"
                                    "MOVE_RIGHT_PHONE" -> "Move Right"
                                    "BACK_PANEL" -> "Move Forward"
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
                                                    "MOVE_LEFT" -> Icons.Rounded.ArrowBack
                                                    "MOVE_BACKWARD" -> Icons.Rounded.ArrowDownward
                                                    "MOVE_RIGHT_PHONE" -> Icons.Rounded.ArrowForward
                                                    "BACK_PANEL" -> Icons.Rounded.ArrowUpward
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
