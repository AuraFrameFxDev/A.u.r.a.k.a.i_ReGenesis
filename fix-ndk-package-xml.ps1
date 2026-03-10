# ============================================================================
# Fix Corrupted NDK package.xml Files
# ============================================================================
# This script fixes corrupted (empty or truncated) package.xml files in
# Android NDK installations by regenerating them with proper content.
#
# Problem: SAXParseException "Premature end of file" (文件提前结束。)
# Cause: Empty or corrupted package.xml files in NDK directories
# Solution: Regenerate package.xml with valid content
#
# Usage: Run this script from PowerShell with Administrator privileges
# ============================================================================

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "NDK package.xml Fix Script" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

# Detect Android SDK location
$AndroidSdkRoot = $env:ANDROID_SDK_ROOT
if (-not $AndroidSdkRoot) {
    $AndroidSdkRoot = $env:ANDROID_HOME
}
if (-not $AndroidSdkRoot) {
    # Try default Windows location
    $AndroidSdkRoot = "$env:LOCALAPPDATA\Android\Sdk"
}

if (-not (Test-Path $AndroidSdkRoot)) {
    Write-Host "ERROR: Cannot find Android SDK directory!" -ForegroundColor Red
    Write-Host "Please set ANDROID_SDK_ROOT or ANDROID_HOME environment variable" -ForegroundColor Yellow
    Write-Host "or ensure SDK is installed at: $AndroidSdkRoot" -ForegroundColor Yellow
    exit 1
}

Write-Host "Android SDK Location: $AndroidSdkRoot" -ForegroundColor Green
Write-Host ""

$NdkDir = Join-Path $AndroidSdkRoot "ndk"

if (-not (Test-Path $NdkDir)) {
    Write-Host "ERROR: NDK directory not found at: $NdkDir" -ForegroundColor Red
    exit 1
}

Write-Host "Scanning NDK installations..." -ForegroundColor Cyan
Write-Host ""

# Get all NDK version directories
$NdkVersions = Get-ChildItem -Path $NdkDir -Directory

$FixedCount = 0
$CorruptedCount = 0

foreach ($NdkVersion in $NdkVersions) {
    $PackageXmlPath = Join-Path $NdkVersion.FullName "package.xml"

    Write-Host "Checking: $($NdkVersion.Name)" -ForegroundColor White

    if (-not (Test-Path $PackageXmlPath)) {
        Write-Host "  [MISSING] package.xml not found, creating..." -ForegroundColor Yellow
        $CorruptedCount++
    }
    else {
        $FileSize = (Get-Item $PackageXmlPath).Length
        if ($FileSize -eq 0) {
            Write-Host "  [CORRUPTED] package.xml is empty (0 bytes)" -ForegroundColor Red
            $CorruptedCount++
        }
        else {
            # Try to parse the XML to check if it's valid
            try {
                [xml]$TestXml = Get-Content $PackageXmlPath -Raw -ErrorAction Stop
                Write-Host "  [OK] package.xml is valid" -ForegroundColor Green
                continue
            }
            catch {
                Write-Host "  [CORRUPTED] package.xml is invalid: $($_.Exception.Message)" -ForegroundColor Red
                $CorruptedCount++
            }
        }
    }

    # Generate new package.xml
    Write-Host "  [FIXING] Generating new package.xml..." -ForegroundColor Yellow

    # Extract version number from directory name (e.g., "25.0.8775105" -> version parts)
    $VersionString = $NdkVersion.Name
    $VersionParts = $VersionString -split '\.'

    # Create backup if file exists
    if (Test-Path $PackageXmlPath) {
        $BackupPath = "$PackageXmlPath.backup"
        Copy-Item $PackageXmlPath $BackupPath -Force
        Write-Host "  [BACKUP] Created backup: package.xml.backup" -ForegroundColor Cyan
    }

    # Generate XML content
    $XmlContent = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:repository xmlns:ns2="http://schemas.android.com/repository/android/common/01" xmlns:ns3="http://schemas.android.com/sdk/android/repo/addon2/01" xmlns:ns4="http://schemas.android.com/repository/android/generic/01" xmlns:ns5="http://schemas.android.com/sdk/android/repo/repository2/01" xmlns:ns6="http://schemas.android.com/sdk/android/repo/sys-img2/01">
    <localPackage path="ndk;$VersionString" obsolete="false">
        <type-details xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns4:genericDetailsType"/>
        <revision>
            <major>$($VersionParts[0])</major>
            <minor>$($VersionParts[1])</minor>
            <micro>$($VersionParts[2])</micro>
        </revision>
        <display-name>NDK (Side by side) $VersionString</display-name>
    </localPackage>
</ns2:repository>
"@

    # Write the new package.xml
    try {
        $XmlContent | Out-File -FilePath $PackageXmlPath -Encoding UTF8 -Force
        Write-Host "  [SUCCESS] Created new package.xml" -ForegroundColor Green
        $FixedCount++
    }
    catch {
        Write-Host "  [ERROR] Failed to write package.xml: $($_.Exception.Message)" -ForegroundColor Red
    }

    Write-Host ""
}

Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  Total NDK versions scanned: $($NdkVersions.Count)" -ForegroundColor White
Write-Host "  Corrupted/Missing package.xml: $CorruptedCount" -ForegroundColor Yellow
Write-Host "  Successfully fixed: $FixedCount" -ForegroundColor Green
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

if ($FixedCount -gt 0) {
    Write-Host "Done! Your NDK package.xml files have been fixed." -ForegroundColor Green
    Write-Host "You can now run your build again." -ForegroundColor Green
}
else {
    Write-Host "No corrupted package.xml files found." -ForegroundColor Green
}

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
