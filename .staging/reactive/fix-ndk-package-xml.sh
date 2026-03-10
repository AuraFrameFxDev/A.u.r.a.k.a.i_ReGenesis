#!/bin/bash
# ============================================================================
# Fix Corrupted NDK package.xml Files
# ============================================================================
# This script fixes corrupted (empty or truncated) package.xml files in
# Android NDK installations by regenerating them with proper content.
#
# Problem: SAXParseException "Premature end of file" (文件提前结束。)
# Cause: Empty or corrupted package.xml files in NDK directories
# Solution: Regenerate package.xml with valid content
#
# Usage: chmod +x fix-ndk-package-xml.sh && ./fix-ndk-package-xml.sh
# ============================================================================

echo "====================================================================="
echo "NDK package.xml Fix Script"
echo "====================================================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Detect Android SDK location
if [ -n "$ANDROID_SDK_ROOT" ]; then
    ANDROID_SDK="$ANDROID_SDK_ROOT"
elif [ -n "$ANDROID_HOME" ]; then
    ANDROID_SDK="$ANDROID_HOME"
elif [ -d "$HOME/Android/Sdk" ]; then
    ANDROID_SDK="$HOME/Android/Sdk"
elif [ -d "$HOME/Library/Android/sdk" ]; then
    # macOS default location
    ANDROID_SDK="$HOME/Library/Android/sdk"
else
    echo -e "${RED}ERROR: Cannot find Android SDK directory!${NC}"
    echo -e "${YELLOW}Please set ANDROID_SDK_ROOT or ANDROID_HOME environment variable${NC}"
    exit 1
fi

if [ ! -d "$ANDROID_SDK" ]; then
    echo -e "${RED}ERROR: Android SDK directory not found at: $ANDROID_SDK${NC}"
    exit 1
fi

echo -e "${GREEN}Android SDK Location: $ANDROID_SDK${NC}"
echo ""

NDK_DIR="$ANDROID_SDK/ndk"

if [ ! -d "$NDK_DIR" ]; then
    echo -e "${RED}ERROR: NDK directory not found at: $NDK_DIR${NC}"
    exit 1
fi

echo -e "${CYAN}Scanning NDK installations...${NC}"
echo ""

FIXED_COUNT=0
CORRUPTED_COUNT=0
TOTAL_COUNT=0

# Iterate through all NDK version directories
for NDK_VERSION_DIR in "$NDK_DIR"/*; do
    if [ ! -d "$NDK_VERSION_DIR" ]; then
        continue
    fi

    TOTAL_COUNT=$((TOTAL_COUNT + 1))
    NDK_VERSION=$(basename "$NDK_VERSION_DIR")
    PACKAGE_XML="$NDK_VERSION_DIR/package.xml"

    echo -e "${WHITE}Checking: $NDK_VERSION${NC}"

    IS_CORRUPTED=false

    if [ ! -f "$PACKAGE_XML" ]; then
        echo -e "  ${YELLOW}[MISSING] package.xml not found, creating...${NC}"
        IS_CORRUPTED=true
        CORRUPTED_COUNT=$((CORRUPTED_COUNT + 1))
    else
        FILE_SIZE=$(stat -f%z "$PACKAGE_XML" 2>/dev/null || stat -c%s "$PACKAGE_XML" 2>/dev/null)
        if [ "$FILE_SIZE" -eq 0 ]; then
            echo -e "  ${RED}[CORRUPTED] package.xml is empty (0 bytes)${NC}"
            IS_CORRUPTED=true
            CORRUPTED_COUNT=$((CORRUPTED_COUNT + 1))
        else
            # Try to parse the XML to check if it's valid
            if ! xmllint --noout "$PACKAGE_XML" 2>/dev/null; then
                echo -e "  ${RED}[CORRUPTED] package.xml is invalid${NC}"
                IS_CORRUPTED=true
                CORRUPTED_COUNT=$((CORRUPTED_COUNT + 1))
            else
                echo -e "  ${GREEN}[OK] package.xml is valid${NC}"
                echo ""
                continue
            fi
        fi
    fi

    if [ "$IS_CORRUPTED" = true ]; then
        echo -e "  ${YELLOW}[FIXING] Generating new package.xml...${NC}"

        # Extract version parts from directory name (e.g., "25.0.8775105")
        IFS='.' read -r MAJOR MINOR MICRO <<< "$NDK_VERSION"

        # Create backup if file exists
        if [ -f "$PACKAGE_XML" ]; then
            cp "$PACKAGE_XML" "$PACKAGE_XML.backup"
            echo -e "  ${CYAN}[BACKUP] Created backup: package.xml.backup${NC}"
        fi

        # Generate XML content
        cat > "$PACKAGE_XML" << EOF
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:repository xmlns:ns2="http://schemas.android.com/repository/android/common/01" xmlns:ns3="http://schemas.android.com/sdk/android/repo/addon2/01" xmlns:ns4="http://schemas.android.com/repository/android/generic/01" xmlns:ns5="http://schemas.android.com/sdk/android/repo/repository2/01" xmlns:ns6="http://schemas.android.com/sdk/android/repo/sys-img2/01">
    <localPackage path="ndk;$NDK_VERSION" obsolete="false">
        <type-details xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns4:genericDetailsType"/>
        <revision>
            <major>$MAJOR</major>
            <minor>$MINOR</minor>
            <micro>$MICRO</micro>
        </revision>
        <display-name>NDK (Side by side) $NDK_VERSION</display-name>
    </localPackage>
</ns2:repository>
EOF

        if [ $? -eq 0 ]; then
            echo -e "  ${GREEN}[SUCCESS] Created new package.xml${NC}"
            FIXED_COUNT=$((FIXED_COUNT + 1))
        else
            echo -e "  ${RED}[ERROR] Failed to write package.xml${NC}"
        fi
    fi

    echo ""
done

echo "====================================================================="
echo -e "${CYAN}Summary:${NC}"
echo -e "  ${WHITE}Total NDK versions scanned: $TOTAL_COUNT${NC}"
echo -e "  ${YELLOW}Corrupted/Missing package.xml: $CORRUPTED_COUNT${NC}"
echo -e "  ${GREEN}Successfully fixed: $FIXED_COUNT${NC}"
echo "====================================================================="
echo ""

if [ $FIXED_COUNT -gt 0 ]; then
    echo -e "${GREEN}Done! Your NDK package.xml files have been fixed.${NC}"
    echo -e "${GREEN}You can now run your build again.${NC}"
else
    echo -e "${GREEN}No corrupted package.xml files found.${NC}"
fi

echo ""
