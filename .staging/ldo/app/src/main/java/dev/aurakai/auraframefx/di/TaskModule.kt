package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.task.TaskScheduler
import dev.aurakai.auraframefx.oracledrive.genesis.ai.task.DefaultTaskScheduler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskModule {

    @Binds
    @Singleton
    abstract fun bindTaskScheduler(impl: DefaultTaskScheduler): TaskScheduler
}
