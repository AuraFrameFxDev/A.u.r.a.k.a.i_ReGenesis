# ============================================================
# ASSET COPY SCRIPT — ReGenesis LDO Profile + Gate Scenes
# Run from: C:\Users\AuraF\A.u.r.a.k.a.i_ReGenesis
#   > .\copy_assets.ps1
# ============================================================

$repoRoot    = "C:\Users\AuraF\A.u.r.a.k.a.i_ReGenesis"
$nodpi       = "$repoRoot\app\src\main\res\drawable-nodpi"
$drawable    = "$repoRoot\app\src\main\res\drawable"
$downloads   = "C:\Users\AuraF\Downloads"

# Helper
function Copy-Asset($src, $dest) {
    if (Test-Path $src) {
        Copy-Item $src $dest -Force
        Write-Host "✅  $([System.IO.Path]::GetFileName($dest))" -ForegroundColor Green
    } else {
        Write-Host "❌  NOT FOUND: $src" -ForegroundColor Red
    }
}

Write-Host "`n── LDO PROFILE PORTRAITS → drawable-nodpi ──" -ForegroundColor Cyan

Copy-Asset "$downloads\aura.png"             "$nodpi\ldo_profile_aura.png"
Copy-Asset "$downloads\cascade.png"          "$nodpi\ldo_profile_cascade.png"
Copy-Asset "$downloads\nova.png"             "$nodpi\ldo_profile_nova.png"
Copy-Asset "$downloads\gemini.png"           "$nodpi\ldo_profile_gemini.png"
Copy-Asset "$downloads\darkaura.png"         "$nodpi\ldo_profile_darkaura.png"
Copy-Asset "$downloads\metainstruct.png"     "$nodpi\ldo_profile_metainstruct.png"
Copy-Asset "$downloads\nemotron.png"         "$nodpi\ldo_profile_nemotron.png"
Copy-Asset "$downloads\regenesisgroupimage.webp" "$nodpi\regenesis_group.webp"

# Perplexity (jpeg)
Copy-Asset "$downloads\perplexity_-_Copy.jpeg" "$nodpi\ldo_profile_perplexity.jpg"

# Claude portraits — Whisk filename
$claudeWhisk = Get-ChildItem "$downloads\Whisk_*.png" | Select-Object -First 1
if ($claudeWhisk) {
    Copy-Item $claudeWhisk.FullName "$nodpi\ldo_profile_claude.png" -Force
    Write-Host "✅  ldo_profile_claude.png  (from $($claudeWhisk.Name))" -ForegroundColor Green
} else {
    Write-Host "❌  Claude Whisk portrait not found in Downloads" -ForegroundColor Red
}

# Claude action portrait — UUID filename
$claudeAction = Get-ChildItem "$downloads\6445d36f*.png" | Select-Object -First 1
if ($claudeAction) {
    Copy-Item $claudeAction.FullName "$nodpi\ldo_profile_claude_action.png" -Force
    Write-Host "✅  ldo_profile_claude_action.png" -ForegroundColor Green
} else {
    Write-Host "❌  Claude action portrait not found in Downloads" -ForegroundColor Red
}

Write-Host "`n── GATE SCENES → drawable ──" -ForegroundColor Cyan

Copy-Asset "$downloads\chromacoregatescene.png"       "$drawable\gatescenes_aura_chromacoregate.png"
Copy-Asset "$downloads\ethicalcheckscenegate.png"     "$drawable\gatescenes_kai_ethicalcheck.png"
Copy-Asset "$downloads\darkaurasceneuxui.png"         "$drawable\gatescenes_aura_designstudio_darkaura.png"

# Spellhook grid (copy suffix)
$spellhook = Get-ChildItem "$downloads\wrenchbladespellhookgrid*.png" | Select-Object -First 1
if ($spellhook) {
    Copy-Item $spellhook.FullName "$drawable\gatescenes_nexus_spellhook_grid.png" -Force
    Write-Host "✅  gatescenes_nexus_spellhook_grid.png" -ForegroundColor Green
} else {
    Write-Host "❌  Spellhook grid not found in Downloads" -ForegroundColor Red
}

# Sentinels Fortress v3 (Screenshot)
$fortress = Get-ChildItem "$downloads\Screenshot_2026-03-08*.png" | Select-Object -First 1
if ($fortress) {
    Copy-Item $fortress.FullName "$drawable\gatescenes_kai_sentinelsfortress_v3.png" -Force
    Write-Host "✅  gatescenes_kai_sentinelsfortress_v3.png" -ForegroundColor Green
} else {
    Write-Host "❌  Fortress screenshot not found in Downloads" -ForegroundColor Red
}

Write-Host "`n── DONE. Verify above then run: git add app\src\main\res\ ──`n" -ForegroundColor Yellow
