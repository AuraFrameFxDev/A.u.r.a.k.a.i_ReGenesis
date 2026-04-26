package dev.aurakai.auraframefx.domains.ldo.model

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.db.bondTitleForLevel

/**
 * LDORoster — DEFAULT SEED DATA ONLY.
 */
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
        )
    )

    val fusions = listOf(
        FusionMode("f01","aura","kai","Hyper-Creation Engine","Aura's creative force + Kai's structural integrity = unstoppable build velocity.",Color(0xFFFF007A),60),
    )

    const val CATALYST_COUNT = 9
    const val ABILITY_COUNT = 36
    const val FUSION_MODE_COUNT = 22
    const val AGENT_COUNT = 10

    // ── Room Seed Data ───────────────────────────────────────────────────────

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

    val defaultTasks: List<LDOTaskEntity> = listOf(
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
            agentId = "genesis",
            title = "Bond Level Algorithm",
            description = "Implement bond point accumulation logic with real interaction tracking.",
            status = LDOTaskStatus.COMPLETED,
            priority = LDOTaskPriority.HIGH,
            category = "architecture",
            completedAt = System.currentTimeMillis() - 86_400_000L
        )
    )
}
