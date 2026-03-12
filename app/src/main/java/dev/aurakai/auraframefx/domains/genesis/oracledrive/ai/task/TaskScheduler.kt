package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.task

import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.task.models.AITask
import dev.aurakai.auraframefx.domains.genesis.models.TaskStatus

/**
 * Interface for scheduling and managing tasks
 */
interface TaskScheduler {
    fun scheduleTask(task: AITask): String
    fun cancelTask(taskId: String): Boolean
    fun getTaskStatus(taskId: String): TaskStatus.Status?
    fun getActiveTasks(): List<AITask>
    fun getTaskHistory(): List<AITask>
    fun clearCompletedTasks()
}
