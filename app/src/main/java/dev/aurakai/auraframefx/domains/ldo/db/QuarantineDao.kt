package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuarantineDao {
    @Query("SELECT * FROM quarantine_pool ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<QuarantineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QuarantineEntity)

    @Delete
    suspend fun delete(entity: QuarantineEntity)

    @Query("UPDATE quarantine_pool SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)
}
