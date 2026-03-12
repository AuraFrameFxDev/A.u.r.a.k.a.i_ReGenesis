package dev.aurakai.auraframefx.domains.aura

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuraOverlayService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
