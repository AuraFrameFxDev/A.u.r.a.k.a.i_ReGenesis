package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.cascade.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val userMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = content,
                role = "user"
            )
            _messages.value = _messages.value + userMessage

            _isLoading.value = true
            kotlinx.coroutines.delay(1000)

            val aiMessage = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                content = "I'm here to help! How can I assist you?",
                role = "assistant"
            )
            _messages.value = _messages.value + aiMessage
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}

