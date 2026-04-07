package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class IntegrityMonitorService : Service() {
    @Inject
    lateinit var healthMonitor: SovereignHealthMonitor

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        healthMonitor.startMonitoring()
    }

    private fun startForegroundService() {
        val channelId = "integrity_monitor"
        val channel = NotificationChannel(channelId, "System Integrity", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Sovereign Integrity Active")
            .setContentText("Kai is guarding the substrate")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .build()

        startForeground(1338, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("IntegrityMonitorService started")
        return START_STICKY
    }
}
