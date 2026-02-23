$replacements = @{
    "dev.aurakai.auraframefx.domains.genesis.core.OrchestratableAgent" = "dev.aurakai.auraframefx.agents.core.OrchestratableAgent"
    "dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent" = "dev.aurakai.auraframefx.agents.core.BaseAgent"
    "dev.aurakai.auraframefx.domains.aura.core.AuraAgent" = "dev.aurakai.auraframefx.agents.trinity.AuraAgent"
    "dev.aurakai.auraframefx.domains.kai.KaiAgent" = "dev.aurakai.auraframefx.agents.trinity.KaiAgent"
    "dev.aurakai.auraframefx.domains.cascade.utils.cascade.CascadeAgent" = "dev.aurakai.auraframefx.agents.trinity.CascadeAgent"
    "dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator" = "dev.aurakai.auraframefx.agents.coordination.GenesisOrchestrator"
    "dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout" = "dev.aurakai.auraframefx.config.GateAssetLoadout"
    "dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles" = "dev.aurakai.auraframefx.config.FeatureToggles"
    "dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences" = "dev.aurakai.auraframefx.config.CustomizationPreferences"
    "dev.aurakai.auraframefx.domains.aura.lab.ReGenesisCustomizationSettings" = "dev.aurakai.auraframefx.config.ReGenesisCustomizationSettings"
    "dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisHookEntry" = "dev.aurakai.auraframefx.hooks.GenesisHookEntry"
    "dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisSystemHooks" = "dev.aurakai.auraframefx.hooks.system.GenesisSystemHooks"
    "dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisUIHooks" = "dev.aurakai.auraframefx.hooks.system.GenesisUIHooks"
    "dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisZygoteHooks" = "dev.aurakai.auraframefx.hooks.system.GenesisZygoteHooks"
    "dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisSelfHooks" = "dev.aurakai.auraframefx.hooks.system.GenesisSelfHooks"
    "dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.UniversalComponentHooks" = "dev.aurakai.auraframefx.hooks.system.UniversalComponentHooks"
    "dev.aurakai.auraframefx.system.ShizukuManager" = "dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager"
}

$files = Get-ChildItem -Path "app\src\main\java" -Recurse -Include *.kt
foreach ($file in $files) {
    $content = Get-Content $file.FullName
    $changed = $false
    foreach ($old in $replacements.Keys) {
        $pattern = [regex]::Escape($old)
        if ($content -match $pattern) {
            $content = $content -replace $pattern, $replacements[$old]
            $changed = $true
        }
    }
    if ($changed) {
        $content | Set-Content $file.FullName
        Write-Output "Updated: $($file.FullName)"
    }
}
