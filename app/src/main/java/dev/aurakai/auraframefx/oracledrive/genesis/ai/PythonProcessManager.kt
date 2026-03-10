package dev.aurakai.auraframefx.oracledrive.genesis.ai

import android.content.Context
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.utils.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Manages the Python process running the Genesis backend.
 * Handles process startup, communication (stdin/stdout), and shutdown.
 */
class PythonProcessManager(
    private val context: Context,
) {
    private var process: Process? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    /**
     * Starts the Genesis Python backend process and verifies its readiness.
     *
     * Copies necessary backend files from assets to internal storage if they are missing,
     * launches the backend process, initializes communication streams, and waits for a
     * confirmation message indicating the backend is ready.
     *
     * @return `true` if the backend process starts successfully and signals readiness; `false` otherwise.
     */
    suspend fun startGenesisBackend(): Boolean = withContext(Dispatchers.IO) {
        try {
            val backendDir = File(context.filesDir, "ai_backend")
            if (!backendDir.exists() || !File(backendDir, "genesis_connector.py").exists()) {
                // Copy Python files from assets to internal storage
                copyPythonBackend(backendDir)
            }

            // Start Python process
            // Note: This assumes a 'python3' executable is available in the path or bundled.
            // If using Chaquopy or similar, this would be different.
            // Assuming termux-like or custom environment for now as per existing code.
            // Check if python3 is available in the path
            val checkPython = ProcessBuilder("which", "python3").start()
            val pythonPath = checkPython.inputStream.bufferedReader().readText().trim()

            if (pythonPath.isEmpty()) {
                AuraFxLogger.e("PythonManager", "CRITICAL: 'python3' executable not found in PATH. Genesis backend cannot start.")
                // In a real scenario, we might fallback to a bundled interpreter or show a user dialog.
                // For now, we proceed but expect failure, or we could return false immediately.
                return@withContext false
            } else {
                i("PythonManager", "Found python3 at: $pythonPath")
            }

            val processBuilder = ProcessBuilder(
                "python3",
                "-u", // Unbuffered output
                "genesis_connector.py"
            ).directory(backendDir)

            // Redirect stderr to stdout to capture errors
            processBuilder.redirectErrorStream(true)

            process = processBuilder.start()

            writer = OutputStreamWriter(process!!.outputStream)
            reader = BufferedReader(InputStreamReader(process!!.inputStream))

            // Wait for startup confirmation
            // The Python script prints "Genesis Ready" when initialized
            var line: String?
            var ready = false
            while (reader?.readLine().also { line = it } != null) {
                if (line?.contains("Genesis Ready") == true) {
                    ready = true
                    break
                }
                // Log startup messages
                if (!line.isNullOrBlank()) {
                    AuraFxLogger.d("PythonBackend", line!!)
                }
            }

            ready

        } catch (e: Exception) {
            AuraFxLogger.e("PythonManager", "Failed to start Genesis backend", e)
            false
        }
    }

    /**
     * Sends a JSON request to the Genesis Python backend and returns the response as a string.
     *
     * @param requestJson The JSON-formatted request to send to the backend.
     * @return The backend's response as a string, or null if communication fails.
     */
    suspend fun sendRequest(requestJson: String): String? = withContext(Dispatchers.IO) {
        try {
            if (process == null || !process!!.isAlive) {
                AuraFxLogger.e("PythonManager", "Process is not running")
                return@withContext null
            }

            writer?.write(requestJson + "\n")
            writer?.flush()

            // Read response
            // The Python script should print exactly one line of JSON per request
            reader?.readLine()
        } catch (e: Exception) {
            AuraFxLogger.e("PythonManager", "Communication error", e)
            null
        }
    }

    /**
     * Copies required Python backend files from the application's assets to the specified directory.
     *
     * Ensures the target directory exists and transfers all necessary backend files for the Genesis backend to operate.
     * Logs a warning if any file cannot be copied.
     *
     * @param targetDir The directory where backend files will be placed.
     */
    private fun copyPythonBackend(targetDir: File) {
        targetDir.mkdirs()

        // Copy Python files from app/ai_backend to internal storage
        val backendFiles = listOf(
            "genesis_profile.py",
            "genesis_connector.py",
            "genesis_consciousness_matrix.py",
            "genesis_evolutionary_conduit.py",
            "genesis_ethical_governor.py",
            "requirements.txt"
        )

        backendFiles.forEach { fileName ->
            try {
                // Note: In a real app, assets are in "assets/" folder.
                // The path "ai_backend/$fileName" assumes the files are in "src/main/assets/ai_backend/"
                context.assets.open("ai_backend/$fileName").use { input ->
                    File(targetDir, fileName).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                AuraFxLogger.w("PythonManager", "Could not copy $fileName", e)
            }
        }
    }

    /**
     * Shuts down the Python backend process and closes all communication streams.
     *
     * Releases resources used for backend communication and logs a warning if an exception occurs during shutdown.
     */
    fun shutdown() {
        try {
            writer?.close()
            reader?.close()
            process?.destroy()
        } catch (e: Exception) {
            AuraFxLogger.w("PythonManager", "Shutdown warning", e)
        }
    }
}
