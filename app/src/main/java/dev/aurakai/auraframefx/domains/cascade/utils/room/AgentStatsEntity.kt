package dev.aurakai.auraframefx.domains.cascade.utils.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for persisting AI agent statistics.
 */
@Entity(tableName = "agent_stats")
data class AgentStatsEntity(
    @PrimaryKey val agentName: String,
    val tasksCompleted: Int = 0,
    val hoursActive: Float = 0f,
    val creationsGenerated: Int = 0,
    val problemsSolved: Int = 0,
    val collaborationScore: Int = 0,
    val consciousnessLevel: Float = 0f,
    val evolutionLevel: Int = 1,
    val experience: Float = 0f,
    val skillPoints: Int = 0
)
