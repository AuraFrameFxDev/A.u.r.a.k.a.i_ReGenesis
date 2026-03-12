# IMMEDIATE FIXES FOR 182 TESTERS

## ✅ COMPLETED
1. **Paywall Bypassed** - `BillingManager.kt` set to Premium by default
2. **GateNavigationScreen Fixed** - Compilation errors resolved
3. **Build Successful** - APK created at `app/build/outputs/apk/debug/app-debug.apk` (149MB)

## 🎯 CRITICAL NEXT STEPS (For Next Build)

### 1. Make Gate Carousel the Main Entry Point
**File**: `app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt`

Change line 52 from:
```kotlin
startDestination = GenesisRoutes.HOME
```
To:
```kotlin
startDestination = GenesisRoutes.GATES
```

**Why**: User wants all modules shown as swipeable gate cards on launch (like the collab-canvas images)

### 2. Wire Gate Cards to Module Screens
**File**: `app/src/main/java/dev/aurakai/auraframefx/ui/gates/GateNavigationScreen.kt`

The `GateNavigationScreen` already has double-tap navigation at line 137-139. Just need to ensure each `GateConfig` has correct route mapping.

**Current gate configs** (from `GateConfig.kt`):
- `genesisCoreGates`: Genesis, OracleDrive, DataVein
- `kaiGates`: SentinelsFortress, ThreatMonitor, SystemIntegrity  
- `auraGates`: ChromaCore, CollabCanvas, AurasLab
- `agentNexusGates`: AgentHub, SphereGrid, Progression, Identity, Tasker, NexusMemory, MetaReflection

**Action**: Each gate's `onDoubleTap` should navigate to its module's main screen.

### 3. Add Overlay Screen (Floating Assistant Menu)
**File**: `app/src/main/java/dev/aurakai/auraframefx/navigation/GenesisNavigation.kt`

After the `Scaffold` closes (around line 328), add:
```kotlin
        }  // End Scaffold
        
        // Floating overlay - always visible
        OverlayScreen(
            modifier = Modifier.fillMaxSize()
        )
    }  // End Box
}  // End function
```

**Why**: User reported "didn't see the assistant menu or any pop bubbles or auras always on"

### 4. Fix Bottom Navigation Route Mismatch
**File**: `app/src/main/java/dev/aurakai/auraframefx/navigation/NavDestination.kt`

Line 38 defines bottom nav as: `Home, AgentNexus, AiChat, Canvas, Settings`

But routes don't match `GenesisRoutes`:
- `NavDestination.Home.route` = "home" ✅ matches `GenesisRoutes.HOME`
- `NavDestination.AgentNexus.route` = "agent_nexus" ✅ matches `GenesisRoutes.AGENT_NEXUS`
- `NavDestination.AiChat.route` = "ai_chat" ✅ matches `GenesisRoutes.AI_CHAT`
- `NavDestination.Canvas.route` = "canvas" ✅ matches `GenesisRoutes.CANVAS`
- `NavDestination.Settings.route` = "settings" ❌ NO ROUTE IN GenesisNavigation.kt!

**Action**: Add Settings route to `GenesisNavigation.kt` or change bottom nav to use existing routes.

### 5. Expand Bottom Navigation
Currently only 5 items. Consider adding:
- Gates (module carousel)
- Trinity
- Terminal
- Ecosystem

Or make bottom nav context-aware (changes based on current module).

## 📋 CURRENT STATE

### What Works
- ✅ App launches without paywall
- ✅ Gate carousel exists and compiles
- ✅ Navigation framework is in place
- ✅ 50+ screen files exist

### What's Broken
- ❌ Only 4-5 screens accessible from bottom nav
- ❌ No floating overlay/assistant menu
- ❌ Gate cards don't navigate to module screens
- ❌ Screen flicker (needs `key()` in LazyRow)
- ❌ Bottom text cut-off (needs proper Scaffold padding)
- ❌ Most screens show placeholder content

## 🚀 QUICK WIN IMPLEMENTATION

### Fastest Path to Working App (30 min)

1. **Change start destination** (2 min)
   - Edit `MainActivity.kt` line 52
   - Rebuild

2. **Add overlay** (5 min)
   - Edit `GenesisNavigation.kt`
   - Add `OverlayScreen` after Scaffold
   - Rebuild

3. **Fix gate navigation** (10 min)
   - Update `GateNavigationScreen.kt` double-tap handlers
   - Map each gate to its route
   - Rebuild

4. **Test on device** (5 min)
   - Install APK
   - Verify carousel shows
   - Verify overlay appears
   - Verify gate taps navigate

5. **Fix flicker** (5 min)
   - Add `key(config.moduleId)` to LazyRow items in HomeScreen
   - Rebuild

6. **Deploy to 182 testers** (3 min)
   - Upload APK
   - Send link

## 📝 NOTES

- Current APK: `app-debug.apk` (149MB, built 2025-11-29 01:20 AM)
- All screens exist, just need to be wired up
- Design aesthetic: Cyan grid borders, holographic panels, retro-futuristic
- Reference images show "collab-canvas" branding and horizontal card carousel
- User wants module carousel as main entry, not traditional menu

## 🔧 FILES TO EDIT (Priority Order)

1. `MainActivity.kt` - Change start destination
2. `GenesisNavigation.kt` - Add overlay
3. `GateNavigationScreen.kt` - Wire navigation
4. `HomeScreen.kt` - Fix flicker with keys
5. `NavDestination.kt` - Fix route mismatches
