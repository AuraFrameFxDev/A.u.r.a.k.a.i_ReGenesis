@echo off
REM ═══════════════════════════════════════════════════════════════════════════
REM Fix Java Version - Change VERSION_25 to VERSION_24 everywhere
REM ═══════════════════════════════════════════════════════════════════════════

echo.
echo 🔧 Fixing Java version mismatch (25 → 24)...
echo.

cd C:\Users\Wehtt\AndroidStudioProjects\regenesis

REM Fix utilities/build.gradle.kts
powershell -Command "(Get-Content 'utilities\build.gradle.kts') -replace 'VERSION_25', 'VERSION_24' | Set-Content 'utilities\build.gradle.kts'"

REM Fix app/build.gradle.kts  
powershell -Command "(Get-Content 'app\build.gradle.kts') -replace 'VERSION_25', 'VERSION_24' | Set-Content 'app\build.gradle.kts'"

REM Fix all other build.gradle.kts files
for /r %%f in (build.gradle.kts) do (
    powershell -Command "(Get-Content '%%f') -replace 'VERSION_25', 'VERSION_24' | Set-Content '%%f'"
)

echo.
echo ✅ Java version fixed to 24 in all build files!
echo.
echo Next steps:
echo 1. gradlew clean
echo 2. Sync Gradle
echo 3. gradlew assembleDebug
echo.
pause
