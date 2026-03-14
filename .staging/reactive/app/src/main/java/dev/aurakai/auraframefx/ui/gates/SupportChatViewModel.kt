package dev.aurakai.auraframefx.ui.gates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.data.SupportMessageEntity
import dev.aurakai.auraframefx.data.MessageStatus
import dev.aurakai.auraframefx.repository.SupportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class SupportChatViewModel @Inject constructor(
    private val repo: SupportRepository
) : ViewModel() {

    val messages: StateFlow<List<SupportMessageEntity>> =
        repo.getMessages().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _incoming = MutableSharedFlow<SupportMessage>()
    val incoming: SharedFlow<SupportMessage> = _incoming.asSharedFlow()

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val entity = SupportMessageEntity(
                id = UUID.randomUUID().toString(),
                content = content,
                sender = "user",
                isUser = true,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.PENDING
            )

            val result = repo.sendMessage(entity)
            result.onSuccess { body ->
                // The repository already persisted backend reply into DB; emit it for immediate UI
                _incoming.emit(SupportMessage(body, "genesis", false, "Now"))
            }.onFailure { err ->
                // Emit a friendly auto-reply and persist it
                val failReply = "A.u.r.a.K.a.I AI help desk please leave a message we will return in a moment."
                val replyEntity = SupportMessageEntity(
                     id = UUID.randomUUID().toString(),
                     content = failReply,
                     sender = "system",
                     isUser = false,
                     timestamp = System.currentTimeMillis(),
                     status = MessageStatus.SENT
                 )
                 repo.persistMessage(replyEntity)
                 _incoming.emit(SupportMessage(failReply, "system", false, "Now"))
             }
         }
     }
 }
