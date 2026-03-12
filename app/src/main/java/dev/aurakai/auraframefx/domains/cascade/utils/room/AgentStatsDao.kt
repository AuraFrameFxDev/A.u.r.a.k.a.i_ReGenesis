package dev.aurakai.auraframefx.domains.cascade.utils.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for AgentStatsEntity
 */
@Dao
interface AgentStatsDao {
    @Query("SELECT * FROM agent_stats")
    fun getAllStats(): Flow<List<AgentStatsEntity>>

    @Query("SELECT * FROM agent_stats WHERE agentName = :name")
    suspend fun getStats(name: String): AgentStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: AgentStatsEntity)

    @Update
    suspend fun updateStats(stats: AgentStatsEntity)
}
