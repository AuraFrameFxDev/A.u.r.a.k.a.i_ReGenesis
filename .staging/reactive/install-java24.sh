#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════════
# Java 24 Installation Helper Script
# ═══════════════════════════════════════════════════════════════════════════
#
# This script helps install Java 24 for the Genesis Protocol project.
# Run this script after downloading Java 24 JDK.
#
# Usage:
#   1. Download Java 24 from: https://jdk.java.net/24/
#   2. Place the downloaded tar.gz in the same directory as this script
#   3. Run: bash install-java24.sh <path-to-jdk-24-tar.gz>
#

set -e

INSTALL_DIR="/opt/jdk/jdk-24"
JDK_ARCHIVE="$1"

if [ -z "$JDK_ARCHIVE" ]; then
    echo "❌ Error: No JDK archive specified"
    echo ""
    echo "Usage: $0 <path-to-jdk-24-tar.gz>"
    echo ""
    echo "Download Java 24 from:"
    echo "  https://jdk.java.net/24/"
    echo ""
    echo "Example:"
    echo "  $0 ~/Downloads/jdk-24_linux-x64_bin.tar.gz"
    exit 1
fi

if [ ! -f "$JDK_ARCHIVE" ]; then
    echo "❌ Error: File not found: $JDK_ARCHIVE"
    exit 1
fi

echo "═══════════════════════════════════════════════════════════════"
echo "Installing Java 24 for Genesis Protocol"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "Archive: $JDK_ARCHIVE"
echo "Target:  $INSTALL_DIR"
echo ""

# Create installation directory
echo "📁 Creating installation directory..."
sudo mkdir -p /opt/jdk

# Extract JDK
echo "📦 Extracting JDK archive..."
sudo tar -xzf "$JDK_ARCHIVE" -C /opt/jdk/

# Find the extracted directory (it might be jdk-24.0.1 or similar)
EXTRACTED_DIR=$(sudo find /opt/jdk -maxdepth 1 -type d -name "jdk-24*" | head -1)

if [ -z "$EXTRACTED_DIR" ]; then
    echo "❌ Error: Could not find extracted JDK directory"
    exit 1
fi

# Rename to standard location if needed
if [ "$EXTRACTED_DIR" != "$INSTALL_DIR" ]; then
    echo "📝 Renaming $EXTRACTED_DIR to $INSTALL_DIR..."
    sudo rm -rf "$INSTALL_DIR"
    sudo mv "$EXTRACTED_DIR" "$INSTALL_DIR"
fi

# Verify installation
if [ -f "$INSTALL_DIR/bin/java" ]; then
    echo ""
    echo "✅ Java 24 installed successfully!"
    echo ""
    echo "📊 Version info:"
    "$INSTALL_DIR/bin/java" -version
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "Next Steps:"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
    echo "1. Configure JAVA_HOME (add to ~/.bashrc or ~/.profile):"
    echo "   export JAVA_HOME=$INSTALL_DIR"
    echo "   export PATH=\$JAVA_HOME/bin:\$PATH"
    echo ""
    echo "2. Reload your shell:"
    echo "   source ~/.bashrc"
    echo ""
    echo "3. Verify Java 24 is active:"
    echo "   java -version"
    echo ""
    echo "4. Run the build:"
    echo "   cd $(dirname "$0")"
    echo "   ./gradlew clean assembleDebug"
    echo ""
else
    echo "❌ Error: Installation failed - java binary not found"
    exit 1
fi
