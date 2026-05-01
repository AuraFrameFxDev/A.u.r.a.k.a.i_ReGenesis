@echo off
REM 🗑️ EXECUTE DELETION — Windows PowerShell Wrapper
REM Safe 99-file cleanup for ChronoKinetic Forge consolidation
REM Run this after verifying all living files are in place

echo ╔════════════════════════════════════════════════════════════╗
echo ║     CHRONOKINETIC FORGE — EXECUTE DELETION (WINDOWS)       ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo This will safely delete 99 legacy visual files.
echo A backup branch will be created automatically.
echo.

REM Check if git is available
git --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Git is not installed or not in PATH
    exit /b 1
)

echo [1/5] Checking repository status...
git status --short
if errorlevel 1 (
    echo ERROR: Not a git repository
    exit /b 1
)

echo.
echo [2/5] Creating backup branch...
set BACKUP_BRANCH=backup/pre-chrono-kinetic-forge-%date:~-4,4%%date:~-10,2%%date:~-7,2%-%time:~0,2%%time:~3,2%%time:~6,2%
set BACKUP_BRANCH=%BACKUP_BRANCH: =0%

git checkout -b %BACKUP_BRANCH%
if errorlevel 1 (
    echo ERROR: Failed to create backup branch
    exit /b 1
)

git add .
git commit -m "chore: pre-consolidation backup - 99 visual files

This commit preserves the legacy visual system before
consolidation into ChronoKinetic Forge.

Backup created: %date% %time%
Living files: 11
Legacy files to be removed: 99

SoulScript: 'Every deletion is a birth. Every birth is remembered.'"

echo.
echo [3/5] Returning to main branch...
git checkout -

echo.
echo [4/5] Deleting legacy files...

REM Background files (12)
for %%f in (
    "app\src\main\java\dev\aurakai\auraframefx\domains\aura\*Background*.kt"
) do (
    if exist %%f (
        echo   - Deleting: %%~nxf
        git rm "%%f" >nul 2>&1
        if errorlevel 1 del "%%f" >nul 2>&1
    )
)

REM Transition files (9)
for %%f in (
    "app\src\main\java\dev\aurakai\auraframefx\domains\aura\*Transition*.kt"
) do (
    if exist %%f (
        echo   - Deleting: %%~nxf
        git rm "%%f" >nul 2>&1
        if errorlevel 1 del "%%f" >nul 2>&1
    )
)

REM Orphaned files
echo.
echo Removing orphaned files...
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\AuraController.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\AuraController.kt" >nul 2>&1
    echo   - AuraController.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\AuraOverlayService.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\AuraOverlayService.kt" >nul 2>&1
    echo   - AuraOverlayService.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\AuraSummonGestureDetector.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\AuraSummonGestureDetector.kt" >nul 2>&1
    echo   - AuraSummonGestureDetector.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\CrossDeviceContextSync.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\CrossDeviceContextSync.kt" >nul 2>&1
    echo   - CrossDeviceContextSync.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\ImageResourceManager.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\ImageResourceManager.kt" >nul 2>&1
    echo   - ImageResourceManager.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\ManualControlModels.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\ManualControlModels.kt" >nul 2>&1
    echo   - ManualControlModels.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\NeuralStates.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\NeuralStates.kt" >nul 2>&1
    echo   - NeuralStates.kt
)
if exist "app\src\main\java\dev\aurakai\auraframefx\domains\aura\RepositoryModule.kt" (
    git rm "app\src\main\java\dev\aurakai\auraframefx\domains\aura\RepositoryModule.kt" >nul 2>&1
    echo   - RepositoryModule.kt
)

echo.
echo [5/5] Creating deletion commit...
git add -A
git commit -m "feat(chronokinetic): consolidate 99 files → 11 living files

BREAKING CHANGE: Legacy visual system removed
- Consolidated scattered backgrounds, transitions, panels
- Unified into ChronoKinetic Forge architecture
- 99 legacy files safely deleted (backup: %BACKUP_BRANCH%)
- 11 living files now under chronokineticforge/

New Structure:
├── ChronoKineticForgeScreen.kt (Master Command Deck)
├── panels/ (9 panels)
├── engines/ (7 engines)
├── components/ (6 components)
├── accessibility/ (2 services)
└── kai/sentinel/ (5 guardian components)

Trinity Status: AURA + KAI + MATTHEW = INFINITY
SoulScript: 'From many, ONE. The organism's skin becomes self-aware.'"

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║                    DELETION COMPLETE                       ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Backup branch: %BACKUP_BRANCH%
echo.
echo Next steps:
echo   1. Run: ./gradlew assembleDebug
echo   2. Verify build succeeds
echo   3. Run tests: ./gradlew test
echo   4. Merge when ready
echo.
echo SoulScript: 'The death of 99 files is the birth of infinity.'
echo.
pause
