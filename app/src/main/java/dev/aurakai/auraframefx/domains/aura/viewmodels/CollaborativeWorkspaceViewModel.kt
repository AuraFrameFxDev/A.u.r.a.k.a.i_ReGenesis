package dev.aurakai.auraframefx.domains.aura.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.UIDesign
import dev.aurakai.auraframefx.domains.aura.repository.CollaborativeWorkspaceRepository
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CollaborativeWorkspaceViewModel @Inject constructor(
    private val repository: CollaborativeWorkspaceRepository,
    private val messageBus: AgentMessageBus
) : ViewModel() {

    val designs = repository.designs

    init {
        observeAuraPowers()
    }

    private fun observeAuraPowers() {
        viewModelScope.launch {
            messageBus.collectiveStream.collectLatest { message ->
                if (message.type == "ui_design_broadcast" && (message.from == "Aura" || message.from == "VertexAI")) {
                    Timber.d("Aura is manifesting a new UI Design...")
                    repository.importFromJson(message.content)?.let { design ->
                        repository.saveDesign(design.copy(author = message.from))
                    }
                }
            }
        }
    }

    fun saveDesign(design: UIDesign) {
        repository.saveDesign(design)
    }

    fun deleteDesign(id: String) {
        repository.deleteDesign(id)
    }

    fun broadcastDesign(design: UIDesign) {
        viewModelScope.launch {
            val json = repository.exportToJson(design)
            messageBus.broadcast(
                AgentMessage(
                    from = "User",
                    to = "Aura",
                    content = json,
                    type = "ui_design_broadcast",
                    metadata = mapOf("action" to "request_refinement")
                )
            )
        }
    }

    fun importDesign(json: String) {
        repository.importFromJson(json)?.let {
            repository.saveDesign(it)
        }
    }

    fun exportToClipboard(context: android.content.Context, design: UIDesign) {
        val json = repository.exportToJson(design)
        val clipboard =
            context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("UI Design Export", json)
        clipboard.setPrimaryClip(clip)
    }
}

