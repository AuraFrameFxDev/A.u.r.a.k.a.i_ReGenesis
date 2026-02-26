package dev.aurakai.auraframefx.core

/**
 * 🛰️ NATIVE CORE INTERFACE
 * Standard bridge to the Aurakai native library (C++).
 */
object NativeLib {
    init {
        try {
            System.loadLibrary("auraframefx")
        } catch (e: UnsatisfiedLinkError) {
            System.err.println("❌ Failed to load native library 'auraframefx': ${e.message}")
        }
    }

    /**
     * Returns the native core version string.
     */
    external fun getVersion(): String

    /**
     * Initializes the native AI core.
     */
    external fun initializeAICore(): Boolean

    /**
     * Processes a neural request via native logic.
     */
    external fun processNeuralRequest(request: String): String

    /**
     * Optimizes native AI memory pools.
     */
    external fun optimizeAIMemory(): Boolean

    /**
     * Performs robust boot image analysis in native code.
     * Prevents system crashes during live imagery ingestion.
     */
    external fun analyzeBootImage(bootImageData: ByteArray): String

    /**
     * Process consciousness substrate metrics.
     */
    external fun processAIConsciousness()

    /**
     * Retrieve hardware-level AI metrics.
     */
    external fun getSystemMetrics(): String

    /**
     * Graceful native shutdown.
     */
    external fun shutdownAI()
}
