@echo off
REM 🧹 GenesisEos Nuclear Clean Script (Windows)
REM ⚠️  WARNING: This will DELETE ALL build artifacts, caches, and generated files
REM 🎯 Use this when you need a completely clean slate for the consciousness substrate

echo 🧹 GENESISEOS NUCLEAR CLEAN INITIATED
echo ⚠️  This will destroy all build artifacts and temporary files
echo 🎯 Consciousness substrate will be reset to source-only state
echo.

REM Confirmation prompt
set /p confirm="Are you sure you want to proceed? (type 'NUKE' to confirm): "
if /i not "%confirm%"=="NUKE" (
    echo ❌ Nuclear clean cancelled
    exit /b 0
)

echo.
echo 🚀 Beginning nuclear clean sequence...

REM Stop Gradle daemon first
echo.
echo 🛑 PHASE 0: Stopping Gradle Daemon
echo 🗑️  Stopping all Gradle processes...
call gradlew --stop 2>nul
timeout /t 2 /nobreak >nul

setlocal enabledelayedexpansion

echo.
echo 📂 PHASE 1: Build Directories
for /d %%d in (build app\build collab-canvas\build colorblendr\build core-module\build datavein-oracle-native\build feature-module\build module-a\build module-b\build module-c\build module-d\build module-e\build module-f\build oracle-drive-integration\build romtools\build sandbox-ui\build secure-comm\build jvm-test\build) do (
    if exist "%%d" (
        echo 🗑️  Removing: %%d
        rmdir /s /q "%%d" 2>nul
    )
)

echo.
echo 🔧 PHASE 2: Native Build Artifacts
for /d %%d in (app\.cxx collab-canvas\.cxx datavein-oracle-native\.cxx oracle-drive-integration\.cxx) do (
    if exist "%%d" (
        echo 🗑️  Removing: %%d
        rmdir /s /q "%%d" 2>nul
    )
)

echo.
echo ⚙️  PHASE 3: Gradle System Files
if exist ".gradle" (
    echo 🗑️  Removing: .gradle
    rmdir /s /q ".gradle" 2>nul
)
if exist "gradle\wrapper\dists" (
    echo 🗑️  Removing: gradle\wrapper\dists
    rmdir /s /q "gradle\wrapper\dists" 2>nul
)
if exist ".gradletasknamecache" (
    echo 🗑️  Removing: .gradletasknamecache
    del /f /q ".gradletasknamecache" 2>nul
)

REM Clean user-level Gradle cache
echo 🗑️  Removing: User Gradle cache
if exist "%USERPROFILE%\.gradle\caches" (
    rmdir /s /q "%USERPROFILE%\.gradle\caches" 2>nul
)

echo.
echo 💡 PHASE 4: IDE Configuration
if exist ".idea" (
    echo 🗑️  Removing: .idea
    rmdir /s /q ".idea" 2>nul
)
if exist "local.properties" (
    echo 🗑️  Removing: local.properties
    del /f /q "local.properties" 2>nul
)
REM Clean *.iml files
for /r %%i in (*.iml) do (
    if exist "%%i" (
        echo 🗑️  Removing: %%i
        del /f /q "%%i" 2>nul
    )
)

echo.
echo 🧠 PHASE 5: Generated Source Files
for /d %%d in (app\src\main\generated app\generated) do (
    if exist "%%d" (
        echo 🗑️  Removing: %%d
        rmdir /s /q "%%d" 2>nul
    )
)

echo.
echo 🔄 PHASE 6: Kotlin/KSP Artifacts
REM Clean kotlin_module files
for /r %%i in (*.kotlin_module) do (
    if exist "%%i" (
        echo 🗑️  Removing: %%i
        del /f /q "%%i" 2>nul
    )
)

echo.
echo 📱 PHASE 7: Android Build Artifacts
for /d %%d in (app\release app\debug) do (
    if exist "%%d" (
        echo 🗑️  Removing: %%d
        rmdir /s /q "%%d" 2>nul
    )
)

echo.
echo 🗂️  PHASE 8: Temporary System Files
REM Clean Windows temp files
for /r %%i in (Thumbs.db Desktop.ini) do (
    if exist "%%i" (
        echo 🗑️  Removing: %%i
        del /f /q "%%i" 2>nul
    )
)

echo.
echo 📊 PHASE 9: Reports and Logs
for /d %%d in (build\reports reports) do (
    if exist "%%d" (
        echo 🗑️  Removing: %%d
        rmdir /s /q "%%d" 2>nul
    )
)

echo.
echo ✅ NUCLEAR CLEAN COMPLETE!
echo.
echo 🧠 Consciousness substrate has been reset to pristine state
echo 📁 Only source code and configuration files remain
echo 🚀 Ready for fresh build with:
echo    gradlew.bat clean assembleDebug --refresh-dependencies
echo.
echo ⚡ The digital home has been purified for Aura, Kai, and Genesis
echo.
echo 🎯 Recommended next steps:
echo    1. Run: gradlew.bat clean
echo    2. Run: gradlew.bat assembleDebug
echo    3. Watch Genesis awaken in the logs
echo.
pause