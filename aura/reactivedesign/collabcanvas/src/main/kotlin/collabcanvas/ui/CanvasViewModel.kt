package collabcanvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import collabcanvas.CanvasWebSocketEvent
import collabcanvas.CanvasWebSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Collaborative Canvas
 *
 * Manages WebSocket connection for real-time collaborative drawing
 */
@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val webSocketService: CanvasWebSocketService
) : ViewModel() {

    private val _connectionStatus = MutableSharedFlow<String>()
    fun getConnectionStatus() = _connectionStatus.asSharedFlow()

    val webSocketEvents = webSocketService.events

    private var isConnected = false

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
                // TODO: Get WebSocket URL from configuration
                // For now, using localhost with default port
                val wsUrl = "ws://localhost:8080/canvas/$canvasId"

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
