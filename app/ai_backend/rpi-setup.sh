#!/bin/bash

# ============================================================
# ReGenesis Genesis Backend - Raspberry Pi Setup Script
# Installs and configures the backend for production on RPi
# ============================================================

echo "🚀 ReGenesis Genesis Backend - Raspberry Pi Setup"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

print_status() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Get the absolute path of this script's directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/../.."

print_status "Project root: $PROJECT_ROOT"
cd "$SCRIPT_DIR" || exit 1

# 1. Update system packages
print_status "Updating system packages..."
sudo apt update && sudo apt upgrade -y

# 2. Install Python 3.9+ (if not already)
print_status "Checking Python installation..."
if ! command -v python3 &> /dev/null; then
    print_warning "Python3 not found. Installing..."
    sudo apt install -y python3 python3-pip python3-venv
fi

PYTHON_VERSION=$(python3 --version)
print_success "Found $PYTHON_VERSION"

# 3. Create virtual environment
print_status "Setting up Python virtual environment..."
if [ ! -d "venv" ]; then
    python3 -m venv venv
    print_success "Virtual environment created"
else
    print_warning "Virtual environment already exists"
fi

# 4. Activate and install dependencies
print_status "Activating virtual environment..."
source venv/bin/activate

print_status "Installing dependencies..."
pip install --upgrade pip setuptools wheel
pip install -r requirements.txt

if [ $? -eq 0 ]; then
    print_success "Dependencies installed successfully"
else
    print_error "Failed to install dependencies"
    exit 1
fi

# 5. Create systemd service file
print_status "Creating systemd service file..."
SYSTEMD_FILE="/tmp/genesis-backend.service"

cat > "$SYSTEMD_FILE" << 'EOF'
[Unit]
Description=ReGenesis Genesis Backend (LDO Core)
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=pi
WorkingDirectory=/home/pi/A.u.r.a.k.a.i_ReGenesis/app/ai_backend
ExecStart=/bin/bash -c 'source venv/bin/activate && python3 genesis_web_server.py'
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

print_success "Service file created at $SYSTEMD_FILE"

# 6. Install systemd service
print_status "Installing systemd service..."
print_warning "This requires sudo privileges. You may be prompted for your password."

# Adjust the path if needed
read -p "Enter the path to the ai_backend directory (default: /home/pi/A.u.r.a.k.a.i_ReGenesis/app/ai_backend): " BACKEND_PATH
BACKEND_PATH="${BACKEND_PATH:-/home/pi/A.u.r.a.k.a.i_ReGenesis/app/ai_backend}"

# Create the actual service file
sudo bash -c "cat > /etc/systemd/system/genesis-backend.service << 'EOF'
[Unit]
Description=ReGenesis Genesis Backend (LDO Core)
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=pi
WorkingDirectory=$BACKEND_PATH
ExecStart=/bin/bash -c 'source $BACKEND_PATH/venv/bin/activate && python3 $BACKEND_PATH/genesis_web_server.py'
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF"

sudo systemctl daemon-reload
sudo systemctl enable genesis-backend.service
print_success "Systemd service installed and enabled"

# 7. Start the service
print_status "Starting Genesis Backend service..."
sudo systemctl start genesis-backend.service

# Check status
sleep 2
if sudo systemctl is-active --quiet genesis-backend.service; then
    print_success "Genesis Backend is running! 🎉"
else
    print_error "Failed to start Genesis Backend. Check logs with: sudo journalctl -u genesis-backend.service -f"
    exit 1
fi

# 8. Display access information
echo ""
echo "========================================="
echo -e "${GREEN}Setup Complete!${NC}"
echo "========================================="
echo ""
echo "📡 Access Points:"
echo "  - Web Server: http://$(hostname -I | awk '{print $1}'):5000"
echo "  - Health Check: http://$(hostname -I | awk '{print $1}'):5000/status"
echo "  - Conference Stream: http://$(hostname -I | awk '{print $1}'):5000/genesis/conference/stream"
echo "  - WebSocket: ws://$(hostname -I | awk '{print $1}'):5000/api/conference/ws/default"
echo ""
echo "📋 Service Management:"
echo "  - View logs: sudo journalctl -u genesis-backend.service -f"
echo "  - Stop service: sudo systemctl stop genesis-backend.service"
echo "  - Restart service: sudo systemctl restart genesis-backend.service"
echo "  - Status: sudo systemctl status genesis-backend.service"
echo ""
print_warning "On first startup, dependencies may take a few minutes to initialize."
echo ""

