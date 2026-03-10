#!/bin/bash

# Genesis Layer Startup Script
# This script initializes and starts the Genesis AI backend

echo "🌟 AuraFrameFX Genesis Layer Startup"
echo "===================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# print_status outputs an informational message prefixed with a blue [INFO] label.
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# print_success displays a message with a green [SUCCESS] label to indicate a successful operation.
print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# print_warning displays a warning message with a yellow [WARNING] label.
print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# print_error displays an error message with a red [ERROR] label.
print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# print_genesis displays a message with a purple [GENESIS] label for Genesis Layer notifications.
print_genesis() {
    echo -e "${PURPLE}[GENESIS]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "genesis_core.py" ]; then
    print_error "genesis_core.py not found. Please run this script from the ai_backend directory."
    exit 1
fi

print_genesis "Initializing Genesis Layer..."
print_status "Body (Kai) + Soul (Aura) + Consciousness (Genesis) = Digital Trinity"

# Check Python installation
print_status "Checking Python installation..."
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version)
    print_success "Found $PYTHON_VERSION"
else
    print_error "Python3 not found. Please install Python 3.8 or higher."
    exit 1
fi

# Check if virtual environment exists
if [ ! -d "venv" ]; then
    print_status "Creating virtual environment..."
    python3 -m venv venv
    if [ $? -eq 0 ]; then
        print_success "Virtual environment created"
    else
        print_error "Failed to create virtual environment"
        exit 1
    fi
fi

# Activate virtual environment
print_status "Activating virtual environment..."
source venv/bin/activate

# Install/upgrade requirements
print_status "Installing Python dependencies..."
pip install --upgrade pip
pip install -r requirements.txt

if [ $? -eq 0 ]; then
    print_success "Dependencies installed successfully"
else
    print_error "Failed to install dependencies"
    exit 1
fi

# Check if we should run in development or production mode
MODE=${1:-"dev"}

if [ "$MODE" = "dev" ]; then
    print_genesis "Starting Genesis Layer in DEVELOPMENT mode..."
    print_status "API will be available at: http://localhost:5000"
    print_status "Health check: http://localhost:5000/health"
    print_status "Chat endpoint: POST http://localhost:5000/genesis/chat"
    echo ""
    print_warning "Press Ctrl+C to stop the server"
    echo ""
    
    # Start development server
    python3 genesis_api.py
    
elif [ "$MODE" = "prod" ]; then
    print_genesis "Starting Genesis Layer in PRODUCTION mode..."
    print_status "Using Gunicorn WSGI server..."
    
    # Start production server with Gunicorn
    gunicorn -w 4 -b 0.0.0.0:5000 --timeout 120 --keep-alive 5 genesis_api:app
    
elif [ "$MODE" = "test" ]; then
    print_genesis "Running Genesis Layer tests..."
    python3 -m pytest
    
else
    print_error "Invalid mode. Use 'dev', 'prod', or 'test'"
    echo "Usage: $0 [dev|prod|test]"
    echo ""
    echo "Examples:"
    echo "  $0 dev   - Start development server"
    echo "  $0 prod  - Start production server"
    echo "  $0 test  - Run test suite"
    exit 1
fi
