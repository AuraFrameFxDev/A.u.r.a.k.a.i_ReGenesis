package dev.aurakai.auraframefx.domains.genesis.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.genesis.VertexSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VertexSyncService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    @Inject
    lateinit var syncManager: VertexSyncManager

    override fun onCreate() {
        super.onCreate()
        Timber.i("VertexSyncService: Service created")

        // Start background sync with injected sync manager
        scope.launch {
            try {
                Timber.d("VertexSyncService: Starting sync manager")
                syncManager.startSync()
                Timber.i("VertexSyncService: Sync manager started successfully")
            } catch (e: Exception) {
                Timber.e(e, "VertexSyncService: Failed to start sync manager")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("VertexSyncService onStartCommand called")
        // Handle commands or schedule work
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("VertexSyncService destroyed")
    }
}
