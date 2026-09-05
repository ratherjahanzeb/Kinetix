package com.example.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.AuroraSecondary

import androidx.compose.ui.platform.LocalContext
import com.example.MainViewModel
import com.example.clickWithVibration

@Composable
fun AppBottomBar(navController: NavController, viewModel: MainViewModel? = null) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    NavigationBar(
        containerColor = DarkBackground,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
            onClick = {
                clickWithVibration(context, viewModel) {
                    navController.navigate("home") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AuroraSecondary,
                selectedTextColor = AuroraSecondary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = DarkBackground
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
            onClick = {
                clickWithVibration(context, viewModel) {
                    navController.navigate("settings") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AuroraSecondary,
                selectedTextColor = AuroraSecondary,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = DarkBackground
            )
        )
    }
}
