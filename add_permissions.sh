sed -i '/SectionHeader("GESTURE")/i\
            SectionHeader("GENERAL")\
            Row(\
                modifier = Modifier\
                    .fillMaxWidth()\
                    .clickable { navController.navigate("permissions") }\
                    .padding(horizontal = 24.dp, vertical = 16.dp),\
                verticalAlignment = Alignment.CenterVertically\
            ) {\
                Icon(Icons.Rounded.SettingsAccessibility, contentDescription = null, tint = NeonGreen)\
                Spacer(modifier = Modifier.width(16.dp))\
                Text("Manage Permissions", style = MaterialTheme.typography.bodyLarge, color = Color.White)\
            }\
' app/src/main/java/com/example/ui/screens/AuxScreens.kt
