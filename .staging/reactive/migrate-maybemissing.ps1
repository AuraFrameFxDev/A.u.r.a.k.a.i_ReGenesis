# PowerShell migration script
# Mimics the behavior of nano migrate-maybemissing.sh on Windows

Set-StrictMode -Version Latest

$ErrorActionPreference = 'Stop'

$Root = Get-Location
$BashScriptPath = Join-Path $Root 'nano migrate-maybemissing.sh'
if (-not (Test-Path $BashScriptPath)) { Write-Error "Required file not found: $BashScriptPath"; exit 1 }

# Read bash script to parse migrate_file calls
$script = Get-Content $BashScriptPath -Raw -ErrorAction Stop
$lines = $script -split "\r?\n"

# Backup directory
$timestamp = Get-Date -Format 'yyyyMMdd_HHmmss'
$BackupDir = Join-Path $Root "docs\backup_$timestamp"
New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
Write-Host "Created backup directory: $BackupDir" -ForegroundColor Green

$CREATED = 0; $UPGRADED = 0; $FAILED = 0

# Optional fallback source roots (will be searched when a source path isn't found).
# Add the OneDrive path you mentioned so the script can look there for missing files.
$FallbackRoots = @(
    'C:\Users\Wehtt\OneDrive\Documents\maybemissing\A.u.r.a.k.a.i-ft.Genesis-main (1)',
    'C:\Users\Wehtt\OneDrive\Documents\maybemissing',
    'C:\Users\Wehtt\OneDrive\Documents\A.u.r.a.k.a.i-ft.Genesis-updates\A.u.r.a.k.a.i-ft.Genesis-updates\auraframefx',
    'C:\Users\Wehtt\OneDrive\Documents\A.u.r.a.k.a.i-ft.Genesis-updates\A.u.r.a.k.a.i-ft.Genesis-updates\benchmark',
    'C:\Users\Wehtt\OneDrive\Documents\A.u.r.a.k.a.i-ft.Genesis-updates\A.u.r.a.k.a.i-ft.Genesis-updates\build-script-tests',
    'C:\Users\Wehtt\OneDrive\Documents\A.u.r.a.k.a.i-ft.Genesis-updates\A.u.r.a.k.a.i-ft.Genesis-updates\CerebralStream',
    'C:\Users\Wehtt\OneDrive\Documents\A.u.r.a.k.a.i-ft.Genesis-updates\A.u.r.a.k.a.i-ft.Genesis-updates'
)

function Normalize-PathString($p) {
    return $p -replace '/', '\\'
}

for ($i = 0; $i -lt $lines.Length; $i++) {
    $line = $lines[$i]
    if ($line -match 'migrate_file') {
        # Collect up to next 6 lines to capture up to 4 string args
        $collect = $line
        for ($k = 1; $k -le 6; $k++) {
            if ($i + $k -lt $lines.Length) {
                $collect += " `n" + $lines[$i + $k]
            }
        }
        # Extract quoted strings
        $matches = [regex]::Matches($collect, '"([^"]+)"') | ForEach-Object { $_.Groups[1].Value }
        if ($matches.Count -lt 2) { continue }
        $source = $matches[0]
        $target = $matches[1]
        $oldpkg = $null; $newpkg = $null
        if ($matches.Count -ge 4) { $oldpkg = $matches[2]; $newpkg = $matches[3] }

        $sourcePath = Join-Path $Root (Normalize-PathString $source)
        $targetPath = Join-Path $Root (Normalize-PathString $target)

        # If the source doesn't exist in the repo docs location, try fallback roots by filename
        if (-not (Test-Path $sourcePath)) {
            $baseName = Split-Path $source -Leaf
            $found = $null
            foreach ($rootCandidate in $FallbackRoots) {
                if (-not (Test-Path $rootCandidate)) { continue }
                $match = Get-ChildItem -Path $rootCandidate -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -ieq $baseName } | Select-Object -First 1
                if ($match) { $found = $match.FullName; break }
            }
            if ($found) {
                Write-Host "FOUND: Fallback source for $baseName at: $found" -ForegroundColor Cyan
                $sourcePath = $found
            } else {
                Write-Host "NOT FOUND: $source" -ForegroundColor Red
                $FAILED++
                continue
            }
        }

        $targetDir = Split-Path $targetPath -Parent
        if (-not (Test-Path $targetDir)) { New-Item -ItemType Directory -Path $targetDir -Force | Out-Null }

        if (Test-Path $targetPath) {
            $backupName = (Split-Path $targetPath -Leaf) + '.backup'
            Copy-Item -Path $targetPath -Destination (Join-Path $BackupDir $backupName) -Force
            Write-Host "BACKUP: Backing up existing: $(Split-Path $targetPath -Leaf)" -ForegroundColor Yellow
            $UPGRADED++
        } else {
            $CREATED++
        }

        Copy-Item -Path $sourcePath -Destination $targetPath -Force

        if ($oldpkg -and $newpkg) {
            $content = Get-Content -Raw -Path $targetPath -ErrorAction Stop
            $content = $content -replace "package\s+" + [regex]::Escape($oldpkg), "package $newpkg"
            $content = $content -replace "import\s+" + [regex]::Escape($oldpkg), "import $newpkg"
            Set-Content -Path $targetPath -Value $content -Force
            Write-Host "MIGRATED (pkg updated): $(Split-Path $targetPath -Leaf)" -ForegroundColor Green
        } else {
            Write-Host "MIGRATED: $(Split-Path $targetPath -Leaf)" -ForegroundColor Green
        }
    }
}

Write-Host ""
Write-Host "MIGRATION COMPLETE" -ForegroundColor Cyan
Write-Host "New files created: $CREATED" -ForegroundColor Green
Write-Host "Existing files upgraded: $UPGRADED" -ForegroundColor Yellow
Write-Host "Failed migrations: $FAILED" -ForegroundColor Red
Write-Host "Backups saved to: $BackupDir" -ForegroundColor Magenta

# Exit with non-zero code if any failed
if ($FAILED -gt 0) { exit 2 } else { exit 0 }
