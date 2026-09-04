#!/bin/bash
sed -i 's/fun HomeScreen(viewModel: MainViewModel, navController: NavController) {/fun HomeScreen(viewModel: MainViewModel, navController: NavController) {\n    androidx.compose.material3.Scaffold(\n        bottomBar = { com.example.ui.screens.AppBottomBar(navController) },\n        containerColor = com.example.ui.theme.DarkBackground\n    ) { innerPadding ->\n/' app/src/main/java/com/example/ui/screens/HomeScreen.kt
sed -i 's/modifier = Modifier/modifier = Modifier.padding(innerPadding)/' app/src/main/java/com/example/ui/screens/HomeScreen.kt
sed -i '$a\
    }\
}' app/src/main/java/com/example/ui/screens/HomeScreen.kt
# Wait, HomeScreen ends with a closing brace for the Column, then a closing brace for the function.
# I will just manually edit HomeScreen using a cleaner script to wrap the Column.
