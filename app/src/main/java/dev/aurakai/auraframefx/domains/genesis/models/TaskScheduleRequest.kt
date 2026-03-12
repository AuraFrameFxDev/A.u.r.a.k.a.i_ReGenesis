package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to schedule a task
 */
@Serializable
data class TaskScheduleRequest(
    @SerialName("task_name")
    val taskName: String,

    @SerialName("schedule")
    val schedule: String,

    @SerialName("parameters")
    val parameters: Map<String, String> = emptyMap()
)
