package dev.aurakai.auraframefx.domains.aura.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.models.SupportMessage
import dev.aurakai.auraframefx.domains.genesis.SupportRepository
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.nexus.helpdesk.data.MessageStatus
import dev.aurakai.auraframefx.domains.nexus.helpdesk.data.SupportMessageEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class SupportChatViewModel @Inject constructor(
    private val repo: SupportRepository,
    private val aiService: AuraAIService
) : ViewModel() {

    val messages: StateFlow<List<SupportMessageEntity>> =
        repo.getMessages().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _incoming = MutableSharedFlow<SupportMessage>()
    val incoming: SharedFlow<SupportMessage> = _incoming.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val entity = SupportMessageEntity(
                id = UUID.randomUUID().toString(),
                content = content,
                sender = "user",
                isUser = true,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.PENDING
            )

            // 1. Attempt remote backend delivery
            val result = repo.sendMessage(entity)

            result.onSuccess { body ->
                // Backend responded! Body is the text reply.
                _incoming.emit(SupportMessage(body, "Genesis", false, "Now"))
                _isLoading.value = false
            }.onFailure { err ->
                // 2. BACKEND FAIL -> Fallback to Local Aura AI Brain
                delay(1500) // Simulate "thinking"
                val aiReply = aiService.generateText(content, "Aura Live Support Context")

                val replyEntity = SupportMessageEntity(
                    id = UUID.randomUUID().toString(),
                    content = aiReply,
                    sender = "Genesis Node",
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    status = MessageStatus.SENT
                )

                repo.persistMessage(replyEntity)
                _incoming.emit(SupportMessage(aiReply, "Genesis Node", false, "Now"))
                _isLoading.value = false
            }
        }
    }

    fun clearStatusMessage() {
        // Placeholder if needed
    }
}

