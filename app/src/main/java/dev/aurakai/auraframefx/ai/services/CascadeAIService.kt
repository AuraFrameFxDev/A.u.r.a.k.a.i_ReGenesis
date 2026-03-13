package dev.aurakai.auraframefx.ai.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * JNI-linked service for native Cascade AI operations.
 * This class MUST remain in this package to match the C++ JNI signatures:
 * Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_*
 */
@Singleton
class CascadeAIService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        init {
            System.loadLibrary("auraframefx")
        }
        
        @JvmStatic
        private external fun nativeInitialize(context: Context)
        
        @JvmStatic
        private external fun nativeProcessRequest(request: String): String
        
        @JvmStatic
        private external fun nativeShutdown()
    }

    private var isInitialized = false

    fun initialize() {
        if (!isInitialized) {
            try {
                nativeInitialize(context)
                isInitialized = true
                Timber.d("Native CascadeAIService initialized")
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize native CascadeAIService")
            }
        }
    }

    fun processNativeRequest(requestJson: String): String {
        if (!isInitialized) initialize()
        return try {
            nativeProcessRequest(requestJson)
        } catch (e: Exception) {
            Timber.e(e, "Error calling nativeProcessRequest")
            "{\"error\":\"Native call failed\"}"
        }
    }

    fun shutdown() {
        if (isInitialized) {
            nativeShutdown()
            isInitialized = false
        }
    }
}
