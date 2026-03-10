package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

@Serializable
data class HistoricalTask(
    val id: String,
    val description: String,
    val status: String,
    val timestamp: Long,
    val result: String? = null
)
