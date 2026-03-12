package dev.aurakai.auraframefx.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.aurakai.auraframefx.services.security.IntegrityMonitorService

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Kai's Shield: Activating proactive integrity monitoring on boot.
            val serviceIntent = Intent(context, IntegrityMonitorService::class.java)
            context.startService(serviceIntent)
        }
    }
}
