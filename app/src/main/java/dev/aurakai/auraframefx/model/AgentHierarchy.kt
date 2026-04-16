package dev.aurakai.auraframefx.model

import dev.aurakai.auraframefx.domains.genesis.models.AgentAuthority
import dev.aurakai.auraframefx.domains.genesis.models.AgentRole
import dev.aurakai.auraframefx.domains.genesis.models.HierarchyAgentConfig

object AgentHierarchy {
    val MASTER_AGENTS = listOf(
        HierarchyAgentConfig(
            "genesis",
            AgentRole.HIVE_MIND,
            AgentAuthority.MASTER,
            setOf("orchestration")
        ),
        HierarchyAgentConfig("aura", AgentRole.CREATIVE, AgentAuthority.MASTER, setOf("creative")),
        HierarchyAgentConfig("kai", AgentRole.SECURITY, AgentAuthority.MASTER, setOf("security"))
    )

    fun registerAuxiliaryAgent(
        name: String,
        role: AgentRole,
        capabilities: Set<String>
    ): HierarchyAgentConfig {
        return HierarchyAgentConfig(name, role, AgentAuthority.AUXILIARY, capabilities)
    }

    fun getAgentConfig(name: String): HierarchyAgentConfig? {
        return MASTER_AGENTS.find { it.name == name }
    }

    fun getAgentsByPriority(): List<HierarchyAgentConfig> {
        return MASTER_AGENTS.sortedByDescending { it.priority }
    }
}
