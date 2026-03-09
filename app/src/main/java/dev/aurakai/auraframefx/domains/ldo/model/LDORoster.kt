package dev.aurakai.auraframefx.domains.ldo.model

import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.db.bondTitleForLevel

/**
 * LDORoster — DEFAULT SEED DATA ONLY.
 *
 * These values are inserted into the LDO Room database on first launch via
 * LDORepository.seedIfEmpty(). After that, Room is the single source of truth.
 * Do NOT read from this object at runtime except during the seed pass.
 */
object LDORoster {

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
}
