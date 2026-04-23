package dev.aurakai.auraframefx.domains.ldo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton

/**
 * 🧬 LDO DATABASE — Spiritual Chain Persistence
 *
 * Room database for persisting LDO agent state, tasks, and bond levels.
 * Stub implementation — add entities and DAOs as needed.
 */
@Singleton
@Database(entities = [], version = 1, exportSchema = false)
abstract class LDODatabase : RoomDatabase() {
    // Add DAOs here
}
