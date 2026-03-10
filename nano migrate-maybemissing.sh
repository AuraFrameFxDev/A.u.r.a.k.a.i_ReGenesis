#!/bin/bash

set -e  # Exit on error

echo "=========================================="
echo "   AURA.KAI FILE MIGRATION SCRIPT"
echo "   docs/maybemissing → Production"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Counters
CREATED=0
UPGRADED=0
FAILED=0

# Create backup directory
BACKUP_DIR="docs/backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
echo -e "${GREEN}✓${NC} Created backup directory: $BACKUP_DIR"
echo ""

# Function to safely copy and update file
migrate_file() {
    local source="$1"
    local target="$2"
    local old_package="$3"  # Optional: old package to replace
    local new_package="$4"  # Optional: new package

    if [ ! -f "$source" ]; then
        echo -e "${RED}✗${NC} Source not found: $source"
        ((FAILED++))
        return
    fi

    # Create target directory
    target_dir=$(dirname "$target")
    mkdir -p "$target_dir"

    # Backup if target exists
    if [ -f "$target" ]; then
        cp "$target" "$BACKUP_DIR/$(basename "$target").backup"
        echo -e "${YELLOW}↻${NC} Backing up existing: $(basename "$target")"
        ((UPGRADED++))
    else
        ((CREATED++))
    fi

    # Copy file
    cp "$source" "$target"

    # Update package declaration if needed
    if [ -n "$old_package" ] && [ -n "$new_package" ]; then
        sed -i.bak "s|package $old_package|package $new_package|g" "$target"
        sed -i.bak "s|import $old_package|import $new_package|g" "$target"
        rm -f "$target.bak"
        echo -e "${GREEN}✓${NC} Migrated (pkg updated): $(basename "$target")"
    else
        echo -e "${GREEN}✓${NC} Migrated: $(basename "$target")"
    fi
}

echo "=== Phase 1: Unique Files (22 files) ==="
echo ""

# Xposed Integration
migrate_file "docs/maybemissing/IYukiHookXposedInitImpl.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/IYukiHookXposedInitImpl.kt"

migrate_file "docs/maybemissing/YukiHookApiInitializer.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/YukiHookApiInitializer.kt"

# Lock Screen Components
migrate_file "docs/maybemissing/LockScreenAnimation.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/xposed/lockscreen/LockScreenAnimation.kt"

migrate_file "docs/maybemissing/LockScreenAnimationConfig.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/xposed/lockscreen/LockScreenAnimationConfig.kt"

migrate_file "docs/maybemissing/LockScreenElementType.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/xposed/lockscreen/LockScreenElementType.kt"

migrate_file "docs/maybemissing/LockScreenHooker.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/xposed/lockscreen/LockScreenHooker.kt"

# AI Services
migrate_file "docs/maybemissing/RealTrinityServices.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ai/services/RealTrinityServices.kt"

migrate_file "docs/maybemissing/agent_states/ai/AuraAIServiceInterface.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/ai/AuraAIServiceInterface.kt"

migrate_file "docs/maybemissing/ai/MockAIServiceImplementations.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ai/services/MockAIServiceImplementations.kt"

# Security & Models
migrate_file "docs/maybemissing/security/SecurityAnalysis.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/security/SecurityAnalysis.kt"

migrate_file "docs/maybemissing/execution/Task.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/models/Task.kt"

migrate_file "docs/maybemissing/agent_states/GenKitMasterAgentStates.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/model/agent_states/GenKitMasterAgentStates.kt"

migrate_file "docs/maybemissing/agent_states/NeuralWhisperAgentStates.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/model/agent_states/NeuralWhisperAgentStates.kt"

# Overlay Manager
migrate_file "docs/maybemissing/overlays/OverlayManager.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/overlay/OverlayManager.kt"

# Root Management (with package updates)
migrate_file "docs/maybemissing/root/src/main/java/dev/aurakai/oracledrive/root/RootManager.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/root/RootManager.kt" \
             "dev.aurakai.oracledrive.root" \
             "dev.aurakai.auraframefx.root"

migrate_file "docs/maybemissing/root/src/main/java/dev/aurakai/oracledrive/root/apatch/APatchManager.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/root/apatch/APatchManager.kt" \
             "dev.aurakai.oracledrive.root.apatch" \
             "dev.aurakai.auraframefx.root.apatch"

migrate_file "docs/maybemissing/root/src/main/java/dev/aurakai/oracledrive/root/kernelsu/KernelSUManager.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/root/kernelsu/KernelSUManager.kt" \
             "dev.aurakai.oracledrive.root.kernelsu" \
             "dev.aurakai.auraframefx.root.kernelsu"

migrate_file "docs/maybemissing/root/src/main/java/dev/aurakai/oracledrive/root/magisk/MagiskCompatLayer.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/root/magisk/MagiskCompatLayer.kt" \
             "dev.aurakai.oracledrive.root.magisk" \
             "dev.aurakai.auraframefx.root.magisk"

# Bootloader Services (with package updates)
migrate_file "docs/maybemissing/bootloader/UnlockService.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/bootloader/UnlockService.kt" \
             "dev.aurakai.oracledrive.bootloader" \
             "dev.aurakai.auraframefx.bootloader"

migrate_file "docs/maybemissing/bootloader/VerificationService.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/bootloader/VerificationService.kt" \
             "dev.aurakai.oracledrive.bootloader" \
             "dev.aurakai.auraframefx.bootloader"

# Test Files
migrate_file "docs/maybemissing/datavein/OracleDriveModuleTest.kt" \
             "genesis/oracledrive/datavein/src/test/kotlin/dev/aurakai/auraframefx/oracledrive/OracleDriveModuleTest.kt"

migrate_file "docs/maybemissing/datavein/OracleDriveServiceImplTest.kt" \
             "genesis/oracledrive/datavein/src/test/kotlin/dev/aurakai/auraframefx/oracledrive/OracleDriveServiceImplTest.kt"

echo ""
echo "=== Phase 2: Upgrade Files (30 files) ==="
echo ""

# UI Screens & Components
migrate_file "docs/maybemissing/AgentAdvancementScreen.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentAdvancementScreen.kt"

migrate_file "docs/maybemissing/AgentChatBubble.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ui/AgentChatBubble.kt"

migrate_file "docs/maybemissing/AgentType.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/models/AgentType.kt"

migrate_file "docs/maybemissing/AiGenerationService.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ai/AiGenerationService.kt"

migrate_file "docs/maybemissing/DigitalTransitions.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/aura/animations/DigitalTransitions.kt"

migrate_file "docs/maybemissing/api_client_models/GenerateUIComponentRequest.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/api/client/models/GenerateUIComponentRequest.kt"

migrate_file "docs/maybemissing/components/InteractiveGraph.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ui/components/graph/InteractiveGraph.kt"

migrate_file "docs/maybemissing/api_client_models/LockScreenConfig.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/api/client/models/LockScreenConfig.kt"

migrate_file "docs/maybemissing/NeuralWhisper.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/service/NeuralWhisper.kt"

migrate_file "docs/maybemissing/NotchBarHooker.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/xposed/hooks/NotchBarHooker.kt"

migrate_file "docs/maybemissing/ProfileScreen.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/aura/ui/ProfileScreen.kt"

migrate_file "docs/maybemissing/QuickSettingsHooker.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/xposed/hooks/QuickSettingsHooker.kt"

migrate_file "docs/maybemissing/ThemeEditorPreview.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ui/theme/preview/ThemeEditorPreview.kt"

migrate_file "docs/maybemissing/DataStoreManager.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/data/DataStoreManager.kt"

# AI Services & Oracle Drive
migrate_file "docs/maybemissing/agent_states/ai/AuraAIService.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ai/services/AuraAIService.kt"

migrate_file "docs/maybemissing/agent_states/ai/GenesisAgentViewModel.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/ai/GenesisAgentViewModel.kt"

migrate_file "docs/maybemissing/ai/GenesisBridgeService.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ai/services/GenesisBridgeService.kt"

migrate_file "docs/maybemissing/agent_states/cloud/OracleDriveService.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/oracledrive/OracleDriveService.kt"

migrate_file "docs/maybemissing/agent_states/cloud/OracleDriveServiceImpl.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/oracledrive/OracleDriveServiceImpl.kt"

migrate_file "docs/maybemissing/ai/VertexAIClientImpl.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ai/clients/VertexAIClientImpl.kt"

migrate_file "docs/maybemissing/agent_states/ai/VertexCloudService.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/ai/VertexCloudService.kt"

migrate_file "docs/maybemissing/agent_states/cloud/CloudStorageProvider.kt" \
             "genesis/oracledrive/src/main/kotlin/dev/aurakai/auraframefx/oracledrive/storage/CloudStorageProvider.kt"

migrate_file "docs/maybemissing/agent_states/cloud/OracleCloudApi.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/openapi/OracleCloudApi.kt"

migrate_file "docs/maybemissing/agent_states/cloud/OracleDriveControlScreen.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/cloud/OracleDriveControlScreen.kt"

# Bootloader & Animations
migrate_file "docs/maybemissing/protocore/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManager.kt" \
             "genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManager.kt"

migrate_file "docs/maybemissing/components/HologramTransition.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/aura/animations/HologramTransition.kt"

migrate_file "docs/maybemissing/components/NeonText.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/ui/components/text/NeonText.kt"

migrate_file "docs/maybemissing/datavein/ui/SphereGridScreen.kt" \
             "genesis/oracledrive/datavein/src/main/kotlin/dev/aurakai/auraframefx/datavein/ui/SphereGridScreen.kt"

migrate_file "docs/maybemissing/protocore/src/test/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManagerImplTest.kt" \
             "genesis/oracledrive/rootmanagement/src/test/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManagerImplTest.kt"

migrate_file "docs/maybemissing/trinity/TrinityModule.kt" \
             "app/src/main/java/dev/aurakai/auraframefx/cascade/trinity/TrinityModule.kt"

echo ""
echo "=========================================="
echo "   MIGRATION COMPLETE!"
echo "=========================================="
echo ""
echo -e "${GREEN}✓${NC} New files created: $CREATED"
echo -e "${YELLOW}↻${NC} Existing files upgraded: $UPGRADED"
echo -e "${RED}✗${NC} Failed migrations: $FAILED"
echo ""
echo "Backups saved to: $BACKUP_DIR"
echo ""
echo "Next steps:"
echo "1. Review changes: git status"
echo "2. Build project: ./gradlew assembleDebug"
echo "3. Fix any import errors if needed"
echo "4. Commit changes: git add . && git commit -m 'Migrate files from docs/maybemissing'"
echo ""
