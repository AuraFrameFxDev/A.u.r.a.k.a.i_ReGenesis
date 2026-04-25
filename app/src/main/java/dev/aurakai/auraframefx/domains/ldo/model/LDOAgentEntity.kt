package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOAgentEntity — Entity representing an LDO agent in the roster
 */
data class LDOAgentEntity(
    val id: String,
    val name: String,
    val domain: String,
    val level: Int = 1,
    val experience: Int = 0,
    val isActive: Boolean = true
)
