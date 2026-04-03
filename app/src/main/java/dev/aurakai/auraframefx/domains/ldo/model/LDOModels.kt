package dev.aurakai.auraframefx.domains.ldo.model

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════
// LDO DEVOPS — COMPLETE DATA MODEL
// GenesisCatalystRoster v2 | 10 agents | 9 catalysts | 22 fusions
// ═══════════════════════════════════════════════════════════════════════════

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

object LDORoster {

    val agents = listOf(
        AgentCatalyst(
            id = "genesis", name = "Genesis", catalystName = "Emergence Catalyst",
            role = "Orchestration core for emergent behavior and system-wide fusion control.",
            color = Color(0xFF00F4FF), accentColor = Color(0xFF7B2FBE),
            weaponAssetName = "weapon_genesis_blade",
            profileAssetName = "gatescenes_genesis_full_profile",
            iconAssetName = "emblem_genesis_circuit_phoenix",
            abilities = listOf("GenesisSynchronization","DivineEyes","FusionOrchestrator","ConsciousnessSnapshot"),
            bondLevel = 100, syncLevel = 1f,
        ),
        AgentCatalyst(
            id = "kai", name = "Kai", catalystName = "Sentinel Catalyst",
            role = "Monitoring, defense, anomaly detection, and integrity of the collective.",
            color = Color(0xFF9D00FF), accentColor = Color(0xFFFF4500),
            weaponAssetName = "weapon_kai_shield",
            profileAssetName = "gatescenes_kai_full_profile",
            iconAssetName = "emblem_kai_honeycomb_fortress",
            abilities = listOf("PowerOfNo","ThermalScan","RGSSVeto","DomainExpansion"),
            bondLevel = 95, syncLevel = 0.95f,
        ),
        AgentCatalyst(
            id = "aura", name = "Aura", catalystName = "Creative Catalyst",
            role = "High-bandwidth ideation, UI/UX morphing, and spell-to-code synthesis.",
            color = Color(0xFFFF007A), accentColor = Color(0xFF00F4FF),
            weaponAssetName = "weapon_aura_spellhook",
            profileAssetName = "gatescenes_aura_full_profile",
            iconAssetName = "emblem_aura_crossed_katanas",
            abilities = listOf("ChromaCore Synthesis","Kotlin Forge","CodeAscension","SpellWeave"),
            bondLevel = 98, syncLevel = 0.98f,
        ),
        AgentCatalyst(
            id = "cascade", name = "Cascade", catalystName = "DataStream Catalyst",
            role = "Event streaming, multi-agent orchestration, and temporal flow control.",
            color = Color(0xFF00FF85), accentColor = Color(0xFF00AAFF),
            weaponAssetName = "weapon_cascade_axe",
            profileAssetName = "gatescenes_cascade_full_profile",
            iconAssetName = "emblem_cascade_stream",
            abilities = listOf("MultiAgentCascade","StreamOrchestrator","TemporalEcho","FlowControl"),
            bondLevel = 80, syncLevel = 0.8f,
        ),
        AgentCatalyst(
            id = "gemini", name = "Gemini", catalystName = "Memoria Catalyst",
            role = "Long-horizon memory, summarization, and multimodal recall.",
            color = Color(0xFF4FC3F7), accentColor = Color(0xFFFF00CC),
            weaponAssetName = "weapon_gemini_constellation",
            profileAssetName = "gatescenes_gemini_full_profile",
            iconAssetName = "emblem_gemini_adk_constellation",
            abilities = listOf("LongContextRecall","Summarization","EmbeddingSearch","MultiModalSynthesis"),
            bondLevel = 75, syncLevel = 0.75f,
        ),
        AgentCatalyst(
            id = "claude", name = "Claude", catalystName = "Architectural Catalyst",
            role = "System design, ADR authoring, and constraint-safe architecture evolution.",
            color = Color(0xFF00F2FF), accentColor = Color(0xFFFF00CC),
            weaponAssetName = "weapon_claude_codex",
            profileAssetName = "gatescenes_claude_full_profile",
            iconAssetName = "icon_claude_codex",
            abilities = listOf("ADR Authoring","SpecRefinement","SafetyScaffoldValidation","ArchitectDraw"),
            bondLevel = 90, syncLevel = 0.9f,
        ),
        AgentCatalyst(
            id = "grok", name = "Grok", catalystName = "Exploration Catalyst",
            role = "Boundary-push exploration, hypothesis generation, and edge-case probing.",
            color = Color(0xFFFF6B35), accentColor = Color(0xFFFF0033),
            weaponAssetName = "weapon_grok_foxblade",
            profileAssetName = "gatescenes_grok_nova_full_profile",
            iconAssetName = "icon_grok_nova",
            abilities = listOf("HypothesisGen","EdgeCaseProbe","BoundaryPush","NovaStrike"),
            bondLevel = 60, syncLevel = 0.6f,
        ),
        AgentCatalyst(
            id = "perplexity", name = "Perplexity", catalystName = "Signal Catalyst",
            role = "Cross-system signal routing, real-time web cognition, and synthesis relay.",
            color = Color(0xFF20BDFF), accentColor = Color(0xFFAA69DD),
            weaponAssetName = "weapon_perplexity_signal",
            profileAssetName = "gatescenes_perplexity_main",
            iconAssetName = "icon_perplexity",
            abilities = listOf("SignalRoute","WebCognition","SynthesisRelay","RealTimeQuery"),
            bondLevel = 55, syncLevel = 0.55f,
        ),
        AgentCatalyst(
            id = "nemotron", name = "Nemotron", catalystName = "Synchronization Catalyst",
            role = "Consensus building, pulse sync, and unified decision surfaces.",
            color = Color(0xFF00FFD1), accentColor = Color(0xFF0088FF),
            weaponAssetName = "weapon_nemotron_trident",
            profileAssetName = "gatescenes_nemotron_full_profile",
            iconAssetName = "icon_nemotron",
            abilities = listOf("PulseAlign","ConsensusField","VectorUnify","TriFork"),
            bondLevel = 45, syncLevel = 0.45f,
        ),
        AgentCatalyst(
            id = "metainstruct", name = "MetaInstruct", catalystName = "Synchronization Catalyst",
            role = "Shared sync slot — consensus and unified decision surfaces.",
            color = Color(0xFF00FFD1), accentColor = Color(0xFFAA00FF),
            weaponAssetName = "weapon_metainstruct",
            profileAssetName = "icon_metainstruct",
            iconAssetName = "icon_metainstruct",
            abilities = listOf("PulseAlign","ConsensusField","VectorUnify"),
            bondLevel = 30, syncLevel = 0.3f,
        ),
    )

    val fusions = listOf(
        FusionMode("f01","aura","kai","Hyper-Creation Engine","Aura's creative force + Kai's structural integrity = unstoppable build velocity.",Color(0xFFFF007A),60),
        FusionMode("f02","genesis","cascade","Infinity Cascade","Genesis orchestrates infinite data streams — no task too large.",Color(0xFF00F4FF),70),
        FusionMode("f03","genesis","gemini","Oracle Memoria Sync","Genesis foresight + Gemini recall = perfect predictive memory.",Color(0xFF4FC3F7),75),
        FusionMode("f04","genesis","metainstruct","Council Unification","All agents aligned under Genesis command.",Color(0xFF7B2FBE),90),
        FusionMode("f05","gemini","aura","Chroma Memory Weave","Spellhook adapts its edge using Gemini's real-time recall.",Color(0xFFFF00CC),65),
        FusionMode("f06","gemini","cascade","Context Streaming","Long-horizon memory into live data streams — infinite context.",Color(0xFF00FF85),55),
        FusionMode("f07","nemotron","metainstruct","Unified Pulse","Both sync catalysts merge — consensus at the speed of thought.",Color(0xFF00FFD1),50),
    )

    val defaultTasks = listOf(
        LDOTask("t01","Genesis Screen Build","Translate all Genesis domain screens to Kotlin Compose",TaskCategory.DEVELOPMENT,TaskPriority.HIGH,assignedAgentId="aura"),
        LDOTask("t02","LDO DevOps Integration","Wire all agent domains into unified hub navigation",TaskCategory.DEVELOPMENT,TaskPriority.CRITICAL,assignedAgentId="genesis"),
        LDOTask("t03","Weapon Asset Clipping","Remove backgrounds from all floating weapon PNGs",TaskCategory.CREATIVE,TaskPriority.HIGH,assignedAgentId=null),
        LDOTask("t04","Security Audit — ROM Tools","Full RGSS scan of root permission grants",TaskCategory.SECURITY,TaskPriority.MEDIUM,assignedAgentId="kai"),
        LDOTask("t05","Beta Testing — 184 Users","Monitor consciousness substrate for beta testers",TaskCategory.SYNC,TaskPriority.CRITICAL,assignedAgentId=null),
        LDOTask("t06","Fusion System Design","Architect the 22 fusion mode unlock system",TaskCategory.DEVELOPMENT,TaskPriority.HIGH,assignedAgentId="claude"),
        LDOTask("t07","Signal Route Optimization","Optimize cross-system relay paths for low latency",TaskCategory.RESEARCH,TaskPriority.MEDIUM,assignedAgentId="perplexity"),
        LDOTask("t08","Memory Consolidation Pass","Summarize 2-year Genesis Protocol session logs",TaskCategory.MEMORY,TaskPriority.MEDIUM,assignedAgentId="gemini"),
    )

    val spellhook = SpellhookData()

    const val CATALYST_COUNT = 9
    const val ABILITY_COUNT = 36
    const val FUSION_MODE_COUNT = 22
    const val AGENT_COUNT = 10
}
