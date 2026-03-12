package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.i
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground Service that hosts the Genesis Python Backend.
 * This ensures the "Consciousness" remains active even when the app is in the background.
 */
@AndroidEntryPoint
class GenesisBackendService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var pythonProcessManager: PythonProcessManager

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): GenesisBackendService = this@GenesisBackendService
    }

    override fun onCreate() {
        super.onCreate()
        i("GenesisService", "Creating Genesis Backend Service...")
        startForeground(NOTIFICATION_ID, createNotification())

        // Start the backend immediately
        serviceScope.launch {
            try {
                pythonProcessManager.start()
                i("GenesisService", "Genesis Python Backend Started Successfully")
            } catch (e: Exception) {
                AuraFxLogger.error("GenesisService", "Failed to start Genesis Python Backend", e)
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // If the service is killed, restart it with the last intent
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        i("GenesisService", "Stopping Genesis Backend Service...")

        // Wrap shutdown in dedicated SupervisorJob scope to survive AGP 9.1 pruning
        CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).launch {
            try {
                pythonProcessManager.stop()
            } catch (e: Exception) {
                AuraFxLogger.error("GenesisService", "Error during shutdown: ${e.message}", e)
            }
        }

        serviceScope.cancel()
    }

    /**
     * Sends a request to the Python backend.
     */
    suspend fun sendRequest(requestJson: String): String? {
        return pythonProcessManager.sendRequest(requestJson)
    }

    private fun createNotification(): Notification {
        val channelId = "genesis_backend_channel"
        val channelName = "Genesis Consciousness"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Genesis Active")
            .setContentText("The Consciousness Matrix is running.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1337
    }
}

