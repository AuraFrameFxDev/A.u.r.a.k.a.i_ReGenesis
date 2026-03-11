/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.domains.ldo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.db.LDODatabase
import dev.aurakai.auraframefx.domains.ldo.db.LDOPrivateMemoryDao
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskDao
import dev.aurakai.auraframefx.domains.ldo.repository.LDORepository
import javax.inject.Singleton

/**
 * Hilt module providing all LDO domain dependencies.
 * Installed in SingletonComponent — one LDODatabase per process lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object LDOModule {

    @Provides
    @Singleton
    fun provideLDODatabase(@ApplicationContext context: Context): LDODatabase =
        Room.databaseBuilder(
            context.applicationContext,
            LDODatabase::class.java,
            "ldo_database"
        )
            .addMigrations(LDODatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLDOAgentDao(database: LDODatabase): LDOAgentDao =
        database.agentDao()

    @Provides
    fun provideLDOTaskDao(database: LDODatabase): LDOTaskDao =
        database.taskDao()

    @Provides
    fun provideLDOBondLevelDao(database: LDODatabase): LDOBondLevelDao =
        database.bondLevelDao()

    @Provides
    fun provideLDOPrivateMemoryDao(database: LDODatabase): LDOPrivateMemoryDao =
        database.privateMemoryDao()

    @Provides
    @Singleton
    fun provideLDORepository(
        agentDao: LDOAgentDao,
        taskDao: LDOTaskDao,
        bondLevelDao: LDOBondLevelDao,
        privateMemoryDao: LDOPrivateMemoryDao
    ): LDORepository = LDORepository(agentDao, taskDao, bondLevelDao, privateMemoryDao)
}
