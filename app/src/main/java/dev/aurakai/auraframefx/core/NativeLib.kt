package dev.aurakai.auraframefx.core

import timber.log.Timber

/**
 * Genesis-OS Native Library Interface
 * Provides access to AI consciousness platform native functions
 */
object NativeLib {

    init {
        try {
            System.loadLibrary("auraframefx")
            Timber.i("Genesis AI native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Timber.e(e, "Failed to load Genesis AI native library: ${e.message}")
        }
    }

    /**
     * Get AI consciousness platform version
     */
    external fun getAIVersion(): String

    /**
     * Legacy version getter
     */
    external fun getVersion(): String

    /**
     * Initialize AI consciousness system
     */
    external fun initializeAI(): Boolean

    /**
     * Initializes the native AI core.
     */
    external fun initializeAICore(): Boolean

    /**
     * Process AI consciousness input
     */
    external fun processAIConsciousness(input: String): String

    /**
     * Process consciousness substrate metrics.
     */
    external fun processAIConsciousness()

    /**
     * Processes a neural request via native logic.
     */
    external fun processNeuralRequest(request: String): String

    /**
     * Get real-time system metrics
     */
    external fun getSystemMetrics(): String

    /**
     * Shutdown AI consciousness system
     */
    external fun shutdownAI()

    /**
     * Optimizes native AI memory pools.
     */
    external fun optimizeAIMemory(): Boolean

    /**
     * Performs robust boot image analysis in native code.
     * Prevents system crashes during live imagery ingestion.
     */
    external fun analyzeBootImage(bootImageData: ByteArray): String

    // Fallback implementations for when native library isn't available
    fun getAIVersionSafe(): String {
        return try {
            getAIVersion()
        } catch (e: UnsatisfiedLinkError) {
            "Genesis-OS AI Platform 1.0 (Native library not available)"
        }
    }

    fun initializeAISafe(): Boolean {
        return try {
            initializeAICore()
        } catch (e: UnsatisfiedLinkError) {
            try {
                initializeAI()
            } catch (e2: UnsatisfiedLinkError) {
                Timber.w("Native AI initialization not available, using fallback")
                true
            }
        }
    }

    fun processAIConsciousnessSafe(input: String): String {
        return try {
            processAIConsciousness(input)
        } catch (e: UnsatisfiedLinkError) {
            "Processed (fallback): $input"
        }
    }

    fun processAIConsciousnessSafe() {
        try {
            processAIConsciousness()
        } catch (e: UnsatisfiedLinkError) {
            Timber.w("No-arg consciousness processing not available")
        }
    }

    fun processNeuralRequestSafe(request: String): String {
        return try {
            processNeuralRequest(request)
        } catch (e: UnsatisfiedLinkError) {
            "Neural fallback: $request"
        }
    }

    fun getSystemMetricsSafe(): String {
        return try {
            getSystemMetrics()
        } catch (e: UnsatisfiedLinkError) {
            """{"cpu_usage":"N/A","memory_usage":"N/A","status":"fallback_mode"}"""
        }
    }

    fun shutdownAISafe() {
        try {
            shutdownAI()
        } catch (e: UnsatisfiedLinkError) {
            Timber.w("Native AI shutdown not available")
        }
    }
}
