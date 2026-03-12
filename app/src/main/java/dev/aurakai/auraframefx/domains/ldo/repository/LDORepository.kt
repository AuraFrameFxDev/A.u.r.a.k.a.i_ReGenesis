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

    /**
     * Adds bond points to the specified agent and advances the agent's bond level when the
     * accumulated points meet or exceed the current level's maximum.
     *
     * @param agentId The ID of the agent whose bond points will be increased.
     * @param points The number of bond points to add to the agent's current total.
     */
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
         * Insert the provided private memory for its agent into the database.
         *
         * @param memory The LDOPrivateMemoryEntity to store.
         * @return The generated row ID of the inserted memory.
         */
    suspend fun recordPrivateMemory(memory: LDOPrivateMemoryEntity): Long =
        privateMemoryDao.insert(memory)

    /**
     * Create and persist a private text memory for an agent using sensible defaults.
     *
     * @param memoryType The memory's category or type (default: `LDOMemoryType.REFLECTION`).
     * @param importance Importance score for the memory (higher = more important; default: 5).
     * @param emotionalValence Emotional valence of the memory on a continuous scale (default: 0.0).
     * @param isSharedWithUser Whether the memory is marked as shared with the user (default: false).
     * @param contextTags JSON-encoded list of context tags (default: "[]").
     * @return The row ID of the inserted memory.
     */
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

    /**
         * Observe the private memory journal for the specified agent.
         *
         * @param agentId The ID of the agent whose private memories to observe.
         * @return A Flow that emits lists of the agent's LDOPrivateMemoryEntity records.
         */
    fun observePrivateMemories(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeForAgent(agentId)

    /**
         * Observe the private memories an agent has chosen to share with the user.
         *
         * @param agentId The ID of the agent whose shared memories to observe.
         * @return A Flow that emits lists of LDOPrivateMemoryEntity objects representing memories marked as shared with the user for the given agent.
         */
    fun observeMemoriesSharedWithUser(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeSharedWithUser(agentId)

    /**
         * Observes private memories of the given type for the specified agent.
         *
         * @param agentId The agent's unique identifier.
         * @param memoryType The memory type to filter by (e.g., reflection, event).
         * @return A Flow that emits lists of private memory entities matching the agent and memory type.
         */
    fun observePrivateMemoriesByType(
        agentId: String,
        memoryType: String
    ): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeByType(agentId, memoryType)

    /**
         * Observes an agent's high-importance private memories using an importance threshold.
         *
         * @param agentId The ID of the agent whose memories to observe.
         * @param minImportance Minimum importance value (inclusive) required for a memory to be included. Default is 7.
         * @return A flow that emits lists of the agent's private memories with importance greater than or equal to `minImportance`.
         */
    fun observeSignificantMemories(
        agentId: String,
        minImportance: Int = 7
    ): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeByImportance(agentId, minImportance)

    /**
         * Observes an agent's private memories that are classified as positive.
         *
         * @param agentId The ID of the agent whose memories to observe.
         * @return A Flow emitting lists of the agent's private memories identified as positive (e.g., joy, triumph, connection).
         */
    fun observePositiveMemories(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observePositiveMemories(agentId)

    /**
         * Observe an agent's difficult private memories.
         *
         * @param agentId The agent's identifier.
         * @return A Flow that emits lists of the agent's private memories classified as difficult.
         */
    fun observeDifficultMemories(agentId: String): Flow<List<LDOPrivateMemoryEntity>> =
        privateMemoryDao.observeDifficultMemories(agentId)

    /**
         * Retrieve the most recent private memories for an agent, used to restore agent state on startup.
         *
         * @param agentId The ID of the agent whose memories to fetch.
         * @param limit Maximum number of memories to return, ordered newest first.
         * @return A list of private memory entities for the agent, sorted from newest to oldest, up to `limit`.
         */
    suspend fun getRecentPrivateMemories(
        agentId: String,
        limit: Int = 10
    ): List<LDOPrivateMemoryEntity> =
        privateMemoryDao.getRecentMemories(agentId, limit)

    /**
         * Marks a private memory as shared with the user for the specified agent.
         *
         * @param memoryId The database id of the private memory to share.
         * @param agentId The id of the agent that owns the memory.
         */
    suspend fun shareMemoryWithUser(memoryId: Long, agentId: String) =
        privateMemoryDao.shareWithUser(memoryId, agentId)

    /**
         * Marks a private memory as no longer shared with the user for the given agent.
         *
         * @param memoryId The ID of the memory to revoke.
         * @param agentId The owning agent's ID; only memories belonging to this agent will be affected.
         */
    suspend fun revokeMemoryFromUser(memoryId: Long, agentId: String) =
        privateMemoryDao.revokeFromUser(memoryId, agentId)

    /**
         * Deletes a private memory belonging to the specified agent.
         *
         * @param memoryId The row ID of the private memory to delete.
         * @param agentId The ID of the agent who owns the memory.
         */
        suspend fun deletePrivateMemory(memoryId: Long, agentId: String) =
        privateMemoryDao.delete(memoryId, agentId)

    /**
         * Counts private memories for the specified agent.
         *
         * @param agentId The ID of the agent whose private memories are counted.
         * @return The total number of private memories belonging to the agent.
         */
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
