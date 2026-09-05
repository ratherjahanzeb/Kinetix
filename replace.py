import os

files_to_update = []
for root, dirs, files in os.walk("app/src/main/java/com/example"):
    for file in files:
        if file.endswith(".kt"):
            files_to_update.append(os.path.join(root, file))

for path in files_to_update:
    with open(path, "r") as f:
        content = f.read()
    
    # Shake -> Move Left
    new_content = content.replace("SHAKE", "MOVE_LEFT")
    new_content = new_content.replace("Shake phone", "Move Left")
    new_content = new_content.replace("Shake the device rapidly", "Move phone left 2 times")
    new_content = new_content.replace("shake_action", "move_left_action")
    new_content = new_content.replace("shake_app_package", "move_left_app_package")
    new_content = new_content.replace("shake_enabled", "move_left_enabled")
    new_content = new_content.replace("shakeAction", "moveLeftAction")
    new_content = new_content.replace("shakeAppPackage", "moveLeftAppPackage")
    new_content = new_content.replace("shakeEnabled", "moveLeftEnabled")
    new_content = new_content.replace("onShake", "onMoveLeft")
    new_content = new_content.replace("ShakeDetector", "MoveLeftDetector")
    new_content = new_content.replace("shakeCount", "moveLeftCount")
    
    # Aliases in HomeScreen
    new_content = new_content.replace("val sh by", "val ml by")
    new_content = new_content.replace("val sh =", "val ml =")
    new_content = new_content.replace("listOf(sh,", "listOf(ml,")
    new_content = new_content.replace("sh ||", "ml ||") 
    new_content = new_content.replace("if (sh)", "if (ml)") 
    
    # Flip -> Move Right
    new_content = new_content.replace("FLIP", "MOVE_RIGHT")
    new_content = new_content.replace("Flip phone", "Move Right")
    new_content = new_content.replace("Place the phone face down", "Move phone right 2 times")
    new_content = new_content.replace("flip_action", "move_right_action")
    new_content = new_content.replace("flip_app_package", "move_right_app_package")
    new_content = new_content.replace("flip_enabled", "move_right_enabled")
    new_content = new_content.replace("flipAction", "moveRightAction")
    new_content = new_content.replace("flipAppPackage", "moveRightAppPackage")
    new_content = new_content.replace("flipEnabled", "moveRightEnabled")
    new_content = new_content.replace("onFlip", "onMoveRight")
    new_content = new_content.replace("FlipDetector", "MoveRightDetector")
    
    # Aliases in HomeScreen
    new_content = new_content.replace("val flip by", "val mr by")
    new_content = new_content.replace("val flip =", "val mr =")
    new_content = new_content.replace(", flip,", ", mr,")
    new_content = new_content.replace("flip ||", "mr ||") 
    new_content = new_content.replace("if (flip)", "if (mr)") 
    
    # Icons
    new_content = new_content.replace("Icons.Rounded.Vibration", "Icons.Rounded.ArrowBack")
    new_content = new_content.replace("Icons.Rounded.ScreenRotation", "Icons.Rounded.ArrowForward")

    if new_content != content:
        with open(path, "w") as f:
            f.write(new_content)
        print(f"Updated {path}")
