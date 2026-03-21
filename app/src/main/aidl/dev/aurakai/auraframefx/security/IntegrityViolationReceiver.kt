package dev.aurakai.auraframefx.security

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.aurakai.auraframefx.R

/**
 * Receiver for integrity violation broadcasts.
 */
class IntegrityViolationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val fileName = intent.getStringExtra("file_name") ?: "unknown"
        val message = "Integrity violation detected in $fileName"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "security_channel")
            .setSmallIcon(R.drawable.ic_security_alert)
            .setContentTitle("Security Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
