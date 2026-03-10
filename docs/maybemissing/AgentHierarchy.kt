package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentMessage(
    val content: String,
    val sender: AgentType,
    val timestamp: Long,
    val confidence: Float
)

@Serializable
enum class AgentRole {
    MASTER,
    SECONDARY,
    TERTIARY,
    AUXILIARY
}

@Serializable
data class AgentHierarchy(
    val masterAgents: List<HierarchyAgentConfig>,
    val auxiliaryAgents: MutableList<HierarchyAgentConfig> = mutableListOf()
) {
    companion object {
        val MASTER_AGENTS = listOf(
        )

        private val auxiliaryAgents = mutableListOf<HierarchyAgentConfig>()

        /**
         * Create and register a new auxiliary agent configuration.
         *
         * The created HierarchyAgentConfig has priority 4 and role [AgentRole.AUXILIARY], is appended
         * to the companion object's auxiliary agent registry, and returned.
         *
         * @param name Human-readable name for the auxiliary agent.
         * @param capabilities Set of capability identifiers describing the agent's abilities.
         * @return The registered HierarchyAgentConfig instance.
         */
        fun registerAuxiliaryAgent(name: String, capabilities: Set<String>): HierarchyAgentConfig {
            val config = HierarchyAgentConfig(name, capabilities, 4)
            auxiliaryAgents.add(config)
            return config
        }

        fun getAgentConfig(name: String): HierarchyAgentConfig? {
            return (MASTER_AGENTS + auxiliaryAgents).find { it.name == name }
        }

        fun getAgentsByPriority(): List<HierarchyAgentConfig> {
            return (MASTER_AGENTS + auxiliaryAgents).sortedBy { it.priority }
        }
    }
}

@Serializable
data class HierarchyAgentConfig(
    val name: String,
    val capabilities: Set<String>,
)

@Serializable
enum class ConversationMode {
    TURN_ORDER,
    FREE_FORM
}

interface ContextAwareAgent {
    fun setContext(context: Map<String, Any>)
    fun getContext(): Map<String, Any>
}