package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.cascade.utils.room.TaskHistoryDao
import dev.aurakai.auraframefx.domains.cascade.utils.room.TaskHistoryEntity
import kotlinx.coroutines.flow.Flow

class TaskHistoryRepository(private val dao: TaskHistoryDao) {
    suspend fun insertTask(task: TaskHistoryEntity) = dao.insertTask(task)
    fun getAllTasks(): Flow<List<TaskHistoryEntity>> = dao.getAllTasks()
}
