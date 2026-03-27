package dev.aurakai.auraframefx.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxServiceImpl
import dev.aurakai.auraframefx.domains.kai.security.SecurePreferences
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PandoraPreferences

@Module
@InstallIn(SingletonComponent::class)
abstract class PandoraBoxModule {

    @Binds
    @Singleton
    abstract fun bindPandoraBoxService(
        pandoraBoxServiceImpl: PandoraBoxServiceImpl
    ): PandoraBoxService

    companion object {
        @Provides
        @Singleton
        @PandoraPreferences
        fun providePandoraSecurePreferences(@ApplicationContext context: Context): SecurePreferences {
            // We create a wrapper that uses a different file name for Pandora state
            return object : SecurePreferences(context) {
                private val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                override val securePrefs by lazy {
                    EncryptedSharedPreferences.create(
                        context,
                        "pandora_secure_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                }
            }
        }
    }
}
