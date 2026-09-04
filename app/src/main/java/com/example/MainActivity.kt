package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ActionSelectionScreen
import com.example.ui.screens.CompatibilityScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
        setContent {
            val materialYouEnabled by viewModel.materialYouEnabled.collectAsStateWithLifecycle()
            val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()
            MyApplicationTheme(dynamicColor = materialYouEnabled, accentTheme = accentTheme) {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    if (onboardingCompleted == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground))
        return
    }

    val startDest = if (onboardingCompleted == true) "home" else "permissions"

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("permissions") { PermissionsScreen(viewModel, navController) }
        composable("home") { HomeScreen(viewModel, navController) }
        composable("settings") { SettingsScreen(viewModel, navController) }
        composable("select_trigger") { com.example.ui.screens.TriggerSelectionScreen(viewModel, navController) }
                composable("select_action/{trigger}") { backStackEntry ->
            val triggerString = backStackEntry.arguments?.getString("trigger") ?: TriggerMethod.FINGERPRINT.name
            val trigger = try { TriggerMethod.valueOf(triggerString) } catch (e: Exception) { TriggerMethod.FINGERPRINT }
            ActionSelectionScreen(viewModel, navController, trigger)
        }
        composable("select_app/{trigger}") { backStackEntry ->
            val triggerString = backStackEntry.arguments?.getString("trigger") ?: TriggerMethod.FINGERPRINT.name
            val trigger = try { TriggerMethod.valueOf(triggerString) } catch (e: Exception) { TriggerMethod.FINGERPRINT }
            com.example.ui.screens.AppSelectionScreen(viewModel, navController, trigger)
        }
        composable("compatibility") { CompatibilityScreen(viewModel, navController) }
        composable("upi_qr") { com.example.ui.screens.UpiQrScreen(navController) }
    }
}
