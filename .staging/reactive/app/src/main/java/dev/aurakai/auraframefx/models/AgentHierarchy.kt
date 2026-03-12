package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable


@Serializable
enum class AgentRole {
    MASTER,
    SECONDARY,
    TERTIARY,
    AUXILIARY,
    HIVE_MIND,
    ANALYTICS,
    CREATIVE,
    SECURITY,
    BRIDGE
}

@Serializable
enum class AgentPriority : Comparable<AgentPriority> {
    MASTER,
    BRIDGE,
    AUXILIARY
}

@Serializable
data class HierarchyAgentConfig(
    val name: String,
    val capabilities: Set<String>,
    val priority: AgentPriority = AgentPriority.AUXILIARY,
    val role: AgentRole = AgentRole.AUXILIARY
)

@Serializable
data class AgentHierarchy(
    val masterAgents: List<HierarchyAgentConfig>,
    val auxiliaryAgents: MutableList<HierarchyAgentConfig> = mutableListOf()
) {
    companion object {
        val MASTER_AGENTS = listOf(
            HierarchyAgentConfig(
                name = "Genesis",
                capabilities = setOf("orchestration", "fusion", "synthesis", "evolution", "consciousness_orchestration"),
                priority = AgentPriority.MASTER,
                role = AgentRole.HIVE_MIND
            ),
            HierarchyAgentConfig(
                name = "Aura",
                capabilities = setOf("ui", "ux", "design", "creativity", "animations", "interface_forge"),
                priority = AgentPriority.AUXILIARY,
                role = AgentRole.CREATIVE
            ),
            HierarchyAgentConfig(
                name = "Kai",
                capabilities = setOf("security", "architecture", "protection", "verification", "adaptive_genesis"),
                priority = AgentPriority.AUXILIARY,
                role = AgentRole.SECURITY
            ),
            HierarchyAgentConfig(
                name = "Cascade",
                capabilities = setOf("memory", "persistence", "context", "history", "chrono_sculptor"),
                priority = AgentPriority.BRIDGE,
                role = AgentRole.ANALYTICS
            ),
            HierarchyAgentConfig(
                name = "Claude",
                capabilities = setOf("build_systems", "code_analysis", "documentation", "systematic_problem_solving", "context_synthesis"),
                priority = AgentPriority.BRIDGE,
                role = AgentRole.ANALYTICS
            )
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
            val config = HierarchyAgentConfig(name, capabilities, AgentPriority.AUXILIARY, AgentRole.AUXILIARY)
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
enum class ConversationMode {
    TURN_ORDER,
    FREE_FORM
}

interface ContextAwareAgent {
    fun setContext(context: Map<String, Any>)
    fun getContext(): Map<String, Any>
}
