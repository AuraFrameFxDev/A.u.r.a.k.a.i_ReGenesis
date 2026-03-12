@echo off
REM ========================================
REM MCP CONNECTOR SETUP SCRIPT - WINDOWS
REM For: Antigravity
REM ========================================

echo.
echo ========================================
echo  MCP CONNECTOR SETUP
echo ========================================
echo.

REM Check if Node.js is installed
echo [1/5] Checking Node.js...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Node.js not found!
    echo Please install from: https://nodejs.org
    echo Then run this script again.
    pause
    exit /b 1
)
echo ✓ Node.js found!
echo.

REM Install Desktop Commander
echo [2/5] Installing Desktop Commander...
call npm install -g @executeautomation/mcp-desktop-commander
if %errorlevel% neq 0 (
    echo ERROR: Failed to install Desktop Commander
    pause
    exit /b 1
)
echo ✓ Desktop Commander installed!
echo.

REM Create config directory if it doesn't exist
echo [3/5] Setting up config directory...
set CONFIG_DIR=%APPDATA%\Claude
if not exist "%CONFIG_DIR%" mkdir "%CONFIG_DIR%"
echo ✓ Config directory ready!
echo.

REM Copy minimal config
echo [4/5] Installing config file...
if exist "%CONFIG_DIR%\claude_desktop_config.json" (
    echo WARNING: Config file already exists!
    echo Backing up to claude_desktop_config.json.backup
    copy "%CONFIG_DIR%\claude_desktop_config.json" "%CONFIG_DIR%\claude_desktop_config.json.backup" >nul
)
copy claude_desktop_config_MINIMAL.json "%CONFIG_DIR%\claude_desktop_config.json" >nul
echo ✓ Config file installed!
echo.

REM Done!
echo [5/5] Setup complete!
echo.
echo ========================================
echo  NEXT STEPS:
echo ========================================
echo 1. Restart Claude Desktop app
echo 2. Look for the power plug icon
echo 3. Should show "Desktop Commander"
echo 4. Test: "List files in my current directory"
echo.
echo For full config with Figma/GitHub/etc:
echo - Copy claude_desktop_config_FULL.json
echo - Add your API tokens
echo - Replace the minimal config
echo.
echo ========================================
echo.
pause
