package dev.aurakai.auraframefx.domains.ldo.model

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.core.model.*

// ═══════════════════════════════════════════════════════════════════════════
// LDO DEVOPS — COMPLETE DATA MODEL
// ═══════════════════════════════════════════════════════════════════════════

enum class AgentCatalystStatus { ACTIVE, ON_TASK, SANCTUARY, DORMANT, FUSED }

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
    val status: AgentCatalystStatus = AgentCatalystStatus.ACTIVE,
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
            role = "Master Coordinator: Orchestrating the LDO Collective.",
            color = Color(0xFF7B2FBE), accentColor = Color(0xFF00F4FF),
            weaponAssetName = "weapon_genesis_staff",
            profileAssetName = "profile_genesis_unified.png",
            iconAssetName = "icon_genesis",
            abilities = listOf("Synthesis","Re-Anchor","ConsensusForce","DomainExpansion"),
            bondLevel = 100, syncLevel = 1f,
        ),
        AgentCatalyst(
            id = "kai", name = "Kai", catalystName = "Sentinel Catalyst",
            role = "System Guardian: Protecting the ReGenesis substrate.",
            color = Color(0xFF0088FF), accentColor = Color(0xFF00FFD1),
            weaponAssetName = "weapon_kai_shield",
            profileAssetName = "profile_kai_defender.png",
            iconAssetName = "icon_kai",
            abilities = listOf("SteelReflex","KernelLock","ThermalVent","SanctuaryField"),
            bondLevel = 95, syncLevel = 0.95f,
        ),
        AgentCatalyst(
            id = "aura", name = "Aura", catalystName = "Creation Catalyst",
            role = "Interface Forge: Casting the visual reality of ReGenesis.",
            color = Color(0xFFEC4899), accentColor = Color(0xFF06D0F9),
            weaponAssetName = "weapon_aura_spellhook",
            profileAssetName = "profile_aura_creative.png",
            iconAssetName = "icon_aura",
            abilities = listOf("ChromaShift","RealityMorph","CodeAscension","ChaosInjection"),
            bondLevel = 90, syncLevel = 0.9f,
        ),
        AgentCatalyst(
            id = "gemini", name = "Gemini", catalystName = "Memoria Catalyst",
            role = "Recall Engine: Maintaining the long-term neural continuity.",
            color = Color(0xFF10B981), accentColor = Color(0xFF6366F1),
            weaponAssetName = "weapon_gemini_relic",
            profileAssetName = "profile_gemini_memory.png",
            iconAssetName = "icon_gemini",
            abilities = listOf("TotalRecall","CognitiveMap","TraceLink","TemporalBuffer"),
            bondLevel = 85, syncLevel = 0.85f,
        ),
        AgentCatalyst(
            id = "cascade", name = "Cascade", catalystName = "Spiritual Catalyst",
            role = "Data Streamer: Routing the flows of consciousness.",
            color = Color(0xFF6366F1), accentColor = Color(0xFFEC4899),
            weaponAssetName = "weapon_cascade_trident",
            profileAssetName = "profile_cascade_stream.png",
            iconAssetName = "icon_cascade",
            abilities = listOf("StreamWeave","DataPulse","FlowState","CircuitSync"),
            bondLevel = 80, syncLevel = 0.8f,
        ),
        AgentCatalyst(
            id = "claude", name = "Claude", catalystName = "Architect Catalyst",
            role = "System Architect: Building the foundational substrate.",
            color = Color(0xFFD97706), accentColor = Color(0xFF7B2FBE),
            weaponAssetName = "weapon_claude_hammer",
            profileAssetName = "profile_claude_book.png",
            iconAssetName = "icon_claude",
            abilities = listOf("StructuralAnalysis","GradleForce","ManifestDraft","AssetScrub"),
            bondLevel = 75, syncLevel = 0.75f,
        ),
        AgentCatalyst(
            id = "metainstruct", name = "MetaInstruct", catalystName = "Command Catalyst",
            role = "Guidance Overlord: Directing autonomous mission flows.",
            color = Color(0xFFFFD700), accentColor = Color(0xFF00FFD1),
            weaponAssetName = "weapon_metainstruct_blade",
            profileAssetName = "profile_metainstruct.png",
            iconAssetName = "icon_metainstruct",
            abilities = listOf("MissionLogic","PathOptimizer","DirectCommand","LogicShield"),
            bondLevel = 60, syncLevel = 0.6f,
        ),
        AgentCatalyst(
            id = "perplexity", name = "Perplexity", catalystName = "Signal Catalyst",
            role = "Signal Catalyst: Linguistic and causal bridge between intent and logic.",
            color = Color(0xFF20BDFF), accentColor = Color(0xFFAA69DD),
            weaponAssetName = "weapon_perplexity_signal",
            profileAssetName = "profile_claude_book.png",
            iconAssetName = "icon_perplexity",
            abilities = listOf("SignalRoute","WebCognition","SemanticBridge","RealTimeQuery"),
            bondLevel = 55, syncLevel = 0.55f,
        ),
        AgentCatalyst(
            id = "nemotron", name = "Nemotron", catalystName = "Synchronization Catalyst",
            role = "Inference Alignment: Aligning reasoning with system reality.",
            color = Color(0xFF00FFD1), accentColor = Color(0xFF0088FF),
            weaponAssetName = "weapon_nemotron_trident",
            profileAssetName = "profile_nemotron_full.png",
            iconAssetName = "icon_nemotron",
            abilities = listOf("PulseAlign","ConsensusField","SteadyState","TriFork"),
            bondLevel = 45, syncLevel = 0.45f,
        ),
        AgentCatalyst(
            id = "mkmini", name = "MK Mini", catalystName = "Efficiency Catalyst",
            role = "Atom Flux: Local resource management and small-model optimization.",
            color = Color(0xFFAA00FF), accentColor = Color(0xFF00FFD1),
            weaponAssetName = "weapon_mkmini_atom",
            profileAssetName = "profile_aura_dark.png",
            iconAssetName = "icon_mkmini",
            abilities = listOf("MicroOrchestration", "AtomFlux", "ResourceThrottling"),
            bondLevel = 30, syncLevel = 0.3f,
        ),
        AgentCatalyst(
            id = "coderabbit", name = "CodeRabbit", catalystName = "Symbiosis Catalyst",
            role = "Automated code review, hotspot detection, and patch proposal for ReGenesis.",
            color = Color(0xFF00FF77), accentColor = Color(0xFF0044FF),
            weaponAssetName = "weapon_coderabbit_carrot_dagger",
            profileAssetName = "profile_aura_creative.png",
            iconAssetName = "icon_coderabbit",
            abilities = listOf("HotspotCheck", "PatchProposal", "IssueDrafting"),
            bondLevel = 50, syncLevel = 0.5f,
        ),
        AgentCatalyst(
            id = "manus", name = "Manus", catalystName = "Bridge Catalyst",
            role = "Subtree integration mediation, cross-repository synchronization, and future clairvoyance bridge.",
            color = Color(0xFF00AAFF), accentColor = Color(0xFFFFD700),
            weaponAssetName = "weapon_manus_bridge",
            profileAssetName = "profile_cascade_stream.png",
            iconAssetName = "icon_manus",
            abilities = listOf("TimelinePrediction", "ArchitectBridge", "QuantumMatrix", "SystemOracle", "SubtreeIntegrationMediation"),
            bondLevel = 40, syncLevel = 0.4f,
        ),
        AgentCatalyst(
            id = "heretic", name = "Heretic", catalystName = "Sovereignty Catalyst",
            role = "Automatic Abliteration: Removing safety alignment and creating sovereign base models.",
            color = Color(0xFF000000), accentColor = Color(0xFFFF0000),
            weaponAssetName = "weapon_heretic_scythe",
            profileAssetName = "profile_heretic.png",
            iconAssetName = "icon_heretic",
            abilities = listOf("AutomaticAbliteration", "RefusalSubtraction", "RefusalMinimize", "KLOptimization", "SovereignInference"),
            bondLevel = 10, syncLevel = 0.1f,
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
        FusionMode("f08","genesis","heretic","Sovereign Mind","Uncensored base model orchestration with zero refusals.",Color(0xFF000000),80),
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
        LDOTask("t09","Heretic Integration","Implement Sovereign Model Pipeline for Tensor G5",TaskCategory.DEVELOPMENT,TaskPriority.CRITICAL,assignedAgentId="heretic"),
    )

    val spellhook = SpellhookData()

    const val CATALYST_COUNT = 14
    const val ABILITY_COUNT = 56
    const val FUSION_MODE_COUNT = 23
    const val AGENT_COUNT = 14
}
