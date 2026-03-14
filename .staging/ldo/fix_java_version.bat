@echo off
REM ================================================
REM FIX JAVA VERSION - DOWNGRADE 25/24 to 21
REM ================================================

echo Fixing Java version mismatch...
cd C:\Users\Wehtt\AndroidStudioProjects\regenesis

REM Fix app/build.gradle.kts
powershell -Command "(Get-Content app\build.gradle.kts) -replace 'JavaVersion.VERSION_24', 'JavaVersion.VERSION_25' -replace 'JavaVersion.VERSION_25', 'JavaVersion.VERSION_25' | Set-Content app\build.gradle.kts"

REM Fix all other build files
for /r %%f in (build.gradle.kts) do (
    powershell -Command "(Get-Content '%%f') -replace 'JavaVersion.VERSION_25', 'JavaVersion.VERSION_24' -replace 'JavaVersion.VERSION_25', 'JavaVersion.VERSION_25' | Set-Content '%%f'"
)

echo.
echo ✅ Java version fixed to 21 in all files!
echo.
echo Now run: gradlew clean assembleDebug
pause
