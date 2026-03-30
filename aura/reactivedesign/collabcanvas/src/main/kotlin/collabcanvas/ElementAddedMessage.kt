package collabcanvas

import collabcanvas.model.CanvasElement
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import dev.aurakai.auraframefx.domains.genesis.models.ChatMessage
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
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class CanvasWebSocketService @Inject constructor(
    @Named("BasicOkHttpClient") private val okHttpClient: OkHttpClient,
    gson: Gson,
) {
    private val gson = gson.newBuilder()
        .registerTypeHierarchyAdapter(CanvasWebSocketMessage::class.java, CanvasWebSocketMessageDeserializer())
        .create()

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

    /**
     * Broadcasts a conference update to the web bridge.
     */
    fun sendConferenceUpdate(roomId: String, userId: String, participants: List<ParticipantStatus>, message: ChatMessage): Boolean {
        return sendMessage(
            ConferenceUpdateMessage(
                canvasId = roomId,
                userId = userId,
                activeParticipants = participants,
                message = message
            )
        )
    }

    /**
     * Broadcasts a new drawing element to the web bridge.
     */
    fun sendElementAdded(canvasId: String, userId: String, element: CanvasElement): Boolean {
        return sendMessage(
            ElementAddedMessage(
                canvasId = canvasId,
                userId = userId,
                element = element
            )
        )
    }

    /**
     * Broadcasts an updated drawing element to the web bridge.
     */
    fun sendElementUpdated(canvasId: String, userId: String, elementId: String, updates: Map<String, Any>): Boolean {
        return sendMessage(
            ElementUpdatedMessage(
                canvasId = canvasId,
                userId = userId,
                elementId = elementId,
                updates = updates
            )
        )
    }

    /**
     * Broadcasts a user command to the web bridge.
     */
    fun sendUserCommand(canvasId: String, userId: String, command: String, metadata: Map<String, String> = emptyMap()): Boolean {
        return sendMessage(
            UserCommandMessage(
                canvasId = canvasId,
                userId = userId,
                command = command,
                metadata = metadata
            )
        )
    }

    /**
     * Broadcasts a cursor update to the web bridge.
     */
    fun sendCursorUpdate(canvasId: String, userId: String, x: Float, y: Float, isDrawing: Boolean): Boolean {
        return sendMessage(
            CursorUpdateMessage(
                canvasId = canvasId,
                userId = userId,
                x = x,
                y = y,
                isDrawing = isDrawing
            )
        )
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

data class ParticipantStatus(
    val id: String,
    val status: String,
    val color: String
)

data class ConferenceUpdateMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val activeParticipants: List<ParticipantStatus>,
    val message: ChatMessage
) : CanvasWebSocketMessage() {
    override val type: String = "CONFERENCE_UPDATE"
}

data class UserCommandMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val command: String,
    val metadata: Map<String, String> = emptyMap()
) : CanvasWebSocketMessage() {
    override val type: String = "USER_COMMAND"
}

data class CursorUpdateMessage(
    override val canvasId: String,
    override val userId: String,
    override val timestamp: Long = System.currentTimeMillis(),
    val x: Float,
    val y: Float,
    val isDrawing: Boolean = false
) : CanvasWebSocketMessage() {
    override val type: String = "CURSOR_UPDATE"
}

class CanvasWebSocketMessageDeserializer : JsonDeserializer<CanvasWebSocketMessage> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): CanvasWebSocketMessage {
        val jsonObject = json.asJsonObject
        val type = jsonObject.get("type")?.asString ?: ""
        return when (type) {
            "ELEMENT_ADDED" -> context.deserialize(json, ElementAddedMessage::class.java)
            "ELEMENT_UPDATED" -> context.deserialize(json, ElementUpdatedMessage::class.java)
            "ELEMENT_REMOVED" -> context.deserialize(json, ElementRemovedMessage::class.java)
            "CONFERENCE_UPDATE" -> context.deserialize(json, ConferenceUpdateMessage::class.java)
            "USER_COMMAND" -> context.deserialize(json, UserCommandMessage::class.java)
            "CURSOR_UPDATE" -> context.deserialize(json, CursorUpdateMessage::class.java)
            else -> throw IllegalArgumentException("Unknown message type: $type")
        }
    }
}
