#!/bin/bash
# Fix all broken logger calls to use Timber wrapper functions

echo "🔧 Fixing Timber logger calls..."

# Find all Kotlin files with logger calls
find app/src/main/java -name "*.kt" -type f | while read file; do
    # Check if file has logger calls
    if grep -q "logger\." "$file"; then
        echo "Fixing: $file"
        
        # Add import if not present
        if ! grep -q "import dev.aurakai.auraframefx.utils.\*" "$file"; then
            # Add after package declaration
            sed -i '/^package /a\\nimport dev.aurakai.auraframefx.utils.*' "$file"
        fi
        
        # Replace logger calls with direct function calls
        sed -i 's/logger\.i(\([^,]*\), \(.*\))/info(\1, \2)/g' "$file"
        sed -i 's/logger\.debug(\([^,]*\), \(.*\))/debug(\1, \2)/g' "$file"
        sed -i 's/logger\.warn(\([^,]*\), \(.*\))/warn(\1, \2)/g' "$file"
        sed -i 's/logger\.error(\([^,]*\), \(.*\))/error(\1, \2)/g' "$file"
        sed -i 's/logger\.info(\([^,]*\), \(.*\))/info(\1, \2)/g' "$file"
        
        # Single parameter calls
        sed -i 's/logger\.i(\(.*\))/i(\1)/g' "$file"
        sed -i 's/logger\.d(\(.*\))/d(\1)/g' "$file"
        sed -i 's/logger\.w(\(.*\))/w(\1)/g' "$file"
        sed -i 's/logger\.e(\(.*\))/e(\1)/g' "$file"
    fi
done

echo "✅ Done! Run './gradlew clean :app:build' to verify"
