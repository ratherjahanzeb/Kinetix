sed -i 's/import androidx.compose.ui.platform.LocalContext/import androidx.compose.ui.platform.LocalContext\nimport androidx.compose.ui.platform.LocalLifecycleOwner\nimport androidx.lifecycle.Lifecycle\nimport androidx.lifecycle.LifecycleEventObserver/g' app/src/main/java/com/example/ui/screens/HomeScreen.kt

sed -i '/val compatibility =/d' app/src/main/java/com/example/ui/screens/HomeScreen.kt

sed -i '/Scaffold(/i\
    val compatibility by remember { mutableStateOf(viewModel.refreshCompatibility()) }\
    val context = LocalContext.current\
    var hasAllPermissions by remember { mutableStateOf(false) }\
    val autostartChecked by viewModel.autostartChecked.collectAsStateWithLifecycle()\
\
    val lifecycleOwner = LocalLifecycleOwner.current\
    DisposableEffect(lifecycleOwner, autostartChecked) {\
        val observer = LifecycleEventObserver { _, event ->\
            if (event == Lifecycle.Event.ON_RESUME) {\
                val accessibility = viewModel.refreshCompatibility().isAccessibilityEnabled\
                val battery = isIgnoringBatteryOptimizations(context)\
                hasAllPermissions = accessibility \&\& battery \&\& autostartChecked\
                \
                if (!hasAllPermissions \&\& isEnabled) {\
                    viewModel.setEnabled(false)\
                }\
            }\
        }\
        lifecycleOwner.lifecycle.addObserver(observer)\
        val accessibility = viewModel.refreshCompatibility().isAccessibilityEnabled\
        val battery = isIgnoringBatteryOptimizations(context)\
        hasAllPermissions = accessibility \&\& battery \&\& autostartChecked\
        if (!hasAllPermissions \&\& isEnabled) {\
            viewModel.setEnabled(false)\
        }\
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }\
    }\
' app/src/main/java/com/example/ui/screens/HomeScreen.kt

