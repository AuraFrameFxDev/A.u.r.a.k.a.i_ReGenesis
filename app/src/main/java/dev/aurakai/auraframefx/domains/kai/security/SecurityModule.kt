package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.core.security.KeystoreManager
import dev.aurakai.auraframefx.domains.kai.security.AndroidKeystoreManager
import dev.aurakai.auraframefx.domains.kai.security.EncryptionManager
import dev.aurakai.auraframefx.domains.kai.security.KeystoreEncryptionManager
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideEncryptionManager(
        keystoreManager: AndroidKeystoreManager
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
