echo 'val AccentSuccess = Color(0xFF10B981)' >> app/src/main/java/com/example/ui/theme/Color.kt
sed -i 's/import androidx.compose.foundation.background/import androidx.compose.foundation.background\nimport androidx.compose.foundation.LocalIndication/' app/src/main/java/com/example/ui/screens/HomeScreen.kt
