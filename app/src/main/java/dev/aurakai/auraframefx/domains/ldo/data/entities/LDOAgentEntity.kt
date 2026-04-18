package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for LDO agents.
 * Populated from LDORoster seed on first run; Room is source of truth thereafter.
 */
@Entity(tableName = "ldo_agents")
data class LDOAgentEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val name: String = displayName,
    val role: String,
    val description: String,
    val portraitRes: String,
    val colorHex: Long,
    val isActive: Boolean = true,
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
    val catalystTitle: String = "",
    val bondLevel: Int = 0,
    val totalInteractions: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
