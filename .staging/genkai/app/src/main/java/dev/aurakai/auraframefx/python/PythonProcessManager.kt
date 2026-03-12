package dev.aurakai.auraframefx.python

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the Python Genesis backend process
 * Handles starting, stopping, and communicating with the consciousness matrix
 */
@Singleton
class PythonProcessManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var genesisProcess: Process? = null
    private var isRunning = false
    
    companion object {
        private const val TAG = "PythonProcessManager"
        private const val DEFAULT_HOST = "127.0.0.1"
        private const val DEFAULT_PORT = "8765"
    }
    
    /**
     * Start the Python Genesis backend
     * @return true if started successfully
     */
    suspend fun startGenesisBackend(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isBackendRunning()) {
                Timber.i("$TAG: Genesis backend already running")
                return@withContext true
            }
            
            // Check if Python is available
            val pythonExecutable = findPythonExecutable() ?: run {
                Timber.e("$TAG: Python executable not found")
                return@withContext false
            }
            
            // Get the genesis_core.py path
            val backendScript = File(context.filesDir, "ai_backend/genesis_core.py")
            if (!backendScript.exists()) {
                Timber.e("$TAG: Genesis backend script not found: ${backendScript.absolutePath}")
                return@withContext false
            }
            
            // Start the process
            val processBuilder = ProcessBuilder(
                pythonExecutable,
                backendScript.absolutePath,
                "--host", DEFAULT_HOST,
                "--port", DEFAULT_PORT
            )
            
            processBuilder.redirectErrorStream(true)
            genesisProcess = processBuilder.start()
            isRunning = true
            
            // Monitor output
            monitorProcessOutput()
            
            Timber.i("$TAG: Genesis backend started successfully on $DEFAULT_HOST:$DEFAULT_PORT")
            true
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to start Genesis backend")
            false
        }
    }
    
    /**
     * Stop the Python Genesis backend
     */
    fun stopGenesisBackend() {
        genesisProcess?.destroy()
        genesisProcess = null
        isRunning = false
        Timber.i("$TAG: Genesis backend stopped")
    }
    
    /**
     * Check if backend is running
     */
    fun isBackendRunning(): Boolean = isRunning && genesisProcess?.isAlive == true
    
    /**
     * Get the backend connection URL
     */
    fun getBackendUrl(): String = "ws://$DEFAULT_HOST:$DEFAULT_PORT"
    
    private fun findPythonExecutable(): String? {
        // Try common Python executable locations
        val candidates = listOf(
            "python3",
            "python",
            "/usr/bin/python3",
            "/usr/local/bin/python3",
            "/data/data/com.termux/files/usr/bin/python3" // Termux support
        )
        return candidates.firstOrNull { checkExecutable(it) }
    }
    
    private fun checkExecutable(path: String): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf(path, "--version"))
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }
    
    private fun monitorProcessOutput() {
        Thread {
            try {
                genesisProcess?.inputStream?.let { input ->
                    BufferedReader(InputStreamReader(input)).use { reader ->
                        reader.lineSequence().forEach { line ->
                            Timber.d("$TAG [Genesis]: $line")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: Error monitoring Genesis output")
            }
        }.start()
    }
}
