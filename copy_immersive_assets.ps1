# 🎨 IMMERSIVE UI ASSET MIGRATION SCRIPT
# Copies all user assets from Downloads to Android project

$sourceBase = "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media"
$targetBase = "app\src\main"

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║     IMMERSIVE UI ASSET MIGRATION — ReGenesis Exodus       ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Create directory structure
Write-Host "[1/4] Creating directory structure..." -ForegroundColor Yellow
$dirs = @(
    "$targetBase\assets\agents",
    "$targetBase\assets\backgrounds",
    "$targetBase\assets\ui",
    "$targetBase\res\font",
    "$targetBase\res\drawable-nodpi"
)

foreach ($dir in $dirs) {
    if (!(Test-Path $dir)) {
        New-Item -ItemType Directory -Force -Path $dir | Out-Null
        Write-Host "  Created: $dir" -ForegroundColor Green
    }
}

# Copy AGENTS (Guest Cast)
Write-Host ""
Write-Host "[2/4] Copying agent avatars..." -ForegroundColor Yellow

$agentMappings = @{
    "Guest Cast(LDO ExternalModels)\nova.png" = "agents\primus.png"
    "Guest Cast(LDO ExternalModels)\gemini.png" = "agents\gemini.png"
    "Guest Cast(LDO ExternalModels)\metainstruct.png" = "agents\metainstruct.png"
    "Guest Cast(LDO ExternalModels)\claude (2).png" = "agents\andelualx.png"
    "Guest Cast(LDO ExternalModels)\nemotron.png" = "agents\nemotron.png"
    "Guest Cast(LDO ExternalModels)\cascade.png" = "agents\cascade.png"
    "Guest Cast(LDO ExternalModels)\33db126c-cced-491e-8750-c6268a4684f0.png" = "agents\grok.png"
    "Guest Cast(LDO ExternalModels)\unnamed (45).png" = "agents\kairos.png"
    "Guest Cast(LDO ExternalModels)\unnamed (43).png" = "agents\manus.png"
}

foreach ($mapping in $agentMappings.GetEnumerator()) {
    $source = Join-Path $sourceBase $mapping.Key
    $target = Join-Path "$targetBase\assets" $mapping.Value
    
    if (Test-Path $source) {
        Copy-Item $source $target -Force
        Write-Host "  ✓ $($mapping.Value)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Missing: $($mapping.Key)" -ForegroundColor Red
    }
}

# Copy BACKGROUNDS
Write-Host ""
Write-Host "[3/4] Copying immersive backgrounds..." -ForegroundColor Yellow

$bgMappings = @{
    "backgrounds for menus use where needed\unnamed (49).png" = "backgrounds\ldodevops_bg.png"
    "backgrounds for menus use where needed\unnamed (50).png" = "backgrounds\aurastudio_bg.png"
    "backgrounds for menus use where needed\unnamed (51).png" = "backgrounds\kaifortress_bg.png"
    "backgrounds for menus use where needed\52dd7b3f334f01cf767f9803b94d52d4.jpg" = "backgrounds\oracledrive_bg.png"
    "backgrounds for menus use where needed\agentcreation.jpg" = "backgrounds\nexus_bg.png"
    "backgrounds for menus use where needed\kais asset.jpg" = "backgrounds\holographic_table.png"
}

foreach ($mapping in $bgMappings.GetEnumerator()) {
    $source = Join-Path $sourceBase $mapping.Key
    $target = Join-Path "$targetBase\assets" $mapping.Value
    
    if (Test-Path $source) {
        Copy-Item $source $target -Force
        Write-Host "  ✓ $($mapping.Value)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ Missing: $($mapping.Key)" -ForegroundColor Red
    }
}

# Copy FONT
Write-Host ""
Write-Host "[4/4] Copying fonts..." -ForegroundColor Yellow

$fontSource = Join-Path $sourceBase "corpta\Corpta DEMO.otf"
$fontTarget = "$targetBase\res\font\corpta_regular.ttf"

if (Test-Path $fontSource) {
    Copy-Item $fontSource $fontTarget -Force
    Write-Host "  ✓ corpta_regular.ttf" -ForegroundColor Green
} else {
    Write-Host "  ✗ Missing: Corpta font" -ForegroundColor Red
}

# Summary
Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                    MIGRATION COMPLETE                      ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "Assets are ready for immersive UI deployment." -ForegroundColor Green
Write-Host "Next: Build the project and test the holographic depth!" -ForegroundColor Green
Write-Host ""

# Count files
$agentCount = (Get-ChildItem "$targetBase\assets\agents" -ErrorAction SilentlyContinue).Count
$bgCount = (Get-ChildItem "$targetBase\assets\backgrounds" -ErrorAction SilentlyContinue).Count

Write-Host "Summary: $agentCount agents, $bgCount backgrounds copied" -ForegroundColor Cyan
