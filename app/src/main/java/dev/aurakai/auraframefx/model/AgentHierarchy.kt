package dev.aurakai.auraframefx.model

object AgentHierarchy {
    val MASTER_AGENTS = listOf(
        HierarchyAgentConfig("genesis", 100, setOf("orchestration")),
        HierarchyAgentConfig("aura", 80, setOf("creative")),
        HierarchyAgentConfig("kai", 80, setOf("security"))
    )

    fun registerAuxiliaryAgent(name: String, capabilities: Set<String>): HierarchyAgentConfig {
        return HierarchyAgentConfig(name, 50, capabilities)
    }

    fun getAgentConfig(name: String): HierarchyAgentConfig? {
        return MASTER_AGENTS.find { it.name == name }
    }

    fun getAgentsByPriority(): List<HierarchyAgentConfig> {
        return MASTER_AGENTS.sortedByDescending { it.priority }
    }
}
