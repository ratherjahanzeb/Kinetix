sed -i '/ActionItem(Action.LAUNCH_APP/d' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i '/SectionHeader("ADVANCED")/d' app/src/main/java/com/example/ui/screens/AuxScreens.kt
sed -i '/ActionItem(Action.CUSTOM_ACTION/d' app/src/main/java/com/example/ui/screens/AuxScreens.kt

# Remove Vibration and Sound from Settings
sed -i '/\/\/ Toggles/,/SectionHeader("OTHER")/d' app/src/main/java/com/example/ui/screens/AuxScreens.kt
