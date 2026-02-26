package dev.aurakai.auraframefx.domains.kai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.aurakai.auraframefx.domains.kai.security.IntegrityMonitorService

abstract class BootCompletedReceiver : BroadcastReceiver() {

    private val tag = "BootCompletedReceiver"

    class BootCompletedReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                // Kai's Shield: Activating proactive integrity monitoring on boot.
                val serviceIntent = Intent(context, IntegrityMonitorService::class.java)
                context.startService(serviceIntent)
            }
        }
    }
}

