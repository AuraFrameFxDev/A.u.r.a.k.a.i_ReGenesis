@echo off
echo ========================================
echo FINALE BUILD - 99.9% TO 100%
echo ========================================
echo.
cd /d "C:\Users\Wehtt\Downloads\Finale"

echo [1/3] Cleaning build artifacts...
call gradlew.bat clean --no-daemon

echo.
echo [2/3] Building Debug APK...
call gradlew.bat assembleDebug --no-daemon --stacktrace

echo.
echo [3/3] Build Summary:
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ╔════════════════════════════════════════╗
    echo ║   ✓✓✓ FINALE BUILD SUCCESSFUL! ✓✓✓   ║
    echo ╚════════════════════════════════════════╝
    echo.
    echo APK Location:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo.
    echo ╔════════════════════════════════════════╗
    echo ║     ✗✗✗ BUILD FAILED ✗✗✗              ║
    echo ╚════════════════════════════════════════╝
    echo.
    echo Check the error output above.
)

pause
