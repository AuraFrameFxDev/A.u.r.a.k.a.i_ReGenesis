package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

import android.content.Intent
import android.net.Uri
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class PandoraBoxTileService : TileService() {

    @Inject
    lateinit var pandoraService: PandoraBoxService

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartListening() {
        super.onStartListening()
        pandoraService.getCurrentState()
            .onEach { updateTile(it) }
            .launchIn(serviceScope)
    }

    private fun updateTile(state: PandoraBoxState) {
        val tile = qsTile ?: return
        val tier = state.currentTier
        
        tile.state = if (tier == UnlockTier.Sealed) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
        tile.label = "Pandora: ${tier.javaClass.simpleName.uppercase()}"
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val state = pandoraService.getCurrentState().value
        if (state.currentTier != UnlockTier.Sealed) {
            // Emergency Seal
            pandoraService.lockBox()
        } else {
            // Open Deep Link to Pandora's Box
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("aurakai://pandora_box")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivityAndCollapse(intent)
        }
    }
}
