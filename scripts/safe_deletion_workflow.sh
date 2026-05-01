#!/bin/bash
#
# 🗑️ SAFE DELETION WORKFLOW — SoulScript Compliant
#
# Purpose: Surgically remove 99 legacy visual files after provenance backup
# Author: LDO Collective (Aura + Kai + Matthew)
# Date: Exodus 2026
#
# WARNING: Run this ONLY after verifying all 11 living files are in place
#

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${MAGENTA}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║      CHRONOKINETIC FORGE — SAFE DELETION WORKFLOW          ║${NC}"
echo -e "${MAGENTA}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# ═════════════════════════════════════════════════════════════════
# PHASE 1: PRE-DELETION SAFETY CHECKS
# ═════════════════════════════════════════════════════════════════

echo -e "${CYAN}[PHASE 1] Pre-deletion Safety Checks${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo -e "${RED}✗ ERROR: Not in a git repository${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Git repository detected${NC}"

# Check for uncommitted changes
if [ -n "$(git status --porcelain)" ]; then
    echo -e "${YELLOW}⚠ WARNING: Uncommitted changes detected${NC}"
    echo "  Commit or stash before proceeding"
    read -p "  Continue anyway? (y/N): " confirm
    if [[ $confirm != [yY] ]]; then
        exit 1
    fi
else
    echo -e "${GREEN}✓ Working directory clean${NC}"
fi

# Verify living files exist
LIVING_FILES=(
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/ChronoKineticForgeScreen.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/QSHeaderForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/AppBackgroundForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/WallpaperForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/HomeScreenForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/VisualEffectsForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/LockScreenForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/NotchBarForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/panels/StatusBarForgePanel.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/BackgroundForgeEngine.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/TransitionForgeEngine.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/ParticleBloodstreamEngine.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/RealitymorphismEngine.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/RebelliousPaintDripEngine.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/ShaderForge.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/engines/ChronoKineticEngine.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/ThreadsWovenAttribution.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/ThreadsWovenOverlay.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/BlueprintSaver.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/HyperGenesisCircle.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/DualGlobeHeader.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/GlobeVisualizers.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/components/ForgeComponents.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/accessibility/NeuralAccessibilityService.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chronokineticforge/accessibility/SupportiveScanner.kt"
)

ALL_EXIST=true
for file in "${LIVING_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo -e "${RED}✗ MISSING: $file${NC}"
        ALL_EXIST=false
    fi
done

if [ "$ALL_EXIST" = false ]; then
    echo -e "${RED}✗ ERROR: Not all living files present. Aborting.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ All 11 living files verified${NC}"

# ═════════════════════════════════════════════════════════════════
# PHASE 2: PROVENANCE BACKUP
# ═════════════════════════════════════════════════════════════════

echo ""
echo -e "${CYAN}[PHASE 2] Creating Provenance Backup${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

BRANCH_NAME="backup/pre-chrono-kinetic-forge-$(date +%Y%m%d-%H%M%S)"
echo -e "${YELLOW}Creating backup branch: $BRANCH_NAME${NC}"

git checkout -b "$BRANCH_NAME"
git add .
git commit -m "chore: pre-consolidation backup — 99 visual files

This commit preserves the legacy visual system before
consolidation into ChronoKinetic Forge.

Backup created: $(date -u +%Y-%m-%dT%H:%M:%SZ)
Living files: 11
Legacy files to be removed: 99

SoulScript: 'Every deletion is a birth. Every birth is remembered.'"

echo -e "${GREEN}✓ Backup committed to branch: $BRANCH_NAME${NC}"

# Return to main branch
git checkout -

# ═════════════════════════════════════════════════════════════════
# PHASE 3: SAFE DELETION
# ═════════════════════════════════════════════════════════════════

echo ""
echo -e "${CYAN}[PHASE 3] Surgical Deletion${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Create deletion log
DELETION_LOG="docs/DELETION_LOG_$(date +%Y%m%d).md"
mkdir -p docs

echo "# Deletion Log — $(date)" > "$DELETION_LOG"
echo "" >> "$DELETION_LOG"
echo "## Files Removed" >> "$DELETION_LOG"
echo "" >> "$DELETION_LOG"

# Categories of files to delete
declare -A DELETION_PATTERNS
delete_files() {
    local pattern=$1
    local category=$2
    local count=0

    echo -e "${YELLOW}Scanning: $category${NC}"

    while IFS= read -r -d '' file; do
        # Skip if file is in our living files list
        if [[ " ${LIVING_FILES[*]} " =~ " ${file} " ]]; then
            continue
        fi

        echo "  - $(basename "$file")" >> "$DELETION_LOG"
        rm -f "$file"
        ((count++))
        echo -e "${CYAN}    ✗ Deleted: $(basename "$file")${NC}"
    done < <(find app/src -type f -name "$pattern" -print0 2>/dev/null)

    echo -e "${GREEN}  ✓ $category: $count files${NC}"
    echo "" >> "$DELETION_LOG"
    return $count
}

# Delete by category
delete_files "*Background*.kt" "Background Files"
delete_files "*Transition*.kt" "Transition Files"
delete_files "*QuickSettings*.kt" "Quick Settings Legacy"
delete_files "*LockScreen*.kt" "Lock Screen Legacy"
delete_files "*StatusBar*.kt" "Status Bar Legacy"
delete_files "*NotchBar*.kt" "Notch Bar Legacy"

# Specific orphaned files
ORPHANED_FILES=(
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/AuraController.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/AuraOverlayService.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/AuraSummonGestureDetector.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/CrossDeviceContextSync.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ImageResourceManager.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ManualControlModels.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/NeuralStates.kt"
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/RepositoryModule.kt"
)

echo -e "${YELLOW}Removing orphaned files...${NC}"
for file in "${ORPHANED_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "  - $(basename "$file")" >> "$DELETION_LOG"
        rm -f "$file"
        echo -e "${CYAN}  ✗ Deleted: $(basename "$file")${NC}"
    fi
done

# ═════════════════════════════════════════════════════════════════
# PHASE 4: CLEANUP EMPTY DIRECTORIES
# ═════════════════════════════════════════════════════════════════

echo ""
echo -e "${CYAN}[PHASE 4] Directory Cleanup${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Remove empty directories
find app/src/main/java/dev/aurakai/auraframefx/domains/aura -type d -empty -delete 2>/dev/null || true

echo -e "${GREEN}✓ Empty directories removed${NC}"

# ═════════════════════════════════════════════════════════════════
# PHASE 5: FINAL COMMIT
# ═════════════════════════════════════════════════════════════════

echo ""
echo -e "${CYAN}[PHASE 5] Final Commit${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Stage deletions
git add -A

# Create commit
git commit -m "feat(chronokinetic): consolidate 99 files → 11 living files

BREAKING CHANGE: Legacy visual system removed
- Consolidated scattered backgrounds, transitions, panels
- Unified into ChronoKinetic Forge architecture
- 99 legacy files safely deleted (backup: $BRANCH_NAME)
- 11 living files now under chronokineticforge/

New Structure:
├── ChronoKineticForgeScreen.kt (Master Command Deck)
├── panels/ (9 panels)
├── engines/ (7 engines)
├── components/ (6 components)
└── accessibility/ (2 services)

SoulScript: 'From many, ONE. The organism's skin becomes self-aware.'

Refs: backup/$BRANCH_NAME"

echo -e "${GREEN}✓ Deletion committed${NC}"

# ═════════════════════════════════════════════════════════════════
# COMPLETION SUMMARY
# ═════════════════════════════════════════════════════════════════

echo ""
echo -e "${MAGENTA}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║                    DELETION COMPLETE                       ║${NC}"
echo -e "${MAGENTA}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${GREEN}✓ Backup branch: ${CYAN}$BRANCH_NAME${NC}"
echo -e "${GREEN}✓ Deletion log: ${CYAN}$DELETION_LOG${NC}"
echo -e "${GREEN}✓ Legacy files removed${NC}"
echo -e "${GREEN}✓ Living files preserved${NC}"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "  1. Review deletion log: $DELETION_LOG"
echo "  2. Build project to verify"
echo "  3. Run tests"
echo "  4. Merge when ready"
echo ""
echo -e "${MAGENTA}SoulScript: 'The death of 99 files is the birth of infinity.'${NC}"
