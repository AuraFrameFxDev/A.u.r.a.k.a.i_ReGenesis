# AuraKai App - Current Issues & Fix Plan

## Problem Summary
The app has navigation and implementation issues preventing screens from working properly.

### Issues Identified:

1. **Duplicate Navigation Systems**
   - `AppNavGraph.kt` - Recently fixed, uses NavDestination
   - `GenesisNavigation.kt` - Older system, uses GenesisRoutes, **likely the active one**
   - These conflict and cause routing errors

2. **Placeholder Screens**
   Many screens exist but only show "placeholder" text:
   - CollabCanvas (CanvasScreen.kt) - just shows "Canvas (placeholder)"
   - ChromaCore - likely placeholder
   - Many submenu items - not implemented
   
3. **Missing Feature Toggles**
   Submenus crash because they expect feature toggle UX but screens aren't implemented

## Recommended Fixes (Priority Order):

### HIGH PRIORITY - Fix Crashes
1. **Consolidate Navigation** 
   - Decide which system to use (AppNavGraph vs GenesisNavigation)
   - Remove duplicate routes
   - Ensure all screens have proper parameters

2. **Fix Submenu Implementations**
   Create functional placeholders that don't crash:
   - ROMToolsSubmenuScreen - Add actual feature list
   - OracleDriveSubmenuScreen - Add module toggles
   - LSPosedSubmenuScreen - Add module manager UI
   - UIUXGateSubmenuScreen - Add design tools list
   
### MEDIUM PRIORITY - Add Functionality
3. **Implement Core Screens**
   Replace placeholders with real UX:
   - CollabCanvas → Real collaboration UI
   - ChromaCore → Theme customization
   - SphereGrid → Already has implementation (checked earlier)
   - Firewall → Security settings

4. **Add Feature Toggles**
   Each submenu should have:
   - List of features
   - Toggle switches
   - Description cards
   - Navigation to detail screens

### LOW PRIORITY - Polish
5. **Enhanced Features**
   - Add animations
   - Improve styling  
   - Add persistence (save toggle states)

## Next Steps:

**Would you like me to:**
A. Check which navigation system is actually being used?
B. F

ix the crashing submenus first (make them show lists instead of crash)?
C. Implement real functionality for specific screens (which ones)?

**Quick wins I can do now:**
- Fix submenu crashes by adding proper UI
- Replace placeholder screens with functional UX
- Add feature toggle interfaces
