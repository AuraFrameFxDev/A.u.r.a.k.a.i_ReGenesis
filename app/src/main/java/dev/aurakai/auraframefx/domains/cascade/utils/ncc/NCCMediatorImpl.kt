package dev.aurakai.auraframefx.domains.cascade.utils.ncc

import dev.aurakai.auraframefx.core.ncc.NCCMediator
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityRepository
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NCCMediatorImpl @Inject constructor(
    private val trinityRepository: TrinityRepository,
    private val messageBus: AgentMessageBus
) : NCCMediator {

    private val _memoriaStream = MutableSharedFlow<String>(replay = 1)
    override fun getMemoriaStream(): SharedFlow<String> = _memoriaStream.asSharedFlow()

    override fun getCurrentNCCSummary(): String {
        val state = trinityRepository.agentState.value
        return "ReGenesis Exodus v3.0 | NCC L1-L6 stable | Trinity: [Kai=${state.kaiStatus}, Aura=${state.auraStatus}, Genesis=${state.genesisStatus}] | Core LOCKED."
    }

    override fun injectIntoMemoria(content: String) {
        _memoriaStream.tryEmit(content)
        // Future: persist to L4 Gemini Memoria Stream via SpiritualChain
    }
}
