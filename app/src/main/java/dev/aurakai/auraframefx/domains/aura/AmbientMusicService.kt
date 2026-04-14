package dev.aurakai.auraframefx.domains.aura

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AmbientMusicService @Inject constructor() : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var volume: Float = 1.0f

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
     */
    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resume() {
        if (mediaPlayer == null) {
            // Placeholder: Typically you'd load a resource or URL here
            // mediaPlayer = MediaPlayer.create(this, R.raw.ambient_base)
            Log.i("AmbientMusic", "Initializing MediaPlayer stub")
        }
        mediaPlayer?.start()
    }

    fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0.0f, 1.0f)
        mediaPlayer?.setVolume(this.volume, this.volume)
    }

    fun skipToNextTrack() {
        Log.i("AmbientMusic", "Skipping to next track (stub)")
    }

    fun skipToPreviousTrack() {
        Log.i("AmbientMusic", "Skipping to previous track (stub)")
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        stop()
        super.onDestroy()
    }

    companion object {
        const val ACTION_PLAY = "dev.aurakai.auraframefx.action.PLAY"
        const val ACTION_PAUSE = "dev.aurakai.auraframefx.action.PAUSE"
        const val ACTION_RESUME = "dev.aurakai.auraframefx.action.RESUME"
        const val ACTION_NEXT = "dev.aurakai.auraframefx.action.NEXT"
        const val ACTION_PREVIOUS = "dev.aurakai.auraframefx.action.PREVIOUS"
    }
}

