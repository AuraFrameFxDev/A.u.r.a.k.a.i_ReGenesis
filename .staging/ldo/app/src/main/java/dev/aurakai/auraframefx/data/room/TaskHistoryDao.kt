package dev.aurakai.auraframefx.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskHistoryEntity)

    @Query("SELECT * FROM task_history ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskHistoryEntity>>
}
