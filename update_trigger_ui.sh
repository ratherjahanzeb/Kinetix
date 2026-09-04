sed -i 's/val activeTrigger by viewModel.activeTrigger.collectAsStateWithLifecycle()/val fingerprintEnabled by viewModel.fingerprintEnabled.collectAsStateWithLifecycle()\n    val powerButtonEnabled by viewModel.powerButtonEnabled.collectAsStateWithLifecycle()\n    val backPanelEnabled by viewModel.backPanelEnabled.collectAsStateWithLifecycle()/' app/src/main/java/com/example/ui/screens/AuxScreens.kt

sed -i 's/isSelected = activeTrigger == TriggerMethod.FINGERPRINT,/isChecked = fingerprintEnabled,/' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i 's/onClick = { viewModel.setActiveTrigger(TriggerMethod.FINGERPRINT) }/onCheckedChange = { viewModel.setFingerprintEnabled(it) }/' app/src/main/java/com/example/ui/screens/AuxScreens.kt

sed -i 's/isSelected = activeTrigger == TriggerMethod.POWER_BUTTON,/isChecked = powerButtonEnabled,/' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i 's/onClick = { viewModel.setActiveTrigger(TriggerMethod.POWER_BUTTON) }/onCheckedChange = { viewModel.setPowerButtonEnabled(it) }/' app/src/main/java/com/example/ui/screens/AuxScreens.kt

sed -i 's/isSelected = activeTrigger == TriggerMethod.BACK_PANEL,/isChecked = backPanelEnabled,/' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i 's/onClick = { viewModel.setActiveTrigger(TriggerMethod.BACK_PANEL) }/onCheckedChange = { viewModel.setBackPanelEnabled(it) }/' app/src/main/java/com/example/ui/screens/AuxScreens.kt

sed -i 's/fun TriggerItem(/fun TriggerItem(\n    isChecked: Boolean,\n    onCheckedChange: (Boolean) -> Unit,/' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i 's/isSelected: Boolean,//' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i 's/onClick: () -> Unit//' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i 's/.clickable(enabled = isSupported, onClick = onClick)/.clickable(enabled = isSupported, onClick = { onCheckedChange(!isChecked) })/' app/src/main/java/com/example/ui/screens/AuxScreens.kt

# Change RadioButton to Switch or Checkbox
sed -i 's/RadioButton(/Switch(\n                checked = isChecked,\n                onCheckedChange = onCheckedChange,\n                colors = SwitchDefaults.colors(\n                    checkedThumbColor = Color.Black,\n                    checkedTrackColor = NeonGreen,\n                    uncheckedThumbColor = Color.White,\n                    uncheckedTrackColor = Color.DarkGray\n                )\n            )\n        }\n    }\n}\n/' app/src/main/java/com/example/ui/screens/AuxScreens.kt

# Delete from RadioButton to end of file and replace with Switch logic above
# It's safer to just replace the RadioButton explicitly.
