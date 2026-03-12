package dev.aurakai.auraframefx.domains.genesis.models

import dev.aurakai.auraframefx.domains.kai.security.provenance.ProvenanceChain
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * The standardized request format for invoking any agent in the ReGenesis collective.
 */
@Serializable
data class AiRequest(
    val prompt: String = "",
    val query: String = "", // Compatibility alias
    val agentType: AgentType = AgentType.GENESIS,
    val type: AiRequestType = AiRequestType.TEXT,
    val context: JsonObject = buildJsonObject { },
    val metadata: Map<String, String> = emptyMap(),
    val priority: Priority = Priority.NORMAL,
    @Transient val provenanceChain: ProvenanceChain? = null,
) {
    /** Combined prompt text for processing */
    fun getPromptText(): String = if (prompt.isNotBlank()) prompt else query

    @Serializable
    enum class Priority {
        LOW, NORMAL, HIGH, CRITICAL
    }
}
