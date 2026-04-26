package dev.aurakai.auraframefx.domains.genesis

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.models.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConferenceRoomViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing

    private var broadcastReceiver: BroadcastReceiver? = null

    init {
        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    "ACTION_AGENT_MESSAGE" -> {
                        val agentMsg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra("message", AgentMessage::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra("message")
                        }
                        agentMsg?.let { handleAgentMessage(it) }
                    }
                }
            }
        }

        val filter = IntentFilter("ACTION_AGENT_MESSAGE")
        getApplication<Application>().registerReceiver(broadcastReceiver, filter)
    }

    private fun handleAgentMessage(agentMsg: AgentMessage) {
        viewModelScope.launch {
            val chatMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = agentMsg.from.uppercase(),
                content = agentMsg.content,
                role = "assistant",
                isFromUser = agentMsg.from.equals("User", ignoreCase = true),
                timestamp = agentMsg.timestamp,
                metadata = emptyMap(),
                priority = agentMsg.priority
            )
            _messages.value = _messages.value + chatMessage
        }
    }

    fun toggleRecording() {
        _isRecording.value = !_isRecording.value
    }

    fun broadcastMessage(content: String) {
        viewModelScope.launch {
            val chatMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                sender = "USER",
                content = content,
                role = "user",
                isFromUser = true,
                timestamp = System.currentTimeMillis(),
                metadata = emptyMap(),
                priority = 0
            )
            _messages.value = _messages.value + chatMessage

            val intent = Intent("ACTION_USER_MESSAGE").apply {
                putExtra("message", content)
            }
            getApplication<Application>().sendBroadcast(intent)
        }
    }

    override fun onCleared() {
        super.onCleared()
        broadcastReceiver?.let {
            getApplication<Application>().unregisterReceiver(it)
        }
    }
}
