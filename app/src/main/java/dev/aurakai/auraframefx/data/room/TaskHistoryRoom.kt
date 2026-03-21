package dev.aurakai.auraframefx.data.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Room Entity for Task History.
 */
data class TaskHistoryEntity(
    val id: Long = 0,
    val taskName: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room DAO for Task History.
 */
interface TaskHistoryDao {
    suspend fun insertTask(task: TaskHistoryEntity)
    fun getAllTasks(): Flow<List<TaskHistoryEntity>>
}

/**
 * Compatibility shim for TaskHistoryDao.
 */
class TaskHistoryDaoShim : TaskHistoryDao {
    override suspend fun insertTask(task: TaskHistoryEntity) {}
    override fun getAllTasks(): Flow<List<TaskHistoryEntity>> = flowOf(emptyList())
}
