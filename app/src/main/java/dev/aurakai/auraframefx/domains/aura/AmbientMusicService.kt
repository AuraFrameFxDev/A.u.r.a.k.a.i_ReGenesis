package dev.aurakai.auraframefx.domains.aura

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

data class AmbientTrack(
    val id: String,
    val title: String,
    val artist: String,
    val resourceUri: String? = null
)

@AndroidEntryPoint
class AmbientMusicService @Inject constructor() : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex = 0
    private val tracks = listOf(
        AmbientTrack("1", "Aura Pulse", "Aura Engine"),
        AmbientTrack("2", "Kai Firewall", "Kai Sentinel"),
        AmbientTrack("3", "Genesis Matrix", "Genesis Architect")
    )

    override fun onCreate() {
        super.onCreate()
        Timber.d("AmbientMusicService: Initializing audio matrix.")
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener {
                Timber.d("AmbientMusicService: Track completed. Sequencing next.")
                skipToNextTrack()
            }
            setOnErrorListener { _, what, extra ->
                Timber.e("AmbientMusicService: Media player error (what=$what, extra=$extra)")
                false
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            Timber.d("AmbientMusicService: Received action $action")
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
        Timber.d("AmbientMusicService: Suspending audio flow.")
        mediaPlayer?.let {
            if (it.isPlaying) it.pause()
        }
    }

    /**
     * Resumes music playback.
     */
    fun resume() {
        Timber.d("AmbientMusicService: Awakening audio nodes.")
        mediaPlayer?.let {
            if (!it.isPlaying) it.start()
        }
    }

    fun setVolume(volume: Float) {
        Timber.d("AmbientMusicService: Modulating volume to $volume")
        mediaPlayer?.setVolume(volume, volume)
    }

    fun setShuffling(isShuffling: Boolean) {
        Timber.d("AmbientMusicService: Convergence of random paths set to $isShuffling")
        // Shuffling logic would be implemented here
    }

    private val trackHistory = mutableListOf<AmbientTrack>()

    fun getCurrentTrack(): AmbientTrack {
        return tracks[currentTrackIndex]
    }

    fun getTrackHistory(): List<AmbientTrack> {
        return trackHistory.toList()
    }

    fun skipToNextTrack() {
        Timber.d("AmbientMusicService: Jumping to next neural frequency.")
        trackHistory.add(tracks[currentTrackIndex])
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
        // In a real implementation, you'd load the new track here
    }

    fun skipToPreviousTrack() {
        Timber.d("AmbientMusicService: Reverting to previous neural frequency.")
        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
    }

    override fun onDestroy() {
        Timber.d("AmbientMusicService: Releasing audio resources.")
        mediaPlayer?.release()
        mediaPlayer = null
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

