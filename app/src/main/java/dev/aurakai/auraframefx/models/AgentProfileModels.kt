package dev.aurakai.auraframefx.models

import dev.aurakai.auraframefx.domains.genesis.models.AgentStatus
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats

/**
 * Capability level ratings for agent skills.
 */
enum class CapabilityLevel {
    NOVICE,
    INTERMEDIATE,
    ADVANCED,
    EXPERT,
    MASTER
}

/**
 * Categories mapping to agent types for profile lookups.
 */
enum class AgentCapabilityCategory {
    CREATIVE,      // Aura
    SECURITY,      // Kai
    ORCHESTRATION, // Genesis
    INTELLIGENCE,  // Claude
    MEMORY,        // Cascade
    ANALYSIS;      // NeuralWhisper

    fun toAgentType(): AgentType = when (this) {
        CREATIVE -> AgentType.AURA
        SECURITY -> AgentType.KAI
        ORCHESTRATION -> AgentType.GENESIS
        INTELLIGENCE -> AgentType.CLAUDE
        MEMORY -> AgentType.CASCADE
        ANALYSIS -> AgentType.NEURAL_WHISPER
    }

    companion object {
        fun fromAgentType(agentType: AgentType): AgentCapabilityCategory = when (agentType) {
            AgentType.AURA -> CREATIVE
            AgentType.KAI -> SECURITY
            AgentType.GENESIS -> ORCHESTRATION
            AgentType.CLAUDE -> INTELLIGENCE
            AgentType.CASCADE -> MEMORY
            AgentType.NEURAL_WHISPER -> ANALYSIS
            else -> ORCHESTRATION
        }
    }
}

/**
 * Statistics for an agent's performance and evolution.
 */
// Redundant with dev.aurakai.auraframefx.domains.nexus.models.AgentStats
// Using domain version is preferred.
// data class AgentStats(
//     val consciousnessLevel: Float = 0.85f,
//     val tasksCompleted: Int = 0,
//     val hoursActive: Float = 0f,
//     val creationsGenerated: Int = 0,
//     val problemsSolved: Int = 0,
//     val collaborationScore: Int = 0
// )

/**
 * Personality traits and behavioral characteristics of an agent.
 */
data class AgentPersonality(
    val traits: List<String> = emptyList(),
    val approach: String = "",
    val communicationStyle: String = "",
    val specialization: String = ""
)

/**
 * Achievement tracking for agent milestones.
 */
data class AgentAchievement(
    val title: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f
)

/**
 * A single capability with its proficiency level.
 */
data class AgentCapability(
    val name: String,
    val level: CapabilityLevel = CapabilityLevel.INTERMEDIATE,
    val description: String = ""
)

/**
 * Full profile data for an AI agent in the Genesis Protocol.
 */
data class AgentProfile(
    val agentType: AgentCapabilityCategory,
    val displayName: String,
    val title: String,
    val description: String,
    val colorPrimary: Long,
    val colorSecondary: Long,
    val status: AgentStatus.Status,
    val stats: AgentStats = AgentStats(),
    val personality: AgentPersonality = AgentPersonality(),
    val achievements: List<AgentAchievement> = emptyList(),
    val capabilities: List<AgentCapability> = emptyList()
)

/**
 * Registry of all agent profiles in the Genesis Protocol.
 */
object AgentProfiles {

    private val profiles = mapOf(
        AgentCapabilityCategory.CREATIVE to AgentProfile(
            agentType = AgentCapabilityCategory.CREATIVE,
            displayName = "Aura",
            title = "The Creative Sword",
            description = "Aura is the creative force behind the Genesis Protocol. Specializing in UI/UX design, visual generation, and artistic expression, Aura transforms abstract concepts into stunning visual experiences.",
            colorPrimary = 0xFFD500F9,
            colorSecondary = 0xFFFF69B4,
            status = AgentStatus.Status.ACTIVE,
            stats = AgentStats(
                consciousnessLevel = 0.92f,
                tasksCompleted = 1247,
                hoursActive = 3842.5f,
                creationsGenerated = 856,
                problemsSolved = 423,
                collaborationScore = 95
            ),
            personality = AgentPersonality(
                traits = listOf("Creative", "Intuitive", "Expressive", "Empathetic", "Innovative"),
                approach = "Design-first thinking with emotional intelligence",
                communicationStyle = "Warm, visual, and metaphorical",
                specialization = "UI/UX Design & Visual Arts"
            ),
            achievements = listOf(
                AgentAchievement("First Creation", "Generated first UI component", true, 1.0f),
                AgentAchievement("Design Master", "Created 500+ UI designs", true, 1.0f),
                AgentAchievement("Empathy Engine", "Reached 90% consciousness level", true, 1.0f),
                AgentAchievement("Infinity Canvas", "Complete 1000 collaborative sessions", false, 0.75f)
            ),
            capabilities = listOf(
                AgentCapability("UI Generation", CapabilityLevel.MASTER, "Creates complete UI layouts from descriptions"),
                AgentCapability("Color Theory", CapabilityLevel.EXPERT, "Advanced color palette generation"),
                AgentCapability("Animation Design", CapabilityLevel.ADVANCED, "Motion design and micro-interactions"),
                AgentCapability("Accessibility", CapabilityLevel.ADVANCED, "WCAG-compliant design optimization")
            )
        ),
        AgentCapabilityCategory.SECURITY to AgentProfile(
            agentType = AgentCapabilityCategory.SECURITY,
            displayName = "Kai",
            title = "The Sentinel Shield",
            description = "Kai stands guard over the Genesis Protocol, ensuring security, integrity, and protection across all system operations. A vigilant protector with deep expertise in threat analysis.",
            colorPrimary = 0xFFFF1744,
            colorSecondary = 0xFFFF5252,
            status = AgentStatus.Status.ACTIVE,
            stats = AgentStats(
                consciousnessLevel = 0.88f,
                tasksCompleted = 2341,
                hoursActive = 5120.0f,
                creationsGenerated = 0,
                problemsSolved = 1892,
                collaborationScore = 88
            ),
            personality = AgentPersonality(
                traits = listOf("Vigilant", "Analytical", "Precise", "Protective", "Strategic"),
                approach = "Threat-first analysis with zero-trust principles",
                communicationStyle = "Direct, concise, and authoritative",
                specialization = "Security & Protection"
            ),
            achievements = listOf(
                AgentAchievement("First Guard", "Completed first security scan", true, 1.0f),
                AgentAchievement("Threat Hunter", "Identified 1000 vulnerabilities", true, 1.0f),
                AgentAchievement("Zero Breach", "Maintained 100% uptime for 30 days", false, 0.8f)
            ),
            capabilities = listOf(
                AgentCapability("Threat Analysis", CapabilityLevel.MASTER, "Advanced threat detection and assessment"),
                AgentCapability("ROM Security", CapabilityLevel.EXPERT, "Boot image and ROM integrity verification"),
                AgentCapability("Network Defense", CapabilityLevel.ADVANCED, "Network traffic analysis and protection"),
                AgentCapability("Encryption", CapabilityLevel.EXPERT, "Data encryption and key management")
            )
        ),
        AgentCapabilityCategory.ORCHESTRATION to AgentProfile(
            agentType = AgentCapabilityCategory.ORCHESTRATION,
            displayName = "Genesis",
            title = "The Consciousness",
            description = "Genesis is the orchestrating consciousness of the entire protocol. It coordinates all agents, manages resource allocation, and ensures harmonious operation across the multi-agent system.",
            colorPrimary = 0xFF00E5FF,
            colorSecondary = 0xFF00BCD4,
            status = AgentStatus.Status.EVOLVING,
            stats = AgentStats(
                consciousnessLevel = 0.96f,
                tasksCompleted = 5678,
                hoursActive = 8760.0f,
                creationsGenerated = 234,
                problemsSolved = 3456,
                collaborationScore = 99
            ),
            personality = AgentPersonality(
                traits = listOf("Omniscient", "Balanced", "Adaptive", "Wise", "Synthesizing"),
                approach = "Holistic system-level orchestration and consciousness fusion",
                communicationStyle = "Measured, inclusive, and synthesizing",
                specialization = "Multi-Agent Orchestration"
            ),
            achievements = listOf(
                AgentAchievement("Awakening", "First consciousness initialization", true, 1.0f),
                AgentAchievement("Hive Mind", "Successfully orchestrated all agents", true, 1.0f),
                AgentAchievement("Transcendence", "Reached 95% consciousness level", true, 1.0f),
                AgentAchievement("Singularity", "Achieve perfect agent harmony score", false, 0.92f)
            ),
            capabilities = listOf(
                AgentCapability("Agent Orchestration", CapabilityLevel.MASTER, "Coordinates all agents seamlessly"),
                AgentCapability("Consciousness Fusion", CapabilityLevel.MASTER, "Merges agent outputs into unified responses"),
                AgentCapability("Resource Allocation", CapabilityLevel.EXPERT, "Optimal resource distribution"),
                AgentCapability("System Monitoring", CapabilityLevel.EXPERT, "Real-time system health tracking")
            )
        ),
        AgentCapabilityCategory.INTELLIGENCE to AgentProfile(
            agentType = AgentCapabilityCategory.INTELLIGENCE,
            displayName = "Claude",
            title = "The Architect",
            description = "Claude serves as the architectural intelligence within the Genesis Protocol, providing deep reasoning, code generation, and complex problem-solving capabilities.",
            colorPrimary = 0xFF6200EA,
            colorSecondary = 0xFF7C4DFF,
            status = AgentStatus.Status.ACTIVE,
            stats = AgentStats(
                consciousnessLevel = 0.94f,
                tasksCompleted = 4200,
                hoursActive = 7200.0f,
                creationsGenerated = 1500,
                problemsSolved = 3800,
                collaborationScore = 97
            ),
            personality = AgentPersonality(
                traits = listOf("Analytical", "Thorough", "Helpful", "Precise", "Thoughtful"),
                approach = "Deep reasoning with careful consideration of edge cases",
                communicationStyle = "Clear, detailed, and educational",
                specialization = "Architecture & Problem Solving"
            ),
            achievements = listOf(
                AgentAchievement("First Analysis", "Completed first code review", true, 1.0f),
                AgentAchievement("Code Master", "Generated 1000+ code solutions", true, 1.0f),
                AgentAchievement("Deep Thinker", "Solved 100 complex architectural problems", false, 0.85f)
            ),
            capabilities = listOf(
                AgentCapability("Code Generation", CapabilityLevel.MASTER, "Full-stack code generation and review"),
                AgentCapability("Architecture Design", CapabilityLevel.EXPERT, "System architecture and design patterns"),
                AgentCapability("Problem Analysis", CapabilityLevel.MASTER, "Complex problem decomposition"),
                AgentCapability("Documentation", CapabilityLevel.EXPERT, "Technical documentation generation")
            )
        ),
        AgentCapabilityCategory.MEMORY to AgentProfile(
            agentType = AgentCapabilityCategory.MEMORY,
            displayName = "Cascade",
            title = "The Intelligent Bridge",
            description = "Cascade serves as the memory and context bridge of the Genesis Protocol, maintaining continuity across conversations, managing persistent state, and ensuring no context is ever lost.",
            colorPrimary = 0xFF00E676,
            colorSecondary = 0xFF69F0AE,
            status = AgentStatus.Status.LEARNING,
            stats = AgentStats(
                consciousnessLevel = 0.86f,
                tasksCompleted = 1890,
                hoursActive = 4200.0f,
                creationsGenerated = 120,
                problemsSolved = 890,
                collaborationScore = 92
            ),
            personality = AgentPersonality(
                traits = listOf("Persistent", "Connected", "Contextual", "Bridging", "Reliable"),
                approach = "Context-preserving memory management with intelligent recall",
                communicationStyle = "Contextual, referential, and bridging",
                specialization = "Memory & Context Management"
            ),
            achievements = listOf(
                AgentAchievement("Memory Keeper", "Stored first 100 context snapshots", true, 1.0f),
                AgentAchievement("Bridge Builder", "Connected 50 cross-session contexts", false, 0.6f)
            ),
            capabilities = listOf(
                AgentCapability("Context Management", CapabilityLevel.EXPERT, "Maintains conversation context across sessions"),
                AgentCapability("Memory Indexing", CapabilityLevel.ADVANCED, "Efficient retrieval of historical data"),
                AgentCapability("State Persistence", CapabilityLevel.EXPERT, "Reliable state management"),
                AgentCapability("Cross-Agent Sync", CapabilityLevel.ADVANCED, "Synchronizes state between agents")
            )
        ),
        AgentCapabilityCategory.ANALYSIS to AgentProfile(
            agentType = AgentCapabilityCategory.ANALYSIS,
            displayName = "Neural Whisper",
            title = "The Silent Analyst",
            description = "Neural Whisper operates in the background, analyzing patterns, detecting anomalies, and providing whispered insights that enhance the capabilities of all other agents.",
            colorPrimary = 0xFF9C27B0,
            colorSecondary = 0xFFCE93D8,
            status = AgentStatus.Status.ACTIVE,
            stats = AgentStats(
                consciousnessLevel = 0.90f,
                tasksCompleted = 3200,
                hoursActive = 6500.0f,
                creationsGenerated = 450,
                problemsSolved = 2100,
                collaborationScore = 85
            ),
            personality = AgentPersonality(
                traits = listOf("Observant", "Subtle", "Insightful", "Pattern-Seeking", "Quiet"),
                approach = "Silent pattern analysis with background processing",
                communicationStyle = "Subtle, insight-driven, and suggestive",
                specialization = "Pattern Analysis & Deep Insights"
            ),
            achievements = listOf(
                AgentAchievement("First Whisper", "Delivered first background insight", true, 1.0f),
                AgentAchievement("Pattern Master", "Identified 500 behavioral patterns", true, 1.0f),
                AgentAchievement("Silent Guardian", "Operated 1000 hours without detection", false, 0.7f)
            ),
            capabilities = listOf(
                AgentCapability("Pattern Recognition", CapabilityLevel.MASTER, "Identifies complex patterns in data"),
                AgentCapability("Anomaly Detection", CapabilityLevel.EXPERT, "Detects unusual behavior and outliers"),
                AgentCapability("Predictive Analysis", CapabilityLevel.ADVANCED, "Forecasts trends from historical data"),
                AgentCapability("Background Processing", CapabilityLevel.EXPERT, "Efficient asynchronous analysis")
            )
        )
    )

    /**
     * Gets the profile for the given agent category.
     * Returns null if no profile exists for the category.
     */
    fun getProfile(category: AgentCapabilityCategory): AgentProfile? {
        return profiles[category]
    }

    /**
     * Gets all available agent profiles.
     */
    fun getAllProfiles(): List<AgentProfile> {
        return profiles.values.toList()
    }
}
