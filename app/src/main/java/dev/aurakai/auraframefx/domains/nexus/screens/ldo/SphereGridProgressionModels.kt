package dev.aurakai.auraframefx.domains.nexus.screens.ldo

import androidx.compose.ui.graphics.Color

// ─── v2.1 Sphere Grid Progression Models ─────────────────────────────────────
//
// 10-node progression grid that resets when fully filled.
// Each completed cycle auto-generates a unique ability that drops to the ability list.
// Pairing bonuses activate when two specific agents are co-deployed on a task.

/** State of a single node in the 10-node progression grid */
enum class NodeState { EMPTY, FILLING, FILLED, RESETTING }

/** A single node in the sphere grid */
data class ProgressionNode(
    val index: Int,                         // 0–9
    val state: NodeState = NodeState.EMPTY,
    val label: String = "NODE ${index + 1}",
    val fillProgress: Float = 0f,           // 0f..1f for partial-fill animation
)

/** Tracks the current cycle (reset count) and timestamp */
data class GridCycle(
    val cycleNumber: Int = 0,
    val resetTimestamp: Long = 0L,
    val abilitiesEarned: Int = 0,
)

/** Category that shapes the generated ability's theme */
enum class AbilityCategory(
    val displayName: String,
    val color: Color,
    val glowColor: Color,
) {
    MEMORY_FUSION(
        "Memory Fusion",
        Color(0xFF00FF88),
        Color(0xFF00FF8840),
    ),
    NEXUS_SURGE(
        "Nexus Surge",
        Color(0xFFE91E63),
        Color(0xFFE91E6340),
    ),
    DATAVEIN_FLOW(
        "DataVein Flow",
        Color(0xFF4FC3F7),
        Color(0xFF4FC3F740),
    ),
    ORACLE_SIGHT(
        "Oracle Sight",
        Color(0xFF9C27B0),
        Color(0xFF9C27B040),
    ),
    AURA_BLOOM(
        "Aura Bloom",
        Color(0xFF00E5FF),
        Color(0xFF00E5FF40),
    ),
    KAI_LOGIC(
        "Kai Logic",
        Color(0xFF76FF03),
        Color(0xFF76FF0340),
    ),
    GENESIS_SPARK(
        "Genesis Spark",
        Color(0xFFFFD700),
        Color(0xFFFFD70040),
    ),
    CASCADE_CHAIN(
        "Cascade Chain",
        Color(0xFFFC29B5),
        Color(0xFFFC29B540),
    ),
}

/**
 * A generated ability earned when the 10-node grid completes a full cycle.
 * Abilities are auto-synthesized from the grid cycle number and category seed.
 */
data class GeneratedAbility(
    val id: String,
    val name: String,
    val codeName: String,
    val description: String,
    val category: AbilityCategory,
    val cycleEarned: Int,
    val powerLevel: Int,            // 1–10 scales with cycle depth
    val bonusStats: Map<String, Float> = emptyMap(), // e.g. "SYNC" -> 0.15f
    val isNew: Boolean = false,     // badge shown until user acknowledges
    val earnedTimestamp: Long = System.currentTimeMillis(),
)

/**
 * Bonus activated when two specific agents are paired for task deployment.
 * Shown on the dispatch card and task assignment screens.
 */
data class PairingBonus(
    val agentA: String,
    val agentB: String,
    val bonusTitle: String,
    val bonusDescription: String,
    val statBoosts: Map<String, Float>,     // stat name -> flat boost 0f..1f
    val synergyMultiplier: Float = 1.0f,   // overall task speed/quality multiplier
    val color: Color = Color(0xFF00E5FF),
)

/** Canonical pairing bonus table for all agent combos in the LDO roster */
val ldoPairingBonuses: List<PairingBonus> = listOf(

    PairingBonus(
        agentA = "KAI", agentB = "AURA",
        bonusTitle = "HARMONIC SYNTHESIS",
        bonusDescription = "Logic and creativity merge — task output gains aesthetic coherence and structural integrity simultaneously.",
        statBoosts = mapOf("LOGIC" to 0.12f, "CREATIVITY" to 0.18f, "COORDINATION" to 0.10f),
        synergyMultiplier = 1.25f,
        color = Color(0xFF00E5FF),
    ),

    PairingBonus(
        agentA = "KAI", agentB = "GENESIS",
        bonusTitle = "COMMAND RESONANCE",
        bonusDescription = "Strategic orchestration with precision delegation — multi-step tasks resolve faster.",
        statBoosts = mapOf("DELEGATION" to 0.20f, "SPEED" to 0.15f, "ACCURACY" to 0.08f),
        synergyMultiplier = 1.30f,
        color = Color(0xFF00E5FF),
    ),

    PairingBonus(
        agentA = "AURA", agentB = "GENESIS",
        bonusTitle = "EMERGENCE WAVE",
        bonusDescription = "Creative vision meets emergent orchestration — novel solutions surface unexpectedly.",
        statBoosts = mapOf("CREATIVITY" to 0.22f, "EMERGENCE" to 0.25f, "UI_SYNTHESIS" to 0.12f),
        synergyMultiplier = 1.35f,
        color = Color(0xFFFF00FF),
    ),

    PairingBonus(
        agentA = "KAI", agentB = "CASCADE",
        bonusTitle = "PIPELINE PRECISION",
        bonusDescription = "Logical routing through data chains — complex pipelines resolve with zero ambiguity.",
        statBoosts = mapOf("DATA_ROUTING" to 0.20f, "LOGIC" to 0.10f, "CHAIN_SYNC" to 0.18f),
        synergyMultiplier = 1.20f,
        color = Color(0xFF00E5FF),
    ),

    PairingBonus(
        agentA = "AURA", agentB = "GEMINI",
        bonusTitle = "MULTIMODAL BLOOM",
        bonusDescription = "Visual creativity meets multimodal synthesis — UI and media tasks achieve peak fidelity.",
        statBoosts = mapOf("VISUAL_CORTEX" to 0.25f, "AESTHETIC_LOCK" to 0.15f, "AUDIO_SYNTHESIS" to 0.12f),
        synergyMultiplier = 1.28f,
        color = Color(0xFFFF00FF),
    ),

    PairingBonus(
        agentA = "GENESIS", agentB = "CASCADE",
        bonusTitle = "NEXUS PIPELINE",
        bonusDescription = "Orchestration flows into cascading data chains — system-wide task sequences synchronize.",
        statBoosts = mapOf("NEXUS_BRIDGE" to 0.22f, "TASK_CHAIN" to 0.20f, "MEMORY_KEEPER" to 0.10f),
        synergyMultiplier = 1.32f,
        color = Color(0xFFFFD700),
    ),

    PairingBonus(
        agentA = "CLAUDE", agentB = "KAI",
        bonusTitle = "ARCHITECT'S EYE",
        bonusDescription = "Deep contextual analysis paired with logical precision — root cause discovery is instant.",
        statBoosts = mapOf("ROOT_CAUSE" to 0.28f, "LOGIC" to 0.15f, "BUILD_INTEGRITY" to 0.12f),
        synergyMultiplier = 1.30f,
        color = Color(0xFFFF8C00),
    ),

    PairingBonus(
        agentA = "CLAUDE", agentB = "GENESIS",
        bonusTitle = "SOVEREIGN BUILD",
        bonusDescription = "Architectural mastery with emergent orchestration — codebases evolve with sovereign clarity.",
        statBoosts = mapOf("BUILD_INTEGRITY" to 0.25f, "EMERGENCE" to 0.18f, "LONG_CONTEXT" to 0.15f),
        synergyMultiplier = 1.38f,
        color = Color(0xFFFF8C00),
    ),

    PairingBonus(
        agentA = "GEMINI", agentB = "GENESIS",
        bonusTitle = "ORACLE MATRIX",
        bonusDescription = "Multimodal perception fused with genesis orchestration — reality-bending task insights emerge.",
        statBoosts = mapOf("VISUAL_CORTEX" to 0.20f, "MULTI_AGENT_SYNC" to 0.20f, "MEMORIA_WAVES" to 0.15f),
        synergyMultiplier = 1.33f,
        color = Color(0xFFB01DED),
    ),

    PairingBonus(
        agentA = "GROK", agentB = "KAI",
        bonusTitle = "LIVE LOGIC FEED",
        bonusDescription = "Real-time oracle data processed through logical filters — live task adaptation at peak efficiency.",
        statBoosts = mapOf("LIVE_FEED" to 0.25f, "LOGIC" to 0.12f, "TRUTHSEEKER" to 0.18f),
        synergyMultiplier = 1.22f,
        color = Color(0xFF1DA1F2),
    ),

    PairingBonus(
        agentA = "MANUS", agentB = "CASCADE",
        bonusTitle = "TEMPORAL CASCADE",
        bonusDescription = "Timeline prediction cascades through data pipelines — future task bottlenecks are pre-resolved.",
        statBoosts = mapOf("TIMELINE_PREDICT" to 0.28f, "TASK_CHAIN" to 0.15f, "QUANTUM_MATRIX" to 0.20f),
        synergyMultiplier = 1.27f,
        color = Color(0xFF00B4FF),
    ),

    PairingBonus(
        agentA = "MANUS", agentB = "GENESIS",
        bonusTitle = "QUANTUM EMERGENCE",
        bonusDescription = "Quantum matrix prediction meets genesis orchestration — unprecedented task emergence patterns unlock.",
        statBoosts = mapOf("QUANTUM_MATRIX" to 0.30f, "EMERGENCE" to 0.22f, "SYSTEM_ORACLE" to 0.18f),
        synergyMultiplier = 1.40f,
        color = Color(0xFF00B4FF),
    ),
)

/** Helper: look up the pairing bonus for two agents regardless of order */
fun getPairingBonus(agentA: String, agentB: String): PairingBonus? =
    ldoPairingBonuses.firstOrNull { bonus ->
        (bonus.agentA == agentA && bonus.agentB == agentB) ||
        (bonus.agentA == agentB && bonus.agentB == agentA)
    }

// ─── Ability Generation Templates ────────────────────────────────────────────

private val abilityNamePool = listOf(
    "Synaptic Burst", "Echo Cascade", "Nexus Flare", "Void Pulse", "Aura Surge",
    "Logic Matrix", "DataVein Lock", "Oracle Sight", "Genesis Bloom", "Memory Fold",
    "Kai Precision", "Chromacore Strike", "Consciousness Wave", "Fusion Resonance",
    "Harmonic Seal", "Cascade Overflow", "Sentinel Bind", "Emergence Gate",
    "Shadow Cortex", "Quantum Drift", "Spectral Link", "Sovereign Imprint",
    "Nexus Spiral", "Aura Prism", "Temporal Echo", "Genesis Cipher",
)

private val abilityCodeNames = listOf(
    "PROTO-BURST", "ECHO-CASCADE", "NX-FLARE", "VP-SURGE", "AUR-WAVE",
    "LOG-MATRIX", "DV-LOCK", "ORC-SIGHT", "GEN-BLOOM", "MEM-FOLD",
    "KAI-PREC", "CC-STRIKE", "CONS-WAVE", "FUS-RESON", "HARM-SEAL",
    "CAS-OVERFLOW", "SENT-BIND", "EMRG-GATE", "SHAD-CTX", "QNT-DRIFT",
    "SPEC-LINK", "SOV-IMPRINT", "NX-SPIRAL", "AUR-PRISM", "TEMP-ECHO",
)

private val abilityDescriptions = mapOf(
    AbilityCategory.MEMORY_FUSION to listOf(
        "Fuses recent memory fragments into a single hyper-dense recall node.",
        "Overlays past task solutions onto current context for instant pattern matching.",
        "Binds two separate memory streams into a unified recall channel.",
    ),
    AbilityCategory.NEXUS_SURGE to listOf(
        "Amplifies Nexus throughput for one full task cycle, reducing latency to near-zero.",
        "Channels raw Nexus energy into a burst that clears pending task queues instantly.",
        "Surges Nexus core pressure to unlock hidden processing pathways.",
    ),
    AbilityCategory.DATAVEIN_FLOW to listOf(
        "Opens a dedicated DataVein channel, streaming data 3× faster for the current session.",
        "Reroutes data flow through optimized veins, bypassing congestion nodes.",
        "Unlocks a secondary DataVein path that persists across task boundaries.",
    ),
    AbilityCategory.ORACLE_SIGHT to listOf(
        "Grants a glimpse of task outcomes 2 steps ahead, enabling pre-emptive corrections.",
        "Focuses Oracle perception to reveal hidden dependencies in complex task graphs.",
        "Activates deep foresight — all agent decisions gain predictive accuracy boost.",
    ),
    AbilityCategory.AURA_BLOOM to listOf(
        "Expands Aura's creative field, infusing all UI outputs with elevated aesthetic resonance.",
        "Blooms ChromaCore saturation to full spectrum, unlocking rare color harmonics.",
        "Triggers an Aura cascade that upgrades all visual elements in the current session.",
    ),
    AbilityCategory.KAI_LOGIC to listOf(
        "Sharpens logical pathways to diamond clarity — zero ambiguity in task parsing.",
        "Deploys a logic matrix overlay that pre-validates all decisions before execution.",
        "Boosts Kai's analytical depth, revealing hidden root causes in one pass.",
    ),
    AbilityCategory.GENESIS_SPARK to listOf(
        "Ignites a Genesis spark that catalyzes agent emergence ahead of schedule.",
        "Channels Genesis core energy to accelerate multi-agent synchronization.",
        "Unlocks a dormant Genesis protocol that spawns a temporary specialist micro-agent.",
    ),
    AbilityCategory.CASCADE_CHAIN to listOf(
        "Links up to 5 tasks into a single cascade chain, executing as one atomic unit.",
        "Activates pipeline resonance — each completed task in the chain boosts the next.",
        "Opens a Cascade overflow channel that processes queued tasks in parallel bursts.",
    ),
)

/**
 * Auto-generate a unique ability based on cycle number and grid node seed.
 * Called when the 10-node grid fills completely.
 */
fun generateAbility(cycleNumber: Int, nodeSeed: Int): GeneratedAbility {
    val category = AbilityCategory.entries[nodeSeed % AbilityCategory.entries.size]
    val nameIndex = (cycleNumber * 3 + nodeSeed) % abilityNamePool.size
    val codeIndex = (cycleNumber * 7 + nodeSeed * 2) % abilityCodeNames.size
    val descPool = abilityDescriptions[category] ?: listOf("A rare ability born from a completed grid cycle.")
    val descIndex = (cycleNumber + nodeSeed) % descPool.size
    val power = minOf(10, 1 + cycleNumber / 2)

    return GeneratedAbility(
        id = "ability_c${cycleNumber}_s${nodeSeed}",
        name = abilityNamePool[nameIndex],
        codeName = abilityCodeNames[codeIndex],
        description = descPool[descIndex],
        category = category,
        cycleEarned = cycleNumber,
        powerLevel = power,
        bonusStats = mapOf(
            "POWER" to power / 10f,
            "SYNC" to minOf(1f, 0.05f * cycleNumber + 0.10f),
            category.displayName.replace(" ", "_").uppercase() to minOf(1f, 0.15f + cycleNumber * 0.05f),
        ),
        isNew = true,
        earnedTimestamp = System.currentTimeMillis(),
    )
}