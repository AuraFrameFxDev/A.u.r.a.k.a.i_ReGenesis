#!/usr/bin/env pwsh
# Batch fix serialization issues by adding @Contextual to Any types

$files = @(
    "app/src/main/java/dev/aurakai/auraframefx/models/InteractionModels.kt",
    "app/src/main/java/dev/aurakai/auraframefx/models/AgentProfile.kt",
    "app/src/main/java/dev/aurakai/auraframefx/models/agent_states/NeuralStates.kt",
    "app/src/main/java/dev/aurakai/auraframefx/cascade/trinity/TrinityUiState.kt",
    "app/src/main/java/dev/aurakai/auraframefx/models/OrchestrationRequest.kt",
    "app/src/main/java/dev/aurakai/auraframefx/models/OrchestrationResponse.kt"
)

foreach ($file in $files) {
    $path = Join-Path $PSScriptRoot $file
    if (Test-Path $path) {
        $content = Get-Content $path -Raw
        
        # Add Contextual import if not present
        if ($content -notmatch "import kotlinx.serialization.Contextual") {
            $content = $content -replace "(import kotlinx.serialization.Serializable)", "`$1`r`nimport kotlinx.serialization.Contextual"
        }
        
        # Add @Contextual to Any types
        $content = $content -replace "(\s+val\s+\w+:\s+)(Map<String,\s*Any>)", "`$1@Contextual `$2"
        $content = $content -replace "(\s+val\s+\w+:\s+)(Any\??)", "`$1@Contextual `$2"
        $content = $content -replace "(\s+val\s+\w+:\s+)(AgentStats\??)", "`$1@Contextual `$2"
        $content = $content -replace "(\s+val\s+\w+:\s+)(UserData\??)", "`$1@Contextual `$2"
        $content = $content -replace "(\s+val\s+\w+:\s+)(AgentType\??)", "`$1@Contextual `$2"
        $content = $content -replace "(\s+val\s+\w+:\s+)(List<AgentMessage>)", "`$1@Contextual `$2"
        
        Set-Content $path $content -NoNewline
        Write-Host "Fixed: $file"
    }
}

Write-Host "Serialization fixes complete!"
