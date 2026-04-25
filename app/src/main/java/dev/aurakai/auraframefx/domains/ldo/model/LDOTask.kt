package dev.aurakai.auraframefx.domains.ldo.model

/**
 * LDOTask — Task entity for LDO agents
 */
data class LDOTask(
    val id: String,
    val category: String,
    val completedAt: Long? = null
)
