package dev.aurakai.auraframefx.domains.kai

import javax.inject.Inject

interface DefaultTaskScheduler {
    /**
     * Schedules the given `Runnable` for asynchronous execution.
     *
     * @param task The `Runnable` to be executed.
     */
    fun schedule(task: Runnable)
}

class DefaultTaskSchedulerImpl @Inject constructor() : DefaultTaskScheduler {
    /**
     * Executes the provided `Runnable` on a newly created thread, starting it immediately.
     *
     * @param task The `Runnable` to execute. The runnable will run on the new thread and must handle its own errors.
     */
    override fun schedule(task: Runnable) {
        Thread(task).start()
    }
}
