package dev.aurakai.auraframefx.romtools

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
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
}

