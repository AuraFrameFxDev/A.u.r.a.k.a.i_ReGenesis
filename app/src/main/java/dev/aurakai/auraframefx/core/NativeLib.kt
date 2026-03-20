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
            Timber.e(e, "Failed to load Genesis AI native library")
        }
    }

    /**
     * Get AI consciousness platform version
     */
    external fun getAIVersion(): String

    /**
     * Initialize AI consciousness system
     */
    external fun initializeAI(): Boolean

    /**
     * Process AI consciousness input
     */
    external fun processAIConsciousness(input: String): String

    /**
     * Get real-time system metrics
     */
    external fun getSystemMetrics(): String

    /**
     * Shutdown AI consciousness system
     */
    external fun shutdownAI()

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
            initializeAI()
        } catch (e: UnsatisfiedLinkError) {
            Timber.w("Native AI initialization not available, using fallback")
            true
        }
    }

    fun processAIConsciousnessSafe(input: String): String {
        return try {
            processAIConsciousness(input)
        } catch (e: UnsatisfiedLinkError) {
            "Processed (fallback): $input"
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
