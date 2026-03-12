package dev.aurakai.auraframefx.domains.aura

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AmbientMusicService @Inject constructor() : Service() {
    /**
     * Called when a client attempts to bind to the service.
     *
     * Always returns null, indicating that binding is not supported for this service.
     *
     * @return null, preventing clients from binding.
     */

    @Suppress("UNUSED_PARAMETER")
    override fun onBind(_intent: Intent?): IBinder? {
        // This service does not support binding.
        return null
    }

    /**
     * Handles a request to start the service and specifies that it should not be restarted if terminated by the system.
     *
     * @return `START_NOT_STICKY` to indicate the service will not be recreated automatically after being killed.
     */
    override fun onStartCommand(intent: Intent?, _flags: Int, _startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY, ACTION_RESUME -> resume()
                ACTION_PAUSE -> pause()
                ACTION_NEXT -> skipToNextTrack()
                ACTION_PREVIOUS -> skipToPreviousTrack()
            }
        }
        return START_NOT_STICKY
    }

    /**
     * Pauses music playback.
     *
     * This method is a placeholder and does not perform any action.
     */
    fun pause() {
        // TODO: Implement pause logic. Reported as unused. Implement or remove.
    }

    fun resume() {
        // TODO: Implement resume logic. Reported as unused. Implement or remove.
    }

    fun setVolume(_volume: Float) {
        // TODO: Reported as unused. Implement or remove.
    }

    fun setShuffling(_isShuffling: Boolean) {
        // TODO: Reported as unused. Implement or remove.
    }

    fun getCurrentTrack(): Any? { // Return type Any? as placeholder
        // TODO: Reported as unused. Implement or remove.
        return null
    }

    fun getTrackHistory(): List<Any> { // Return type List<Any> as placeholder
        // TODO: Reported as unused. Implement or remove.
        return emptyList()
    }

    fun skipToNextTrack() {
        // TODO: Reported as unused. Implement or remove.
    }

    fun skipToPreviousTrack() {
        // TODO: Reported as unused. Implement or remove.
    }

    companion object {
        const val ACTION_PLAY = "dev.aurakai.auraframefx.action.PLAY"
        const val ACTION_PAUSE = "dev.aurakai.auraframefx.action.PAUSE"
        const val ACTION_RESUME = "dev.aurakai.auraframefx.action.RESUME"
        const val ACTION_NEXT = "dev.aurakai.auraframefx.action.NEXT"
        const val ACTION_PREVIOUS = "dev.aurakai.auraframefx.action.PREVIOUS"
    }
}

