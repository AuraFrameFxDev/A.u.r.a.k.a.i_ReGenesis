package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.system.impl.DefaultSystemMonitor
import dev.aurakai.auraframefx.system.monitor.SystemMonitor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class SystemModule {

    /**
     * Binds DefaultSystemMonitor as the singleton implementation of SystemMonitor in the dependency graph.
     *
     * @param impl The DefaultSystemMonitor instance to bind.
     * @return The bound SystemMonitor.
     */
    @Binds
    @Singleton
    abstract fun bindSystemMonitor(impl: DefaultSystemMonitor): SystemMonitor
}
