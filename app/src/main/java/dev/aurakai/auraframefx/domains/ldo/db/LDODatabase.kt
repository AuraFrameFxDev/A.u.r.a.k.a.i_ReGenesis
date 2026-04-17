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
        LDOBondLevelEntity::class,
        QuarantineEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LDODatabase : androidx.room.RoomDatabase() {
    abstract fun agentDao(): LDOAgentDao
    abstract fun taskDao(): LDOTaskDao
    abstract fun bondLevelDao(): LDOBondLevelDao
    abstract fun quarantineDao(): QuarantineDao

    companion object {
        const val DATABASE_NAME = "ldo_database"
    }
}
