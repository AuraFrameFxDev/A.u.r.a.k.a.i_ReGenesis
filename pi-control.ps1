# ============================================================
# ReGenesis Raspberry Pi Control & Diagnostics (Windows PowerShell)
# For developers on Windows who need to manage the Pi backend
# ============================================================

param(
    [Parameter(Mandatory=$false)]
    [string]$Pi = "raspberrypi.local",

    [Parameter(Mandatory=$false)]
    [string]$User = "pi",

    [Parameter(Mandatory=$false)]
    [ValidateSet("status", "start", "stop", "restart", "logs", "test", "setup", "deploy")]
    [string]$Action = "status"
)

# Color functions for Windows PowerShell
function Write-Status { Write-Host "[INFO]" -ForegroundColor Blue -NoNewline; Write-Host " $args" }
function Write-Success { Write-Host "[SUCCESS]" -ForegroundColor Green -NoNewline; Write-Host " $args" }
function Write-Warning { Write-Host "[WARNING]" -ForegroundColor Yellow -NoNewline; Write-Host " $args" }
function Write-Error-Custom { Write-Host "[ERROR]" -ForegroundColor Red -NoNewline; Write-Host " $args" }

Write-Host ""
Write-Host "ReGenesis Genesis Backend - Pi Control Center" -ForegroundColor Magenta
Write-Host "=============================================" -ForegroundColor Magenta
Write-Host ""

# Verify SSH is available
if (-not (Get-Command ssh -ErrorAction SilentlyContinue)) {
    Write-Error-Custom "SSH not found. Please ensure Git for Windows or Windows OpenSSH is installed."
    exit 1
}

# Test Pi connectivity
Write-Status "Testing connectivity to $Pi..."
try {
    $ping = Test-Connection -ComputerName $Pi -Count 1 -ErrorAction Stop
    Write-Success "Pi is reachable ✓"
} catch {
    Write-Error-Custom "Cannot reach $Pi - check network/power"
    exit 1
}

# Perform requested action
switch ($Action) {
    "status" {
        Write-Host ""
        Write-Status "Checking service status on $Pi..."
        ssh "$User@$Pi" 'sudo systemctl status genesis-backend.service --no-pager'
        Write-Host ""
        Write-Status "Checking if port 5000 is listening..."
        ssh "$User@$Pi" 'ss -tlnp | grep 5000 || echo "Port 5000 NOT listening"'
        Write-Host ""
        Write-Status "Testing backend API..."
        $response = ssh "$User@$Pi" 'curl -s http://localhost:5000/status || echo "{\"status\":\"unreachable\"}"'
        Write-Host "Response: $response"
    }

    "start" {
        Write-Status "Starting Genesis Backend..."
        ssh "$User@$Pi" 'sudo systemctl start genesis-backend.service'
        Start-Sleep -Seconds 3
        ssh "$User@$Pi" 'sudo systemctl status genesis-backend.service --no-pager'
        Write-Success "Service started ✓"
    }

    "stop" {
        Write-Warning "Stopping Genesis Backend..."
        ssh "$User@$Pi" 'sudo systemctl stop genesis-backend.service'
        Write-Success "Service stopped ✓"
    }

    "restart" {
        Write-Status "Restarting Genesis Backend..."
        ssh "$User@$Pi" 'sudo systemctl restart genesis-backend.service'
        Start-Sleep -Seconds 3
        ssh "$User@$Pi" 'sudo systemctl status genesis-backend.service --no-pager'
        Write-Success "Service restarted ✓"
    }

    "logs" {
        Write-Status "Fetching service logs..."
        Write-Host ""
        ssh "$User@$Pi" 'sudo journalctl -u genesis-backend.service -n 50 --no-pager'
    }

    "test" {
        Write-Status "Running backend tests..."
        $PiIP = ssh "$User@$Pi" 'hostname -I | awk "{print \`$1}"'
        Write-Host ""
        Write-Status "Testing endpoints on $PiIP..."
        Write-Host ""

        Write-Status "1. Health check (/status)..."
        $status = Invoke-WebRequest -Uri "http://$PiIP`:5000/status" -UseBasicParsing -ErrorAction SilentlyContinue
        if ($status) {
            Write-Success "✓ $($status.Content)"
        } else {
            Write-Warning "✗ Unreachable"
        }

        Write-Status "2. Conference stream (/genesis/conference/stream)..."
        $stream = Invoke-WebRequest -Uri "http://$PiIP`:5000/genesis/conference/stream" -UseBasicParsing -ErrorAction SilentlyContinue -TimeoutSec 3
        if ($stream) {
            Write-Success "✓ Stream is active"
        } else {
            Write-Warning "✗ Stream unavailable or timed out"
        }
    }

    "setup" {
        Write-Host ""
        Write-Warning "This will install/setup the backend on the Pi."
        Write-Host ""

        # Check if setup script exists locally
        $setupScript = "app/ai_backend/rpi-setup.sh"
        if (-not (Test-Path $setupScript)) {
            Write-Error-Custom "Setup script not found at $setupScript"
            exit 1
        }

        Write-Status "Copying setup script to Pi..."
        scp $setupScript "$User@$Pi`:~/setup.sh"

        Write-Status "Running setup on Pi (this may take a few minutes)..."
        ssh "$User@$Pi" 'bash ~/setup.sh'

        Write-Success "Setup complete! ✓"
    }

    "deploy" {
        Write-Host ""
        Write-Status "Deploying latest code to Pi..."

        # This is a simple sync - adjust paths as needed
        $sourceDir = "app/ai_backend"
        $destDir = "/home/$User/A.u.r.a.k.a.i_ReGenesis/app/ai_backend"

        Write-Status "Syncing code..."
        # Note: WinSCP or rsync would be better, but scp works for small updates
        Get-ChildItem -Path $sourceDir -Filter "genesis*.py" | ForEach-Object {
            Write-Host "  Uploading $($_.Name)..."
            scp $_.FullName "$User@$Pi`:$destDir/"
        }

        Write-Status "Restarting service..."
        ssh "$User@$Pi" 'sudo systemctl restart genesis-backend.service'

        Write-Success "Deployment complete! ✓"
    }

    default {
        Write-Error-Custom "Unknown action: $Action"
        exit 1
    }
}

Write-Host ""
Write-Success "Operation complete!"
Write-Host ""

