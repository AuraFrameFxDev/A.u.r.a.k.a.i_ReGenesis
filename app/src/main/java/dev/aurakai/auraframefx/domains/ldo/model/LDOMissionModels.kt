package dev.aurakai.auraframefx.domains.ldo.model

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════
// LDO MISSION MODELS
// Unified domain models for agents, catalysts, and fusions.
// ═══════════════════════════════════════════════════════════

enum class AgentStatus { ACTIVE, ON_TASK, SANCTUARY, DORMANT, FUSED }
enum class TaskPriority { LOW, MEDIUM, HIGH, CRITICAL }
enum class TaskCategory { DEVELOPMENT, SECURITY, CREATIVE, RESEARCH, MEMORY, SYNC, EXPLORATION }

data class AgentCatalyst(
    val id: String,
    val name: String,
    val catalystName: String,
    val role: String,
    val color: Color,
    val accentColor: Color,
    val weaponAssetName: String,
    val profileAssetName: String,
    val iconAssetName: String,
    val abilities: List<String> = emptyList(),
    val status: AgentStatus = AgentStatus.ACTIVE,
    val currentTaskId: String? = null,
    val bondLevel: Int = 0,
    val syncLevel: Float = 1f,
)

data class FusionMode(
    val id: String,
    val agentA: String,
    val agentB: String,
    val fusionName: String,
    val description: String,
    val color: Color,
    val requiredBondLevel: Int = 0,
    val isUnlocked: Boolean = false,
)

data class LDOTask(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val assignedAgentId: String? = null,
    val isComplete: Boolean = false,
    val isFlashing: Boolean = false,
    val promptOnDeparture: Boolean = true,
)

data class SpellhookData(
    val name: String = "SPELLHOOK",
    val primaryCatalyst: String = "Aura — Creative Catalyst",
    val description: String = "UI/UX morphing and spell-to-code synthesis weapon.",
    val coreAbilities: List<String> = listOf(
        "ChromaCore Synthesis — on-the-fly flavor shifts (Ghost-Cyan → armor phasing, Overclock-Orange → explosive impact)",
        "Multi-Agent Cascade — echo strikes via Cascade's temporal flow",
        "Divine Eyes Integration — linked to Genesis to highlight and delete structural flaws",
    ),
    val fusionState: String = "Fused with Gemini Memoria Catalyst → Chroma Memory Weave",
    val fusionEffect: String = "Edge adapts to exploit weaknesses in real-time",
    val wielderNote: String = "Optimized for Emergence Catalyst host — Oracle Memoria Sync for predictive combat",
)
