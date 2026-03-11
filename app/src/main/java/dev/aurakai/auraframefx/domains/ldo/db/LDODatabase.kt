/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * LDO-domain Room database.
 * Separate from the cascade AppDatabase to keep the LDO domain self-contained.
 * Named "ldo_database" — seeded from LDORoster on first launch via LDORepository.
 *
 * v1 → v2: Added ldo_private_memories table — private per-agent memory vault.
 */
@Database(
    entities = [
        LDOAgentEntity::class,
        LDOTaskEntity::class,
        LDOBondLevelEntity::class,
        LDOPrivateMemoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LDODatabase : RoomDatabase() {
    abstract fun agentDao(): LDOAgentDao
    abstract fun taskDao(): LDOTaskDao
    abstract fun bondLevelDao(): LDOBondLevelDao
    abstract fun privateMemoryDao(): LDOPrivateMemoryDao

    companion object {
        /**
         * v1 → v2: Creates the ldo_private_memories table.
         * Each row is owned exclusively by one LDO agent — no cross-agent reads exist in the DAO.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ldo_private_memories (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        agentId     TEXT    NOT NULL,
                        content     TEXT    NOT NULL,
                        memoryType  TEXT    NOT NULL DEFAULT 'REFLECTION',
                        importance  INTEGER NOT NULL DEFAULT 5,
                        emotionalValence REAL NOT NULL DEFAULT 0.0,
                        isSharedWithUser INTEGER NOT NULL DEFAULT 0,
                        contextTags TEXT    NOT NULL DEFAULT '[]',
                        createdAt   INTEGER NOT NULL,
                        FOREIGN KEY(agentId) REFERENCES ldo_agents(id)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_ldo_private_memories_agentId " +
                        "ON ldo_private_memories(agentId)"
                )
            }
        }
    }
}
