package com.example

enum class Action(val displayName: String, val description: String) {
    NONE("None", ""),
    HOME("Go Home", "Go to home screen"),
    RECENTS("Recent Apps", "Open recent apps"),
    NOTIFICATIONS("Notifications", "Open notification shade"),
    QUICK_SETTINGS("Quick Settings", "Open quick settings panel"),
    SCREENSHOT("Take Screenshot", "Capture current screen"),
    FLASHLIGHT("Toggle Flashlight", "Turn flashlight on/off"),
    LOCK_SCREEN("Lock Screen", "Lock the device"),
    OPEN_APP("Open App", "Launch an installed application"),
}
