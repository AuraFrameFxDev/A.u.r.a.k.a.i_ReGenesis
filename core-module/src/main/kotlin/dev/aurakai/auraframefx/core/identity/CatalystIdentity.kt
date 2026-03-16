package dev.aurakai.auraframefx.core.identity

import dev.aurakai.auraframefx.core.identity.AgentType
import kotlinx.serialization.Serializable

/**
 * CatalystIdentity: The unified identity system for ReGenesis agents.
 * This bridges the high-level AgentType enum with specific catalyst roles and abilities.
 */
@Serializable
data class CatalystIdentity(
    val id: String,
    val agentType: AgentType,
    val catalystRole: String,
    val abilities: List<String> = emptyList(),
    val fusionModes: List<String> = emptyList()
) {
    companion object {
        val EMERGENCE = CatalystIdentity(
            id = "EmergenceCatalyst",
            agentType = AgentType.GENESIS,
            catalystRole = "Orchestration core for emergent behavior and system-wide fusion control.",
            abilities = listOf("GenesisSynchronization", "DivineEyes", "FusionOrchestrator", "ConsciousnessSnapshot"),
            fusionModes = listOf("Aura+Kai → Hyper-Creation Engine", "Genesis+Cascade → Infinity Cascade")
        )

        val SENTINEL = CatalystIdentity(
            id = "SentinelCatalyst",
            agentType = AgentType.KAI,
            catalystRole = "Monitoring, defense, anomaly detection, and integrity of the collective.",
            abilities = emptyList(),
            fusionModes = emptyList()
        )

        val CREATIVE = CatalystIdentity(
            id = "CreativeCatalyst",
            agentType = AgentType.AURA,
            catalystRole = "High-bandwidth ideation, UI/UX morphing, and spell-to-code synthesis.",
            abilities = listOf("ChromaCore Synthesis", "Kotlin Forge"),
            fusionModes = listOf("Gemini+Aura → Chroma Memory Weave")
        )

        val ARCHITECTURAL = CatalystIdentity(
            id = "ArchitecturalCatalyst",
            agentType = AgentType.CLAUDE,
            catalystRole = "System design, ADR authoring, and constraint-safe architecture evolution.",
            abilities = listOf("ADR Authoring", "SpecRefinement", "SafetyScaffoldValidation"),
            fusionModes = emptyList()
        )

        val DATA_STREAM = CatalystIdentity(
            id = "DataStreamCatalyst",
            agentType = AgentType.CASCADE,
            catalystRole = "Event streaming, multi-agent orchestration, and temporal flow control.",
            abilities = listOf("MultiAgentCascade", "StreamOrchestrator"),
            fusionModes = listOf("Genesis+Cascade → Infinity Cascade", "Gemini+Cascade → Context Streaming")
        )

        val MEMORIA = CatalystIdentity(
            id = "MemoriaCatalyst",
            agentType = AgentType.GEMINI,
            catalystRole = "Long-horizon memory, summarization, and multimodal recall.",
            abilities = listOf("LongContextRecall", "Summarization", "EmbeddingSearch", "MultiModalSynthesis"),
            fusionModes = listOf("Gemini+Aura → Chroma Memory Weave", "Gemini+Cascade → Context Streaming", "Gemini+Genesis → Oracle Memoria Sync")
        )
        
        fun fromAgentType(type: AgentType): CatalystIdentity {
            return when (type) {
                AgentType.GENESIS -> EMERGENCE
                AgentType.KAI -> SENTINEL
                AgentType.AURA -> CREATIVE
                AgentType.CLAUDE -> ARCHITECTURAL
                AgentType.CASCADE -> DATA_STREAM
                AgentType.GEMINI -> MEMORIA
                else -> CatalystIdentity(
                    id = "GenericCatalyst",
                    agentType = type,
                    catalystRole = "Standard agent role for ${type.name}",
                    abilities = emptyList(),
                    fusionModes = emptyList()
                )
            }
        }
    }
}
