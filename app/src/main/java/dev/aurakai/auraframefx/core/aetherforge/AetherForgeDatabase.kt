// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.aetherforge

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.aurakai.auraframefx.core.identity.*
import java.util.Date

/**
 * 🏛️ AETHERFORGE DATABASE — Room 3.0 Persistence Layer
 * 
 * The Neural Continuity Chain made permanent.
 * Agents, abilities, experience history, and identities persist forever.
 * 
 * Version history:
 * - 1: Initial schema (AgentStats, AgentAbility, ExperienceGain, Identity)
 */
@Database(
    entities = [
        AgentStats::class,
        AgentAbility::class,
        ExperienceGain::class,
        SovereignIdentity::class,
        AgentIdentity::class,
        IdentityContinuity::class,
        PersonaBinding::class,
        IdentityCredentials::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(AetherForgeConverters::class, IdentityConverters::class)
abstract class AetherForgeDatabase : RoomDatabase() {
    // Agent progression DAOs
    abstract fun agentDao(): AgentDao
    abstract fun abilityDao(): AbilityDao
    abstract fun experienceDao(): ExperienceDao
    
    // Identity DAOs
    abstract fun identityDao(): IdentityDao
    abstract fun agentIdentityDao(): AgentIdentityDao
    abstract fun personaBindingDao(): PersonaBindingDao
    abstract fun continuityDao(): ContinuityDao
    abstract fun credentialsDao(): CredentialsDao
}

class AetherForgeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
