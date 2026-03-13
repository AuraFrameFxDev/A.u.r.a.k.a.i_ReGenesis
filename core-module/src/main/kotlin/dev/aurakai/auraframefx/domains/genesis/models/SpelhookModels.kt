package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * ⚡ Spelhook: A generative unit of executable code.
 * Used by the Hyper-Creation Engine to define on-the-fly system modifications or UI components.
 */
@Serializable
data class Spelhook(
    val id: String,
    val code: String,
    val description: String,
    val agentOwner: AgentType,
    val complexity: Int = 1,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Result of a Spelhook generation or forge operation.
 */
@Serializable
sealed class SpelhookResult {
    @Serializable
    data class Success(
        val spelhook: Spelhook,
        val logs: List<String> = emptyList()
    ) : SpelhookResult()

    @Serializable
    data class Error(
        val message: String,
        val trace: String? = null
    ) : SpelhookResult()
}
