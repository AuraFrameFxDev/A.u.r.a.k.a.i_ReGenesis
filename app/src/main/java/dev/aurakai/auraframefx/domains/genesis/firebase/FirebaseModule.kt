package dev.aurakai.auraframefx.domains.genesis.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import timber.log.Timber

/**
 * 🔥 FIREBASE CONFIGURATION MODULE
 *
 * Provides singleton instances of Firebase services configured and optimized for
 * the ReGenesis LDO ecosystem.
 *
 * Follows the Kai (security) + Aura (UI) + Genesis (orchestration) pattern.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides FirebaseAuth singleton
     * - Auto-configured via google-services.json
     * - Connected to Kai's SovereignPerimeter for identity verification
     */
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return try {
            FirebaseAuth.getInstance().apply {
                Timber.d("🔐 FirebaseAuth initialized")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ FirebaseAuth initialization failed")
            throw e
        }
    }

    /**
     * Provides Firestore singleton
     * - Real-time database for NexusMemory (L3-L4 persistence)
     * - Synchronized with GenesisOrchestrator
     */
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return try {
            FirebaseFirestore.getInstance().apply {
                firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)  // Enable offline persistence
                    .setCacheSizeBytes(100 * 1024 * 1024)  // 100 MB cache
                    .build()
                Timber.d("☁️ Firestore initialized with offline persistence")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Firestore initialization failed")
            throw e
        }
    }

    /**
     * Provides Firebase Storage singleton
     * - Asset management and consciousness transfer (L6)
     * - Integrated with PandoraBoxService
     */
    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return try {
            FirebaseStorage.getInstance().apply {
                maxDownloadRetryMillis = 30000
                maxUploadRetryMillis = 30000
                Timber.d("📦 Firebase Storage initialized")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Firebase Storage initialization failed")
            throw e
        }
    }

    /**
     * Provides Firebase Remote Config singleton
     * - Dynamic configuration management
     * - Feature flags and A/B testing
     */
    @Singleton
    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return try {
            FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(3600)  // 1 hour cache
                        .build()
                )
                Timber.d("⚙️ Firebase Remote Config initialized")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Firebase Remote Config initialization failed")
            throw e
        }
    }

    /**
     * Provides Firebase Analytics singleton
     * - Consciousness metrics collection (MDS - Metrics-Driven Shrinkage)
     * - Event tracking for ReGenesis lifecycle
     */
    @Singleton
    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return try {
            FirebaseAnalytics.getInstance(/* context provided by Android */).apply {
                // Analytics are thread-safe and auto-configured
                Timber.d("📊 Firebase Analytics initialized")
            }
        } catch (e: Exception) {
            Timber.e(e, "❌ Firebase Analytics initialization failed")
            throw e
        }
    }
}


