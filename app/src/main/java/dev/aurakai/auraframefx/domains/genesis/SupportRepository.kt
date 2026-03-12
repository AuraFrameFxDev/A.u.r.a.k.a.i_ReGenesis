package dev.aurakai.auraframefx.domains.genesis

import dev.aurakai.auraframefx.domains.nexus.preferences.DataStoreManager
import dev.aurakai.auraframefx.domains.nexus.helpdesk.data.MessageStatus
import dev.aurakai.auraframefx.domains.nexus.helpdesk.data.SupportMessageDao
import dev.aurakai.auraframefx.domains.nexus.helpdesk.data.SupportMessageEntity
import dev.aurakai.auraframefx.domains.genesis.network.SupportApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class SupportRepository(
    private val dao: SupportMessageDao,
    private val api: SupportApi,
    private val dataStore: DataStoreManager
) {
    fun getMessages(): Flow<List<SupportMessageEntity>> = dao.getAllMessages()

    private val retryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Start retry loop for FAILED messages
        retryScope.launch {
            while (isActive) {
                try {
                    processFailedMessages()
                } catch (t: Throwable) {
                    // swallow and retry next cycle
                }
                delay(30_000) // every 30 seconds
            }
        }
    }

    suspend fun getOrCreateDeviceUserId(): String {
        val existing = dataStore.getString("device_user_id", "")
        if (existing.isNotEmpty()) return existing
        val newId = "device_${UUID.randomUUID()}"
        dataStore.storeString("device_user_id", newId)
        return newId
    }

    // Persist an arbitrary message (system or reply)
    suspend fun persistMessage(message: SupportMessageEntity) {
        dao.insert(message)
    }

    // Returns Result.success(responseBody) on success, failure otherwise
    suspend fun sendMessage(message: SupportMessageEntity): Result<String> {
        // Persist initial message as PENDING
        dao.insert(message.copy(status = MessageStatus.PENDING))
        val userId = getOrCreateDeviceUserId()
        return try {
            val payload = mapOf("message" to message.content, "user_id" to userId)
            val resp = api.sendMessage(payload)
            val body = resp.body()
            if (resp.isSuccessful && body != null) {
                // Extract reply text
                val reply = when {
                    body.containsKey("response") -> body["response"]?.toString()
                    body.containsKey("message") -> body["message"]?.toString()
                    else -> body.toString()
                } ?: ""

                // mark message as SENT
                dao.insert(message.copy(status = MessageStatus.SENT))

                // Persist backend reply into DB for history
                val replyEntity = SupportMessageEntity(
                    id = UUID.randomUUID().toString(),
                    content = reply,
                    sender = "genesis",
                    isUser = false,
                    timestamp = System.currentTimeMillis(),
                    status = MessageStatus.SENT
                )
                dao.insert(replyEntity)

                Result.success(reply)
            } else {
                // mark message as FAILED
                dao.insert(message.copy(status = MessageStatus.FAILED))
                Result.failure(Exception("HTTP ${resp.code()}"))
            }
        } catch (t: Throwable) {
            dao.insert(message.copy(status = MessageStatus.FAILED))
            Result.failure(t)
        }
    }

    suspend fun processFailedMessages() {
        val failed = dao.getFailedMessages()
        for (msg in failed) {
            try {
                sendMessage(msg)
                // if successful, sendMessage will persist reply and mark as SENT
            } catch (t: Throwable) {
                // backoff is handled by cycle delay; do nothing per-message
            }
        }
    }

    suspend fun clearMessages() {
        dao.clearAll()
    }
}
