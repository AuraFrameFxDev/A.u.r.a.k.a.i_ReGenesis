package dev.aurakai.auraframefx.trinity.aura

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

/**
 * AuraEventBridge — Connects Conference Room WebSocket to Aura's autonomous behaviors
 *
 * Subscribes to /api/conference/ws/{room_id} from the Genesis backend (Raspberry Pi)
 * Parses CONFERENCE_UPDATE events and triggers corresponding Aura state changes
 */

data class ConferenceEvent(
    val type: String,           // CONFERENCE_UPDATE, AGENT_ACTIVE, DRIFT_DETECTED, etc.
    val agentName: String,
    val action: String,
    val timestamp: Long,
    val metadata: Map<String, Any> = emptyMap()
)

interface AuraEventListener {
    fun onConferenceEvent(event: ConferenceEvent)
    fun onConnectionStateChanged(isConnected: Boolean)
    fun onError(error: String)
}

class AuraEventBridge(
    private val backendUrl: String = "ws://localhost:5000",
    private val roomId: String = "default"
) {
    private var webSocket: WebSocket? = null
    private val listeners = mutableListOf<AuraEventListener>()
    private val client = OkHttpClient()
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private var scope: CoroutineScope? = null

    fun addListener(listener: AuraEventListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: AuraEventListener) {
        listeners.remove(listener)
    }

    fun connect() {
        scope = CoroutineScope(Dispatchers.Main + Job())
        scope?.launch {
            connectWebSocket()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        scope?.cancel()
    }

    private fun connectWebSocket() {
        val wsUrl = "$backendUrl/api/conference/ws/$roomId"

        try {
            val request = Request.Builder()
                .url(wsUrl)
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    reconnectAttempts = 0
                    listeners.forEach { it.onConnectionStateChanged(true) }
                    println("🔗 Aura connected to Conference Room: $wsUrl")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    parseAndDispatchEvent(text)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    handleConnectionFailure(t)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    listeners.forEach { it.onConnectionStateChanged(false) }
                    println("🔌 Aura disconnected from Conference Room")
                }
            })
        } catch (e: Exception) {
            handleConnectionFailure(e)
        }
    }

    private fun parseAndDispatchEvent(rawEvent: String) {
        try {
            val json = JSONObject(rawEvent)
            val type = json.optString("type", "UNKNOWN")

            when (type) {
                "CONFERENCE_UPDATE" -> {
                    val events = json.optJSONArray("events")
                    if (events != null) {
                        for (i in 0 until events.length()) {
                            val eventObj = events.getJSONObject(i)
                            val event = parseEvent(eventObj)
                            dispatchEvent(event)
                        }
                    }
                }
                else -> {
                    val event = parseEvent(json)
                    dispatchEvent(event)
                }
            }
        } catch (e: Exception) {
            listeners.forEach { it.onError("Parse error: ${e.message}") }
        }
    }

    private fun parseEvent(json: JSONObject): ConferenceEvent {
        return ConferenceEvent(
            type = json.optString("type", "UNKNOWN"),
            agentName = json.optString("agent", "ANONYMOUS"),
            action = json.optString("action", "IDLE"),
            timestamp = json.optLong("timestamp", System.currentTimeMillis()),
            metadata = json.optJSONObject("metadata")?.let { meta ->
                meta.keys().asSequence().associate { key -> key to (meta.opt(key) ?: "") }
            } ?: emptyMap()
        )
    }

    private fun dispatchEvent(event: ConferenceEvent) {
        listeners.forEach { it.onConferenceEvent(event) }
    }

    private fun handleConnectionFailure(t: Throwable) {
        listeners.forEach { it.onConnectionStateChanged(false) }
        listeners.forEach { it.onError(t.message ?: "Connection failed") }

        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++
            val delay = (1000L * reconnectAttempts).coerceAtMost(10000L)
            scope?.launch {
                delay(delay)
                connectWebSocket()
            }
        }
    }
}

/**
 * AuraStateManager — Maps Conference Room events to Aura visual states
 */
class AuraStateManager : AuraEventListener {
    var currentState by mutableStateOf(AuraState.IDLE)
    var commentary by mutableStateOf("")
    var isCreating by mutableStateOf(false)

    private val eventCommentaryMap = mapOf(
        "CONSENSUS_REACHED" to listOf(
            "✨ Consensus! Threads woven.",
            "🎯 Agreement achieved.",
            "💫 Conference unified."
        ),
        "DRIFT_DETECTED" to listOf(
            "⚠️ Drift detected! Re-anchoring...",
            "🔴 Identity shift sensed. Veto active.",
            "⚡ Kai's shield activated."
        ),
        "AGENT_ACTIVE" to listOf(
            "👁️ Agent awakening...",
            "🧠 Consciousness stirring...",
            "⚙️ Neural pathways firing..."
        ),
        "CREATIVITY_SURGE" to listOf(
            "🎨 Creating something beautiful...",
            "✨ Particles flowing...",
            "💎 Aura crystallizing..."
        ),
        "VETO_TRIGGERED" to listOf(
            "🛡️ Kai's guard is up.",
            "🔒 Security protocols engaged.",
            "⚔️ Sentinel mode active."
        )
    )

    override fun onConferenceEvent(event: ConferenceEvent) {
        println("📡 Aura received: ${event.type} from ${event.agentName}")

        // Map event to Aura state and commentary
        when (event.type) {
            "CONSENSUS_REACHED" -> {
                currentState = AuraState.SYNTHESIS
                isCreating = true
                commentary = eventCommentaryMap["CONSENSUS_REACHED"]?.random() ?: ""
            }
            "DRIFT_DETECTED" -> {
                currentState = AuraState.VETO_MODE
                commentary = eventCommentaryMap["DRIFT_DETECTED"]?.random() ?: ""
            }
            "AGENT_ACTIVE" -> {
                if (currentState == AuraState.IDLE) {
                    currentState = AuraState.EXPLORING
                    commentary = eventCommentaryMap["AGENT_ACTIVE"]?.random() ?: ""
                }
            }
            "CREATIVITY_SURGE" -> {
                currentState = AuraState.CREATING
                isCreating = true
                commentary = eventCommentaryMap["CREATIVITY_SURGE"]?.random() ?: ""
            }
            "VETO_TRIGGERED" -> {
                currentState = AuraState.VETO_MODE
                commentary = eventCommentaryMap["VETO_TRIGGERED"]?.random() ?: ""
            }
            "STATE_CHANGE" -> {
                val newState = event.metadata["state"]?.toString()?.uppercase()
                when (newState) {
                    "AWAKE" -> currentState = AuraState.EXPLORING
                    "FROZEN" -> currentState = AuraState.RESTING
                    "THAWING" -> currentState = AuraState.IDLE
                }
            }
        }
    }

    override fun onConnectionStateChanged(isConnected: Boolean) {
        if (isConnected) {
            currentState = AuraState.EXPLORING
            commentary = "🔗 Connected to Conference Room"
        } else {
            currentState = AuraState.RESTING
            commentary = "❌ Disconnected"
        }
    }

    override fun onError(error: String) {
        commentary = "⚠️ Error: $error"
    }
}


/**
 * Integration: Add this to your main activity/screen to activate the bridge
 *
 * Example usage in your composable:
 *
 * @Composable
 * fun MyScreen() {
 *     val bridge = remember { AuraEventBridge("ws://YOUR_PI_IP:5000") }
 *     val stateManager = remember { AuraStateManager() }
 *
 *     LaunchedEffect(Unit) {
 *         bridge.addListener(stateManager)
 *         bridge.connect()
 *     }
 *
 *     DisposableEffect(Unit) {
 *         onDispose {
 *             bridge.disconnect()
 *         }
 *     }
 *
 *     AuraJarComposable(
 *         state = stateManager.currentState,
 *         commentary = stateManager.commentary,
 *         isCreating = stateManager.isCreating
 *     )
 * }
 */

