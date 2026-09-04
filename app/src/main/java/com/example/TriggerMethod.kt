package com.example

enum class TriggerMethod(val displayName: String, val description: String) {
    FINGERPRINT("Fingerprint Sensor", "Double tap your physical fingerprint sensor"),
    POWER_BUTTON("Double Press Power Button", "Press the power button twice"),
    BACK_PANEL("Double Press Back Panel", "Tap the back of your phone twice")
}
