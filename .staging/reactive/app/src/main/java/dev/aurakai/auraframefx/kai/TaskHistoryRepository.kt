package dev.aurakai.auraframefx.kai

import dev.aurakai.auraframefx.data.room.TaskHistoryDao
import dev.aurakai.auraframefx.data.room.TaskHistoryEntity
import kotlinx.coroutines.flow.Flow

class TaskHistoryRepository(private val dao: TaskHistoryDao) {
    suspend fun insertTask(task: TaskHistoryEntity) = dao.insertTask(task)
    fun getAllTasks(): Flow<List<TaskHistoryEntity>> = dao.getAllTasks()
}
