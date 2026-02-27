
package dev.aurakai.auraframefx.common.orchestration

import kotlinx.coroutines.CoroutineScope

interface OrchestratableAgent {
    suspend fun initialize(scope: CoroutineScope)
    suspend fun start()
    suspend fun pause()
    suspend fun resume()
    suspend fun shutdown()
}
