#!/bin/bash
cat << 'INNER_EOF' >> app/src/main/java/com/example/ui/screens/AuxScreens.kt

@Composable
fun AppBottomBar(navController: NavController) {
    val navBackStackEntry by androidx.navigation.compose.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = DarkBackground,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
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
                navController.navigate("settings") {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonGreen,
                selectedTextColor = NeonGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = DarkBackground
            )
        )
    }
}
INNER_EOF
bash update_aux.sh
