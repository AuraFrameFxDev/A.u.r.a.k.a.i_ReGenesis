package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideEncryptionManager(
        keystoreManager: KeystoreManager
    ): EncryptionManager {
        // Default to Keystore-backed encryption for KAI domain
        return KeystoreEncryptionManager(keystoreManager)
    }

    @Provides
    @Singleton
    @Named("OracleDrive")
    fun provideOracleDriveEncryptionManager(
        keystoreManager: KeystoreManager
    ): dev.aurakai.auraframefx.domains.genesis.oracledrive.security.EncryptionManager {
        return dev.aurakai.auraframefx.domains.genesis.oracledrive.security.EncryptionManager(keystoreManager)
    }
}
