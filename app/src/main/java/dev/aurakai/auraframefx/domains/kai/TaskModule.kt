package dev.aurakai.auraframefx.domains.kai

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskModule {

    @Binds
    @Singleton
    abstract fun bindTaskScheduler(impl: DefaultTaskSchedulerImpl): DefaultTaskScheduler
}
