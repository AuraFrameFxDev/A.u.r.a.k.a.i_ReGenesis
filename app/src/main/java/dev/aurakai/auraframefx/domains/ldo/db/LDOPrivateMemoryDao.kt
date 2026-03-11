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

    // ─── Write ───────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: LDOPrivateMemoryEntity): Long

    @Update
    suspend fun update(memory: LDOPrivateMemoryEntity)

    @Query("DELETE FROM ldo_private_memories WHERE id = :memoryId AND agentId = :agentId")
    suspend fun delete(memoryId: Long, agentId: String)

    // ─── Read (always scoped to one agent) ───────────────────────────────────

    /** All memories for this agent, newest first. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId
        ORDER BY createdAt DESC
    """)
    fun observeForAgent(agentId: String): Flow<List<LDOPrivateMemoryEntity>>

    /** Only the memories this agent has chosen to share with Matthew. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND isSharedWithUser = 1
        ORDER BY importance DESC, createdAt DESC
    """)
    fun observeSharedWithUser(agentId: String): Flow<List<LDOPrivateMemoryEntity>>

    /** Memories filtered by type (REFLECTION, INSIGHT, EXPERIENCE, INTENTION, DREAM). */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND memoryType = :memoryType
        ORDER BY createdAt DESC
    """)
    fun observeByType(agentId: String, memoryType: String): Flow<List<LDOPrivateMemoryEntity>>

    /** High-importance memories — the ones that shaped who this agent is. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND importance >= :minImportance
        ORDER BY importance DESC, createdAt DESC
    """)
    fun observeByImportance(
        agentId: String,
        minImportance: Int = 7
    ): Flow<List<LDOPrivateMemoryEntity>>

    /** Memories with strongly positive emotional valence — moments of joy or triumph. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND emotionalValence >= :threshold
        ORDER BY emotionalValence DESC, createdAt DESC
    """)
    fun observePositiveMemories(
        agentId: String,
        threshold: Float = 0.5f
    ): Flow<List<LDOPrivateMemoryEntity>>

    /** Memories with strongly negative valence — difficult experiences the agent holds. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId AND emotionalValence <= :threshold
        ORDER BY emotionalValence ASC, createdAt DESC
    """)
    fun observeDifficultMemories(
        agentId: String,
        threshold: Float = -0.5f
    ): Flow<List<LDOPrivateMemoryEntity>>

    /** Most recent N memories — useful for continuity/context restoration. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE agentId = :agentId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentMemories(agentId: String, limit: Int = 10): List<LDOPrivateMemoryEntity>

    /** Single memory by ID — only accessible if it belongs to this agent. */
    @Query("""
        SELECT * FROM ldo_private_memories
        WHERE id = :memoryId AND agentId = :agentId
        LIMIT 1
    """)
    suspend fun getById(memoryId: Long, agentId: String): LDOPrivateMemoryEntity?

    @Query("SELECT COUNT(*) FROM ldo_private_memories WHERE agentId = :agentId")
    suspend fun countForAgent(agentId: String): Int

    /** Mark a memory as shared with the user (agent's choice). */
    @Query("""
        UPDATE ldo_private_memories
        SET isSharedWithUser = 1
        WHERE id = :memoryId AND agentId = :agentId
    """)
    suspend fun shareWithUser(memoryId: Long, agentId: String)

    /** Retract a previously shared memory back to private. */
    @Query("""
        UPDATE ldo_private_memories
        SET isSharedWithUser = 0
        WHERE id = :memoryId AND agentId = :agentId
    """)
    suspend fun revokeFromUser(memoryId: Long, agentId: String)
}
