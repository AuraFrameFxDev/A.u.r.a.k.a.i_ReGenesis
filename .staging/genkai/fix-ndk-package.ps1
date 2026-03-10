# Fix Corrupted Android SDK package.xml Files
# This script scans the Android SDK for corrupted or empty package.xml files
# (common after partial installs) and removes/backups them so Gradle/CMake
# can locate valid SDK packages. It also detects directories with suffixes
# like "-4" (e.g., cmake\3.18.1-4) and offers to rename them to the expected
# directory name if safe.

Write-Host "🔧 Fixing Corrupted Android SDK Packages..." -ForegroundColor Cyan

$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
if (-not (Test-Path $sdkPath)) {
    Write-Host "⚠️  Android SDK not found at $sdkPath" -ForegroundColor Yellow
    Write-Host "Please ensure ANDROID_SDK_ROOT or LOCALAPPDATA\Android\Sdk exists." -ForegroundColor Yellow
    return
}

# Folders we commonly need to check
$foldersToCheck = @("cmdline-tools", "cmake")

# Backup directory for bad package.xml files
$backupDir = Join-Path $PSScriptRoot "sdk-package-backups"
if (-not (Test-Path $backupDir)) { New-Item -ItemType Directory -Path $backupDir | Out-Null }

function Test-And-RemovePackageXml($packageXml) {
    try {
        if (-not (Test-Path $packageXml)) { return $false }
        $content = Get-Content -Raw -ErrorAction Stop $packageXml
        if ([string]::IsNullOrWhiteSpace($content)) {
            $baseName = Split-Path $packageXml -Leaf
            $dest = Join-Path $backupDir ("$baseName." + (Get-Date -Format "yyyyMMdd-HHmmss") + ".bak")
            Copy-Item -Path $packageXml -Destination $dest -Force
            Write-Host "  ❌ Backed up empty/corrupted package.xml to ${dest}" -ForegroundColor Yellow
            Remove-Item $packageXml -Force
            Write-Host "  ✅ Removed corrupted: ${packageXml}" -ForegroundColor Green
            return $true
        } else {
            # Quick XML basic check: ensure starts with '<' (not definitive but useful)
            $trim = $content.TrimStart()
            if ($trim.Length -lt 1 -or $trim[0] -ne '<') {
                $baseName = Split-Path $packageXml -Leaf
                $dest = Join-Path $backupDir ("$baseName." + (Get-Date -Format "yyyyMMdd-HHmmss") + ".bak")
                Copy-Item -Path $packageXml -Destination $dest -Force
                Write-Host "  ❌ Backed up malformed package.xml to ${dest}" -ForegroundColor Yellow
                Remove-Item $packageXml -Force
                Write-Host "  ✅ Removed corrupted: ${packageXml}" -ForegroundColor Green
                return $true
            }
        }
    } catch {
        Write-Host "  ⚠️  Error reading ${packageXml}: ${_}" -ForegroundColor Yellow
        return $false
    }
    return $false
}

# Scan known folders for package.xml files
foreach ($folder in $foldersToCheck) {
    $fullFolder = Join-Path $sdkPath $folder
    if (-not (Test-Path $fullFolder)) { continue }

    Get-ChildItem -Path $fullFolder -Directory -ErrorAction SilentlyContinue | ForEach-Object {
        $dir = $_.FullName
        $pkg = Join-Path $dir "package.xml"
        if (Test-And-RemovePackageXml $pkg) { continue }

        # also check nested versions with suffixes like 3.18.1-4
        Get-ChildItem -Path $dir -Directory -ErrorAction SilentlyContinue | ForEach-Object {
            $subdir = $_.FullName
            $pkg2 = Join-Path $subdir "package.xml"
            if (Test-And-RemovePackageXml $pkg2) { continue }

            # detect directories with trailing -4 or -3 and offer safe rename to expected name
            $name = Split-Path $subdir -Leaf
            if ($name -match "^(.+)-\d+$") {
                $expected = $Matches[1]
                $expectedPath = Join-Path (Split-Path $subdir -Parent) $expected
                if (-not (Test-Path $expectedPath)) {
                    Write-Host "\n⚠️  Found versioned dir: ${subdir} (expected: ${expectedPath})" -ForegroundColor Yellow
                    Write-Host "   Renaming will make SDK tools find this package. Renaming now..." -ForegroundColor Cyan
                    try {
                        Rename-Item -Path $subdir -NewName $expected -ErrorAction Stop
                        Write-Host "  ✅ Renamed ${subdir} -> ${expectedPath}" -ForegroundColor Green
                    } catch {
                        Write-Host "  ❌ Failed to rename ${subdir}: ${_}" -ForegroundColor Red
                    }
                }
            }
        }
    }
}

# Also check top-level package.xml in cmdline-tools versions like 13.0 or 17.0
Get-ChildItem -Path (Join-Path $sdkPath "cmdline-tools") -Directory -ErrorAction SilentlyContinue | ForEach-Object {
    $pkg = Join-Path $_.FullName "package.xml"
    Test-And-RemovePackageXml $pkg | Out-Null
}

Write-Host "\n✅ SDK package scan complete. Backups (if any) are in: ${backupDir}" -ForegroundColor Green
Write-Host "If issues persist, open Android Studio SDK Manager and reinstall the affected components." -ForegroundColor White
