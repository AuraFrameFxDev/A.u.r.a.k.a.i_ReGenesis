package dev.aurakai.auraframefx.domains.aura

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuraDriveService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
