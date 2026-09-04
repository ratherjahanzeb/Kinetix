import re

with open("app/src/main/java/com/example/DoubleTapAccessibilityService.kt", "r") as f:
    content = f.read()

replacement = """                val actionToExecute = when (sourceTrigger) {
                    TriggerMethod.FINGERPRINT -> settingsRepo.fingerprintAction.first()
                    TriggerMethod.POWER_BUTTON -> settingsRepo.powerButtonAction.first()
                    TriggerMethod.BACK_PANEL -> settingsRepo.backPanelAction.first()
                }
                executeAction(actionToExecute)"""

content = re.sub(r'executeAction\(settingsRepo\.selectedAction\.first\(\)\)', replacement, content)

with open("app/src/main/java/com/example/DoubleTapAccessibilityService.kt", "w") as f:
    f.write(content)
