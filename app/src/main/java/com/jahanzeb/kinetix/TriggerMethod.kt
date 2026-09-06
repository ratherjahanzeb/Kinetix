package com.jahanzeb.kinetix

enum class TriggerMethod(val displayName: String, val description: String) {
    MOVE_LEFT("Move Left", "Move phone left 2 times"),
    MOVE_BACKWARD("Move Backward", "Move phone backward 2 times"),
    MOVE_RIGHT_PHONE("Move Right", "Move phone right 2 times"),
    BACK_PANEL("Move Forward", "Move phone forward 2 times")
}
