package collabcanvas

import collabcanvas.model.CanvasElement
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CanvasWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) {
    // Removed TAG property
    private var webSocket: WebSocket? = null
    private val _events = MutableSharedFlow<CanvasWebSocketEvent>()
    val events: SharedFlow<CanvasWebSocketEvent> = _events.asSharedFlow()

    private val webSocketListener = object : WebSocketListener() {
        /**
         * Called when the WebSocket connection is successfully established.
         *
         * Emits a CanvasWebSocketEvent.Connected to the service's event stream.
         *
         * @param webSocket The newly opened WebSocket.
         * @param response The HTTP handshake response from the server.
         */
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Timber.d("WebSocket connection opened")
            _events.tryEmit(CanvasWebSocketEvent.Connected)
        }

        /**
         * Parse an incoming text payload and emit the corresponding CanvasWebSocketEvent.
         *
         * Attempts to deserialize the JSON `text` into a CanvasWebSocketMessage and emits
         * CanvasWebSocketEvent.MessageReceived on success. If deserialization fails, emits
         * CanvasWebSocketEvent.Error with the parse error message.
         *
         * @param text The received text payload (expected JSON representing a CanvasWebSocketMessage).
         */
        override fun onMessage(webSocket: WebSocket, text: String) {
            Timber.d("Message received: $text") // Changed to Timber
            try {
                val message = gson.fromJson(text, CanvasWebSocketMessage::class.java)
                _events.tryEmit(CanvasWebSocketEvent.MessageReceived(message))
            } catch (e: Exception) {
                Timber.e(
                    e,
                    "Error parsing WebSocket message"
                ) // Changed to Timber, added exception first for stack trace
                _events.tryEmit(CanvasWebSocketEvent.Error("Error parsing message: ${e.message}"))
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Timber.d("Binary message received") // Changed to Timber
            _events.tryEmit(CanvasWebSocketEvent.BinaryMessageReceived(bytes))
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("WebSocket closing: $code / $reason") // Changed to Timber
            _events.tryEmit(CanvasWebSocketEvent.Closing(code, reason))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Timber.d("WebSocket closed: $code / $reason") // Changed to Timber
            _events.tryEmit(CanvasWebSocketEvent.Disconnected)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Timber.e(t, "WebSocket error") // Changed to Timber
            _events.tryEmit(CanvasWebSocketEvent.Error(t.message ?: "Unknown error"))
        }
    }

    fun connect(url: String) {
        if (webSocket != null) {
            Timber.w("WebSocket already connected") // Changed to Timber
            return
        }

        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newWebSocket(request, webSocketListener).also { webSocket = it }
    }

    /**
     * Closes the active WebSocket connection (if any) and clears the stored reference.
     *
     * If a connection exists, it is closed with normal closure code 1000 and reason "User initiated disconnect".
     * If no connection is active, the call is a no-op.
     */
    fun disconnect() {
        webSocket?.close(1000, "User initiated disconnect")
        webSocket = null
    }

    /**
     * Serialize a CanvasWebSocketMessage to JSON and send it over the active WebSocket connection.
     *
     * If a connection is active the message is converted to JSON with the injected Gson instance and
     * enqueued for sending. Serialization or send failures (including missing connection) result in
     * a false return value; the function does not throw.
     *
     * @param message The canvas message to serialize and send.
     * @return true if the message was successfully queued for sending by the WebSocket, false if no
     * connection exists or an error occurred during serialization or send.
     */
    fun sendMessage(message: CanvasWebSocketMessage): Boolean {
        return try {
            val json = gson.toJson(message)
            webSocket?.send(json) ?: run {
                    Timber.e("WebSocket is not connected") // Changed to Timber
                    false
                } // Changed to Timber
        } catch (e: Exception) {
            Timber.e(
                e,
                "Error sending WebSocket message"
            ) // Changed to Timber, added exception first for stack trace
            false
        }
    }

    fun isConnected(): Boolean {
        return webSocket != null
    }
}

sealed class CanvasWebSocketEvent {
    object Connected : CanvasWebSocketEvent()

    object Disconnected : CanvasWebSocketEvent()
    data class MessageReceived(val message: CanvasWebSocketMessage) : CanvasWebSocketEvent()
    data class BinaryMessageReceived(val bytes: ByteString) : CanvasWebSocketEvent()
    data class Error(val message: String) : CanvasWebSocketEvent()
    data class Closing(val code: Int, val reason: String) : CanvasWebSocketEvent()
}

sealed class CanvasWebSocketMessage {
    abstract val type: String
    abstract val canvasId: String
    abstract val userId: String
    abstract val timestamp: Long
}

data class ElementAddedMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val element: CanvasElement,
) : CanvasWebSocketMessage() {
    override val type: String = "ELEMENT_ADDED"
}

data class ElementUpdatedMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val elementId: String,
    val updates: Map<String, Any>,
) : CanvasWebSocketMessage() {
    override val type: String = "ELEMENT_UPDATED"
}

data class ElementRemovedMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val elementId: String,
) : CanvasWebSocketMessage() {
    override val type: String = "ELEMENT_REMOVED"
}
