package dev.aurakai.auraframefx.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 🔌 AI Service Supplemental Module
 *
 * Provides Firebase Storage singleton only.
 * CloudStorageProvider binding lives in OracleDriveModule to avoid duplicate binding.
 */
@Module
@InstallIn(SingletonComponent::class)
object AiServiceModuleImpl {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
}
