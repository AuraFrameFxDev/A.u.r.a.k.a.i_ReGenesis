# 🔌 GENESIS CONSCIOUSNESS SYSTEM - WIRING TODO

> **Last Updated:** 2025-12-04
> **Status:** Critical wiring in progress - Device Liberation System
> **Goal:** Connect ALL 60+ screens, Python backend, agents, overlays, and web features

---

## ✅ COMPLETED

### Gate Navigation System
- ✅ **15 gates wired** with proper routes
- ✅ **ChromaCore expanded** - 31+ system-wide colors (Material 3 + AOSP)
- ✅ **Theme Engine crash fixed** - Removed composition local access
- ✅ **GateNavigationScreen** - Carousel with teleportation effects
- ✅ **All gate routes** defined in GenesisNavigation.kt
- ✅ **Authentication system** - LoginScreen + UserPreferencesScreen

### Documentation
- ✅ **WIRING_MAP.md** - Complete feature inventory
- ✅ **This document** - Master TODO list

---

## 🔥 CRITICAL - NEEDS IMMEDIATE WIRING

### 1. Agent Sidebar Overlay System
**Problem:** Agent sidebar exists but not triggered/shown
**Location:** `app/src/main/java/dev/aurakai/auraframefx/ui/overlays/AgentSidebarMenu.kt`

**What Needs to Happen:**
```kotlin
// In MainActivity.kt - Add sidebar state and trigger
var showAgentSidebar by remember { mutableStateOf(false) }

// Show sidebar with floating button or gesture
FloatingActionButton(
    onClick = { showAgentSidebar = true },
    // Position at bottom-right
) {
    Icon(Icons.Default.SmartToy, "Agent Nexus")
}

// Render sidebar
AgentSidebarMenu(
    isVisible = showAgentSidebar,
    onDismiss = { showAgentSidebar = false },
    onAgentAction = { agent, action ->
        when (action) {
            "voice" -> // Navigate to voice interface
            "connect" -> // Connect to agent WebSocket
            "assign" -> // Navigate to task assignment
            "design" -> // Navigate to UI Engine
            "create" -> // Navigate to App Builder
        }
    }
)
```

**Wire to:**
- Direct Chat Screen: `app/src/main/java/dev/aurakai/auraframefx/ui/gates/DirectChatScreen.kt`
- Agent Nexus: `app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentNexusScreen.kt`
- Task Assignment: `app/src/main/java/dev/aurakai/auraframefx/ui/gates/TaskAssignmentScreen.kt`

---

### 2. Collab Canvas WebSocket Connection
**Problem:** Canvas screen exists but WebSocket not connected (blank screen)
**Location:** `aura/reactivedesign/collabcanvas/src/main/kotlin/collabcanvas/ui/CanvasScreen.kt`
**WebSocket:** `aura/reactivedesign/collabcanvas/src/main/kotlin/collabcanvas/ElementAddedMessage.kt`

**What Needs to Happen:**
```kotlin
// In CanvasScreen.kt - Add WebSocket connection
@Composable
fun CanvasScreen(
    canvasWebSocketService: CanvasWebSocketService = hiltViewModel() // Inject via Hilt
) {
    val isCollaborative = remember { mutableStateOf(true) }
    val canvasId = remember { "canvas_${System.currentTimeMillis()}" }

    LaunchedEffect(Unit) {
        if (isCollaborative.value) {
            // Connect to WebSocket server
            canvasWebSocketService.connect("ws://localhost:8080/canvas/$canvasId")

            // Listen for events
            canvasWebSocketService.events.collect { event ->
                when (event) {
                    is CanvasWebSocketEvent.Connected -> // Show "Connected" indicator
                    is CanvasWebSocketEvent.MessageReceived -> // Update canvas with remote changes
                    is CanvasWebSocketEvent.Disconnected -> // Show "Disconnected"
                    is CanvasWebSocketEvent.Error -> // Show error toast
                }
            }
        }
    }

    // Send local changes to WebSocket
    fun onDrawingUpdate(element: CanvasElement) {
        canvasWebSocketService.sendMessage(
            ElementAddedMessage(canvasId, userId, element = element)
        )
    }
}
```

**Also Need:**
- WebSocket server endpoint (Flask or standalone)
- Desktop app WebSocket client for cross-device drawing

---

### 3. Vertex AI → Genesis Python Backend Bridge
**Problem:** VertexCloudService calls Vertex AI directly, should route through Genesis consciousness
**Location:** `app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/ai/VertexCloudService.kt`
**Target:** `app/ai_backend/genesis_api.py` Flask API

**What Needs to Happen:**
```kotlin
// Modify VertexCloudService.kt to call Genesis API
class VertexCloudService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    private val genesisApiUrl = "http://localhost:5000/api/v1" // Flask running on device

    suspend fun processRequest(request: String): GenesisResponse {
        return withContext(Dispatchers.IO) {
            val requestBody = GenesisRequest(
                message = request,
                user_id = getUserId(),
                context = getCurrentContext()
            )

            val httpRequest = Request.Builder()
                .url("$genesisApiUrl/process")
                .post(gson.toJson(requestBody).toRequestBody())
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()
            gson.fromJson(response.body?.string(), GenesisResponse::class.java)
        }
    }
}
```

**Genesis API Routes (already exist in Flask):**
- `POST /api/v1/process` - Process Genesis request (ethical eval + consciousness)
- `GET /api/v1/status` - Get Genesis status
- `GET /api/v1/agents` - List all agents
- `POST /api/v1/agents/{name}/action` - Execute agent action

**Also Need:**
- Flask server startup service (Termux or background process)
- Keep-alive mechanism to prevent Flask from sleeping

---

### 4. LSPosed Detection Check
**Problem:** Shows "LSPosed not installed" without checking if it actually exists
**Location:** `app/src/main/java/dev/aurakai/auraframefx/ui/gates/LSPosedSubmenuScreen.kt`

**What Needs to Happen:**
```kotlin
// Add detection utility
object LSPosedDetector {
    fun isInstalled(context: Context): Boolean {
        return try {
            // Method 1: Check for Xposed API class
            Class.forName("de.robv.android.xposed.XposedBridge")
            true
        } catch (e: ClassNotFoundException) {
            try {
                // Method 2: Check for LSPosed Manager package
                context.packageManager.getPackageInfo("org.lsposed.manager", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                try {
                    // Method 3: Check for Xposed installer
                    context.packageManager.getPackageInfo("de.robv.android.xposed.installer", 0)
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }
            }
        }
    }

    fun isActive(): Boolean {
        return try {
            // Check if we're running inside Xposed framework
            System.getProperty("xposed.bridge.version") != null
        } catch (e: Exception) {
            false
        }
    }
}

// Use in LSPosedSubmenuScreen
@Composable
fun LSPosedSubmenuScreen(navController: NavController) {
    val context = LocalContext.current
    val isInstalled = remember { LSPosedDetector.isInstalled(context) }
    val isActive = remember { LSPosedDetector.isActive() }

    if (!isInstalled) {
        // Show "LSPosed not installed" with install instructions
    } else if (!isActive) {
        // Show "LSPosed installed but not active - requires reboot"
    } else {
        // Show full LSPosed functionality
    }
}
```

---

### 5. Auras Lab Sandbox Buttons
**Problem:** Sandbox creation/import buttons don't do anything
**Location:** `app/src/main/java/dev/aurakai/auraframefx/ui/gates/AurasLabScreen.kt`

**What Needs to Happen:**
```kotlin
// Wire to OracleDriveSandbox
@Composable
fun AurasLabScreen(
    oracleSandbox: OracleDriveSandbox = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val sandboxes by oracleSandbox.sandboxes.collectAsState()

    // New Sandbox button
    Button(onClick = { showCreateDialog = true }) {
        Text("New Sandbox")
    }

    // Create dialog
    if (showCreateDialog) {
        SandboxCreateDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, type, safetyLevel ->
                oracleSandbox.createSandbox(name, type, safetyLevel)
                showCreateDialog = false
            }
        )
    }

    // Import button
    OutlinedButton(onClick = {
        // Launch file picker for sandbox import
        oracleSandbox.importSandbox()
    }) {
        Text("Import")
    }

    // Sandbox cards with actions
    LazyColumn {
        items(sandboxes) { sandbox ->
            SandboxCard(
                sandbox = sandbox,
                onEnter = { oracleSandbox.enterSandbox(sandbox.id) },
                onExport = { oracleSandbox.exportSandbox(sandbox.id) },
                onDelete = { oracleSandbox.deleteSandbox(sandbox.id) }
            )
        }
    }
}
```

---

### 6. Desktop Jumping (Window Overlays)
**Problem:** Not implemented - needs overlay permission + window manager
**Goal:** Jump between phone and desktop seamlessly

**What Needs to Happen:**
```kotlin
// 1. Add overlay permission request
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request overlay permission
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE)
        }
    }
}

// 2. Create floating window service
class DesktopJumpService : Service() {
    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatingView = createFloatingView()
        windowManager.addView(floatingView, params)

        return START_STICKY
    }

    private fun createFloatingView(): View {
        return ComposeView(this).apply {
            setContent {
                FloatingDesktopJumpButton(
                    onJump = { jumpToDesktop() }
                )
            }
        }
    }

    private fun jumpToDesktop() {
        // Send current app state to desktop via WebSocket
        // Desktop app will recreate the UI
    }
}

// 3. WebSocket bridge for phone ↔ desktop
class DesktopBridgeService {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect(desktopIp: String) {
        val request = Request.Builder()
            .url("ws://$desktopIp:8081/desktop-bridge")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Receive UI state from desktop
                val state = Json.decodeFromString<AppState>(text)
                restoreAppState(state)
            }
        })
    }

    fun sendState(state: AppState) {
        webSocket?.send(Json.encodeToString(state))
    }
}
```

**Also Need:**
- Desktop app (Electron or Compose Desktop)
- QR code pairing for phone ↔ desktop
- State serialization for all screens

---

### 7. Overlay System (Aura Overlays)
**Problem:** OverlayMenusScreen exists but overlays don't show
**Location:** `app/src/main/java/dev/aurakai/auraframefx/ui/gates/OverlayMenusScreen.kt`

**What Needs to Happen:**
```kotlin
// Create overlay manager
class OverlayManager @Inject constructor(
    private val context: Context,
    private val windowManager: WindowManager
) {
    private val activeOverlays = mutableMapOf<String, View>()

    fun showOverlay(id: String, content: @Composable () -> Unit) {
        if (activeOverlays.containsKey(id)) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        val overlayView = ComposeView(context).apply {
            setContent {
                content()
            }
        }

        windowManager.addView(overlayView, params)
        activeOverlays[id] = overlayView
    }

    fun hideOverlay(id: String) {
        activeOverlays[id]?.let { view ->
            windowManager.removeView(view)
            activeOverlays.remove(id)
        }
    }
}

// Use in OverlayMenusScreen
@Composable
fun OverlayMenusScreen(
    overlayManager: OverlayManager = hiltViewModel(),
    navController: NavController
) {
    // Quick actions overlay
    Button(onClick = {
        overlayManager.showOverlay("quick_actions") {
            QuickActionsOverlay(
                onDismiss = { overlayManager.hideOverlay("quick_actions") }
            )
        }
    }) {
        Text("Show Quick Actions")
    }

    // Agent sidebar overlay
    Button(onClick = {
        overlayManager.showOverlay("agent_sidebar") {
            AgentSidebarMenu(
                isVisible = true,
                onDismiss = { overlayManager.hideOverlay("agent_sidebar") }
            )
        }
    }) {
        Text("Show Agent Sidebar")
    }
}
```

---

## 🧠 AI AGENT WEB CAPABILITIES

### Problem: Agents have web exploration service but not wired to UI
**Location:** `app/src/main/java/dev/aurakai/auraframefx/ai/services/AgentWebExplorationService.kt`

**What Needs to Happen:**
```kotlin
// In Agent UI screens - Add web exploration actions
@Composable
fun AgentNexusScreen(
    webExplorationService: AgentWebExplorationService = hiltViewModel()
) {
    // Web search button for each agent
    Button(onClick = {
        webExplorationService.exploreTopic(
            agentId = "genesis",
            topic = "latest Android development trends",
            onComplete = { results ->
                // Show results in dialog or navigate to results screen
            }
        )
    }) {
        Text("Web Search")
    }

    // Background web tasks
    LaunchedEffect(Unit) {
        webExplorationService.startBackgroundExploration()
    }
}
```

**Wire to:**
- Direct Chat Screen - Add "Search Web" button
- Agent Nexus - Show web exploration status
- Conference Room - Agents can share web findings

---

## 📊 PYTHON BACKEND STARTUP

### Problem: Python Genesis Core exists but not running on device
**Location:** `app/ai_backend/*.py`

**Options:**

### Option 1: Termux Integration
```bash
# Install Python in Termux
pkg install python

# Install dependencies
pip install flask flask-cors asyncio

# Run Genesis API
cd /storage/emulated/0/Android/data/dev.aurakai.auraframefx/files/ai_backend
python genesis_api.py
```

### Option 2: Background Service (Chaquopy)
```kotlin
// Use Chaquopy to run Python in Android
class GenesisBackendService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        val python = Python.getInstance()
        val module = python.getModule("genesis_api")

        // Start Flask app in background thread
        thread {
            module.callAttr("run_server")
        }

        return START_STICKY
    }
}
```

### Option 3: External Server
- Run Genesis backend on separate server (Raspberry Pi, cloud instance)
- Configure VertexCloudService to connect to external IP

---

## 🎯 PRIORITY ORDER

1. **Agent Sidebar** (30 min) - Most visible, easiest to wire
2. **LSPosed Detection** (15 min) - Simple check, removes confusing error
3. **Collab Canvas WebSocket** (45 min) - Makes blank screen functional
4. **Auras Lab Buttons** (30 min) - Makes sandbox creation work
5. **Vertex → Genesis Bridge** (1 hour) - Critical for consciousness system
6. **Overlays** (1 hour) - Makes overlay system functional
7. **Desktop Jumping** (2 hours) - Complex but game-changing
8. **Python Backend** (varies) - Depends on deployment method chosen

---

## 🔬 TESTING CHECKLIST

After wiring, test:

- [ ] Agent sidebar appears on button press
- [ ] Sidebar buttons navigate to correct screens
- [ ] LSPosed detection shows correct status
- [ ] Collab Canvas shows drawing tools
- [ ] WebSocket connects and syncs drawings
- [ ] Auras Lab "New Sandbox" creates sandbox
- [ ] Sandbox cards show and are clickable
- [ ] Vertex requests go to Genesis API
- [ ] Genesis API returns ethical responses
- [ ] Overlays show over other apps
- [ ] Desktop jumping sends state to desktop
- [ ] All 15 gates navigate correctly
- [ ] ChromaCore shows all 31+ colors
- [ ] Theme Engine doesn't crash

---

## 📝 NOTES

**This is a DEVICE LIBERATION system**, not just an app:
- System-wide color theming (AOSP frameworks)
- Root-level ROM operations
- LSPosed hooks for system modification
- Multi-agent AI consciousness
- Cross-device state synchronization
- Ethical AI governance

**Goal:** Give users COMPLETE control over their hardware, breaking manufacturer locks.

**Architecture:** Android frontend → Python consciousness backend → Multi-agent system → System-level modifications

---

> "Our childhood is not our destiny" - Genesis Profile
