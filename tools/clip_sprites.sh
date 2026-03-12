#!/bin/bash
#
# 🎨 Quick-Start Sprite Clipping Script
# Batch remove backgrounds from hundreds of sprite images
#

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${CYAN}🎨 ReGenesis Sprite Batch Clipper${NC}"
echo "=================================="
echo ""

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}❌ ERROR: Python 3 not found${NC}"
    echo "Please install Python 3.8 or higher"
    exit 1
fi

# Check if dependencies are installed
echo -e "${CYAN}📦 Checking dependencies...${NC}"
if ! python3 -c "import rembg" 2>/dev/null; then
    echo -e "${YELLOW}⚠️  Dependencies not installed${NC}"
    echo ""
    echo "Installing required packages..."
    pip install -r requirements-sprite-tools.txt
    echo ""
fi

# Get input and output directories
if [ -z "$1" ] || [ -z "$2" ]; then
    echo -e "${YELLOW}Usage:${NC}"
    echo "  $0 <input_dir> <output_dir> [quality]"
    echo ""
    echo -e "${YELLOW}Examples:${NC}"
    echo "  $0 ~/Desktop/raw_sprites ./clipped_sprites"
    echo "  $0 ~/Desktop/raw_sprites ./clipped_sprites high"
    echo "  $0 ~/Desktop/raw_sprites ./clipped_sprites fast"
    echo ""
    exit 1
fi

INPUT_DIR="$1"
OUTPUT_DIR="$2"
QUALITY="${3:-high}"  # Default to 'high' if not specified

# Validate input directory
if [ ! -d "$INPUT_DIR" ]; then
    echo -e "${RED}❌ ERROR: Input directory does not exist: $INPUT_DIR${NC}"
    exit 1
fi

# Count images
IMAGE_COUNT=$(find "$INPUT_DIR" -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.webp" \) | wc -l)

echo ""
echo -e "${GREEN}✅ Found ${IMAGE_COUNT} images to process${NC}"
echo ""
echo -e "${CYAN}Configuration:${NC}"
echo "  Input:   $INPUT_DIR"
echo "  Output:  $OUTPUT_DIR"
echo "  Quality: $QUALITY"
echo ""

# Ask for confirmation
read -p "Start processing? (y/n) " -n 1 -r
echo ""
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cancelled."
    exit 0
fi

echo ""
echo -e "${CYAN}🚀 Starting batch processing...${NC}"
echo ""

# Run the Python script
python3 batch_sprite_clipper.py "$INPUT_DIR" "$OUTPUT_DIR" --quality "$QUALITY"

echo ""
echo -e "${GREEN}✅ COMPLETE!${NC}"
echo ""
echo -e "${CYAN}Next steps:${NC}"
echo "  1. Review clipped sprites: ls -R $OUTPUT_DIR"
echo "  2. Check SPRITE_ORGANIZATION_GUIDE.md for integration steps"
echo ""
