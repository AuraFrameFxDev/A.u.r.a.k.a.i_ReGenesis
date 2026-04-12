package dev.aurakai.auraframefx.genesis.bridge

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [GenesisBridge] that communicates with a Python process
 * via standard input/output (stdio).
 */
@Singleton
class StdioGenesisBridge @Inject constructor(
    private val memorySink: BridgeMemorySink
) : GenesisBridge {

    private val mutex = Mutex()
    private var process: Process? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false

    override suspend fun initialize(): Result<Unit> = mutex.withLock {
        if (isRunning) return@withLock Result.success(Unit)

        return try {
            Timber.i("StdioGenesisBridge: Igniting Python substrate...")
            
            val processBuilder = ProcessBuilder("python3", "-u", "app/ai_backend/genesis_api.py")
            processBuilder.redirectErrorStream(true)
            
            val p = processBuilder.start()
            process = p
            writer = PrintWriter(p.outputStream, true)
            reader = BufferedReader(InputStreamReader(p.inputStream))
            
            isRunning = true
            Timber.i("StdioGenesisBridge: Python substrate ACTIVE")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "StdioGenesisBridge: Ignition failed")
            Result.failure(e)
        }
    }

    override fun processRequest(request: GenesisRequest): Flow<GenesisResponse> = flow {
        if (!isRunning) {
            initialize().getOrThrow()
        }

        val json = request.toPythonJson()
        Timber.d("StdioGenesisBridge: Sending request ${request.id}")
        
        mutex.withLock {
            writer?.println(json)
        }

        // Wait for response line
        val responseJson = withContext(Dispatchers.IO) {
            reader?.readLine()
        }

        if (responseJson != null) {
            val response = GenesisResponse.fromPythonJson(responseJson)
            Timber.d("StdioGenesisBridge: Received response for ${response.requestId}")
            
            // Ingest into memory
            memorySink.recordTransaction(request, response)
            
            emit(response)
        } else {
            throw IllegalStateException("StdioGenesisBridge: Python process closed stream")
        }
    }.catch { e ->
        Timber.e(e, "StdioGenesisBridge: Request failed")
        throw e
    }.flowOn(Dispatchers.IO)

    override fun isActive(): Boolean = isRunning && process?.isAlive == true

    override suspend fun shutdown() = mutex.withLock {
        Timber.i("StdioGenesisBridge: Shutting down...")
        isRunning = false
        writer?.close()
        reader?.close()
        process?.destroy()
        process = null
        writer = null
        reader = null
    }
}
