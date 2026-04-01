package dev.aurakai.auraframefx.core.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production AlertNotifier — routes to Timber + Android Notification channel.
 */
@Singleton
class SystemAlertNotifier @Inject constructor(
    @ApplicationContext private val context: Context
) : AlertNotifier {

    companion object {
        private const val CHANNEL_ID = "aurakai_sentinel_alerts"
        private const val CHANNEL_NAME = "Kai Sentinel Alerts"
        private const val TAG = "LDO-ALERT"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Kai Sentinel critical events" }
        notificationManager.createNotificationChannel(channel)
    }

    override fun notify(message: String, level: AlertLevel) {
        when (level) {
            AlertLevel.INFO -> Timber.tag(TAG).i(message)
            AlertLevel.WARNING -> Timber.tag(TAG).w(message)
            AlertLevel.ERROR -> Timber.tag(TAG).e(message)
            AlertLevel.CRITICAL -> {
                Timber.tag(TAG).e("CRITICAL: $message")
                postNotification(message)
            }
        }
    }

    override fun notifyCritical(message: String, exception: Throwable?) {
        Timber.tag(TAG).e(exception, "CRITICAL: $message")
        postNotification(message)
    }

    override fun notifyAgentAnomaly(agentId: String, reason: String) {
        Timber.tag(TAG).w("Agent anomaly — agent=$agentId, reason=$reason")
    }

    private fun postNotification(message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Kai Sentinel Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(message.hashCode(), notification)
    }
}
