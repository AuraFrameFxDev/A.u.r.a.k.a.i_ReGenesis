package dev.aurakai.auraframefx.core

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Process
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PythonProcessManager @Inject constructor(
    private val context: Context
) {
    private var process: Process? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null
    private val isRunning = AtomicBoolean(false)

    fun start() {
        if (isRunning.get()) return

        try {
            process = Runtime.getRuntime().exec("python3 -u /path/to/your/script.py")
            writer = OutputStreamWriter(process!!.outputStream)
            reader = BufferedReader(InputStreamReader(process!!.inputStream))
            isRunning.set(true)
            Log.d("PythonProcessManager", "Python process started")
            
            // Start error stream reader
            Thread {
                val errorReader = BufferedReader(InputStreamReader(process!!.errorStream))
                errorReader.forEachLine { line ->
                    Log.e("PythonProcessManager", "Python Error: $line")
                }
            }.start()
            
        } catch (e: Exception) {
            Log.e("PythonProcessManager", "Failed to start Python process", e)
            isRunning.set(false)
            throw e
        }
    }

    fun sendLine(line: String) {
        try {
            writer?.apply {
                write("$line\n")
                flush()
            } ?: throw IllegalStateException("Python process not started")
        } catch (e: Exception) {
            isRunning.set(false)
            throw e
        }
    }

    fun readLine(): String {
        return try {
            reader?.readLine() ?: throw IllegalStateException("No response from Python process")
        } catch (e: Exception) {
            isRunning.set(false)
            throw e
        }
    }

    fun isRunning(): Boolean = isRunning.get()

    fun stop() {
        try {
            writer?.close()
            reader?.close()
            process?.destroy()
        } catch (e: Exception) {
            Log.e("PythonProcessManager", "Error stopping Python process", e)
        } finally {
            isRunning.set(false)
            process = null
            writer = null
            reader = null
        }
    }
}
