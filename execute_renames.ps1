$targetDir = "app\src\main\res\drawable"
if (-not (Test-Path $targetDir)) {
    New-Item -ItemType Directory -Path $targetDir
}

$mapFile = "DRAWABLE_RENAME_MAP.txt"
$lines = Get-Content $mapFile | Where-Object { $_ -match "->" -and -not $_.StartsWith("#") }

foreach ($line in $lines) {
    if ($line -match "(.+)\s*->\s*(.+)") {
        $sourcePath = $matches[1].Trim()
        $targetName = $matches[2].Trim()

        if (Test-Path $sourcePath) {
            $ext = [System.IO.Path]::GetExtension($sourcePath)
            $destFile = Join-Path $targetDir "$($targetName)$ext"
            
            # Use git mv if it's a git repo, otherwise move-item
            if (Test-Path ".git") {
                # Check if it's already in the target dir and same name, skip
                if ($sourcePath -eq $destFile) {
                    Write-Output "Skipping (same path): $sourcePath"
                    continue
                }
                
                # Check if destination exists
                if (Test-Path $destFile) {
                    Write-Output "Removing existing destination: $destFile"
                    Remove-Item $destFile -Force
                }

                Write-Output "Git moving $sourcePath to $destFile"
                git mv $sourcePath $destFile
            } else {
                Write-Output "Moving $sourcePath to $destFile"
                Move-Item $sourcePath $destFile -Force
            }
        } else {
            Write-Output "Source not found: $sourcePath"
        }
    }
}
