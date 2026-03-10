package dev.aurakai.auraframefx.ui.gates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveSandbox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Aura's Lab (Sandbox Environment)
 *
 * Manages sandbox creation, deletion, and interaction with OracleDriveSandbox system
 */
@HiltViewModel
open class AurasLabViewModel @Inject constructor(
    private val oracleDriveSandbox: OracleDriveSandbox
) : ViewModel() {

    private val _sandboxes = MutableStateFlow<List<SandboxItem>>(emptyList())
    val sandboxes: StateFlow<List<SandboxItem>> = _sandboxes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        initializeSandboxSystem()
        loadSandboxes()
    }

    /**
     * Initialize the OracleDrive Sandbox system
     */
    private fun initializeSandboxSystem() {
        viewModelScope.launch {
            try {
                Timber.d("Initializing OracleDrive Sandbox system")
                val result = oracleDriveSandbox.initialize()

                if (result.success) {
                    Timber.i("✅ Sandbox system initialized: ${result.message}")
                    _statusMessage.value = result.message
                } else {
                    Timber.e("❌ Sandbox initialization failed: ${result.message}")
                    _statusMessage.value = "Sandbox initialization failed"
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize sandbox system")
                _statusMessage.value = "Error initializing sandbox system"
            }
        }
    }

    /**
     * Load active sandboxes from OracleDriveSandbox
     */
    private fun loadSandboxes() {
        viewModelScope.launch {
            oracleDriveSandbox.activeSandboxes.collect { sandboxes ->
                _sandboxes.value = sandboxes.map { env ->
                    SandboxItem(
                        name = env.name,
                        type = env.type.name,
                        safetyLevel = env.safetyLevel.name,
                        isActive = env.isActive
                    )
                }
                Timber.d("Loaded ${sandboxes.size} active sandboxes")
            }
        }
    }

    /**
     * Create a new sandbox environment
     *
     * @param name Display name for the sandbox
     * @param type Sandbox type (UI_THEMING, PERFORMANCE, SECURITY_TESTING, etc.)
     */
    fun createSandbox(name: String, type: String = "UI_THEMING") {
        viewModelScope.launch {
            try {
                Timber.d("Creating new sandbox: $name (type: $type)")

                val sandboxType = when (type.uppercase()) {
                    "UI_THEMING" -> OracleDriveSandbox.SandboxType.UI_THEMING
                    "PERFORMANCE" -> OracleDriveSandbox.SandboxType.PERFORMANCE_TUNING
                    "SECURITY" -> OracleDriveSandbox.SandboxType.SECURITY_TESTING
                    "CUSTOM_ROM" -> OracleDriveSandbox.SandboxType.CUSTOM_ROM
                    else -> OracleDriveSandbox.SandboxType.SYSTEM_MODIFICATION
                }

                val result = oracleDriveSandbox.createSandbox(
                    name = name,
                    type = sandboxType,
                    description = "Aura's experimental sandbox: $name"
                )

                if (result.success) {
                    Timber.i("✅ Sandbox created successfully: $name")
                    _statusMessage.value = "Created sandbox: $name"
                } else {
                    Timber.e("❌ Failed to create sandbox: ${result.message}")
                    _statusMessage.value = "Failed to create sandbox"
                }
            } catch (e: Exception) {
                Timber.e(e, "Error creating sandbox")
                _statusMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Import a sandbox from external source
     * (Placeholder for future implementation)
     */
    fun importSandbox(path: String) {
        viewModelScope.launch {
            Timber.d("Import sandbox from: $path")
            _statusMessage.value = "Import feature coming soon!"
            // TODO: Implement sandbox import from file/URI
        }
    }

    /**
     * Clear status message
     */
    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
