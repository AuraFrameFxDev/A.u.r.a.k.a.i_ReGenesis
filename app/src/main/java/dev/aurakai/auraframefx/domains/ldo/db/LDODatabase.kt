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
    /**
 * Access the DAO for LDO agent entities.
 *
 * @return The `LDOAgentDao` used to query and modify LDO agent records.
 */
abstract fun agentDao(): LDOAgentDao
    /**
 * Provides access to the DAO for performing operations on LDO task entities.
 *
 * @return The {@link LDOTaskDao} used to query, insert, update, and delete LDO tasks.
 */
abstract fun taskDao(): LDOTaskDao
    /**
 * Access the DAO responsible for persisting and querying LDO bond level entities.
 *
 * @return The `LDOBondLevelDao` used to read and modify bond level records.
 */
abstract fun bondLevelDao(): LDOBondLevelDao
    /**
 * Accessor for the DAO that manages LDO private memory records.
 *
 * @return The `LDOPrivateMemoryDao` used for querying and modifying LDO private memories.
 */
abstract fun privateMemoryDao(): LDOPrivateMemoryDao

    companion object {
        /**
         * v1 → v2: Creates the ldo_private_memories table.
         * Each row is owned exclusively by one LDO agent — no cross-agent reads exist in the DAO.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            /**
             * Creates the `ldo_private_memories` table and its `agentId` index as part of the schema migration.
             *
             * The created table stores private memories for LDO agents and includes a foreign key to `ldo_agents(id)` with
             * `ON DELETE CASCADE`.
             *
             * @param database The writable SQLite database to which the migration SQL will be applied.
             */
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
