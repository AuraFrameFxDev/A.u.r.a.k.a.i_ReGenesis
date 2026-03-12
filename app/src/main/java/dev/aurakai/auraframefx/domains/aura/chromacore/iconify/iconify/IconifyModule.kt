package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * 🎨 Iconify Hilt Module
 *
 * Provides dependency injection for Iconify services.
 */
@Module
@InstallIn(SingletonComponent::class)
object IconifyModule {

    @Provides
    @Singleton
    fun provideIconCacheManager(
        @ApplicationContext context: Context
    ): IconCacheManager {
        return IconCacheManager(context)
    }

    // NOTE: Use the application-wide OkHttpClient from NetworkModule.
    // If Iconify requires a specialized client in future, provide it with a @Named qualifier.

    @Provides
    @Singleton
    fun provideIconifyService(
        @Named("BasicOkHttpClient") okHttpClient: OkHttpClient,
        iconCacheManager: IconCacheManager
    ): IconifyService {
        return IconifyService(okHttpClient, iconCacheManager)
    }
}
