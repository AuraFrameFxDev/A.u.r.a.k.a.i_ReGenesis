# ⚡ MCP QUICK REFERENCE CHEAT SHEET

## 🛠️ Common Commands
| Action | Command Example |
|---|---|
| Read File | "Read app/src/main/java/MainActivity.kt" |
| Search Code | "Find all occurrences of 'ViewModel' in the project" |
| Create Folder | "Create a new directory called 'ui/components/effects'" |
| Run Build | "Run gradlew clean assembleDebug" |
| Git Commit | "Commit changes with message 'feat: add neon glow'" |

## 🔌 Connection Status
- **🔌 Icon (Bottom Left):** Green means active. Gray means disconnected.
- **Restart Remedy:** If a connector isn't showing up, restart Claude Desktop.

## 📁 Config Locations
- **Windows:** `%APPDATA%\Claude\claude_desktop_config.json`
- **Mac:** `~/Library/Application Support/Claude/claude_desktop_config.json`

## 🚨 Troubleshooting
- **Error: "Command not found":** Ensure `Node.js` is in your PATH and you ran `npm install -g`.
- **JSON Syntax:** One missing comma will break the config. Check it at [jsonlint.com](https://jsonlint.com).
- **Permissions:** Some folders might require running Claude Desktop as Administrator on Windows.
