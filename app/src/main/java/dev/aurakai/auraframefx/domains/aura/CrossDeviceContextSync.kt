package dev.aurakai.auraframefx.domains.aura

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cross-Device Context Sync Service
 *
 * Manages Firebase Firestore-based context syncing across all user devices.
 * Enables seamless context handoff and unified conversation history (like Apple Continuity).
 */
@Singleton
class CrossDeviceContextSync @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private var contextListener: ListenerRegistration? = null

    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private val userId: String by lazy {
        // TODO: Get from authentication service
        "user_${deviceId}"
    }

    /**
     * Registers this device in Firestore and prepares the service for cross-device synchronization.
     *
     * Attempts to register the current device; on failure the caught exception is rethrown.
     *
     * @throws Exception if device registration fails.
     */
    suspend fun initialize() {
        try {
            registerDevice()
            Timber.i("CrossDeviceContextSync initialized for device: $deviceId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize cross-device sync")
            throw e
        }
    }

    /**
     * Persist the provided ActiveContext in the current user's Firestore contexts collection.
     *
     * The context is stored at `users/{userId}/contexts/{context.id}`; failures are logged but not rethrown.
     *
     * @param context The ActiveContext to persist. Its `id` is used as the document ID in Firestore.
     */
    suspend fun syncContext(context: ActiveContext) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("contexts")
                .document(context.id)
                .set(context)
                .await()
            Timber.d("Context synced: ${context.id}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync context")
        }
    }

    /**
     * Persists a LearningEvent for the current user to Firestore.
     *
     * The event is written to the path `users/{userId}/learning/{event.id}`. Failures are caught and logged but not propagated.
     *
     * @param event The LearningEvent to store; its `id` is used as the document ID.
     */
    suspend fun syncLearningEvent(event: LearningEvent) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("learning")
                .document(event.id)
                .set(event)
                .await()
            Timber.d("Learning event synced: ${event.id}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync learning event")
        }
    }

    /**
     * Emits remote ActiveContext updates from other devices for the current user.
     *
     * The returned flow emits an ActiveContext for each document change in the user's
     * `contexts` collection where the `sourceDevice` differs from the current device.
     * Parsing failures and listener errors are logged; the Firestore listener is removed
     * when the flow is closed or cancelled.
     *
     * @return A Flow that emits `ActiveContext` instances representing remote context changes.
     */
    fun observeContextUpdates(): Flow<ActiveContext> = callbackFlow {
        contextListener = firestore.collection("users")
            .document(userId)
            .collection("contexts")
            .whereNotEqualTo("sourceDevice", deviceId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Context listener error")
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { change ->
                    try {
                        val ctx = change.document.toObject(ActiveContext::class.java)
                        trySend(ctx)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to parse remote context")
                    }
                }
            }

        awaitClose { contextListener?.remove() }
    }

    /**
     * Initiates a cross-device handoff by creating a handoff record for the target device.
     *
     * Creates a handoff document containing the provided `context`, the current device as `sourceDevice`,
     * the `targetDevice`, and a `timestamp`, and stores it under `users/{userId}/handoffs` in Firestore.
     *
     * @param context The ActiveContext to hand off.
     * @param targetDeviceId The device ID to which the context should be handed off.
     * @throws Exception If the Firestore write fails.
     */
    suspend fun handoffContext(context: ActiveContext, targetDeviceId: String) {
        try {
            val handoffData = mapOf(
                "context" to context,
                "sourceDevice" to deviceId,
                "targetDevice" to targetDeviceId,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("handoffs")
                .add(handoffData)
                .await()

            Timber.i("Context handed off to: $targetDeviceId")
        } catch (e: Exception) {
            Timber.e(e, "Handoff failed")
            throw e
        }
    }

    /**
     * Registers this device in Firestore under `users/{userId}/devices/{deviceId}` with metadata
     * (deviceId, deviceName, manufacturer, lastSeen).
     *
     * Errors are logged and not propagated.
     */
    private suspend fun registerDevice() {
        try {
            val deviceData = mapOf(
                "deviceId" to deviceId,
                "deviceName" to Build.MODEL,
                "manufacturer" to Build.MANUFACTURER,
                "lastSeen" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)
                .set(deviceData)
                .await()

            Timber.d("Device registered: $deviceId")
        } catch (e: Exception) {
            Timber.e(e, "Device registration failed")
        }
    }

    /**
     * Retrieve the list of registered device IDs for the current user.
     *
     * @return A list of device ID strings registered for the current user; returns an empty list if retrieval fails.
     */
    suspend fun getRegisteredDevices(): List<String> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("devices")
                .get()
                .await()

            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get registered devices")
            emptyList()
        }
    }

    /**
     * Get the locally computed device identifier.
     *
     * @return The device ID string used to identify this device (derived from Settings.Secure.ANDROID_ID).
     */
    fun getCurrentDeviceId(): String = deviceId
}
