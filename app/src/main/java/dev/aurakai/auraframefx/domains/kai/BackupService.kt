package dev.aurakai.auraframefx.domains.kai

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BackupService @Inject constructor() : Service() {
    // TODO: If this service has dependencies to be injected, add them to the constructor.

    private val tag = "BackupService"

    override fun onCreate() {
        super.onCreate()
        Timber.tag(tag).d("BackupService created.")
        // TODO: Initialization logic for the backup service.
    }

    override fun onBind(_intent: Intent?): IBinder? {
        Timber.tag(tag).d("onBind called, returning null.")
        // This service does not support binding by default.
        // TODO: Implement if binding is necessary for a specific use case.
        return null
    }

    override fun onStartCommand(_intent: Intent?, _flags: Int, _startId: Int): Int {
        Timber.tag(tag).d("onStartCommand called.")
        // TODO: Implement backup logic here.
        // Consider running in a separate thread if tasks are long-running.
        // Use _intent, _flags, _startId if needed by the actual implementation.
        return START_NOT_STICKY // Or START_STICKY / START_REDELIVER_INTENT as appropriate
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(tag).d("BackupService destroyed.")
        // TODO: Cleanup logic, if any.
    }
}

