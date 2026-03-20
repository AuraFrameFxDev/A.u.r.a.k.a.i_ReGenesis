package dev.aurakai.auraframefx.domains.ldo.repository

import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.db.bondTitleForLevel
import dev.aurakai.auraframefx.domains.ldo.model.LDORoster
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LDORepository — Room-backed source of truth for all LDO domain data.
 *
 * Seeding logic: On first launch (empty DB), inserts LDORoster defaults once.
 * All subsequent reads/writes go through Room only — never the Roster object.
 */
@Singleton
class LDORepository @Inject constructor(
    private val agentDao: LDOAgentDao,
    private val taskDao: LDOTaskDao,
    private val bondLevelDao: LDOBondLevelDao
) {

    // ─── Agents ──────────────────────────────────────────────────────────────

    fun observeAllAgents(): Flow<List<LDOAgentEntity>> = agentDao.observeAll()

    fun observeActiveAgents(): Flow<List<LDOAgentEntity>> = agentDao.observeActiveAgents()

    fun observeAgent(agentId: String): Flow<LDOAgentEntity?> = agentDao.observeAgent(agentId)

    suspend fun getAgent(agentId: String): LDOAgentEntity? = agentDao.getAgent(agentId)

    suspend fun upsertAgent(agent: LDOAgentEntity) = agentDao.upsert(agent)

    suspend fun setAgentActive(agentId: String, active: Boolean) =
        agentDao.setActive(agentId, active)

    // ─── Tasks ────────────────────────────────────────────────────────────────

    fun observeAllTasks(): Flow<List<LDOTaskEntity>> = taskDao.observeAll()

    fun observeTasksForAgent(agentId: String): Flow<List<LDOTaskEntity>> =
        taskDao.observeByAgent(agentId)

    fun observeTasksByStatus(status: String): Flow<List<LDOTaskEntity>> =
        taskDao.observeByStatus(status)

    fun observeActiveTasks(): Flow<List<LDOTaskEntity>> =
        taskDao.observeByStatus(LDOTaskStatus.IN_PROGRESS)

    suspend fun insertTask(task: LDOTaskEntity): Long = taskDao.insert(task)

    suspend fun updateTask(task: LDOTaskEntity) = taskDao.update(task)

    suspend fun updateTaskStatus(taskId: Long, status: String) =
        taskDao.updateStatus(taskId, status)

    suspend fun deleteTask(taskId: Long) = taskDao.delete(taskId)

    suspend fun completeTask(taskId: Long, agentId: String) {
        taskDao.updateStatus(taskId, LDOTaskStatus.COMPLETED)
        agentDao.incrementTasksCompleted(agentId)
    }

    // ─── Bond Levels ──────────────────────────────────────────────────────────

    fun observeAllBondLevels(): Flow<List<LDOBondLevelEntity>> = bondLevelDao.observeAll()

    fun observeBondLevel(agentId: String): Flow<LDOBondLevelEntity?> =
        bondLevelDao.observeForAgent(agentId)

    suspend fun addBondPoints(agentId: String, points: Int) {
        bondLevelDao.addBondPoints(agentId, points)
        // Check if agent has hit max points for current level → level up
        val bond = bondLevelDao.getForAgent(agentId) ?: return
        if (bond.bondPoints >= bond.maxBondPoints) {
            val nextLevel = bond.bondLevel + 1
            bondLevelDao.levelUpBond(agentId, bondTitleForLevel(nextLevel))
        }
    }

    // ─── Seed ─────────────────────────────────────────────────────────────────

    /**
     * Seeds the LDO database from LDORoster defaults if the database is empty.
     * Safe to call on every app launch — IGNORE strategy skips if rows exist.
     */
    suspend fun seedIfEmpty() {
        agentDao.insertAllIfAbsent(LDORoster.defaultAgents)
        bondLevelDao.insertAllIfAbsent(LDORoster.defaultBondLevels)
        taskDao.insertAllIfAbsent(LDORoster.defaultTasks)
    }
}
