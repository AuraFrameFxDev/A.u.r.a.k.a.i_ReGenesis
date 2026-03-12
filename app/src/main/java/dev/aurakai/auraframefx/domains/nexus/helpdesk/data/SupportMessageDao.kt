package dev.aurakai.auraframefx.domains.nexus.helpdesk.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: SupportMessageEntity)

    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<SupportMessageEntity>>

    @Query("SELECT * FROM support_messages WHERE status = :status ORDER BY timestamp ASC")
    suspend fun getMessagesByStatus(status: String): List<SupportMessageEntity>

    @Query("SELECT * FROM support_messages WHERE status = 'FAILED' ORDER BY timestamp ASC")
    suspend fun getFailedMessages(): List<SupportMessageEntity>

    @Query("DELETE FROM support_messages")
    suspend fun clearAll()
}
