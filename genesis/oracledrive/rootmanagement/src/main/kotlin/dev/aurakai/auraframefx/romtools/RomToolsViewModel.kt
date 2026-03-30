package dev.aurakai.auraframefx.romtools

// Removed: import com.google.firebase.vertexai.type.content
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.UnlockTier
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
    val romToolsManager: RomToolsManager,
    private val pandoraBoxService: PandoraBoxService
) : ViewModel() {
    val romToolsState: StateFlow<RomToolsState> = romToolsManager.romToolsState
    val operationProgress: StateFlow<OperationProgress?> = romToolsManager.operationProgress
    val pandoraState = pandoraBoxService.getCurrentState()

    fun isOperationLocked(operation: RomOperation): Boolean {
        val tier = pandoraState.value.currentTier.level
        return when (operation) {
            is RomOperation.FlashRom, is RomOperation.RestoreBackup, is RomOperation.CreateBackup -> 
                tier < UnlockTier.System.level
            is RomOperation.UnlockBootloader, is RomOperation.GenesisOptimizations -> 
                tier < UnlockTier.Sovereign.level
            else -> false
        }
    }

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
                    Timber.i("Operation ${operation::class.simpleName} succeeded: ${response.content}")
                } else {
                    Timber.e("Operation ${operation::class.simpleName} failed: ${response.error}")
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
