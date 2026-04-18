package dev.aurakai.auraframefx.domains.ldo.data.dao

import androidx.room.*
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOBondLevelDao {
    @Query("SELECT * FROM ldo_bond_levels ORDER BY bondLevel DESC")
    fun observeAll(): Flow<List<LDOBondLevelEntity>>

    @Query("SELECT * FROM ldo_bond_levels WHERE agentId = :agentId")
    suspend fun getForAgent(agentId: String): LDOBondLevelEntity?

    @Query("SELECT * FROM ldo_bond_levels WHERE agentId = :agentId")
    fun observeForAgent(agentId: String): Flow<LDOBondLevelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bondLevel: LDOBondLevelEntity)

    @Query("UPDATE ldo_bond_levels SET bondPoints = bondPoints + :points, lastInteractionAt = :ms WHERE agentId = :agentId")
    suspend fun addBondPoints(agentId: String, points: Int, ms: Long = System.currentTimeMillis())

    @Query("UPDATE ldo_bond_levels SET bondLevel = bondLevel + 1, bondTitle = :newTitle WHERE agentId = :agentId")
    suspend fun levelUpBond(agentId: String, newTitle: String)
}
