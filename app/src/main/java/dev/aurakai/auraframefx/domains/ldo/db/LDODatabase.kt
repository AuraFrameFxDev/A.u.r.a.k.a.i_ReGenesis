package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * LDO-domain Room database.
 * Separate from the cascade AppDatabase to keep the LDO domain self-contained.
 * Named "ldo_database" — seeded from LDORoster on first launch via LDORepository.
 */
@Database(
    entities = [
        LDOAgentEntity::class,
        LDOTaskEntity::class,
        LDOBondLevelEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LDODatabase : androidx.room.RoomDatabase() {
    abstract fun agentDao(): LDOAgentDao
    abstract fun taskDao(): LDOTaskDao
    abstract fun bondLevelDao(): LDOBondLevelDao
}
