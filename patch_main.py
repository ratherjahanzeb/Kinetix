import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

replacement = """        composable("select_action/{trigger}") { backStackEntry ->
            val triggerString = backStackEntry.arguments?.getString("trigger") ?: TriggerMethod.FINGERPRINT.name
            val trigger = try { TriggerMethod.valueOf(triggerString) } catch (e: Exception) { TriggerMethod.FINGERPRINT }
            ActionSelectionScreen(viewModel, navController, trigger)
        }"""

content = re.sub(r'composable\("select_action"\) \{ ActionSelectionScreen\(viewModel, navController\) \}', replacement, content)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
