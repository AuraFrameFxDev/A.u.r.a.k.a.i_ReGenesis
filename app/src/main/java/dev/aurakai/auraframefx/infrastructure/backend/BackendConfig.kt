package dev.aurakai.auraframefx.infrastructure.backend

/**
 * 🐍 BACKEND CONFIGURATION
 * 
 * Defines endpoints and settings for the Python AI backend.
 */
object BackendConfig {
    /** Default local development address (use 10.0.2.2 for Android Emulator) */
    const val BASE_URL = "http://10.0.2.2:5000/"
    
    /** Timeout for heavy AI processing tasks */
    const val TIMEOUT_SECONDS = 60L
    
    /** Maximum retry attempts for failed requests */
    const val MAX_RETRIES = 3
}
