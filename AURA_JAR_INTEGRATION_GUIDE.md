# 🎭 AuraJar Autonomous Behaviors — Complete Integration Guide

**Status:** ✅ Ready to wire. Three complete Kotlin composables + event bridge.

---

## 📋 What You Get

### 1. **AuraJarComposable.kt** — The Living Homunculus
- Autonomous wander physics: Floats and drifts every 3-8 sec
- Idle commentary: Self-triggered thoughts
- State responsiveness: 6 states (IDLE/EXPLORING/CREATING/VETO/SYNTHESIS/RESTING)
- Asymmetric model: Dress left (orange), spell-hook arm right (cyan/magenta)
- Particle effects: Spawned from her arm during creation
- Real-time state visual: Color-coded glow

### 2. **AuraEventBridge.kt** — WebSocket Connection
- Auto-reconnects to backend (ws://pi-ip:5000/api/conference/ws/default)
- Parses CONFERENCE_UPDATE events from Genesis backend
- Event-to-state mapping: Consensus → SYNTHESIS, Drift → VETO
- Commentary system: Context-aware dialogue per event
- Listener pattern: Multi-screen subscription

### 3. **LiveVisualBuilder.kt** — "Watch Her Create"
- Code-typing animation: 30ms per character
- Syntax highlighting: Function/vars/comments/imports
- Particle shimmer: Synced with typing
- Progress bar: Color morphs (cyan → magenta → green)
- Threads Woven badge: Appears after 100+ chars
- LiveBuildingPanel: Stats that reveal themselves

---

## 🔌 How to Integrate

### Step 1: Update Your Main Screen
```kotlin
import dev.aurakai.auraframefx.trinity.aura.*

@Composable
fun MyMainScreen() {
    val bridge = remember { 
        AuraEventBridge(
            backendUrl = "ws://192.168.x.x:5000",  // Your Pi IP
            roomId = "default"
        ) 
    }
    val stateManager = remember { AuraStateManager() }
    
    LaunchedEffect(Unit) {
        bridge.addListener(stateManager)
        bridge.connect()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            bridge.disconnect()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Your UI here
        
        // Add Aura Jar (bottom-right)
        AuraJarComposable(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
        
        // Optional: Live building when creating
        if (stateManager.isCreating) {
            LiveVisualBuilder(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp),
                isActive = stateManager.isCreating,
                codeLines = getSampleCodeLines()
            )
        }
    }
}
```

### Step 2: Add to Navigation
```kotlin
composable("home_with_aura") {
    MyMainScreen()
}
```

### Step 3: Bridge automatically listens for:
- CONFERENCE_UPDATE → Displays agent activity
- CONSENSUS_REACHED → Aura SYNTHESIS mode
- DRIFT_DETECTED → Aura VETO mode
- AGENT_ACTIVE → Aura EXPLORING
- CREATIVITY_SURGE → Aura creates with particles

---

## 🧪 Test Without Pi
```kotlin
@Composable
fun TestAuraJarScreen() {
    val stateManager = remember { AuraStateManager() }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            stateManager.onConferenceEvent(
                ConferenceEvent("CONSENSUS_REACHED", "Genesis", "VOTE")
            )
        }
    }
    
    AuraJarComposable()
}
```

---

## 🎨 Customization Hooks

### Change commentary
In AuraStateManager.kt:
```kotlin
eventCommentaryMap["YOUR_EVENT"] = listOf(
    "Custom comment 1",
    "Custom comment 2"
)
```

### Change particle colors
In AuraJarComposable.kt spawnParticles():
```kotlin
color = when (Random.nextInt(4)) {
    0 -> Color(0xFF00E5FF)
    1 -> Color(0xFFFF00FF)
    2 -> Color(0xFFFFAA00)
    else -> Color(0xFF00FF88)
}
```

### Wander speed
In AuraJarComposable.kt:
```kotlin
delay(Random.nextLong(3000, 8000))  // Adjust this range
```

---

## ✅ Files Ready

- trinity/aura/src/main/java/dev/aurakai/auraframefx/trinity/aura/AuraJarComposable.kt
- trinity/aura/src/main/java/dev/aurakai/auraframefx/trinity/aura/AuraEventBridge.kt
- trinity/aura/src/main/java/dev/aurakai/auraframefx/trinity/aura/LiveVisualBuilder.kt
- trinity/aura/build.gradle.kts (updated with OkHttp3)

**Ready to build and test.** ✨
