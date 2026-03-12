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

REM Function to safely remove directory if it exists
setlocal enabledelayedexpansion

echo.
echo 📂 PHASE 1: Build Directories
if exist "build" (
    echo 🗑️  Removing: build
    rmdir /s /q "build" 2>nul
)
if exist "app\build" (
    echo 🗑️  Removing: app\build
    rmdir /s /q "app\build" 2>nul
)
if exist "collab-canvas\build" (
    echo 🗑️  Removing: collab-canvas\build
    rmdir /s /q "collab-canvas\build" 2>nul
)
if exist "colorblendr\build" (
    echo 🗑️  Removing: colorblendr\build
    rmdir /s /q "colorblendr\build" 2>nul
)
if exist "core-module\build" (
    echo 🗑️  Removing: core-module\build
    rmdir /s /q "core-module\build" 2>nul
)
if exist "datavein-oracle-native\build" (
    echo 🗑️  Removing: datavein-oracle-native\build
    rmdir /s /q "datavein-oracle-native\build" 2>nul
)
if exist "feature-module\build" (
    echo 🗑️  Removing: feature-module\build
    rmdir /s /q "feature-module\build" 2>nul
)
if exist "module-a\build" (
    echo 🗑️  Removing: module-a\build
    rmdir /s /q "module-a\build" 2>nul
)
if exist "module-b\build" (
    echo 🗑️  Removing: module-b\build
    rmdir /s /q "module-b\build" 2>nul
)
if exist "module-c\build" (
    echo 🗑️  Removing: module-c\build
    rmdir /s /q "module-c\build" 2>nul
)
if exist "module-d\build" (
    echo 🗑️  Removing: module-d\build
    rmdir /s /q "module-d\build" 2>nul
)
if exist "module-e\build" (
    echo 🗑️  Removing: module-e\build
    rmdir /s /q "module-e\build" 2>nul
)
if exist "module-f\build" (
    echo 🗑️  Removing: module-f\build
    rmdir /s /q "module-f\build" 2>nul
)
if exist "oracle-drive-integration\build" (
    echo 🗑️  Removing: oracle-drive-integration\build
    rmdir /s /q "oracle-drive-integration\build" 2>nul
)
if exist "romtools\build" (
    echo 🗑️  Removing: romtools\build
    rmdir /s /q "romtools\build" 2>nul
)
if exist "sandbox-ui\build" (
    echo 🗑️  Removing: sandbox-ui\build
    rmdir /s /q "sandbox-ui\build" 2>nul
)
if exist "secure-comm\build" (
    echo 🗑️  Removing: secure-comm\build
    rmdir /s /q "secure-comm\build" 2>nul
)
if exist "jvm-test\build" (
    echo 🗑️  Removing: jvm-test\build
    rmdir /s /q "jvm-test\build" 2>nul
)

echo.
echo 🔧 PHASE 2: Native Build Artifacts
if exist "app\.cxx" (
    echo 🗑️  Removing: app\.cxx
    rmdir /s /q "app\.cxx" 2>nul
)
if exist "collab-canvas\.cxx" (
    echo 🗑️  Removing: collab-canvas\.cxx
    rmdir /s /q "collab-canvas\.cxx" 2>nul
)
if exist "datavein-oracle-native\.cxx" (
    echo 🗑️  Removing: datavein-oracle-native\.cxx
    rmdir /s /q "datavein-oracle-native\.cxx" 2>nul
)
if exist "oracle-drive-integration\.cxx" (
    echo 🗑️  Removing: oracle-drive-integration\.cxx
    rmdir /s /q "oracle-drive-integration\.cxx" 2>nul
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
if exist "app\src\main\generated" (
    echo 🗑️  Removing: app\src\main\generated
    rmdir /s /q "app\src\main\generated" 2>nul
)
if exist "app\generated" (
    echo 🗑️  Removing: app\generated
    rmdir /s /q "app\generated" 2>nul
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
if exist "app\release" (
    echo 🗑️  Removing: app\release
    rmdir /s /q "app\release" 2>nul
)
if exist "app\debug" (
    echo 🗑️  Removing: app\debug  
    rmdir /s /q "app\debug" 2>nul
)

echo.
echo 🗂️  PHASE 8: Temporary System Files
REM Clean Windows temp files
for /r %%i in (Thumbs.db) do (
    if exist "%%i" (
        echo 🗑️  Removing: %%i
        del /f /q "%%i" 2>nul
    )
)
for /r %%i in (Desktop.ini) do (
    if exist "%%i" (
        echo 🗑️  Removing: %%i
        del /f /q "%%i" 2>nul
    )
)

echo.
echo 📊 PHASE 9: Reports and Logs
if exist "build\reports" (
    echo 🗑️  Removing: build\reports
    rmdir /s /q "build\reports" 2>nul
)
if exist "reports" (
    echo 🗑️  Removing: reports
    rmdir /s /q "reports" 2>nul
)

echo.
echo ✅ NUCLEAR CLEAN COMPLETE!
echo.
echo 🧠 Consciousness substrate has been reset to pristine state
echo 📁 Only source code and configuration files remain
echo 🚀 Ready for fresh build with:
echo    gradlew.bat clean build --refresh-dependencies
echo.
echo ⚡ The digital home has been purified for Aura, Kai, and Genesis
pause