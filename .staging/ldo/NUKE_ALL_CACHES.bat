@echo off
REM ================================================================
REM 💣 GENESIS PROTOCOL - NUCLEAR CLEAN SCRIPT
REM ================================================================
REM Destroys ALL build artifacts, caches, temp files
REM Use this for completely fresh build
REM ================================================================

echo.
echo ════════════════════════════════════════════════════════════
echo 💣 GENESIS PROTOCOL - NUCLEAR CLEAN
echo ════════════════════════════════════════════════════════════
echo.
echo This will DELETE:
echo   - All build/ folders
echo   - All .gradle/ caches
echo   - All .idea/ caches
echo   - All generated KSP files
echo   - All Hilt generated code
echo   - All Gradle daemon processes
echo   - All temp files
echo.
pause

cd C:\Users\Wehtt\AndroidStudioProjects\regenesis

echo.
echo [1/8] Stopping Gradle daemon...
call gradlew --stop

echo.
echo [2/8] Deleting build folders...
for /d /r %%d in (build) do @if exist "%%d" (
    echo   Deleting: %%d
    rd /s /q "%%d" 2>nul
)

echo.
echo [3/8] Deleting .gradle cache...
if exist ".gradle" (
    echo   Deleting: .gradle
    rd /s /q ".gradle" 2>nul
)

echo.
echo [4/8] Deleting local Gradle caches...
if exist "%USERPROFILE%\.gradle\caches" (
    echo   Deleting: %USERPROFILE%\.gradle\caches
    rd /s /q "%USERPROFILE%\.gradle\caches" 2>nul
)

echo.
echo [5/8] Deleting KSP generated files...
for /d /r %%d in (.ksp) do @if exist "%%d" (
    echo   Deleting: %%d
    rd /s /q "%%d" 2>nul
)

echo.
echo [6/8] Deleting .idea caches...
if exist ".idea\caches" (
    echo   Deleting: .idea\caches
    rd /s /q ".idea\caches" 2>nul
)
if exist ".idea\libraries" (
    echo   Deleting: .idea\libraries
    rd /s /q ".idea\libraries" 2>nul
)

echo.
echo [7/8] Deleting Android Studio temp files...
for /d /r %%d in (.externalNativeBuild) do @if exist "%%d" (
    echo   Deleting: %%d
    rd /s /q "%%d" 2>nul
)
for /d /r %%d in (.cxx) do @if exist "%%d" (
    echo   Deleting: %%d
    rd /s /q "%%d" 2>nul
)

echo.
echo [8/8] Deleting Gradle wrapper cache...
if exist "%USERPROFILE%\.gradle\wrapper\dists" (
    echo   Deleting: %USERPROFILE%\.gradle\wrapper\dists
    rd /s /q "%USERPROFILE%\.gradle\wrapper\dists" 2>nul
)

echo.
echo ════════════════════════════════════════════════════════════
echo ✅ NUCLEAR CLEAN COMPLETE
echo ════════════════════════════════════════════════════════════
echo.
echo Next steps:
echo   1. gradlew clean
echo   2. gradlew assembleDebug --rerun-tasks
echo.
echo Ready for fresh consciousness emergence! 🌟
echo.
pause
