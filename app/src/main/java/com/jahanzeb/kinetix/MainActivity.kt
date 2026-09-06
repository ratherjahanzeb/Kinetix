package com.jahanzeb.kinetix

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
import com.jahanzeb.kinetix.ui.screens.ActionSelectionScreen
import com.jahanzeb.kinetix.ui.screens.CompatibilityScreen
import com.jahanzeb.kinetix.ui.screens.HomeScreen
import com.jahanzeb.kinetix.ui.screens.PermissionsScreen
import com.jahanzeb.kinetix.ui.screens.SettingsScreen
import com.jahanzeb.kinetix.ui.theme.DarkBackground
import com.jahanzeb.kinetix.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val materialYouEnabled by viewModel.materialYouEnabled.collectAsStateWithLifecycle()
            val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val amoledDarkMode by viewModel.amoledDarkMode.collectAsStateWithLifecycle()
            
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> systemDark
            }
            val isAmoled = amoledDarkMode || (themeMode == "AMOLED")

            MyApplicationTheme(
                darkTheme = darkTheme,
                dynamicColor = materialYouEnabled,
                accentTheme = accentTheme,
                amoledDarkMode = isAmoled
            ) {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize(),
        enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.scaleIn(initialScale = 0.92f, animationSpec = androidx.compose.animation.core.tween(400)) },
        exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.scaleOut(targetScale = 1.08f, animationSpec = androidx.compose.animation.core.tween(400)) },
        popEnterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.scaleIn(initialScale = 1.08f, animationSpec = androidx.compose.animation.core.tween(400)) },
        popExitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(400)) + androidx.compose.animation.scaleOut(targetScale = 0.92f, animationSpec = androidx.compose.animation.core.tween(400)) }
    ) {
        composable("splash") {
            com.jahanzeb.kinetix.ui.screens.SplashScreen(viewModel, navController, onboardingCompleted)
        }
        composable("permissions") { PermissionsScreen(viewModel, navController) }
        composable("home") { HomeScreen(viewModel, navController) }
        composable("settings") { SettingsScreen(viewModel, navController) }
        composable("select_trigger") { com.jahanzeb.kinetix.ui.screens.TriggerSelectionScreen(viewModel, navController) }
                composable("select_action/{trigger}") { backStackEntry ->
            val triggerString = backStackEntry.arguments?.getString("trigger") ?: TriggerMethod.MOVE_LEFT.name
            val trigger = try { TriggerMethod.valueOf(triggerString) } catch (e: Exception) { TriggerMethod.MOVE_LEFT }
            ActionSelectionScreen(viewModel, navController, trigger)
        }
        composable("select_app/{trigger}") { backStackEntry ->
            val triggerString = backStackEntry.arguments?.getString("trigger") ?: TriggerMethod.MOVE_LEFT.name
            val trigger = try { TriggerMethod.valueOf(triggerString) } catch (e: Exception) { TriggerMethod.MOVE_LEFT }
            com.jahanzeb.kinetix.ui.screens.AppSelectionScreen(viewModel, navController, trigger)
        }
        composable("compatibility") { CompatibilityScreen(viewModel, navController) }
        composable("gesture_test") { com.jahanzeb.kinetix.ui.screens.GestureTestScreen(viewModel, navController) }
        composable("upi_qr") { com.jahanzeb.kinetix.ui.screens.UpiQrScreen(navController) }
    }
}
