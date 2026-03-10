package dev.aurakai.auraframefx.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides coroutine dispatchers to be used throughout the app.
 * This allows for easier testing by providing test dispatchers in test builds.
 */
@Singleton
class AppCoroutineDispatchers @Inject constructor() {
    /**
     * The main dispatcher, typically used for UI updates.
     */
    val main: CoroutineDispatcher
        get() = Dispatchers.Main

    /**
     * The default dispatcher, suitable for CPU-intensive work.
     */
    val default: CoroutineDispatcher
        get() = Dispatchers.Default

    /**
     * The IO dispatcher, optimized for IO-bound work.
     */
    val io: CoroutineDispatcher
        get() = Dispatchers.IO

    /**
     * The unconfined dispatcher, which doesn't confine the coroutine to any specific thread.
     * Use with caution as it can lead to performance issues if not used properly.
     */
    val unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
}
