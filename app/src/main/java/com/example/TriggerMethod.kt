package com.example

enum class TriggerMethod(val displayName: String, val description: String) {
    SHAKE("Shake Phone", "Shake your phone firmly"),
    PROXIMITY_WAVE("Proximity Wave", "Wave hand over the top of the phone"),
    FLIP_PHONE("Flip Phone", "Place phone face down, then face up"),
    BACK_PANEL("Double Press Back Panel", "Tap the back of your phone twice")
}
