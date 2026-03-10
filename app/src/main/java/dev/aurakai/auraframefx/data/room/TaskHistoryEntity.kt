package dev.aurakai.auraframefx.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_history")
data class TaskHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskName: String,
    val agentType: String,
    val status: String,
    val result: String?,
    val timestamp: Long,
)
