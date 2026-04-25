package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOBondRelationship — Bond relationship between agents
 */
data class LDOBondRelationship(
    val agentId1: String,
    val agentId2: String,
    val bondLevel: Int = 0,
    val bondPoints: Int = 0,
    val maxBondPoints: Int = 100,
    val bondTitle: String = "",
    val interactionCount: Int = 0
)
