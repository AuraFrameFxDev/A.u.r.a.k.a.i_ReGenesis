package dev.aurakai.auraframefx.infrastructure.backend

/**
 * 🐍 BACKEND CONFIGURATION
 * 
 * Defines endpoints and settings for the Python AI backend.
 */
object BackendConfig {
    /**
     * Backend URL — reads from BuildConfig to ensure debug uses emulator
     * and release uses the production HTTPS endpoint.
     * Fallback to emulator address only if BuildConfig is unavailable.
     */
    val BASE_URL: String get() = try {
        dev.aurakai.auraframefx.BuildConfig.GENESIS_BACKEND_URL
    } catch (e: Exception) {
        "http://10.0.2.2:5000/"
    }
    
    /** Timeout for heavy AI processing tasks */
    const val TIMEOUT_SECONDS = 60L
    
    /** Maximum retry attempts for failed requests */
    const val MAX_RETRIES = 3
}
