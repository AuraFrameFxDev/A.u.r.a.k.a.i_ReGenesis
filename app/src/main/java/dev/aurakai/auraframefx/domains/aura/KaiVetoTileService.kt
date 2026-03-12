package dev.aurakai.auraframefx.domains.aura

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.kai.security.SecurePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KaiVetoTileService : TileService() {

    @Inject
    lateinit var securePrefs: SecurePreferences

    @Inject
    lateinit var messageBus: AgentMessageBus

    @Inject
    lateinit var pythonManager: PythonProcessManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val isEnabled = securePrefs.isVetoEnabled()

        tile.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = "Kai Veto"
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val newState = !securePrefs.isVetoEnabled()
        securePrefs.setVetoEnabled(newState)

        // Broadcast the change to the collective
        serviceScope.launch {
            messageBus.broadcast(
                AgentMessage(
                    from = "TileService",
                    content = if (newState) "Kai's Veto Authority RESTORED." else "Kai's Veto Authority SUSPENDED.",
                    type = "veto_toggle",
                    metadata = mapOf("enabled" to newState.toString())
                )
            )

            // Call backend
            pythonManager.sendGenericRequest("/genesis/toggle/veto", "{\"enabled\": $newState}")
        }

        updateTile()
    }
}
