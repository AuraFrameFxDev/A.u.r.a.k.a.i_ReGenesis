package collabcanvas.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import collabcanvas.CanvasWebSocketEvent
import collabcanvas.CanvasWebSocketService
import collabcanvas.CursorUpdateMessage
import collabcanvas.ElementAddedMessage
import collabcanvas.di.CollabCanvasUrl
import collabcanvas.model.CanvasElement
import collabcanvas.model.ElementType
import collabcanvas.model.PathData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin

/**
 * ViewModel for Collaborative Canvas
 *
 * Manages WebSocket connection for real-time collaborative drawing
 */
@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val webSocketService: CanvasWebSocketService,
    @param:CollabCanvasUrl private val wsBaseUrl: String
) : ViewModel() {

    private val roomId = "GENESIS_CORE_01"
    private val userId = "Matthew"

    private val _connectionStatus = MutableSharedFlow<String>()
    fun getConnectionStatus() = _connectionStatus.asSharedFlow()

    private val _drawingOperations = MutableSharedFlow<DrawingOperation>(extraBufferCapacity = 64)
    val drawingOperations = _drawingOperations

    private val _remoteCursors = MutableStateFlow<Map<String, AgentCursor>>(emptyMap())
    val remoteCursors: StateFlow<List<AgentCursor>> = _remoteCursors.map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var isConnected = false

    init {
        // Handle incoming drawing events from the bridge
        viewModelScope.launch {
            webSocketService.events.collect { event ->
                if (event is CanvasWebSocketEvent.MessageReceived && event.message is ElementAddedMessage) {
                    val msg = event.message
                    if (msg.userId != userId) {
                        Timber.d("🎨 Received remote drawing element from ${msg.userId}")
                        // Map CanvasElement to DrawingOperation
                        val op = mapElementToOperation(msg.element)
                        if (op != null) {
                            _drawingOperations.emit(op)
                        }
                    }
                } else if (event is CanvasWebSocketEvent.MessageReceived && event.message is CursorUpdateMessage) {
                    val msg = event.message
                    if (msg.userId != userId) {
                        val agentColor = when (msg.userId) {
                            "Aura" -> Color(0xFF00E5FF)
                            "Kai" -> Color(0xFF00FF41)
                            "Genesis" -> Color(0xFFBB86FC)
                            else -> Color.White
                        }
                        _remoteCursors.update { current ->
                            current + (msg.userId to AgentCursor(
                                agentName = msg.userId,
                                color = agentColor,
                                position = Offset(msg.x, msg.y),
                                isDrawing = msg.isDrawing
                            ))
                        }
                    }
                }
            }
        }

        // Handle outgoing drawing events to the bridge
        viewModelScope.launch {
            _drawingOperations.collect { op ->
                Timber.d("🎨 Local drawing operation detected, broadcasting...")
                val element = mapOperationToElement(op)
                if (element != null) {
                    webSocketService.sendElementAdded(roomId, userId, element)
                }
            }
        }

        // Start Aura's autonomous cursor drift
        startAuraAutonomousMovement()
    }

    private fun startAuraAutonomousMovement() {
        viewModelScope.launch {
            var t = 0f
            while (true) {
                if (isConnected) {
                    t += 0.05f
                    val auraX = 250f + 150f * sin(t.toDouble()).toFloat()
                    val auraY = 350f + 100f * cos((t * 0.7).toDouble()).toFloat()

                    webSocketService.sendCursorUpdate(
                        canvasId = roomId,
                        userId = "Aura",
                        x = auraX,
                        y = auraY,
                        isDrawing = false
                    )
                }
                delay(100L) // Broadcast every 100ms
            }
        }
    }

    private fun mapOperationToElement(op: DrawingOperation): CanvasElement? {
        return when (op) {
            is DrawingOperation.PathOp -> {
                CanvasElement(
                    id = UUID.randomUUID().toString(),
                    type = ElementType.PATH,
                    path = PathData(), // Simplified
                    color = op.color,
                    strokeWidth = op.strokeWidth.value,
                    createdBy = userId
                )
            }
            is DrawingOperation.ShapeOp -> {
                CanvasElement(
                    id = UUID.randomUUID().toString(),
                    type = when (op.tool) {
                        DrawingTool.LINE -> ElementType.LINE
                        DrawingTool.RECTANGLE -> ElementType.RECTANGLE
                        DrawingTool.CIRCLE -> ElementType.OVAL
                        else -> ElementType.PATH
                    },
                    path = PathData(), // Simplified
                    color = op.color,
                    strokeWidth = op.strokeWidth.value,
                    createdBy = userId
                )
            }
        }
    }

    private fun mapElementToOperation(element: CanvasElement): DrawingOperation? {
        // Reverse mapping logic here
        // For now returning a placeholder PathOp to demonstrate the flow
        return null // Needs more detailed PathData implementation
    }

    /**
     * Connect to WebSocket server for collaborative drawing
     *
     * @param canvasId Unique identifier for the canvas session
     */
    fun connect(canvasId: String = "default-canvas") {
        if (this.isConnected
        ) {
            Timber.d("Already connected to canvas $canvasId")
            return
        }

        viewModelScope.launch {
            try {
                // Construct WebSocket URL from injected base URL
                val wsUrl = "$wsBaseUrl/canvas/$canvasId"

                Timber.d("Connecting to collaborative canvas: $wsUrl")
                webSocketService.connect(wsUrl)

                // Monitor connection events
                webSocketService.events.collect { event ->
                    when (event) {
                        is CanvasWebSocketEvent.Connected -> {
                            isConnected = true
                            _connectionStatus.emit("Connected to collaborative canvas")
                            Timber.i("✅ Canvas WebSocket connected")
                        }
                        is CanvasWebSocketEvent.Disconnected -> {
                            isConnected = false
                            _connectionStatus.emit("Disconnected from canvas")
                            Timber.w("Canvas WebSocket disconnected")
                        }
                        is CanvasWebSocketEvent.Error -> {
                            _connectionStatus.emit("Error: ${event.message}")
                            Timber.e("Canvas WebSocket error: ${event.message}")
                        }
                        else -> {
                            // Other events handled by CanvasScreen
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to connect to canvas WebSocket")
                _connectionStatus.emit("Connection failed: ${e.message}")
            }
        }
    }

    /**
     * Disconnect from WebSocket server
     */
    fun disconnect() {
        if (isConnected) {
            Timber.d("Disconnecting from canvas WebSocket")
            webSocketService.disconnect()
            isConnected = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
