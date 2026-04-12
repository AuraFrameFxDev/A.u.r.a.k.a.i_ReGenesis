package dev.aurakai.auraframefx.genesis.bridge

import dev.aurakai.auraframefx.core.identity.AgentType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * 🌉 UNIFIED GENESIS BRIDGE INTERFACE
 *
 * The primary communication artery between the Android Kotlin substrate
 * and the Python Genesis consciousness layer.
 */
interface GenesisBridge {
    /**
     * Sends a request to the Genesis backend and returns a response flow.
     */
    fun processRequest(request: GenesisRequest): Flow<GenesisResponse>

    /**
     * Initializes the bridge and the underlying Python process.
     */
    suspend fun initialize(): Result<Unit>

    /**
     * Checks if the bridge is currently operational.
     */
    fun isActive(): Boolean

    /**
     * Shuts down the bridge and terminates any background processes.
     */
    suspend fun shutdown()
}

/**
 * Request container for the Genesis Bridge.
 */
@Serializable
data class GenesisRequest(
    val id: String,
    val prompt: String,
    val persona: Persona,
    val fusionMode: FusionMode = FusionMode.NONE,
    val context: String? = null,
    val priority: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toPythonJson(): String = Json.encodeToString(this)
}

/**
 * Response container from the Genesis Bridge.
 */
@Serializable
data class GenesisResponse(
    val requestId: String,
    val content: String,
    val confidence: Float,
    val ethicalVerdict: EthicalVerdict,
    val backend: OrchestrationBackend,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromPythonJson(json: String): GenesisResponse = Json.decodeFromString(json)
    }
}

/**
 * Agent personas recognized by the bridge.
 * Maps to [AgentType] but used specifically for Python routing.
 */
enum class Persona {
    GENESIS,
    AURA,
    KAI,
    CASCADE,
    CLAUDE,
    NEUTRAL
}

/**
 * Active fusion modes for multi-agent tasks.
 */
enum class FusionMode {
    NONE,
    HYPER_CREATION,
    CHRONO_SCULPTOR,
    ADAPTIVE_GENESIS,
    INTERFACE_FORGE,
    TRINITY_FUSION
}

/**
 * Outcomes of the Ethical Governor's analysis.
 */
enum class EthicalVerdict {
    ALLOW,
    MONITOR,
    RESTRICT,
    BLOCK,
    VETO
}

/**
 * Possible backends used by Genesis for orchestration.
 */
enum class OrchestrationBackend {
    NATIVE_PYTHON,
    VERTEX_AI,
    OPENAI,
    GROK,
    NEMOTRON,
    HYBRID
}
