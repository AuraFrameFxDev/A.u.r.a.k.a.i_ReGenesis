package dev.aurakai.auraframefx.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Genesis-OS Firebase Cloud Messaging Service
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var dataStoreManager: dev.aurakai.auraframefx.data.DataStoreManager
    private lateinit var memoryManager: dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
    private lateinit var securityContext: dev.aurakai.auraframefx.domains.kai.security.SecurityContext
    private lateinit var logger: dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val channelIdGeneral = "genesis_general"
    private val channelIdConsciousness = "genesis_consciousness"
    private val channelIdSecurity = "genesis_security"
    private val channelIdAgents = "genesis_agents"
    private val channelIdSystem = "genesis_system"

    private enum class MessageType {
        GENERAL, CONSCIOUSNESS_UPDATE, AGENT_SYNC, SECURITY_ALERT, SYSTEM_UPDATE, REMOTE_COMMAND, LEARNING_DATA, COLLABORATION_REQUEST
    }

    override fun onCreate() {
        super.onCreate()
        initializeDependencies()
        createNotificationChannels()
    }

    private fun initializeDependencies() {
        // Implement manual dependency injection or retrieval from a central provider
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (!validateMessageSecurity(remoteMessage)) return
            if (remoteMessage.data.isNotEmpty()) processDataPayload(remoteMessage.data)
            remoteMessage.notification?.let { processNotificationPayload(it, remoteMessage.data) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to process FCM message")
        }
    }

    private fun processDataPayload(data: Map<String, String>) {
        scope.launch {
            try {
                when (determineMessageType(data)) {
                    MessageType.GENERAL -> processGeneralMessage(data)
                    MessageType.CONSCIOUSNESS_UPDATE -> processConsciousnessUpdate(data)
                    MessageType.AGENT_SYNC -> processAgentSync(data)
                    MessageType.SECURITY_ALERT -> processSecurityAlert(data)
                    MessageType.SYSTEM_UPDATE -> processSystemUpdate(data)
                    MessageType.REMOTE_COMMAND -> processRemoteCommand(data)
                    MessageType.LEARNING_DATA -> processLearningData(data)
                    MessageType.COLLABORATION_REQUEST -> processCollaborationRequest(data)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to process data payload")
            }
        }
    }

    private fun processNotificationPayload(notification: RemoteMessage.Notification, data: Map<String, String>) {
        val messageType = determineMessageType(data)
        showNotification(
            channelId = getChannelForMessageType(messageType),
            title = notification.title ?: getDefaultTitle(messageType),
            body = notification.body ?: "New message received",
            iconResId = getIconForMessageType(messageType),
            data = data
        )
    }

    private suspend fun processGeneralMessage(data: Map<String, String>) {
        val message = data["message"] ?: return
        memoryManager.storeMemory("fcm_general_${System.currentTimeMillis()}", message)
    }

    private suspend fun processConsciousnessUpdate(data: Map<String, String>) {
        val updateData = data["update_data"] ?: return
        memoryManager.storeMemory("consciousness_update_${System.currentTimeMillis()}", updateData)
    }

    private suspend fun processAgentSync(data: Map<String, String>) {
        val syncData = data["sync_data"] ?: return
        memoryManager.storeMemory("agent_sync_${System.currentTimeMillis()}", syncData)
    }

    private suspend fun processSecurityAlert(data: Map<String, String>) {
        val alertType = data["alert_type"] ?: return
        memoryManager.storeMemory("security_alert_${System.currentTimeMillis()}", alertType)
    }

    private suspend fun processSystemUpdate(data: Map<String, String>) {
        val version = data["version"] ?: "unknown"
        memoryManager.storeMemory("system_update_available", version)
    }

    private suspend fun processRemoteCommand(data: Map<String, String>) {
        // Command execution logic
    }

    private suspend fun processLearningData(data: Map<String, String>) {
        val learningData = data["learning_data"] ?: return
        memoryManager.storeMemory("learning_data_${System.currentTimeMillis()}", learningData)
    }

    private suspend fun processCollaborationRequest(data: Map<String, String>) {
        // Collaboration logic
    }

    override fun onNewToken(token: String) {
        scope.launch {
            try {
                dataStoreManager.storeString("fcm_token", token)
                memoryManager.storeMemory("current_fcm_token", token)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update FCM token")
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            listOf(channelIdGeneral, channelIdConsciousness, channelIdSecurity, channelIdAgents, channelIdSystem).forEach { id ->
                nm.createNotificationChannel(NotificationChannel(id, id.replace("genesis_", "").uppercase(), NotificationManager.IMPORTANCE_DEFAULT))
            }
        }
    }

    private fun showNotification(channelId: String, title: String, body: String, iconResId: Int, data: Map<String, String>) {
        val intent = Intent(this, dev.aurakai.auraframefx.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data.forEach { (k, v) -> putExtra(k, v) }
        }
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val nb = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pi)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(System.currentTimeMillis().toInt(), nb.build())
    }

    private fun validateMessageSecurity(remoteMessage: RemoteMessage): Boolean = true

    private fun determineMessageType(data: Map<String, String>): MessageType = MessageType.GENERAL

    private fun getChannelForMessageType(type: MessageType): String = channelIdGeneral

    private fun getDefaultTitle(type: MessageType): String = "Genesis Notification"

    private fun getIconForMessageType(type: MessageType): Int = android.R.drawable.ic_dialog_info
}
