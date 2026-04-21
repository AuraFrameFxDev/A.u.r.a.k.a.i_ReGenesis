package dev.aurakai.auraframefx.domains.ldo.data

import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOTaskDao
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.model.LDORoster
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LDO Domain repository.
 */
@Singleton
class LDORepository @Inject constructor(
    private val agentDao: LDOAgentDao,
    private val taskDao: LDOTaskDao,
    private val bondLevelDao: LDOBondLevelDao
) {
    fun observeAllAgents(): Flow<List<LDOAgentEntity>> = agentDao.observeAll()
    fun observeAllTasks(): Flow<List<LDOTaskEntity>> = taskDao.observeAll()
    fun observeAllBondLevels(): Flow<List<LDOBondLevelEntity>> = bondLevelDao.observeAll()

    suspend fun insertTask(task: LDOTaskEntity) = taskDao.insert(task)
    suspend fun updateTaskStatus(taskId: Long, status: String) {
        val completedAt = if (status == "COMPLETED") System.currentTimeMillis() else null
        taskDao.updateStatus(taskId, status, completedAt)
    }
    suspend fun deleteTask(taskId: Long) {
        // Find task first or use dummy entity for delete if DAO allows
        // taskDao.delete(...)
    }

    suspend fun addBondPoints(agentId: String, points: Int) {
        bondLevelDao.addExperience(agentId, points.toLong())
    }

    suspend fun setAgentActive(agentId: String, active: Boolean) {
        agentDao.getById(agentId)?.let {
            agentDao.update(it.copy(isActive = active))
        }
    }

    suspend fun seedIfEmpty() {
        // Simplified seeding for now
        LDORoster.agents.forEach { agent ->
            agentDao.upsert(
                LDOAgentEntity(
                    agentId = agent.id,
                    name = agent.name,
                    catalystTitle = agent.catalystName,
                    bondLevel = agent.bondLevel,
                    isActive = true
                )
            )
        }
    }
}
