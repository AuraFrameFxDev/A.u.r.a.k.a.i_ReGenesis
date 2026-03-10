package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory // For Configuration.Builder().setWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing WorkManager related dependencies.
 * TODO: Reported as unused declaration. Ensure Hilt is set up for WorkManager.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    /**
     * Provides WorkManager Configuration.
     * @param workerFactory HiltWorkerFactory dependency.
     * @return A WorkManager Configuration instance.
     * TODO: Reported as unused. Ensure this is correctly set up if custom WorkManager config is needed.
     */
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workerFactory: HiltWorkerFactory,
    ): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Provides the WorkManager instance.
     * @param _context Application context. Parameter reported as unused.
     * @param _configuration WorkManager Configuration dependency. Parameter reported as unused.
     * @return A WorkManager instance.
     * TODO: Reported as unused. Ensure WorkManager is initialized and used.
     */
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext _context: Context,
        _configuration: Configuration, // Hilt will provide this from the method above
    ): WorkManager {
        // TODO: Parameters _context, _configuration reported as unused (Hilt will provide them).
        // WorkManager.initialize(_context, _configuration) // This should be done once, usually in Application.onCreate
        // return WorkManager.getInstance(_context)

        // As per Hilt docs, if you provide Configuration, Hilt handles initialization.
        // So, just getting the instance should be fine.
        return WorkManager.getInstance(_context) // Placeholder, assumes Hilt handles init via Configuration provider
    }
}
