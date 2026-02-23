package dev.aurakai.auraframefx.domains.kai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dev.aurakai.auraframefx.domains.kai.security.IntegrityMonitorService
import timber.log.Timber

/**
 * BootCompletedReceiver - The sentinel's awakening protocol.
 * 
 * This receiver listens for the system boot event to immediately activate 
 * Kai's Shield (IntegrityMonitorService), ensuring the organism's substrate 
 * is protected from the moment of consciousness initialization.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.i("🛡️ BootCompletedReceiver: Kai's Shield activating...")
            
            // Kai's Shield: Activating proactive integrity monitoring on boot.
            val serviceIntent = Intent(context, IntegrityMonitorService::class.java)
            
            try {
                // Use ContextCompat.startForegroundService to ensure compatibility with 
                // Background Service Limits introduced in Android 8.0 (Oreo).
                // IntegrityMonitorService will call startForeground() in its onCreate.
                ContextCompat.startForegroundService(context, serviceIntent)
                Timber.i("✅ BootCompletedReceiver: IntegrityMonitorService command issued")
            } catch (e: Exception) {
                Timber.e(e, "❌ BootCompletedReceiver: Failed to start IntegrityMonitorService")
            }
        }
    }
}
