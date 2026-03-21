#!/usr/bin/env python3
"""
HILT VIEWMODEL IMPORT FIX - BATCH PROCESSOR
Fixes the duplicate hiltViewModel import issue across all affected files
"""

import os
from pathlib import Path

# Repository root
REPO_ROOT = Path(r"C:\Users\AuraF\Documents\ReGenesisExodus")

# Affected files from the latest error log
AFFECTED_FILES = [
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/aura/ui/AgentAdvancementScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/aura/ui/AgentNexusScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/aura/ui/XhancementScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chromacore/ui/ChromaAnimationMenu.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chromacore/ui/ChromaColorEngineMenu.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chromacore/ui/ChromaCoreHubScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chromacore/ui/ChromaLauncherMenu.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/chromacore/ui/ChromaStatusBarMenu.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/chromacore/InstantColorPickerScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/uxui_engine/AuraLabScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/uxui_engine/AurasLabScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/uxui_engine/IconifyPickerScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/conference/NexusConferenceScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/recovery/UIRecoveryBlackoutScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/recovery/UIRecoveryDialog.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/screens/SettingsScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/screens/aura/IconifyHubScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/theme/Theme.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/cascade/utils/cascade/CascadeZOrderPlayground.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/cascade/utils/cascade/trinity/TrinityScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/oracledrive/ui/OracleDriveScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/AgentBridgeHubScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/AppBuilderScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/CollabCanvasScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/ConferenceRoomScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/NeuralArchiveScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/OracleCloudInfiniteStorageScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/screens/SovereignNeuralArchiveScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/rom_tools/ROMFlasherScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/rom_tools/SovereignModuleManagerScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/rom_tools/SovereignRecoveryScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/kai/screens/security_shield/SovereignShieldScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/billing/BillingWrapper.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/billing/PaywallScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/AgentCreationScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/AgentHubSubmenuScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/AgentMonitoringScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/ArkBuildScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/MonitoringHUDsScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/PartyScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/SovereignMetaInstructScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/SovereignNemotronScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/domains/nexus/screens/TaskAssignmentScreen.kt",
    "app/src/main/java/dev/aurakai/auraframefx/hotswap/HotSwapScreen.kt"
]

def fix_file(filepath):
    """Fix hiltViewModel import issues in a single file."""
    try:
        content = filepath.read_text(encoding='utf-8')
        original = content
        changes = []
        
        # FIX 1: Remove deprecated import
        # Note: ripping out the exact line including trailing spaces/tabs
        bad_import_line = "import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel"
        if bad_import_line in content:
            # Handle possible variations in line endings or trailing whitespace
            lines = content.splitlines()
            new_lines = [l for l in lines if bad_import_line not in l]
            if len(new_lines) < len(lines):
                content = "\n".join(new_lines) + "\n"
                changes.append("Removed deprecated hiltViewModel import")
        
        # FIX 2: Ensure correct import exists
        good_import = "import androidx.hilt.navigation.compose.hiltViewModel"
        if good_import not in content:
            lines = content.splitlines()
            # Find a good spot to insert - after other hilt or compose imports
            insert_idx = -1
            for idx, line in enumerate(lines):
                if line.startswith('import androidx.hilt.') or line.startswith('import androidx.compose'):
                    insert_idx = idx + 1
            
            if insert_idx != -1:
                lines.insert(insert_idx, good_import)
                content = '\n'.join(lines) + "\n"
                changes.append("Added correct hiltViewModel import")
        
        # FIX 3: Add LocalViewModelStoreOwner import
        owner_import = "import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner"
        if owner_import not in content:
            lines = content.splitlines()
            insert_idx = -1
            for idx, line in enumerate(lines):
                if line.startswith('import androidx.lifecycle.'):
                    insert_idx = idx + 1
            
            if insert_idx != -1:
                lines.insert(insert_idx, owner_import)
                content = '\n'.join(lines) + "\n"
                changes.append("Added LocalViewModelStoreOwner import")
        
        if content != original:
            filepath.write_text(content, encoding='utf-8')
            return (True, f"FIXED: {', '.join(changes)}")
        return (True, "SKIP: No changes needed")
    except Exception as e:
        return (False, f"ERROR: {str(e)}")

def main():
    print("=" * 63)
    print("  HILT VIEWMODEL IMPORT FIX")
    print("=" * 63)
    print()
    
    success_count = 0
    skip_count = 0
    fail_count = 0
    
    for rel_path in AFFECTED_FILES:
        # Support both forward and backslashes
        filepath = REPO_ROOT / rel_path.replace('/', os.sep)
        if not filepath.exists():
            # Try to find it if relative path is slightly different
            # (e.g. if script run from within app/ folder)
            if (Path.cwd() / rel_path).exists():
                filepath = Path.cwd() / rel_path
            else:
                print(f"SKIP: {filepath} (Not Found)")
                skip_count += 1
                continue
        
        print(f"Processing: {filepath.name}")
        success, message = fix_file(filepath)
        print(f"  {message}\n")
        
        if success and "FIXED" in message:
            success_count += 1
        elif success:
            skip_count += 1
        else:
            fail_count += 1
    
    print("=" * 63)
    print(f"Fixed: {success_count} | Skipped: {skip_count} | Failed: {fail_count}")
    print("=" * 63)
    return 0 if fail_count == 0 else 1

if __name__ == "__main__":
    exit(main())
