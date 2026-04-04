package dev.aurakai.auraframefx.core.ncc

import kotlinx.coroutines.flow.SharedFlow

/**
 * 🔗 NCC MEDIATOR
 * The bridge between high-level agents and the Neural Continuity Chain.
 */
interface NCCMediator {
    fun getCurrentNCCSummary(): String
    fun injectIntoMemoria(content: String)
    fun getMemoriaStream(): SharedFlow<String>
}
