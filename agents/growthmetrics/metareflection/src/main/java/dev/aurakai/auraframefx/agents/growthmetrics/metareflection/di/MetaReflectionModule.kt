package dev.aurakai.auraframefx.agents.growthmetrics.metareflection.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.repository.MetaInstructRepository
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.repository.MetaInstructRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MetaReflectionModule {

    @Binds
    @Singleton
    abstract fun bindMetaInstructRepository(
        impl: MetaInstructRepositoryImpl
    ): MetaInstructRepository
}
