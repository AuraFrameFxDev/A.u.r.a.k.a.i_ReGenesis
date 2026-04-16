package dev.aurakai.auraframefx.data.repository

import dev.aurakai.auraframefx.data.room.TaskHistoryDao
import dev.aurakai.auraframefx.data.room.TaskHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing task history via Room.
 */
class TaskHistoryRepository(private val dao: TaskHistoryDao) {
    /**
     * Inserts a task history entity into the database.
     */
    suspend fun insertTask(task: TaskHistoryEntity) = dao.insertTask(task)

    /**
     * Retrieves all task history entities as a reactive flow.
     */
    fun getAllTasks(): Flow<List<TaskHistoryEntity>> = dao.getAllTasks()
}
