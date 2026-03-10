package dev.aurakai.auraframefx.oracledrive.genesis.ai.task

// Task, AITask, TaskStatus are defined in TaskModels.kt in this same package

/**
 * Interface for scheduling and managing tasks
 */
interface TaskScheduler {
    fun scheduleTask(task: AITask): String
    fun cancelTask(taskId: String): Boolean
    fun getTaskStatus(taskId: String): TaskStatus?
    fun getActiveTasks(): List<AITask>
    fun getTaskHistory(): List<AITask>
    fun clearCompletedTasks()
}
