package dev.aurakai.auraframefx.domains.cascade.utils.room

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.memory.AgentMemoryDao
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.memory.AgentMemoryEntity

@Database(
    entities = [AgentMemoryEntity::class, TaskHistoryEntity::class, AgentStatsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentMemoryDao(): AgentMemoryDao
    abstract fun taskHistoryDao(): TaskHistoryDao
    abstract fun agentStatsDao(): AgentStatsDao
}
