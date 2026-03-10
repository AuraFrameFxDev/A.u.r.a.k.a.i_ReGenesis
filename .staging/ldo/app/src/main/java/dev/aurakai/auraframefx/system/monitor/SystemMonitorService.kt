package dev.aurakai.auraframefx.system.monitor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SystemMonitorService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    companion object {
        private const val TAG = "SystemMonitorService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: SystemMonitorService created.")
        // TODO: Initialize any resources needed for monitoring (e.g., sensors, listeners)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: SystemMonitorService started.")

        // Start monitoring tasks in a coroutine
        serviceScope.launch {
            monitorSystem()
        }

        // If the service is killed, restart it
        return START_STICKY
    }

    /**
     * Continuously monitors system metrics such as CPU, memory, battery, and network status while the service is active.
     *
     * Intended to run in a background coroutine, gathering and processing system information at regular intervals.
     */
    private suspend fun monitorSystem() {
        // Loop indefinitely (or until service is stopped) to perform monitoring
        while (serviceScope.isActive) {
            // TODO: Implement CPU usage monitoring
            // Log.d(TAG, "Current CPU Usage: ...")

            // TODO: Implement Memory usage monitoring
            // Log.d(TAG, "Current Memory Usage: ...")

            // TODO: Implement Battery level and status monitoring
            // Log.d(TAG, "Current Battery Level: ...")

            // TODO: Implement Network status and traffic monitoring
            // Log.d(TAG, "Current Network Status: ...")

            // TODO: Potentially read system logs or specific app logs if permissions allow and it's required
            // Log.d(TAG, "Checking relevant logs...")

            // TODO: Implement logic to report or act on gathered metrics
            // This could involve sending broadcasts, updating a database, or notifying other components.

            delay(60000) // Example: Monitor every 60 seconds
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called, returning null as this is not a bound service.")
        // This service is not intended to be bound, so return null.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: SystemMonitorService destroyed.")
        serviceJob.cancel() // Cancel all coroutines started by this service
        // TODO: Release any resources acquired in onCreate
    }
}
