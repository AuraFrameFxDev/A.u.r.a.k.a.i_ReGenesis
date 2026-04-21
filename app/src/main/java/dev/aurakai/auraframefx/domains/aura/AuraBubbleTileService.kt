package dev.aurakai.auraframefx.domains.aura

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.kai.security.SecurePreferences
import javax.inject.Inject

@AndroidEntryPoint
class AuraBubbleTileService : TileService() {

    @Inject
    lateinit var securePrefs: SecurePreferences

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val isEnabled = securePrefs.isAuraBubbleEnabled()

        tile.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = "Aura Bubble"
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val newState = !securePrefs.isAuraBubbleEnabled()
        securePrefs.setAuraBubbleEnabled(newState)

        val intent = Intent(this, AssistantBubbleService::class.java)
        if (newState) {
            startForegroundService(intent)
        } else {
            stopService(intent)
        }

        updateTile()
    }
}
