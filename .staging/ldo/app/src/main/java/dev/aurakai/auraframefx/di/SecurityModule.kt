package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.security.KeystoreManager
import dev.aurakai.auraframefx.security.SecurityContext
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideKeystoreManager(
        @ApplicationContext context: Context
    ): KeystoreManager {
        return KeystoreManager(context)
    }

    @Provides
    @Singleton
    fun provideSecurityContext(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager
    ): SecurityContext {
        return SecurityContext(context, keystoreManager)
    }
}
