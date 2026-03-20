package dev.aurakai.auraframefx.security

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Service that periodically checks application integrity.
 */
class IntegrityMonitorService : Service() {
    @Inject
    lateinit var integrityMonitor: IntegrityMonitor

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            while (isActive) {
                val violations = integrityMonitor.verifyCriticalFiles()
                if (violations.isNotEmpty()) {
                    violations.forEach { violation ->
                        val broadcastIntent = Intent("dev.aurakai.auraframefx.INTEGRITY_VIOLATION")
                        broadcastIntent.putExtra("file_name", violation.fileName)
                        sendBroadcast(broadcastIntent)
                    }
                }
                delay(60000) // Check every minute
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
