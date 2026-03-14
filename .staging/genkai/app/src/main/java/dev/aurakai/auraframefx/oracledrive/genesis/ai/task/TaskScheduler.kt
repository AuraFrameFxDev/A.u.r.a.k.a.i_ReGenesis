package dev.aurakai.auraframefx.oracledrive.genesis.ai.task

import dev.aurakai.auraframefx.models.Task

/**
 * Interface for scheduling and managing tasks
 */
interface TaskScheduler {
    suspend fun scheduleTask(task: Task)
    suspend fun cancelTask(taskId: String)
    suspend fun getTaskStatus(taskId: String): String?
}
