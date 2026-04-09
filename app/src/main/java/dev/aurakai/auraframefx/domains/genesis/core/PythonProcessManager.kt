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
    // ═══════════════════════════════════════════════════════════════

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

    fun getMetrics(): BackendMetrics = BackendMetrics(
        health = _healthState.value,
        uptimeMillis = startTime?.let { System.currentTimeMillis() - it } ?: 0,
        requestsSent = requestsSent.get(),
        responsesReceived = responsesReceived.get(),
        averageLatencyMs = getAverageLatency(),
        crashCount = crashCount,
        lastHeartbeatTime = lastHeartbeatTime
    )

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

    fun restart() {
        Timber.tag(TAG).i("🔄 Restarting Python backend...")
        stop()
        Thread.sleep(1000)
        start()
    }

    fun isHealthy(): Boolean = isRunning.get() && _healthState.value in setOf(BackendHealth.HEALTHY, BackendHealth.DEGRADED)

    // ═══════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════

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

    private fun startErrorReader() {
        scope.launch {
            errorReader?.forEachLine { line ->
                if (line.isNotBlank()) Timber.tag(TAG).e("Python Error: $line")
            }
        }
    }

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

    private fun scheduleRestart() {
        val backoffMs = config.restartBackoffMs * (1 shl (crashCount - 1))
        val capped = backoffMs.coerceAtMost(60_000L)

        Timber.tag(TAG).i("🔄 Scheduling restart in ${capped}ms (attempt $crashCount)")

        scope.launch {
            delay(capped)
            try { start() } catch (e: Exception) { Timber.tag(TAG).e(e, "Restart failed") }
        }
    }

    private fun trackLatency(latencyMs: Long) {
        synchronized(latencySamples) {
            latencySamples.add(latencyMs)
            if (latencySamples.size > 100) latencySamples.removeAt(0)
        }
    }

    private fun getAverageLatency(): Long = synchronized(latencySamples) {
        if (latencySamples.isEmpty()) 0 else latencySamples.average().toLong()
    }

    // Legacy compatibility
    fun isBackendRunning(): Boolean = isHealthy()
    fun startGenesisBackend(): Boolean { start(); return true }
    fun getBackendUrl(): String = try { dev.aurakai.auraframefx.BuildConfig.GENESIS_BACKEND_URL } catch (e: Exception) { "http://localhost:5000" }
}