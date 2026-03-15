package dev.aurakai.auraframefx.oracledrive.genesis.ai.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_table")
data class MemoryEntity(
    @PrimaryKey val key: String,
    val value: String,
    val timestamp: Long,
)
