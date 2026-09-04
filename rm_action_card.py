import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

# Try to remove the "Double-Tap Action" card entirely since now actions are in the Trigger menu
# Or I can rename it to "Configure Actions" and point to select_trigger. Wait, it's already redundant since select_trigger has actions now.

action_card_regex = r'            // Double Tap Action\n            item \{\n                AuroraCard\(onClick = \{ navController\.navigate\("select_action"\) \}\) \{\n                    Row\(\n                        modifier = Modifier\.fillMaxWidth\(\),\n                        verticalAlignment = Alignment\.CenterVertically\n                    \) \{\n                        GlowingIconBox\(icon = Icons\.Rounded\.AutoAwesome, isActive = true, color = AuroraPrimary\)\n                        Spacer\(modifier = Modifier\.width\(16\.dp\)\)\n                        Column\(modifier = Modifier\.weight\(1f\)\) \{\n                            Text\(\n                                text = "Double-Tap Action",\n                                style = MaterialTheme\.typography\.titleLarge,\n                                color = TextPrimary\n                            \)\n                            Text\(\n                                text = selectedAction\.displayName,\n                                style = MaterialTheme\.typography\.bodyMedium,\n                                color = TextSecondary\n                            \)\n                        \}\n                        Icon\(Icons\.Rounded\.ChevronRight, contentDescription = null, tint = TextSecondary\)\n                    \}\n                \}\n            \}\n'

content = re.sub(action_card_regex, "", content)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)

