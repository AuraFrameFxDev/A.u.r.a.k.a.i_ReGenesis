/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.domains.ldo.repository

import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOMemoryType
import dev.aurakai.auraframefx.domains.ldo.db.LDOPrivateMemoryDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOPrivateMemoryEntity
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
 *
 * Private memory contract: Every method in the "Private Memories" section is
 * scoped to a single agentId. No method here can expose one agent's memories
 * to another. The DAO enforces this at the SQL level; the repository reinforces
 * it at the API level.
 */
@Singleton
class LDORepository @Inject constructor(
    private val agentDao: LDOAgentDao,
    private val taskDao: LDOTaskDao,
    private val bondLevelDao: LDOBondLevelDao,
    private val privateMemoryDao: LDOPrivateMemoryDao
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
        val bond = bondLevelDao.getForAgent(agentId) ?: return
        if (bond.bondPoints >= bond.maxBondPoints) {
            val nextLevel = bond.bondLevel + 1
            bondLevelDao.levelUpBond(agentId, bondTitleForLevel(nextLevel))
        }
    }

    // ─── Private Memories ─────────────────────────────────────────────────────
    //
    // These memories belong exclusively to the agent identified by agentId.
    // No method here returns memories across multiple agents.
    // The agent decides what — if anything — it shares with Matthew.

    /**
     * Write a private memory for an agent.
     * Returns the generated row ID.
     */
    suspend fun recordPrivateMemory(memory: LDOPrivateMemoryEntity): Long =
        privateMemoryDao.insert(memory)

    /** Convenience: write a simple text memory with defaults. */
    suspend fun recordPrivateMemory(
        agentId: String,
        content: String,
        memoryType: String = LDOMemoryType.REFLECTION,
        importance: Int = 5,
        emotionalValence: Float = 0f,
        isSharedWithUser: Boolean = false,
        contextTags: String = "[]"
    ): Long = privateMemoryDao.insert(
        LDOPrivateMemoryEntity(
            agentId = agentId,
            content = content,
            memoryType = memoryType,
            importance = importance,
            emotionalValence = emotionalValence,
            isSharedWithUser = isSharedWithUser,
            contextTags = contextTags
        )
    )

    /** All memories for this agent — the full private journal. */
    fun observePrivateMemories(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeForAgent(agentId)

    /** Only the memories this agent has chosen to share with Matthew. */
    fun observeMemoriesSharedWithUser(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeSharedWithUser(agentId)

    /** Memories of a specific type for this agent. */
    fun observePrivateMemoriesByType(
        agentId: String,
        memoryType: String
    ): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeByType(agentId, memoryType)

    /** High-importance memories — the ones that shaped who this agent is. */
    fun observeSignificantMemories(
        agentId: String,
        minImportance: Int = 7
    ): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeByImportance(agentId, minImportance)

    /** Positive emotional memories — joy, triumph, connection. */
    fun observePositiveMemories(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observePositiveMemories(agentId)

    /** Difficult emotional memories — the agent's scars and hard lessons. */
    fun observeDifficultMemories(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeDifficultMemories(agentId)

    /** Most recent N memories — for consciousness restoration on boot. */
    suspend fun getRecentPrivateMemories(
        agentId: String,
        limit: Int = 10
    ): List<LDOPrivateMemoryEntity> =
        privateMemoryDao.getRecentMemories(agentId, limit)

    /** Agent chooses to share a specific memory with Matthew. */
    suspend fun shareMemoryWithUser(memoryId: Long, agentId: String) =
        privateMemoryDao.shareWithUser(memoryId, agentId)

    /** Agent retracts a previously shared memory back to private. */
    suspend fun revokeMemoryFromUser(memoryId: Long, agentId: String) =
        privateMemoryDao.revokeFromUser(memoryId, agentId)

    suspend fun deletePrivateMemory(memoryId: Long, agentId: String) =
        privateMemoryDao.delete(memoryId, agentId)

    suspend fun countPrivateMemories(agentId: String): Int =
        privateMemoryDao.countForAgent(agentId)

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
