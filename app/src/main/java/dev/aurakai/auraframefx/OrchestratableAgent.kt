package dev.aurakai.auraframefx.orchestration

import kotlinx.coroutines.CoroutineScope

interface OrchestratableAgent {
    suspend fun initialize(scope: CoroutineScope)
    suspend fun start()
    suspend fun pause()
    suspend fun resume()
    suspend fun shutdown()
}
