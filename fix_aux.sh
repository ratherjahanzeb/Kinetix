sed -i '/selected = isSelected,/,$d' app/src/main/java/com/example/ui/screens/AuxScreens.kt
echo -e '                )\n            )\n        }\n    }\n}' >> app/src/main/java/com/example/ui/screens/AuxScreens.kt
