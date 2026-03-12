#!/bin/bash
# Gradle Daemon Cleanup Script for Genesis Protocol
# Fixes: "Unable to load class 'com.android.ide.gradle.model.builder.AndroidStudioToolingPlugin'"

echo "🧹 Genesis Protocol - Gradle Daemon Cleanup"
echo "==========================================="
echo ""

echo "Step 1: Stopping all Gradle daemons..."
./gradlew --stop

echo ""
echo "Step 2: Killing any remaining Java processes..."
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    # Windows
    taskkill /F /IM java.exe 2>/dev/null || echo "No Java processes found"
else
    # Linux/Mac
    pkill -9 java 2>/dev/null || echo "No Java processes found"
fi

echo ""
echo "Step 3: Clearing Gradle caches..."
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/daemon/
rm -rf .gradle/

echo ""
echo "Step 4: Clearing build directories..."
find . -type d -name "build" -exec rm -rf {} + 2>/dev/null || true

echo ""
echo "Step 5: Re-downloading dependencies..."
./gradlew clean --refresh-dependencies

echo ""
echo "✅ Cleanup complete! Now run:"
echo "   ./gradlew assembleDebug"
echo ""
