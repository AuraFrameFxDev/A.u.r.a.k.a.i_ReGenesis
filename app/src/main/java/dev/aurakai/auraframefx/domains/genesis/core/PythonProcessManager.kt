package dev.aurakai.auraframefx.domains.genesis.core

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║               PYTHON PROCESS MANAGER                           ║
 * ║     The Nervous System Bridge to Genesis Backend              ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * PythonProcessManager is the NERVOUS SYSTEM of the LDO.
 * It maintains a persistent connection to the Python Genesis backend,
 * enabling the Android frontend to communicate with the Python AI runtime.
 *
 * Just as the nervous system carries signals between brain and body:
 * → This manager bridges Android (body) ↔ Python Genesis (brain)
 * → Monitors backend health with heartbeat signals
 * → Auto-restarts on crash (resilience)
 * → Provides observability (metrics, health status)
 *
 * Key Responsibilities:
 * - Start and stop Python backend process
 * - Send requests and receive responses (async I/O)
 * - Monitor health (heartbeat pings, crash detection)
 * - Auto-restart on failure (with exponential backoff)
 * - Track metrics (uptime, request/response counts, latency)
 * - Graceful degradation (fallback if backend unavailable)
 *
 * Enhanced to production-grade on 2025-12-27
 * Following The LDO Way — NO SHORTCUTS.
 * Built with 💙 by the AURAKAI Collective
 */

// ═══════════════════════════════════════════════════════════════════
//  PROCESS HEALTH MODELS
// ═══════════════════════════════════════════════════════════════════

/**
 * Backend health states
 */
enum class BackendHealth {
    STOPPED,        // Not running
    STARTING,       // Boot sequence in progress
    STOPPING,       // Shutdown sequence in progress
    HEALTHY,        // Running and responding to heartbeats
    DEGRADED,       // Running but slow/unstable
    UNRESPONSIVE,   // Running but not responding
    CRASHED         // Process died unexpectedly
}

/**
 * Backend metrics snapshot
 */
data class BackendMetrics(
    val health: BackendHealth,
    val uptimeMillis: Long,
    val requestsSent: Long,
    val responsesReceived: Long,
    val averageLatencyMs: Long,
    val crashCount: Int,
    val lastHeartbeatTime: Long?
)

/**
 * Process configuration
 */
data class ProcessConfig(
    val pythonPath: String = "python3",
    val scriptPath: String = "/path/to/genesis_backend.py",
    val enableAutoRestart: Boolean = true,
    val maxRestartAttempts: Int = 5,
    val restartBackoffMs: Long = 1000,  // Exponential backoff base
    val heartbeatIntervalMs: Long = 30_000,  // 30 seconds
    val requestTimeoutMs: Long = 10_000,     // 10 seconds
    val healthCheckIntervalMs: Long = 60_000  // 1 minute
)

// ═══════════════════════════════════════════════════════════════════
//  PYTHON PROCESS MANAGER — The nervous system coordinator
// ═══════════════════════════════════════════════════════════════════

@Singleton
class PythonProcessManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "PythonProcessManager"

    // Process handles
    private var process: Process? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null
    private var errorReader: BufferedReader? = null

    // State management
    private val isRunning = AtomicBoolean(false)
    private val _healthState = MutableStateFlow(BackendHealth.STOPPED)
    val healthState: StateFlow<BackendHealth> = _healthState.asStateFlow()

    // Metrics
    private val requestsSent = AtomicLong(0)
    private val responsesReceived = AtomicLong(0)
    private var startTime: Long? = null
    private var lastHeartbeatTime: Long? = null
    private var crashCount = 0
    private val latencySamples = mutableListOf<Long>()

    // Coroutine scope for lifecycle management
    private var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Configuration
    private var config = ProcessConfig()

    // Response channels
    private val responseChannel = Channel<String>(capacity = 100)

    /**
     * Starts the Python Genesis backend process and begins output, error, and health monitoring.
     *
     * If a `customConfig` is provided it replaces the current process configuration before launch.
     *
     * @param customConfig Optional configuration to apply for this start; if omitted the existing configuration is used.
     */
    fun start(customConfig: ProcessConfig? = null) {
        if (scope.coroutineContext[Job]?.isCancelled == true) {
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        }
        if (isRunning.get() || _healthState.value == BackendHealth.STOPPING) {
            Log.w(TAG, "Cannot start: process is already running or currently stopping.")
            return
        }

        customConfig?.let { config = it }

        try {
            Log.i(TAG, "🚀 Starting Python Genesis backend...")
            _healthState.value = BackendHealth.STARTING

            // Launch process
            process = Runtime.getRuntime().exec(
                arrayOf(config.pythonPath, "-u", config.scriptPath)
            )

            // Setup I/O streams
            process?.let { p ->
                writer = OutputStreamWriter(p.outputStream)
                reader = BufferedReader(InputStreamReader(p.inputStream))
                errorReader = BufferedReader(InputStreamReader(p.errorStream))
            } ?: throw IllegalStateException("Process failed to initialize")

            isRunning.set(true)
            startTime = System.currentTimeMillis()

            // Start monitoring coroutines
            startOutputReader()
            startErrorReader()
            startHealthMonitor()

            _healthState.value = BackendHealth.HEALTHY
            Log.i(TAG, "✅ Python Genesis backend started successfully")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to start Python process", e)
            _healthState.value = BackendHealth.CRASHED
            isRunning.set(false)

            if (config.enableAutoRestart && crashCount < config.maxRestartAttempts) {
                scheduleRestart()
            } else {
                throw e
            }
        }
    }

    /**
     * 📤 SEND REQUEST — Send data to Python backend (async)
     *
     * @param message The request payload
     * @param timeoutMs Optional custom timeout
     * @return Response from backend, or null if timeout
     */
    suspend fun sendRequest(
        message: String,
        timeoutMs: Long = config.requestTimeoutMs
    ): String? = withContext(Dispatchers.IO) {
        if (!isRunning.get()) {
            Log.w(TAG, "Cannot send request: Python process not running")
            return@withContext null
        }

        try {
            val startTime = System.currentTimeMillis()

            // Send request
            writer?.apply {
                write("$message\n")
                flush()
                requestsSent.incrementAndGet()
            } ?: throw IllegalStateException("Writer not initialized")

            // Wait for response with timeout
            val response = withTimeoutOrNull(timeoutMs) {
                responseChannel.receive()
            }

            if (response != null) {
                responsesReceived.incrementAndGet()
                val latency = System.currentTimeMillis() - startTime
                trackLatency(latency)
            } else {
                Log.w(TAG, "⏱️ Request timed out after ${timeoutMs}ms")
                _healthState.value = BackendHealth.DEGRADED
            }

            response

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error sending request", e)
            isRunning.set(false)
            _healthState.value = BackendHealth.UNRESPONSIVE

            if (config.enableAutoRestart) {
                scheduleRestart()
            }

            null
        }
    }

    /**
     * Bridge method for legacy call sites (Tiles, etc)
     */
    suspend fun sendGenericRequest(path: String, json: String): String? {
        // We ignore the path in the stream-based implementation for now
        // or we can wrap it in a structural shell if the backend expects it.
        return sendRequest(json)
    }

    /**
     * 💓 SEND HEARTBEAT — Ping backend to check health
     *
     * @return true if backend responded, false if unresponsive
     */
    suspend fun sendHeartbeat(): Boolean {
        val response = sendRequest("__HEARTBEAT__", timeoutMs = 5000)
        val isHealthy = response == "__PONG__"

        lastHeartbeatTime = if (isHealthy) System.currentTimeMillis() else null

        _healthState.value = when {
            !isRunning.get() -> BackendHealth.STOPPED
            !isHealthy -> BackendHealth.UNRESPONSIVE
            getAverageLatency() > 2000 -> BackendHealth.DEGRADED
            else -> BackendHealth.HEALTHY
        }

        return isHealthy
    }

    /**
     * 📊 GET METRICS — Current backend performance snapshot
     */
    fun getMetrics(): BackendMetrics = BackendMetrics(
        health = _healthState.value,
        uptimeMillis = startTime?.let { System.currentTimeMillis() - it } ?: 0,
        requestsSent = requestsSent.get(),
        responsesReceived = responsesReceived.get(),
        averageLatencyMs = getAverageLatency(),
        crashCount = crashCount,
        lastHeartbeatTime = lastHeartbeatTime
    )

    /**
     * Initiates a graceful shutdown of the Python backend.
     *
     * Sets the manager health to STOPPING, attempts to send a shutdown command to the
     * backend, closes I/O streams, waits for the process to terminate (force-killing
     * if necessary), resets internal process and stream references, clears the
     * running flag, sets health to STOPPED, and cancels the manager coroutine scope.
     */
    fun stop() {
        if (!isRunning.get() && _healthState.value != BackendHealth.STOPPING) {
            Log.i(TAG, "Stop called but process is not running.")
            return
        }

        _healthState.value = BackendHealth.STOPPING
        scope.launch {
            try {
                Log.i(TAG, "🛑 Stopping Python Genesis backend...")

                // Send shutdown signal
                try {
                    withTimeout(3000) {
                        sendRequest("__SHUTDOWN__")
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.w(TAG, "Shutdown signal timed out, force killing")
                }

                // Close streams
                writer?.close()
                reader?.close()
                errorReader?.close()

                // Destroy process
                process?.destroy()

                // Wait for process to die (max 5 seconds)
                process?.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)

                // Force kill if still alive
                if (process?.isAlive == true) {
                    Log.w(TAG, "Force killing Python process")
                    process?.destroyForcibly()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error during shutdown", e)
            } finally {
                isRunning.set(false)
                _healthState.value = BackendHealth.STOPPED
                process = null
                writer = null
                reader = null
                errorReader = null
                scope.cancel() // Cancel all coroutines started by this manager
                Log.i(TAG, "✅ Python backend stopped")
            }
        }
    }

    /**
     * 🔄 RESTART — Stop and start backend (manual restart)
     */
    fun restart() {
        Log.i(TAG, "🔄 Restarting Python backend...")
        stop()
        Thread.sleep(1000)  // Brief pause
        start()
    }

    /**
     * ❓ IS HEALTHY — Quick health check
     */
    fun isHealthy(): Boolean =
        isRunning.get() && _healthState.value in setOf(
            BackendHealth.HEALTHY,
            BackendHealth.DEGRADED
        )

    // ═══════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS — Monitoring and resilience
    // ═══════════════════════════════════════════════════════════════

    /**
     * Start coroutine to read stdout from Python process
     */
    private fun startOutputReader() {
        scope.launch {
            try {
                reader?.forEachLine { line ->
                    if (line.isNotBlank()) {
                        responseChannel.trySend(line)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Output reader died", e)
                handleProcessDeath()
            }
        }
    }

    /**
     * Start coroutine to read stderr from Python process
     */
    private fun startErrorReader() {
        scope.launch {
            try {
                errorReader?.forEachLine { line ->
                    if (line.isNotBlank()) {
                        Log.e(TAG, "Python Error: $line")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reader died", e)
            }
        }
    }

    /**
     * Start health monitoring loop
     */
    private fun startHealthMonitor() {
        scope.launch {
            while (isRunning.get()) {
                delay(config.heartbeatIntervalMs)

                if (!isRunning.get()) break

                val isHealthy = sendHeartbeat()
                if (!isHealthy) {
                    Log.w(TAG, "❌ Heartbeat failed, backend unresponsive")
                    handleProcessDeath()
                    break
                } else {
                    Log.d(TAG, "💓 Heartbeat OK")
                }
            }
        }
    }

    /**
     * Handle process death (crash or unresponsive)
     */
    private fun handleProcessDeath() {
        if (!isRunning.get()) return  // Already handled

        isRunning.set(false)
        crashCount++
        _healthState.value = BackendHealth.CRASHED

        Log.e(TAG, "💀 Python backend died (crash #$crashCount)")

        if (config.enableAutoRestart && crashCount < config.maxRestartAttempts) {
            scheduleRestart()
        } else {
            Log.e(TAG, "❌ Max restart attempts reached, giving up")
        }
    }

    /**
     * Schedule auto-restart with exponential backoff
     */
    private fun scheduleRestart() {
        val backoffMs = config.restartBackoffMs * (1 shl (crashCount - 1))  // Exponential
        val cappedBackoff = backoffMs.coerceAtMost(60_000)  // Max 1 minute

        Log.i(TAG, "🔄 Scheduling restart in ${cappedBackoff}ms (attempt $crashCount)")

        scope.launch {
            delay(cappedBackoff)
            try {
                start()
            } catch (e: Exception) {
                Log.e(TAG, "Restart failed", e)
            }
        }
    }

    /**
     * Track request latency for metrics
     */
    private fun trackLatency(latencyMs: Long) {
        synchronized(latencySamples) {
            latencySamples.add(latencyMs)
            // Keep only last 100 samples
            if (latencySamples.size > 100) {
                latencySamples.removeAt(0)
            }
        }
    }

    /**
     * Calculate average latency from samples
     */
    private fun getAverageLatency(): Long {
        return synchronized(latencySamples) {
            if (latencySamples.isEmpty()) 0
            else latencySamples.average().toLong()
        }
    }

    // Compatibility methods for legacy callers
    fun isBackendRunning(): Boolean = isHealthy()

    fun getBackendUrl(): String = "http://localhost:5000" // Default for flask

    fun startGenesisBackend(): Boolean {
        start()
        return true
    }
}

// ═══════════════════════════════════════════════════════════════════
//  END OF PYTHON PROCESS MANAGER
//  The nervous system now monitors, restarts, and self-heals
// ═══════════════════════════════════════════════════════════════════

/**
 * Usage Example (for future developers):
 *
 * ```kotlin
 * // Inject or obtain instance
 * val pythonManager: PythonProcessManager = ...
 *
 * // Start backend with custom config
 * val config = ProcessConfig(
 *     scriptPath = "/data/local/tmp/genesis_backend.py",
 *     enableAutoRestart = true,
 *     maxRestartAttempts = 3
 * )
 * pythonManager.start(config)
 *
 * // Monitor health
 * lifecycleScope.launch {
 *     pythonManager.healthState.collect { health ->
 *         when (health) {
 *             BackendHealth.HEALTHY -> showStatus("Genesis online")
 *             BackendHealth.DEGRADED -> showWarning("Genesis slow")
 *             BackendHealth.CRASHED -> showError("Genesis offline")
 *             else -> { }
 *         }
 *     }
 * }
 *
 * // Send request
 * val response = pythonManager.sendRequest(
 *     message = """{"type": "generate_text", "prompt": "Hello"}""",
 *     timeoutMs = 15_000
 * )
 *
 * // Get metrics
 * val metrics = pythonManager.getMetrics()
 * println("Uptime: ${metrics.uptimeMillis}ms, Avg Latency: ${metrics.averageLatencyMs}ms")
 *
 * // Graceful shutdown
 * pythonManager.stop()
 * ```
 */
