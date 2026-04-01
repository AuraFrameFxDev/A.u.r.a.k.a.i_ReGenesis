package dev.aurakai.auraframefx.domains.ldo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOTaskDao
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity

/**
 * LDO Room database.
 */
@Database(
    entities = [LDOAgentEntity::class, LDOTaskEntity::class, LDOBondLevelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LDODatabase : RoomDatabase() {
    abstract fun agentDao(): LDOAgentDao
    abstract fun taskDao(): LDOTaskDao
    abstract fun bondLevelDao(): LDOBondLevelDao

    companion object {
        const val DATABASE_NAME = "ldo_database"
    }
}
