import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

# Replace GlowingIconBox
old_glowing = """@Composable
fun GlowingIconBox(
    icon: ImageVector,
    isActive: Boolean = false,
    color: Color = AuroraPrimary
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
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
    }
}"""

new_glowing = """@Composable
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
}"""

content = content.replace(old_glowing, new_glowing)

old_trigger = """            // Trigger Methods
            item {
                AuroraCard(onClick = { navController.navigate("select_trigger") }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlowingIconBox(icon = Icons.Rounded.TouchApp, isActive = activeCount > 0, color = AuroraSecondary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trigger Methods",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = if (activeCount == 0) "None selected" else "$activeCount methods active",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }"""

new_trigger = """            // Trigger Methods
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
                            if (fp || pb || bp) {
                                if (fp) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.Fingerprint, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (pb) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.PowerSettingsNew, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                                if (bp) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        GlowingIconBox(icon = Icons.Rounded.TapAndPlay, isActive = true, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                    }
                                }
                            } else {
                                GlowingIconBox(icon = Icons.Rounded.TouchApp, isActive = false, color = AuroraSecondary, boxSize = 48.dp, iconSize = 24.dp, cornerRadius = 12.dp)
                                Text("No triggers selected", modifier = Modifier.align(Alignment.CenterVertically), color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }"""

content = content.replace(old_trigger, new_trigger)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)
