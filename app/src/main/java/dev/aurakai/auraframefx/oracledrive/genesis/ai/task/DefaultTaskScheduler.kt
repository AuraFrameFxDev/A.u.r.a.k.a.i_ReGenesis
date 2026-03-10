package dev.aurakai.auraframefx.oracledrive.genesis.ai.task

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTaskScheduler @Inject constructor() : TaskScheduler {

    private val tasks = mutableMapOf<String, AITask>()

    override fun scheduleTask(task: AITask): String {
        val taskId = task.id.ifEmpty { UUID.randomUUID().toString() }
        tasks[taskId] = task.copy(id = taskId, status = TaskStatus.PENDING)
        return taskId
    }

    override fun cancelTask(taskId: String): Boolean {
        val task = tasks[taskId] ?: return false
        tasks[taskId] = task.copy(status = TaskStatus.CANCELLED)
        return true
    }

    override fun getTaskStatus(taskId: String): TaskStatus? {
        return tasks[taskId]?.status
    }

    override fun getActiveTasks(): List<AITask> {
        return tasks.values.filter { it.status == TaskStatus.PENDING || it.status == TaskStatus.IN_PROGRESS }
    }

    override fun getTaskHistory(): List<AITask> {
        return tasks.values.toList()
    }

    override fun clearCompletedTasks() {
        tasks.entries.removeIf { it.value.status == TaskStatus.COMPLETED || it.value.status == TaskStatus.FAILED || it.value.status == TaskStatus.CANCELLED }
    }
}
