import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

content = re.sub(r'    val selectedAction by viewModel\.selectedAction\.collectAsStateWithLifecycle\(\)\n', '', content)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)

