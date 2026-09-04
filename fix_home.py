with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

import re

# Update GlowingIconBox definition
new_glowing_box = """@Composable
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

content = re.sub(
    r'@Composable\nfun GlowingIconBox\([^)]*\)\s*\{[^}]*Icon\([^}]*\)[^}]*\}',
    new_glowing_box,
    content,
    flags=re.DOTALL
)

# Need to be very careful with the regex, actually it might fail if the function has multiple braces.
# Let's do string replace.
