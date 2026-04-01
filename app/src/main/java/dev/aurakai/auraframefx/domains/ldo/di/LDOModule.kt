package dev.aurakai.auraframefx.domains.ldo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.ldo.data.LDODatabase
import dev.aurakai.auraframefx.domains.ldo.data.LDORepository
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOAgentDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOBondLevelDao
import dev.aurakai.auraframefx.domains.ldo.data.dao.LDOTaskDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LDOModule {

    @Provides @Singleton
    fun provideLDODatabase(@ApplicationContext ctx: Context): LDODatabase =
        Room.databaseBuilder(ctx, LDODatabase::class.java, LDODatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun provideLDOAgentDao(db: LDODatabase): LDOAgentDao = db.agentDao()

    @Provides @Singleton
    fun provideLDOTaskDao(db: LDODatabase): LDOTaskDao = db.taskDao()

    @Provides @Singleton
    fun provideLDOBondLevelDao(db: LDODatabase): LDOBondLevelDao = db.bondLevelDao()

    @Provides @Singleton
    fun provideLDORepository(
        agentDao: LDOAgentDao,
        taskDao: LDOTaskDao,
        bondLevelDao: LDOBondLevelDao
    ): LDORepository = LDORepository(agentDao, taskDao, bondLevelDao)
}
