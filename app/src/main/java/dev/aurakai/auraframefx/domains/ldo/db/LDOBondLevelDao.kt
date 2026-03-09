package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOBondLevelDao {

    @Query("SELECT * FROM ldo_bond_levels ORDER BY bondLevel DESC, bondPoints DESC")
    fun observeAll(): Flow<List<LDOBondLevelEntity>>

    @Query("SELECT * FROM ldo_bond_levels WHERE agentId = :agentId")
    fun observeForAgent(agentId: String): Flow<LDOBondLevelEntity?>

    @Query("SELECT * FROM ldo_bond_levels WHERE agentId = :agentId")
    suspend fun getForAgent(agentId: String): LDOBondLevelEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(bond: LDOBondLevelEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(bonds: List<LDOBondLevelEntity>)

    @Update
    suspend fun update(bond: LDOBondLevelEntity)

    @Query("""
        UPDATE ldo_bond_levels
        SET bondPoints = MIN(bondPoints + :points, maxBondPoints),
            interactionCount = interactionCount + 1,
            lastInteractionAt = :now,
            updatedAt = :now
        WHERE agentId = :agentId
    """)
    suspend fun addBondPoints(
        agentId: String,
        points: Int,
        now: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE ldo_bond_levels
        SET bondLevel = bondLevel + 1,
            bondPoints = 0,
            maxBondPoints = maxBondPoints + 50,
            bondTitle = :newTitle,
            updatedAt = :now
        WHERE agentId = :agentId
    """)
    suspend fun levelUpBond(
        agentId: String,
        newTitle: String,
        now: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM ldo_bond_levels")
    suspend fun count(): Int
}
