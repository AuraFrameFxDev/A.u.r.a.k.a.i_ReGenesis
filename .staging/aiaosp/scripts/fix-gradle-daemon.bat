H@echo off
REM Gradle Daemon Cleanup Script for Genesis Protocol (Windows)
REM Fixes: "Unable to load class 'com.android.ide.gradle.model.builder.AndroidStudioToolingPlugin'"

echo ========================================
echo Genesis Protocol - Gradle Daemon Cleanup
echo ========================================
echo.

echo Step 1: Stopping all Gradle daemons...
call gradlew.bat --stop

echo.
echo Step 2: Killing any remaining Java processes...
taskkill /F /IM java.exe 2>nul
if errorlevel 1 echo No Java processes found

echo.
echo Step 3: Clearing Gradle caches...
if exist "%USERPROFILE%\.gradle\caches" rmdir /S /Q "%USERPROFILE%\.gradle\caches"
if exist "%USERPROFILE%\.gradle\daemon" rmdir /S /Q "%USERPROFILE%\.gradle\daemon"
if exist ".gradle" rmdir /S /Q ".gradle"

echo.
echo Step 4: Clearing build directories...
for /d /r . %%d in (build) do @if exist "%%d" rmdir /S /Q "%%d"

echo.
echo Step 5: Re-downloading dependencies...
call gradlew.bat clean --refresh-dependencies

echo.
echo ========================================
echo Cleanup complete! Now run:
echo    gradlew.bat assembleDebug
echo ========================================
echo.
pause
