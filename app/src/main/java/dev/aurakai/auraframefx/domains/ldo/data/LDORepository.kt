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
        taskDao.delete(taskId)
    }

    suspend fun addBondPoints(agentId: String, points: Int) {
        bondLevelDao.addBondPoints(agentId, points)
    }

    suspend fun setAgentActive(agentId: String, active: Boolean) {
        agentDao.setActive(agentId, active)
    }

    suspend fun seedIfEmpty() {
        // Simplified seeding for now using rich defaults from LDORoster
        LDORoster.defaultAgents.forEach { agent ->
            agentDao.upsert(agent)
        }
        LDORoster.defaultBondLevels.forEach { bond ->
            bondLevelDao.insert(bond)
        }
        LDORoster.defaultTasks.forEach { task ->
            taskDao.insert(task)
        }
    }
}
