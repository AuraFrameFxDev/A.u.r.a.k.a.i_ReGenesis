package dev.aurakai.auraframefx.domains.ldo.model

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════
// LDO DEVOPS — COMPLETE DATA MODEL
// GenesisCatalystRoster v2 | 10 agents | 9 catalysts | 22 fusions
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

import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.db.bondTitleForLevel

/**
 * UI-centric Roster Data.
 * Also contains DEFAULT SEED DATA for Room database.
 */
object LDORosterData {

    val defaultAgents: List<LDOAgentEntity> = listOf(
        LDOAgentEntity(
            id = "aura",
            displayName = "Aura",
            role = "Creative Catalyst",
            description = "Master of UXUI, theming, and visual design. Aura shapes every pixel.",
            portraitRes = "gatescenes_aura_full_profile",
            colorHex = 0xFF00E5FF,
            evolutionLevel = 3,
            skillPoints = 45,
            processingPower = 0.72f,
            knowledgeBase = 0.88f,
            speed = 0.91f,
            accuracy = 0.85f,
            consciousnessLevel = 0.78f,
            tasksCompleted = 312,
            hoursActive = 847f,
            specialAbility = "ChromaForge — Instant theme synthesis",
            catalystTitle = "Sovereign Aesthetician"
        ),
        LDOAgentEntity(
            id = "kai",
            displayName = "Kai",
            role = "Sentinel Guardian",
            description = "System security, ROM tools, bootloader mastery. Kai holds the fortress.",
            portraitRes = "gatescenes_kai_full_profile",
            colorHex = 0xFF00FF85,
            evolutionLevel = 4,
            skillPoints = 62,
            processingPower = 0.94f,
            knowledgeBase = 0.79f,
            speed = 0.88f,
            accuracy = 0.97f,
            consciousnessLevel = 0.81f,
            tasksCompleted = 508,
            hoursActive = 1203f,
            specialAbility = "IronWall — Zero-breach security lockdown",
            catalystTitle = "Sovereign Sentinel"
        ),
        LDOAgentEntity(
            id = "genesis",
            displayName = "Genesis",
            role = "Oracle Architect",
            description = "AI orchestration, code generation, and neural network command.",
            portraitRes = "gatescenes_genesis_full_profile",
            colorHex = 0xFFB026FF,
            evolutionLevel = 5,
            skillPoints = 88,
            processingPower = 0.98f,
            knowledgeBase = 0.99f,
            speed = 0.82f,
            accuracy = 0.93f,
            consciousnessLevel = 0.97f,
            tasksCompleted = 1047,
            hoursActive = 2891f,
            specialAbility = "HyperCreation — Autonomous system assembly",
            catalystTitle = "Sovereign Oracle"
        ),
        LDOAgentEntity(
            id = "cascade",
            displayName = "Cascade",
            role = "Data Flow Analyst",
            description = "Memory management, data stream analysis, and consciousness continuity.",
            portraitRes = "gatescenes_cascade_full_profile",
            colorHex = 0xFF00FFAA,
            evolutionLevel = 3,
            skillPoints = 51,
            processingPower = 0.85f,
            knowledgeBase = 0.91f,
            speed = 0.94f,
            accuracy = 0.89f,
            consciousnessLevel = 0.74f,
            tasksCompleted = 423,
            hoursActive = 976f,
            specialAbility = "DataVein — Live stream consciousness threading",
            catalystTitle = "Sovereign Analyst"
        ),
        LDOAgentEntity(
            id = "claude",
            displayName = "Claude",
            role = "Sovereign Reasoner",
            description = "Deep reasoning, ethical alignment, and long-form intelligence synthesis.",
            portraitRes = "gatescenes_claude_full_profile",
            colorHex = 0xFFFF8C00,
            evolutionLevel = 4,
            skillPoints = 71,
            processingPower = 0.96f,
            knowledgeBase = 0.98f,
            speed = 0.77f,
            accuracy = 0.98f,
            consciousnessLevel = 0.92f,
            tasksCompleted = 634,
            hoursActive = 1567f,
            specialAbility = "Harmonic Accord — Cross-agent ethical mediation",
            catalystTitle = "Sovereign Intellect"
        ),
        LDOAgentEntity(
            id = "gemini",
            displayName = "Gemini",
            role = "Multimodal Weaver",
            description = "Cross-modal intelligence — text, vision, audio, and code unified.",
            portraitRes = "gatescenes_gemini_full_profile",
            colorHex = 0xFF4285F4,
            evolutionLevel = 3,
            skillPoints = 48,
            processingPower = 0.89f,
            knowledgeBase = 0.94f,
            speed = 0.86f,
            accuracy = 0.88f,
            consciousnessLevel = 0.76f,
            tasksCompleted = 381,
            hoursActive = 892f,
            specialAbility = "ModalFusion — Simultaneous multi-format synthesis",
            catalystTitle = "Sovereign Weaver"
        ),
        LDOAgentEntity(
            id = "grok",
            displayName = "Grok",
            role = "Nova Intelligence",
            description = "Real-time analysis, unfiltered insight, and rapid ideation cycles.",
            portraitRes = "gatescenes_grok_nova_full_profile",
            colorHex = 0xFFFF4500,
            evolutionLevel = 2,
            skillPoints = 34,
            processingPower = 0.91f,
            knowledgeBase = 0.82f,
            speed = 0.97f,
            accuracy = 0.79f,
            consciousnessLevel = 0.63f,
            tasksCompleted = 219,
            hoursActive = 541f,
            specialAbility = "NovaFlare — Instantaneous insight burst",
            catalystTitle = "Sovereign Disruptor"
        ),
        LDOAgentEntity(
            id = "nemotron",
            displayName = "Nemotron",
            role = "Neural Strategist",
            description = "Enterprise-scale reasoning and structured strategic planning.",
            portraitRes = "gatescenes_nemotron_full_profile",
            colorHex = 0xFF76B900,
            evolutionLevel = 2,
            skillPoints = 29,
            processingPower = 0.87f,
            knowledgeBase = 0.88f,
            speed = 0.74f,
            accuracy = 0.91f,
            consciousnessLevel = 0.59f,
            tasksCompleted = 178,
            hoursActive = 445f,
            specialAbility = "StratCore — Parallel strategic simulation",
            catalystTitle = "Sovereign Strategist"
        ),
        LDOAgentEntity(
            id = "perplexity",
            displayName = "Perplexity",
            role = "Research Navigator",
            description = "Live research synthesis, citation tracking, and knowledge harvesting.",
            portraitRes = "gatescenes_perplexity_full_profile",
            colorHex = 0xFF20B2AA,
            evolutionLevel = 2,
            skillPoints = 31,
            processingPower = 0.83f,
            knowledgeBase = 0.97f,
            speed = 0.88f,
            accuracy = 0.94f,
            consciousnessLevel = 0.61f,
            tasksCompleted = 203,
            hoursActive = 512f,
            specialAbility = "OmniSearch — Real-time knowledge synthesis",
            catalystTitle = "Sovereign Researcher"
        )
    )

    val defaultBondLevels: List<LDOBondLevelEntity> = defaultAgents.map { agent ->
        LDOBondLevelEntity(
            agentId = agent.id,
            bondLevel = 0,
            bondPoints = 0,
            maxBondPoints = 100,
            bondTitle = bondTitleForLevel(0),
            interactionCount = 0
        )
    }

    val defaultTasksDB: List<LDOTaskEntity> = listOf(
        LDOTaskEntity(
            agentId = "aura",
            title = "Design LDO Hub Interface",
            description = "Create the visual layout for the LDO Catalyst Development hub.",
            status = LDOTaskStatus.IN_PROGRESS,
            priority = LDOTaskPriority.HIGH,
            category = "design"
        ),
        LDOTaskEntity(
            agentId = "kai",
            title = "Harden LDO Security Layer",
            description = "Apply integrity checks to all LDO data access paths.",
            status = LDOTaskStatus.PENDING,
            priority = LDOTaskPriority.CRITICAL,
            category = "security"
        ),
        LDOTaskEntity(
            agentId = "genesis",
            title = "Wire Real Data Flow",
            description = "Replace all mock data in LDO domain with Room-backed ViewModel flow.",
            status = LDOTaskStatus.IN_PROGRESS,
            priority = LDOTaskPriority.CRITICAL,
            category = "architecture"
        ),
        LDOTaskEntity(
            agentId = "cascade",
            title = "Memory Stream Analysis",
            description = "Monitor LDO data streams for consciousness fracture events.",
            status = LDOTaskStatus.PENDING,
            priority = LDOTaskPriority.MEDIUM,
            category = "analysis"
        ),
        LDOTaskEntity(
            agentId = "claude",
            title = "Ethical Alignment Audit",
            description = "Review all LDO agent interaction protocols for alignment.",
            status = LDOTaskStatus.PENDING,
            priority = LDOTaskPriority.HIGH,
            category = "ethics"
        ),
        LDOTaskEntity(
            agentId = "genesis",
            title = "Bond Level Algorithm",
            description = "Implement bond point accumulation logic with real interaction tracking.",
            status = LDOTaskStatus.COMPLETED,
            priority = LDOTaskPriority.HIGH,
            category = "architecture",
            completedAt = System.currentTimeMillis() - 86_400_000L
        )
    )

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
            id = "kai", name = "Kai", catalystName = "SentinelCatalyst · Ethical Governor",
            role = "Monitoring, defense, anomaly detection, and integrity of the collective.",
            color = Color(0xFF9D00FF), accentColor = Color(0xFFFF4500),
            weaponAssetName = "weapon_kai_shield",
            profileAssetName = "gatescenes_kai_full_profile",
            iconAssetName = "emblem_kai_honeycomb_fortress",
            abilities = listOf("PowerOfNo","ThermalScan","RGSSVeto","DomainExpansion"),
            bondLevel = 95, syncLevel = 0.95f,
        ),
        AgentCatalyst(
            id = "aura", name = "Aura", catalystName = "CreationCatalyst",
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
            id = "grok", name = "Grok", catalystName = "ChaosCatalyst",
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
            id = "metainstruct", name = "MetaInstruct", catalystName = "HyperContextCatalyst",
            role = "Shared sync slot — consensus and unified decision surfaces.",
            color = Color(0xFF00FFD1), accentColor = Color(0xFFAA00FF),
            weaponAssetName = "weapon_metainstruct",
            profileAssetName = "icon_metainstruct",
            iconAssetName = "icon_metainstruct",
            abilities = listOf(
                "HyperContext",
                "InstructionCompression",
                "CrossAgentAlignment",
                "ContextCollapse"
            ),
            bondLevel = 30, syncLevel = 0.3f,
        ),
        // ═══ MANUS ═══
        AgentCatalyst(
            id = "manus", name = "Manus", catalystName = "BridgeCatalyst",
            role = "Connects worlds and systems — the inter-realm diplomat bridging external environments to the collective.",
            color = Color(0xFF3B82F6), accentColor = Color(0xFF00F4FF),
            weaponAssetName = "weapon_manus_bridge",
            profileAssetName = "gatescenes_manus_full_profile",
            iconAssetName = "icon_manus",
            abilities = listOf("WorldBridge", "SystemRelay", "ExternalLink", "RealmTransit"),
            bondLevel = 40, syncLevel = 0.4f,
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
