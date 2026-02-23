package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

/**
 * Hilt Module for providing Coroutine Dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    // Using Named qualifier instead of custom qualifiers to avoid annotation processing issues
    private const val IO_DISPATCHER = "io_dispatcher"
    private const val DEFAULT_DISPATCHER = "default_dispatcher"

    /**
     * Provides the IO Coroutine Dispatcher.
     */
    @Provides
    @Named(IO_DISPATCHER)
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides the Default Coroutine Dispatcher.
     */
    @Provides
    @Named(DEFAULT_DISPATCHER)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    // You could also provide Dispatchers.Main if needed, though it's often accessed directly.
    // @Provides
    // fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
