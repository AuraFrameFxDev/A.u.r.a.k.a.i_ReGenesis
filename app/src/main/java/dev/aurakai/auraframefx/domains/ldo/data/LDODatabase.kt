package dev.aurakai.auraframefx.domains.ldo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOTaskDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.QuarantineDao
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.QuarantineEntity

/**
 * LDO Room database.
 */
@Database(
    entities = [
        LDOAgentEntity::class, 
        LDOTaskEntity::class, 
        LDOBondLevelEntity::class,
        QuarantineEntity::class
    ],
    version = 2, // Upgraded for Quarantine
    exportSchema = false
)
abstract class LDODatabase : RoomDatabase() {
    abstract fun agentDao(): LDOAgentDao
    abstract fun taskDao(): LDOTaskDao
    abstract fun bondLevelDao(): LDOBondLevelDao
    abstract fun quarantineDao(): QuarantineDao

    companion object {
        const val DATABASE_NAME = "ldo_database"
    }
}
