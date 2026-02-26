package dev.aurakai.auraframefx.romtools

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Removed: import com.google.firebase.vertexai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Lightweight ViewModel wrapper that exposes the RomToolsManager state flows for Compose.
 * Uses Hilt for injection of RomToolsManager.
 */
@HiltViewModel
class RomToolsViewModel @Inject constructor(
    val romToolsManager: RomToolsManager
) : ViewModel() {
    val romToolsState: StateFlow<RomToolsState> = romToolsManager.romToolsState
    val operationProgress: StateFlow<OperationProgress?> = romToolsManager.operationProgress

    private val _lastResponse = MutableStateFlow<AgentResponse?>(null)
    val lastResponse: StateFlow<AgentResponse?> = _lastResponse.asStateFlow()

    fun performOperation(operation: RomOperation, context: Context, uri: Uri? = null) {
        viewModelScope.launch {
            val request = RomOperationRequest(
                operation = operation,
                uri = uri,
                context = context
            )

            try {
                val response = romToolsManager.processRomOperation(request)
                _lastResponse.value = response
                if (response.isSuccess) {
                    Timber.i("Operation ${operation.javaClass.simpleName} succeeded: ${response.content}")
                } else {
                    Timber.e("Operation ${operation.javaClass.simpleName} failed: ${response.error}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error performing ROM operation")
                _lastResponse.value = AgentResponse.error(
                    message = e.message ?: "Unknown error",
                    agentName = "RomTools",
                    agentType = AgentType.GENESIS
                )
            }
        }
    }

    fun clearResponse() {
        _lastResponse.value = null
    }
}
