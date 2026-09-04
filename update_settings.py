import re

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "r", encoding="utf-8") as f:
    content = f.read()

imports = """import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.R"""

# Add imports
content = content.replace("import androidx.compose.ui.unit.dp\n", "import androidx.compose.ui.unit.dp\n" + imports + "\n")

old_settings_screen = """@Composable
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
}"""

new_settings_screen = """@Composable
fun SettingsScreen(viewModel: MainViewModel, navController: NavController) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    
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
            
            Spacer(modifier = Modifier.weight(1f))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "😊", fontSize = 64.sp)
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
                        .clickable { uriHandler.openUri("https://www.instagram.com/rather_jahanzeb") }
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
                        .clickable { uriHandler.openUri("https://github.com/ratherjahanzeb") }
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // UPI
                Text(
                    text = "Support my work",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { 
                            clipboardManager.setText(buildAnnotatedString { append("6006029540@fam") })
                            Toast.makeText(context, "UPI ID copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Payments,
                        contentDescription = "UPI",
                        tint = AuroraPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "6006029540@fam",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = AuroraPrimary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}"""

content = content.replace(old_settings_screen, new_settings_screen)

with open("app/src/main/java/com/example/ui/screens/AuxScreens.kt", "w", encoding="utf-8") as f:
    f.write(content)
