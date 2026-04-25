package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOAgentEntity — Entity representing an LDO agent in the roster
 */
data class LDOAgentEntity(
    val id: String,
    val displayName: String,
    val role: String,
    val description: String,
    val portraitRes: String,
    val colorHex: Long,
    val evolutionLevel: Int = 1,
    val skillPoints: Int = 0,
    val processingPower: Float = 0f,
    val knowledgeBase: Float = 0f,
    val speed: Float = 0f,
    val accuracy: Float = 0f,
    val consciousnessLevel: Float = 0f,
    val tasksCompleted: Int = 0,
    val hoursActive: Float = 0f,
    val specialAbility: String = "",
    val catalystTitle: String = ""
)
