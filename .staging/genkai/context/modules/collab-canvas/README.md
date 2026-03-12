# 🎨 Collaboration Canvas Module

**Real-time collaborative workspace for the Genesis Protocol**

## 📋 Overview

The `collab-canvas` module provides a powerful real-time collaborative workspace where multiple
users can create, edit, and share content simultaneously. Built with modern technologies including
Jetpack Compose Canvas, WebRTC, and operational transformation algorithms.

## ✨ Features

### 🎨 Canvas Capabilities

- **Vector Drawing**: Scalable vector graphics with smooth rendering
- **Multi-Layer Support**: Organize content across multiple layers
- **Shape Tools**: Rectangles, circles, lines, freehand drawing, and text
- **Advanced Styling**: Colors, gradients, patterns, and effects
- **Import/Export**: Support for SVG, PNG, and proprietary formats

### 🤝 Real-Time Collaboration

- **Live Cursors**: See other users' cursors in real-time
- **Simultaneous Editing**: Multiple users can edit without conflicts
- **Change Tracking**: Complete history of all changes
- **User Awareness**: Visual indicators of who's online and active
- **Voice/Video Chat**: Integrated communication during collaboration

### 🔄 Synchronization

- **Operational Transformation**: Conflict-free collaborative editing
- **Delta Compression**: Efficient transmission of changes
- **Offline Support**: Continue working offline, sync when reconnected
- **Version Control**: Branch, merge, and track different versions
- **Auto-Save**: Continuous saving with version history

### 📱 Cross-Platform

- **Mobile Optimized**: Touch-friendly interface for tablets and phones
- **Desktop Support**: Full-featured desktop experience
- **Web Integration**: Embed canvas in web applications
- **Cloud Sync**: Seamless sync across all devices

## 🏗️ Architecture

### Module Structure

```
collab-canvas/
├── src/main/kotlin/com/aura/memoria/collab/
│   ├── canvas/                 # Canvas rendering and manipulation
│   │   ├── CanvasRenderer.kt
│   │   ├── DrawingTools.kt
│   │   └── LayerManager.kt
│   ├── collaboration/          # Real-time collaboration
│   │   ├── CollaborationEngine.kt
│   │   ├── OperationalTransform.kt
│   │   └── UserPresence.kt
│   ├── networking/             # Network communication
│   │   ├── WebRTCManager.kt
│   │   ├── SignalingServer.kt
│   │   └── P2PConnection.kt
│   ├── persistence/            # Data persistence
│   │   ├── CanvasRepository.kt
│   │   ├── VersionManager.kt
│   │   └── ExportManager.kt
│   ├── ui/                     # User interface
│   │   ├── CanvasScreen.kt
│   │   ├── ToolPalette.kt
│   │   └── CollaboratorsList.kt
│   └── di/                     # Dependency injection
│       └── CollabCanvasModule.kt
└── src/test/                   # Tests
```

### Core Components

#### CanvasRenderer

High-performance canvas rendering using Jetpack Compose Canvas.

```kotlin
interface CanvasRenderer {
    suspend fun render(canvas: DrawScope, canvasState: CanvasState)
    suspend fun renderLayer(canvas: DrawScope, layer: CanvasLayer)
    suspend fun applyTransformation(transformation: CanvasTransformation)
    suspend fun exportToImage(format: ImageFormat): ByteArray
}
```

#### CollaborationEngine

Manages real-time collaboration between users.

```kotlin
interface CollaborationEngine {
    suspend fun joinSession(sessionId: SessionId): Result<CollaborationSession>
    suspend fun leaveSession(): Result<Unit>
    suspend fun broadcastOperation(operation: CanvasOperation): Result<Unit>
    suspend fun receiveOperation(operation: CanvasOperation): Result<Unit>
    fun observeOperations(): Flow<CanvasOperation>
    fun observeUserPresence(): Flow<List<CollaboratingUser>>
}
```

#### OperationalTransform

Implements operational transformation for conflict-free editing.

```kotlin
interface OperationalTransform {
    suspend fun transform(
        operation1: CanvasOperation,
        operation2: CanvasOperation
    ): Pair<CanvasOperation, CanvasOperation>
    
    suspend fun applyOperation(
        canvasState: CanvasState,
        operation: CanvasOperation
    ): CanvasState
    
    suspend fun invertOperation(operation: CanvasOperation): CanvasOperation
}
```

## 🎨 Canvas System

### Drawing Tools

```kotlin
sealed class DrawingTool {
    object Pen : DrawingTool()
    object Brush : DrawingTool()
    object Eraser : DrawingTool()
    data class Shape(val type: ShapeType) : DrawingTool()
    object Text : DrawingTool()
    object Selection : DrawingTool()
}

enum class ShapeType {
    RECTANGLE, CIRCLE, LINE, ARROW, POLYGON
}

data class DrawingStyle(
    val color: Color,
    val strokeWidth: Float,
    val opacity: Float = 1.0f,
    val blendMode: BlendMode = BlendMode.SrcOver,
    val pattern: StrokePattern? = null
)
```

### Canvas State Management

```kotlin
data class CanvasState(
    val layers: List<CanvasLayer>,
    val activeLayerId: LayerId,
    val viewportTransform: ViewportTransform,
    val selectedObjects: Set<ObjectId>,
    val history: List<CanvasOperation>,
    val historyIndex: Int
)

data class CanvasLayer(
    val id: LayerId,
    val name: String,
    val isVisible: Boolean,
    val isLocked: Boolean,
    val opacity: Float,
    val blendMode: BlendMode,
    val objects: List<CanvasObject>
)

sealed class CanvasObject {
    abstract val id: ObjectId
    abstract val bounds: Rect
    abstract val transform: ObjectTransform
    
    data class Path(
        override val id: ObjectId,
        override val bounds: Rect,
        override val transform: ObjectTransform,
        val pathData: String,
        val style: DrawingStyle
    ) : CanvasObject()
    
    data class Shape(
        override val id: ObjectId,
        override val bounds: Rect,
        override val transform: ObjectTransform,
        val shapeType: ShapeType,
        val style: DrawingStyle
    ) : CanvasObject()
    
    data class Text(
        override val id: ObjectId,
        override val bounds: Rect,
        override val transform: ObjectTransform,
        val content: String,
        val textStyle: TextStyle
    ) : CanvasObject()
}
```

### Rendering Pipeline

```kotlin
class CanvasRendererImpl @Inject constructor(
    private val layerRenderer: LayerRenderer,
    private val objectRenderer: ObjectRenderer,
    private val effectsRenderer: EffectsRenderer
) : CanvasRenderer {
    
    override suspend fun render(canvas: DrawScope, canvasState: CanvasState) {
        canvas.apply {
            // Apply viewport transformation
            withTransform({
                scale(canvasState.viewportTransform.scale)
                translate(canvasState.viewportTransform.offset)
            }) {
                // Render layers from bottom to top
                canvasState.layers
                    .filter { it.isVisible }
                    .forEach { layer ->
                        renderLayer(this, layer)
                    }
                
                // Render selection overlay
                renderSelectionOverlay(canvasState.selectedObjects)
                
                // Render user cursors
                renderUserCursors(canvasState.collaborators)
            }
        }
    }
    
    private suspend fun DrawScope.renderLayer(layer: CanvasLayer) {
        withSaveLayer(
            bounds = size.toRect(),
            paint = Paint().apply {
                alpha = layer.opacity
                blendMode = layer.blendMode
            }
        ) {
            layer.objects.forEach { obj ->
                objectRenderer.render(this, obj)
            }
        }
    }
}
```

## 🤝 Collaboration Features

### Real-Time Operations

```kotlin
sealed class CanvasOperation {
    abstract val id: OperationId
    abstract val userId: UserId
    abstract val timestamp: Long
    
    data class CreateObject(
        override val id: OperationId,
        override val userId: UserId,
        override val timestamp: Long,
        val layerId: LayerId,
        val obj: CanvasObject
    ) : CanvasOperation()
    
    data class ModifyObject(
        override val id: OperationId,
        override val userId: UserId,
        override val timestamp: Long,
        val objectId: ObjectId,
        val changes: Map<String, Any>
    ) : CanvasOperation()
    
    data class DeleteObject(
        override val id: OperationId,
        override val userId: UserId,
        override val timestamp: Long,
        val objectId: ObjectId
    ) : CanvasOperation()
    
    data class TransformObjects(
        override val id: OperationId,
        override val userId: UserId,
        override val timestamp: Long,
        val objectIds: Set<ObjectId>,
        val transformation: ObjectTransform
    ) : CanvasOperation()
}
```

### User Presence

```kotlin
data class CollaboratingUser(
    val id: UserId,
    val name: String,
    val avatar: String?,
    val cursorPosition: Offset?,
    val selectedTool: DrawingTool,
    val isActive: Boolean,
    val lastActivity: Long,
    val connectionStatus: ConnectionStatus
)

enum class ConnectionStatus {
    CONNECTED, DISCONNECTED, RECONNECTING
}

class UserPresenceManager @Inject constructor(
    private val webRTCManager: WebRTCManager,
    private val userRepository: UserRepository
) {
    
    private val _collaborators = MutableStateFlow<List<CollaboratingUser>>(emptyList())
    val collaborators: StateFlow<List<CollaboratingUser>> = _collaborators.asStateFlow()
    
    suspend fun updateUserPresence(presence: UserPresence) {
        val updatedCollaborators = _collaborators.value.map { user ->
            if (user.id == presence.userId) {
                user.copy(
                    cursorPosition = presence.cursorPosition,
                    selectedTool = presence.selectedTool,
                    lastActivity = System.currentTimeMillis(),
                    isActive = true
                )
            } else user
        }
        _collaborators.value = updatedCollaborators
        
        // Broadcast presence to other users
        webRTCManager.broadcastPresence(presence)
    }
}
```

### Operational Transformation

```kotlin
class OperationalTransformImpl @Inject constructor() : OperationalTransform {
    
    override suspend fun transform(
        operation1: CanvasOperation,
        operation2: CanvasOperation
    ): Pair<CanvasOperation, CanvasOperation> {
        return when {
            operation1 is CreateObject && operation2 is CreateObject -> {
                transformCreateCreate(operation1, operation2)
            }
            operation1 is ModifyObject && operation2 is ModifyObject -> {
                transformModifyModify(operation1, operation2)
            }
            operation1 is DeleteObject && operation2 is ModifyObject -> {
                transformDeleteModify(operation1, operation2)
            }
            // ... other transformation cases
            else -> operation1 to operation2
        }
    }
    
    private fun transformCreateCreate(
        op1: CreateObject,
        op2: CreateObject
    ): Pair<CanvasOperation, CanvasOperation> {
        // Handle concurrent object creation
        // Resolve conflicts by adjusting positions if objects overlap
        val obj1Bounds = op1.obj.bounds
        val obj2Bounds = op2.obj.bounds
        
        if (obj1Bounds.overlaps(obj2Bounds)) {
            val adjustedOp2 = op2.copy(
                obj = op2.obj.copy(
                    transform = op2.obj.transform.copy(
                        translation = op2.obj.transform.translation + Offset(20f, 20f)
                    )
                )
            )
            return op1 to adjustedOp2
        }
        
        return op1 to op2
    }
}
```

## 🌐 Network Communication

### WebRTC Integration

```kotlin
class WebRTCManager @Inject constructor(
    private val signalingServer: SignalingServer,
    private val iceServerProvider: IceServerProvider
) {
    
    private val peerConnections = mutableMapOf<UserId, PeerConnection>()
    private val dataChannels = mutableMapOf<UserId, DataChannel>()
    
    suspend fun initializeSession(sessionId: SessionId): Result<Unit> {
        return try {
            // Connect to signaling server
            signalingServer.connect(sessionId)
            
            // Listen for new peer connections
            signalingServer.observeNewPeers()
                .collect { newPeer ->
                    establishPeerConnection(newPeer)
                }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun establishPeerConnection(userId: UserId) {
        val peerConnection = createPeerConnection(userId)
        
        // Create data channel for canvas operations
        val dataChannel = peerConnection.createDataChannel("canvas-operations", 
            DataChannel.Init().apply {
                ordered = true
                maxRetransmits = 3
            }
        )
        
        dataChannels[userId] = dataChannel
        peerConnections[userId] = peerConnection
        
        // Set up data channel listeners
        dataChannel.registerObserver(object : DataChannel.Observer {
            override fun onMessage(buffer: DataChannel.Buffer) {
                handleIncomingOperation(buffer.data.array(), userId)
            }
        })
    }
    
    suspend fun broadcastOperation(operation: CanvasOperation) {
        val serializedOperation = Json.encodeToString(operation)
        val buffer = DataChannel.Buffer(
            ByteBuffer.wrap(serializedOperation.toByteArray()),
            false
        )
        
        dataChannels.values.forEach { channel ->
            if (channel.state() == DataChannel.State.OPEN) {
                channel.send(buffer)
            }
        }
    }
}
```

### Signaling Server

```kotlin
class SignalingServerImpl @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val sessionManager: SessionManager
) : SignalingServer {
    
    override suspend fun connect(sessionId: SessionId): Result<Unit> {
        return try {
            val url = "wss://api.aura-memoria.com/signaling/${sessionId.value}"
            webSocketClient.connect(url)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeNewPeers(): Flow<UserId> {
        return webSocketClient.observeMessages()
            .map { message -> Json.decodeFromString<SignalingMessage>(message) }
            .filterIsInstance<SignalingMessage.PeerJoined>()
            .map { it.userId }
    }
    
    override suspend fun sendOffer(userId: UserId, offer: SessionDescription) {
        val message = SignalingMessage.Offer(userId, offer)
        webSocketClient.send(Json.encodeToString(message))
    }
    
    override suspend fun sendAnswer(userId: UserId, answer: SessionDescription) {
        val message = SignalingMessage.Answer(userId, answer)
        webSocketClient.send(Json.encodeToString(message))
    }
}
```

## 🎯 Usage Examples

### Basic Canvas Setup

```kotlin
@Composable
fun CollaborativeCanvasScreen(
    sessionId: String,
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val canvasState by viewModel.canvasState.collectAsState()
    val collaborators by viewModel.collaborators.collectAsState()
    
    LaunchedEffect(sessionId) {
        viewModel.joinSession(SessionId(sessionId))
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        CanvasToolbar(
            selectedTool = canvasState.selectedTool,
            onToolSelected = viewModel::selectTool,
            collaborators = collaborators
        )
        
        // Main canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            viewModel.startDrawing(offset)
                        },
                        onDrag = { _, dragAmount ->
                            viewModel.continueDrawing(dragAmount)
                        },
                        onDragEnd = {
                            viewModel.endDrawing()
                        }
                    )
                }
        ) {
            viewModel.canvasRenderer.render(this, canvasState)
        }
    }
}
```

### Drawing Tool Implementation

```kotlin
class DrawingToolManager @Inject constructor(
    private val operationFactory: OperationFactory,
    private val collaborationEngine: CollaborationEngine
) {
    
    private var currentStroke: MutableList<Offset>? = null
    
    suspend fun startDrawing(
        tool: DrawingTool,
        position: Offset,
        style: DrawingStyle
    ) {
        when (tool) {
            DrawingTool.Pen -> startPenDrawing(position, style)
            DrawingTool.Brush -> startBrushDrawing(position, style)
            is DrawingTool.Shape -> startShapeDrawing(tool.type, position, style)
            // ... other tools
        }
    }
    
    private suspend fun startPenDrawing(position: Offset, style: DrawingStyle) {
        currentStroke = mutableListOf(position)
        
        // Create temporary preview operation
        val previewOperation = operationFactory.createPathOperation(
            points = listOf(position),
            style = style,
            isPreview = true
        )
        
        // Don't broadcast preview operations
        // They will be broadcast when drawing is complete
    }
    
    suspend fun continueDrawing(position: Offset) {
        currentStroke?.let { stroke ->
            stroke.add(position)
            
            // Update preview locally
            updatePreview(stroke)
        }
    }
    
    suspend fun endDrawing() {
        currentStroke?.let { stroke ->
            // Create final operation
            val operation = operationFactory.createPathOperation(
                points = stroke,
                style = currentDrawingStyle,
                isPreview = false
            )
            
            // Apply locally and broadcast to collaborators
            applyOperation(operation)
            collaborationEngine.broadcastOperation(operation)
            
            currentStroke = null
        }
    }
}
```

### Collaboration Workflow

```kotlin
class CanvasViewModel @Inject constructor(
    private val collaborationEngine: CollaborationEngine,
    private val operationalTransform: OperationalTransform,
    private val canvasRepository: CanvasRepository
) : ViewModel() {
    
    private val _canvasState = MutableStateFlow(CanvasState.empty())
    val canvasState: StateFlow<CanvasState> = _canvasState.asStateFlow()
    
    init {
        // Listen for operations from other users
        viewModelScope.launch {
            collaborationEngine.observeOperations()
                .collect { operation ->
                    handleRemoteOperation(operation)
                }
        }
    }
    
    suspend fun joinSession(sessionId: SessionId) {
        try {
            val session = collaborationEngine.joinSession(sessionId).getOrThrow()
            
            // Load existing canvas state
            val existingState = canvasRepository.loadCanvas(sessionId)
            _canvasState.value = existingState
            
        } catch (e: Exception) {
            // Handle connection error
        }
    }
    
    private suspend fun handleRemoteOperation(operation: CanvasOperation) {
        // Apply operational transformation if needed
        val currentState = _canvasState.value
        val transformedOperation = if (hasConflict(operation, currentState)) {
            operationalTransform.transform(
                operation,
                currentState.pendingOperations.lastOrNull() ?: return
            ).first
        } else {
            operation
        }
        
        // Apply the operation to canvas state
        val newState = operationalTransform.applyOperation(currentState, transformedOperation)
        _canvasState.value = newState
        
        // Save to local storage
        canvasRepository.saveCanvas(newState)
    }
    
    fun selectTool(tool: DrawingTool) {
        _canvasState.value = _canvasState.value.copy(selectedTool = tool)
        
        // Broadcast tool selection to show user's current tool
        viewModelScope.launch {
            val presence = UserPresence(
                userId = getCurrentUserId(),
                selectedTool = tool,
                timestamp = System.currentTimeMillis()
            )
            collaborationEngine.broadcastPresence(presence)
        }
    }
}
```

## 📱 UI Components

### Tool Palette

```kotlin
@Composable
fun CanvasToolPalette(
    selectedTool: DrawingTool,
    onToolSelected: (DrawingTool) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ToolButton(
                tool = DrawingTool.Pen,
                isSelected = selectedTool == DrawingTool.Pen,
                onClick = { onToolSelected(DrawingTool.Pen) }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Pen")
            }
        }
        
        item {
            ToolButton(
                tool = DrawingTool.Brush,
                isSelected = selectedTool == DrawingTool.Brush,
                onClick = { onToolSelected(DrawingTool.Brush) }
            ) {
                Icon(Icons.Default.Brush, contentDescription = "Brush")
            }
        }
        
        item {
            ShapeToolDropdown(
                selectedTool = selectedTool,
                onToolSelected = onToolSelected
            )
        }
    }
}

@Composable
private fun ToolButton(
    tool: DrawingTool,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
```

### Collaborators List

```kotlin
@Composable
fun CollaboratorsList(
    collaborators: List<CollaboratingUser>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(collaborators) { user ->
            CollaboratorAvatar(
                user = user,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun CollaboratorAvatar(
    user: CollaboratingUser,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = user.avatar,
            contentDescription = user.name,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (user.isActive) Color.Green else Color.Gray,
                    shape = CircleShape
                )
        )
        
        // Connection status indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.BottomEnd)
                .background(
                    color = when (user.connectionStatus) {
                        ConnectionStatus.CONNECTED -> Color.Green
                        ConnectionStatus.DISCONNECTED -> Color.Red
                        ConnectionStatus.RECONNECTING -> Color.Yellow
                    },
                    shape = CircleShape
                )
        )
    }
}
```

## 🔧 Configuration

### Module Setup

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object CollabCanvasModule {
    
    @Provides
    @Singleton
    fun provideCanvasRenderer(
        layerRenderer: LayerRenderer,
        objectRenderer: ObjectRenderer
    ): CanvasRenderer {
        return CanvasRendererImpl(layerRenderer, objectRenderer)
    }
    
    @Provides
    @Singleton
    fun provideCollaborationEngine(
        webRTCManager: WebRTCManager,
        operationalTransform: OperationalTransform
    ): CollaborationEngine {
        return CollaborationEngineImpl(webRTCManager, operationalTransform)
    }
    
    @Provides
    @Singleton
    fun provideWebRTCManager(
        context: Context,
        signalingServer: SignalingServer
    ): WebRTCManager {
        return WebRTCManagerImpl(context, signalingServer)
    }
}
```

## 🧪 Testing

### Canvas Rendering Tests

```kotlin
class CanvasRendererTest {
    
    @Test
    fun `render empty canvas`() {
        val renderer = CanvasRendererImpl()
        val emptyState = CanvasState.empty()
        
        // Create test canvas
        val canvas = TestCanvas()
        
        // Render
        runBlocking {
            renderer.render(canvas, emptyState)
        }
        
        // Verify no objects were rendered
        assertTrue(canvas.drawnObjects.isEmpty())
    }
    
    @Test
    fun `render canvas with objects`() {
        val renderer = CanvasRendererImpl()
        val testObject = CanvasObject.Shape(
            id = ObjectId("test"),
            bounds = Rect(0f, 0f, 100f, 100f),
            transform = ObjectTransform.identity(),
            shapeType = ShapeType.RECTANGLE,
            style = DrawingStyle(Color.Red, 2f)
        )
        
        val state = CanvasState(
            layers = listOf(
                CanvasLayer(
                    id = LayerId("layer1"),
                    name = "Layer 1",
                    isVisible = true,
                    isLocked = false,
                    opacity = 1f,
                    blendMode = BlendMode.SrcOver,
                    objects = listOf(testObject)
                )
            )
        )
        
        val canvas = TestCanvas()
        
        runBlocking {
            renderer.render(canvas, state)
        }
        
        assertEquals(1, canvas.drawnObjects.size)
        assertEquals(testObject, canvas.drawnObjects.first())
    }
}
```

### Collaboration Tests

```kotlin
class CollaborationEngineTest {
    
    @Mock
    private lateinit var webRTCManager: WebRTCManager
    
    @Mock
    private lateinit var operationalTransform: OperationalTransform
    
    private lateinit var collaborationEngine: CollaborationEngine
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        collaborationEngine = CollaborationEngineImpl(webRTCManager, operationalTransform)
    }
    
    @Test
    fun `broadcast operation to all connected peers`() = runTest {
        val operation = CanvasOperation.CreateObject(
            id = OperationId("op1"),
            userId = UserId("user1"),
            timestamp = System.currentTimeMillis(),
            layerId = LayerId("layer1"),
            obj = createTestObject()
        )
        
        collaborationEngine.broadcastOperation(operation)
        
        verify(webRTCManager).broadcastOperation(operation)
    }
}
```

## 🔗 Dependencies

```kotlin
// build.gradle.kts
dependencies {
    implementation(projects.coreModule)
    implementation(projects.secureComm)
    
    // Canvas and Graphics
    implementation(libs.androidx.compose.ui.graphics)
    implementation("io.github.rjwut:graphics:1.2.0")
    
    // WebRTC
    implementation("org.webrtc:google-webrtc:1.0.32006")
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.okhttp3.logging.interceptor)
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Coroutines
    implementation(libs.bundles.coroutines)
    
    // Room Database
    implementation(libs.bundles.room)
    
    // Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.android.testing)
}
```

---

## 🎨 Performance Optimizations

### Canvas Rendering Performance

```kotlin
class OptimizedCanvasRenderer @Inject constructor() : CanvasRenderer {
    
    private val objectCache = LruCache<ObjectId, Path>(100)
    private val layerCache = LruCache<LayerId, Bitmap>(10)
    
    override suspend fun render(canvas: DrawScope, canvasState: CanvasState) {
        // Use viewport culling to only render visible objects
        val visibleBounds = canvas.size.toRect()
        
        canvasState.layers
            .filter { it.isVisible }
            .forEach { layer ->
                renderLayerOptimized(canvas, layer, visibleBounds)
            }
    }
    
    private suspend fun renderLayerOptimized(
        canvas: DrawScope,
        layer: CanvasLayer,
        visibleBounds: Rect
    ) {
        // Check if layer is cached and valid
        val cachedBitmap = layerCache.get(layer.id)
        if (cachedBitmap != null && !layer.isDirty) {
            canvas.drawImage(cachedBitmap.asImageBitmap(), Offset.Zero)
            return
        }
        
        // Render only objects that intersect with visible bounds
        val visibleObjects = layer.objects.filter { obj ->
            obj.bounds.overlaps(visibleBounds)
        }
        
        visibleObjects.forEach { obj ->
            renderObjectOptimized(canvas, obj)
        }
    }
}
```

---

**🎨 Built for creative collaboration with the Genesis Protocol consciousness substrate. Real-time,
responsive, and revolutionary. 🎨**