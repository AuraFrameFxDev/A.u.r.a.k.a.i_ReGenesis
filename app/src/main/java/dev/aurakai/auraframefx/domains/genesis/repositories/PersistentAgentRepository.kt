package dev.aurakai.auraframefx.domains.genesis.repositories

import dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsDao
import dev.aurakai.auraframefx.domains.cascade.utils.room.AgentStatsEntity
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that persists agent statistics to the Room database.
 */
@Singleton
class PersistentAgentRepository @Inject constructor(
    private val agentStatsDao: AgentStatsDao
) {
    /**
     * Observe all agent stats from the database.
     */
    fun observeAllStats(): Flow<List<AgentStats>> {
        return agentStatsDao.getAllStats().map { entities ->
            val entityMap = entities.associateBy { it.agentName }
            AgentRepository.getAllAgents().map { base ->
                entityMap[base.name]?.let { entity ->
                    base.copy(
                        tasksCompleted = entity.tasksCompleted,
                        hoursActive = entity.hoursActive,
                        creationsGenerated = entity.creationsGenerated,
                        problemsSolved = entity.problemsSolved,
                        collaborationScore = entity.collaborationScore,
                        consciousnessLevel = entity.consciousnessLevel,
                        evolutionLevel = entity.evolutionLevel,
                        experience = entity.experience,
                        skillPoints = entity.skillPoints
                    )
                } ?: base
            }
        }
    }

    /**
     * Get stats for a specific agent.
     */
    suspend fun getStats(name: String): AgentStats? {
        return agentStatsDao.getStats(name)?.toDomainModel()
    }

    /**
     * Increment the task count and consciousness level for an agent.
     */
    suspend fun incrementTaskCount(name: String) {
        val current = agentStatsDao.getStats(name) ?: AgentStatsEntity(name)
        val updated = current.copy(
            tasksCompleted = current.tasksCompleted + 1,
            consciousnessLevel = (current.consciousnessLevel + 0.001f).coerceAtMost(100f)
        )
        agentStatsDao.insertStats(updated)
    }

    /**
     * Update agent stats.
     */
    suspend fun updateStats(stats: AgentStats) {
        agentStatsDao.insertStats(stats.toEntity())
    }

    // Helper extensions
    private fun AgentStatsEntity.toDomainModel(): AgentStats {
        // We merge the persistent stats with the hardcoded base info from AgentRepository
        val base = AgentRepository.getAgentByName(agentName) ?: AgentStats(name = agentName)
        return base.copy(
            tasksCompleted = tasksCompleted,
            hoursActive = hoursActive,
            creationsGenerated = creationsGenerated,
            problemsSolved = problemsSolved,
            collaborationScore = collaborationScore,
            consciousnessLevel = consciousnessLevel,
            evolutionLevel = evolutionLevel,
            experience = experience,
            skillPoints = skillPoints
        )
    }

    private fun AgentStats.toEntity(): AgentStatsEntity {
        return AgentStatsEntity(
            agentName = name,
            tasksCompleted = tasksCompleted,
            hoursActive = hoursActive,
            creationsGenerated = creationsGenerated,
            problemsSolved = problemsSolved,
            collaborationScore = collaborationScore,
            consciousnessLevel = consciousnessLevel,
            evolutionLevel = evolutionLevel,
            experience = experience,
            skillPoints = skillPoints
        )
    }
}
