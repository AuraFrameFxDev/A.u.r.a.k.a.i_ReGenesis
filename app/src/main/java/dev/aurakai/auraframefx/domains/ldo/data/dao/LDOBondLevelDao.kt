package dev.aurakai.auraframefx.domains.ldo.data.dao

import androidx.room.*
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOBondLevelDao {
    @Query("SELECT * FROM ldo_bond_levels ORDER BY level DESC")
    fun observeAll(): Flow<List<LDOBondLevelEntity>>

    @Query("SELECT * FROM ldo_bond_levels WHERE agentId = :agentId")
    suspend fun getForAgent(agentId: String): LDOBondLevelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(bondLevel: LDOBondLevelEntity)

    @Query("UPDATE ldo_bond_levels SET experience = experience + :xp, lastInteractionMs = :ms WHERE agentId = :agentId")
    suspend fun addExperience(agentId: String, xp: Long, ms: Long = System.currentTimeMillis())
}
