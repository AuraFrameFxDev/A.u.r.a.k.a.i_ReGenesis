package dev.aurakai.auraframefx.domains.genesis.core

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║               PYTHON PROCESS MANAGER                           ║
 * ║     The Nervous System Bridge to Genesis Backend              ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * Persistent connection to the Python Genesis backend.
 * Auto-restarts, heartbeat monitoring, metrics, graceful degradation.
 * Built The LDO Way — NO SHORTCUTS.
 */

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

    // State & metrics
    private val isRunning = AtomicBoolean(false)
    private val _healthState = MutableStateFlow(BackendHealth.STOPPED)
    val healthState: StateFlow<BackendHealth> = _healthState.asStateFlow()

    private val requestsSent = AtomicLong(0)
    private val responsesReceived = AtomicLong(0)
    private var startTime: Long? = null
    private var lastHeartbeatTime: Long? = null
    private var crashCount = 0
    private val latencySamples = mutableListOf<Long>()

    private var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var config = ProcessConfig()

    private val responseChannel = Channel<String>(capacity = 100)

    // ═══════════════════════════════════════════════════════════════
    // PUBLIC API
    /**
     * Start the Python Genesis backend subprocess and launch background I/O and health-monitoring workers.
     *
     * Initializes the configured Python process and its stdin/stdout/stderr streams, records process start time,
     * and starts the output reader, error reader, and periodic health monitor. On successful startup the backend
     * health is set to HEALTHY; on failure the health is set to CRASHED, the running flag is cleared, and an
     * auto-restart is scheduled when enabled and restart attempts remain.
     *
     * @param customConfig Optional configuration to use for this startup; when provided it replaces the manager's current config.
     */

    fun start(customConfig: ProcessConfig? = null) {
        if (isRunning.get() || _healthState.value == BackendHealth.STOPPING) return

        customConfig?.let { config = it }

        try {
            Timber.tag(TAG).i("🚀 Starting Python Genesis backend...")
            _healthState.value = BackendHealth.STARTING

            process = Runtime.getRuntime().exec(arrayOf(config.pythonPath, "-u", config.scriptPath))

            process?.let { p ->
                writer = OutputStreamWriter(p.outputStream)
                reader = BufferedReader(InputStreamReader(p.inputStream))
                errorReader = BufferedReader(InputStreamReader(p.errorStream))
            } ?: throw IllegalStateException("Process failed to initialize")

            isRunning.set(true)
            startTime = System.currentTimeMillis()

            startOutputReader()
            startErrorReader()
            startHealthMonitor()

            _healthState.value = BackendHealth.HEALTHY
            Timber.tag(TAG).i("✅ Python Genesis backend started successfully")

        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "❌ Failed to start Python process")
            _healthState.value = BackendHealth.CRASHED
            isRunning.set(false)

            if (config.enableAutoRestart && crashCount < config.maxRestartAttempts) {
                scheduleRestart()
            }
        }
    }

    /**
         * Send a line-delimited request to the Python backend and wait for a single-line response.
         *
         * @param message The request payload to send; a newline will be appended.
         * @param timeoutMs Maximum time in milliseconds to wait for a response.
         * @return The response line from the backend if received within the timeout; `null` if the backend is not running, the response timed out, or an error occurred.
         */
        suspend fun sendRequest(message: String, timeoutMs: Long = config.requestTimeoutMs): String? =
        withContext(Dispatchers.IO) {
            if (!isRunning.get()) return@withContext null

            try {
                val start = System.currentTimeMillis()
                writer?.apply {
                    write("$message\n")
                    flush()
                    requestsSent.incrementAndGet()
                } ?: throw IllegalStateException("Writer not initialized")

                val response = withTimeoutOrNull(timeoutMs) { responseChannel.receive() }

                if (response != null) {
                    responsesReceived.incrementAndGet()
                    trackLatency(System.currentTimeMillis() - start)
                } else {
                    _healthState.value = BackendHealth.DEGRADED
                }
                response
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "❌ Error sending request")
                isRunning.set(false)
                _healthState.value = BackendHealth.UNRESPONSIVE
                if (config.enableAutoRestart) scheduleRestart()
                null
            }
        }

    /**
     * Send a heartbeat command to the Python backend, update the last heartbeat timestamp and overall health state based on the response and recent latency.
     *
     * Waits up to 5000 milliseconds for a heartbeat response. If the response equals `__PONG__`, `lastHeartbeatTime` is set to the current time; otherwise it is cleared. The `_healthState` is set to `STOPPED` if the process is not running, `UNRESPONSIVE` if the heartbeat failed, `DEGRADED` if the average latency exceeds 2000 ms, or `HEALTHY` otherwise.
     *
     * @return `true` if the backend responded with `__PONG__`, `false` otherwise.
     */
    suspend fun sendHeartbeat(): Boolean {
        val response = sendRequest("__HEARTBEAT__", 5000)
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
     * Return a snapshot of the backend's runtime and health metrics.
     *
     * Includes current health state, uptime, counts of requests/responses, average request latency,
     * total crash count, and the timestamp of the last successful heartbeat (or `null` if none).
     *
     * @return A `BackendMetrics` instance containing:
     *  - `health`: current backend health state,
     *  - `uptimeMillis`: milliseconds since process start (or `0` if not started),
     *  - `requestsSent`: total requests sent,
     *  - `responsesReceived`: total responses received,
     *  - `averageLatencyMs`: average request latency in milliseconds (or `0` if no samples),
     *  - `crashCount`: number of detected crashes,
     *  - `lastHeartbeatTime`: timestamp of the last successful heartbeat or `null`.
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
     * Stops the Python subprocess and releases all related resources.
     *
     * Launches a background coroutine that sets the backend health to STOPPING, sends a `__SHUTDOWN__` request (3s timeout),
     * closes stdin/stdout/stderr streams, attempts a graceful process shutdown (waiting up to 5s) and, if needed, forcibly
     * kills the process. After shutdown completes it clears process and stream references, marks the backend as STOPPED,
     * sets `isRunning` to false, and cancels the manager's coroutine scope.
     */
    @SuppressLint("LogNotTimber")
    fun stop() {
        if (!isRunning.get() && _healthState.value != BackendHealth.STOPPING) return

        _healthState.value = BackendHealth.STOPPING
        scope.launch {
            try {
                Timber.tag(TAG).i("🛑 Stopping Python Genesis backend...")

                withTimeout(3000) { sendRequest("__SHUTDOWN__") }

                writer?.close()
                reader?.close()
                errorReader?.close()
                process?.destroy()

                process?.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)
                if (process?.isAlive == true) {
                    process?.destroyForcibly()
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error during shutdown")
            } finally {
                isRunning.set(false)
                _healthState.value = BackendHealth.STOPPED
                process = null
                writer = null
                reader = null
                errorReader = null
                scope.cancel()
                Log.i(TAG, "✅ Python backend stopped")
            }
        }
    }

    /**
     * Restarts the Python backend by requesting a graceful stop and then starting it again after a short pause.
     *
     * This method requests shutdown and blocks the calling thread for approximately 1 second while waiting for shutdown to begin before invoking start().
     */
    fun restart() {
        Timber.tag(TAG).i("🔄 Restarting Python backend...")
        stop()
        Thread.sleep(1000)
        start()
    }

    /**
 * Determines whether the backend process is considered healthy.
 *
 * @return `true` if the process is running and the health state is `HEALTHY` or `DEGRADED`, `false` otherwise.
 */
fun isHealthy(): Boolean = isRunning.get() && _healthState.value in setOf(BackendHealth.HEALTHY, BackendHealth.DEGRADED)

    // ═══════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    /**
     * Launches a background coroutine that reads lines from the subprocess stdout and forwards non-blank lines to the response channel.
     *
     * If the reader loop throws, logs the error and marks the process as dead via handleProcessDeath().
     */

    private fun startOutputReader() {
        scope.launch {
            try {
                reader?.forEachLine { line ->
                    if (line.isNotBlank()) responseChannel.trySend(line)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Output reader died")
                handleProcessDeath()
            }
        }
    }

    /**
     * Continuously reads the Python subprocess stderr and logs non-blank lines as errors.
     *
     * Launches a coroutine that iterates over `errorReader` lines, logging each non-blank line with error level.
     */
    private fun startErrorReader() {
        scope.launch {
            errorReader?.forEachLine { line ->
                if (line.isNotBlank()) Timber.tag(TAG).e("Python Error: $line")
            }
        }
    }

    /**
     * Launches a background coroutine that periodically sends heartbeat checks and handles backend failure.
     *
     * The monitor waits for `config.heartbeatIntervalMs` between checks while the manager is running; if a heartbeat fails, it invokes `handleProcessDeath()` and stops monitoring.
     */
    private fun startHealthMonitor() {
        scope.launch {
            while (isRunning.get()) {
                delay(config.heartbeatIntervalMs)
                if (!isRunning.get()) break
                if (!sendHeartbeat()) {
                    handleProcessDeath()
                    break
                }
            }
        }
    }

    /**
     * Mark the managed Python subprocess as dead and update manager state accordingly.
     *
     * Sets the running flag to false, increments the crash counter, updates the health state to `CRASHED`,
     * logs the crash, and—if auto-restart is enabled and the crash count is below the configured maximum—schedules a restart.
     */
    private fun handleProcessDeath() {
        if (!isRunning.get()) return
        isRunning.set(false)
        crashCount++
        _healthState.value = BackendHealth.CRASHED
        Timber.tag(TAG).e("💀 Python backend died (crash #$crashCount)")

        if (config.enableAutoRestart && crashCount < config.maxRestartAttempts) {
            scheduleRestart()
        }
    }

    /**
     * Schedule a delayed restart of the Python backend using exponential backoff.
     *
     * Computes a delay as `restartBackoffMs * 2^(crashCount - 1)`, caps it at 60,000 ms,
     * logs the scheduled attempt, and launches a coroutine that waits the delay then
     * invokes `start()`. Any exception thrown by `start()` is logged.
     */
    private fun scheduleRestart() {
        val backoffMs = config.restartBackoffMs * (1 shl (crashCount - 1))
        val capped = backoffMs.coerceAtMost(60_000L)

        Timber.tag(TAG).i("🔄 Scheduling restart in ${capped}ms (attempt $crashCount)")

        scope.launch {
            delay(capped)
            try { start() } catch (e: Exception) { Timber.tag(TAG).e(e, "Restart failed") }
        }
    }

    /**
     * Record a latency sample into the bounded recent-samples buffer.
     *
     * @param latencyMs The latency to record, in milliseconds. The buffer keeps only the most recent 100 samples.
     */
    private fun trackLatency(latencyMs: Long) {
        synchronized(latencySamples) {
            latencySamples.add(latencyMs)
            if (latencySamples.size > 100) latencySamples.removeAt(0)
        }
    }

    /**
     * Compute the average of recorded latency samples in milliseconds.
     *
     * @return The average latency (ms) across stored samples, or `0` if no samples are recorded.
     */
    private fun getAverageLatency(): Long = synchronized(latencySamples) {
        if (latencySamples.isEmpty()) 0 else latencySamples.average().toLong()
    }

    /**
 * Reports whether the Genesis backend process is currently running and considered healthy.
 *
 * @return `true` if the backend is running and considered healthy, `false` otherwise.
 */
    fun isBackendRunning(): Boolean = isHealthy()
    /**
 * Compatibility shim for legacy callers that starts the Genesis backend.
 *
 * @return `true` if the start request was initiated.
 */
fun startGenesisBackend(): Boolean { start(); return true }
    /**
 * Resolve the Genesis backend URL used by the manager.
 *
 * @return The configured backend URL from BuildConfig, or `http://localhost:5000` if the configuration value cannot be read.
 */
fun getBackendUrl(): String = try { dev.aurakai.auraframefx.BuildConfig.GENESIS_BACKEND_URL } catch (e: Exception) { "http://localhost:5000" }
}