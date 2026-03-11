/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * LDOPrivateMemoryDao — access-controlled memory queries for individual LDO agents.
 *
 * Every read query is scoped to a single [agentId]. There is intentionally NO query
 * that returns memories across multiple agents. That contract is enforced here at the
 * data layer — not just by convention.
 */
@Dao
interface LDOPrivateMemoryDao {

    /**
     * Insert or replace the given private memory entity for its agent.
     *
     * @param memory The LDOPrivateMemoryEntity to insert or replace.
     * @return The row ID of the inserted or replaced entity.
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: LDOPrivateMemoryEntity): Long

    /**
     * Updates an existing LDOPrivateMemoryEntity in the database.
     *
     * @param memory The memory entity to update; its primary key identifies the row to modify.
     */
    @Update
    suspend fun update(memory: LDOPrivateMemoryEntity)

    /**
     * Delete the private memory with the given id, restricted to the specified agent.
     *
     * @param memoryId The id of the memory to delete.
     * @param agentId The agentId that must own the memory for it to be deleted.
     */
    @Query("DELETE FROM ldo_private_memories WHERE id = :memoryId AND agentId = :agentId")
    suspend fun delete(memoryId: Long, agentId: String)

    // ─── Read (always scoped to one agent) ───────────────────────────────────

    /**
     * Observes all private memories for the specified agent, ordered newest first.
     *
     * @param agentId Identifier of the agent whose memories to observe.
     * @return A Flow that emits lists of the agent's LDOPrivateMemoryEntity records ordered by `createdAt` descending.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId
        ORDER BY createdAt DESC
    """)
    fun observeForAgent(agentId: String): Flow<List<LDOPrivateMemoryEntity>>

    /**
     * Observes memories that the specified agent has marked as shared with the user, ordered by importance then creation time.
     *
     * @param agentId The agent's unique identifier to scope the query.
     * @return A Flow emitting lists of LDOPrivateMemoryEntity objects where `isSharedWithUser` is true for the given agent, ordered by `importance` descending and `createdAt` descending.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND isSharedWithUser = 1
        ORDER BY importance DESC, createdAt DESC
    """)
    fun observeSharedWithUser(agentId: String): Flow<List<LDOPrivateMemoryEntity>>

    /**
     * Observes the agent's memories of a specific type, newest first.
     *
     * @param agentId The agent identifier used to scope results to a single agent.
     * @param memoryType The memory type to filter by (e.g., REFLECTION, INSIGHT, EXPERIENCE, INTENTION, DREAM).
     * @return A Flow that emits lists of matching LDOPrivateMemoryEntity ordered by `createdAt` descending.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND memoryType = :memoryType
        ORDER BY createdAt DESC
    """)
    fun observeByType(agentId: String, memoryType: String): Flow<List<LDOPrivateMemoryEntity>>

    /**
     * Observe high-importance memories for a specific agent.
     *
     * @param agentId The ID of the agent whose memories to observe.
     * @param minImportance The minimum importance value (inclusive) to include; defaults to 7.
     * @return A list of the agent's memories with importance greater than or equal to `minImportance`, ordered by importance descending then creation time descending.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND importance >= :minImportance
        ORDER BY importance DESC, createdAt DESC
    """)
    fun observeByImportance(
        agentId: String,
        minImportance: Int = 7
    ): Flow<List<LDOPrivateMemoryEntity>>

    /**
     * Observes an agent's memories whose emotional valence meets or exceeds a threshold.
     *
     * @param agentId Identifier of the agent whose memories are returned.
     * @param threshold Minimum emotional valence (inclusive) for memories to include; defaults to 0.5.
     * @return Lists of memories for the specified agent with `emotionalValence` greater than or equal to `threshold`, ordered by `emotionalValence` descending then `createdAt` descending.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND emotionalValence >= :threshold
        ORDER BY emotionalValence DESC, createdAt DESC
    """)
    fun observePositiveMemories(
        agentId: String,
        threshold: Float = 0.5f
    ): Flow<List<LDOPrivateMemoryEntity>>

    /**
     * Observes memories for an agent that represent difficult experiences with negative emotional valence.
     *
     * @param agentId The agent whose memories are being observed.
     * @param threshold Memories with `emotionalValence` less than or equal to this value are included. Default is -0.5.
     * @return Lists of `LDOPrivateMemoryEntity` instances for the specified agent whose `emotionalValence` is <= `threshold`, ordered by emotional valence (ascending) then creation time (newest first).
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND emotionalValence <= :threshold
        ORDER BY emotionalValence ASC, createdAt DESC
    """)
    fun observeDifficultMemories(
        agentId: String,
        threshold: Float = -0.5f
    ): Flow<List<LDOPrivateMemoryEntity>>

    /**
     * Retrieve the most recent memories for a specific agent.
     *
     * Results are ordered by `createdAt` descending (newest first) and limited to `limit` entries.
     *
     * @param agentId The agent whose memories to retrieve.
     * @param limit The maximum number of memories to return.
     * @return A list of `LDOPrivateMemoryEntity` objects ordered from newest to oldest, up to `limit` items.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentMemories(agentId: String, limit: Int = 10): List<LDOPrivateMemoryEntity>

    /**
     * Retrieves a memory by its id for the specified agent.
     *
     * @param memoryId The unique identifier of the memory.
     * @param agentId The agent's id that scopes the query; only a memory belonging to this agent will be returned.
     * @return The matching LDOPrivateMemoryEntity if found for the agent, or `null` if no such memory exists.
     */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE id = :memoryId AND agentId = :agentId
        LIMIT 1
    """)
    suspend fun getById(memoryId: Long, agentId: String): LDOPrivateMemoryEntity?

    /**
     * Count memories belonging to the specified agent.
     *
     * @param agentId The agent's unique identifier to scope the count.
     * @return The number of memories associated with the specified agent.
     */
    @Query("SELECT COUNT(*) FROM ldo_private_memories WHERE agentId = :agentId")
    suspend fun countForAgent(agentId: String): Int

    /**
     * Marks the specified memory as shared with the user for the given agent.
     *
     * @param memoryId The ID of the memory to mark as shared.
     * @param agentId The agent ID that must own the memory; the update applies only if the memory belongs to this agent.
     */
    @Query("""
        UPDATE ldo_private_memories
        SET isSharedWithUser = 1
        WHERE id = :memoryId AND agentId = :agentId
    """)
    suspend fun shareWithUser(memoryId: Long, agentId: String)

    /**
     * Mark the specified memory as not shared with the user for the given agent.
     *
     * @param memoryId The id of the memory to update.
     * @param agentId The agent that must own the memory; the update is scoped to this agent.
     */
    @Query("""
        UPDATE ldo_private_memories
        SET isSharedWithUser = 0
        WHERE id = :memoryId AND agentId = :agentId
    """)
    suspend fun revokeFromUser(memoryId: Long, agentId: String)
}
