package dev.aurakai.auraframefx.agents


import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dev.aurakai.auraframefx.models.AgentCapabilityCategory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import dev.aurakai.auraframefx.config.CapabilityPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A secure wrapper around Firebase services that enforces capability policies.
 * All Firebase operations must go through this class to ensure proper access control.
 */
@Singleton
class AgentFirebase @Inject constructor(
    private val policy: CapabilityPolicy,
    private val firebaseApp: FirebaseApp
) {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance(firebaseApp) }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance(firebaseApp) }
    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(if (firebaseApp.isDefaultApp) 3600 else 0)
                    .setFetchTimeoutInSeconds(30)
                    .build()
            )
        }
    }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance(firebaseApp) }

    // Firestore Operations

    suspend fun getDocument(collection: String, docId: String): Map<String, Any>? =
        withContext(Dispatchers.IO) {
            policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_READ)
            policy.validateCollectionAccess("$collection/$docId")

            firestore.collection(collection).document(docId).get().await()
                ?.data
                ?.also { validateDocumentSize(it) }
        }

    suspend fun saveDocument(
        collection: String,
        docId: String,
        data: Map<String, Any>,
        merge: Boolean = true
    ) = withContext(Dispatchers.IO) {
        policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_WRITE)
        policy.validateCollectionAccess("$collection/$docId")
        validateDocumentSize(data)

        firestore.collection(collection).document(docId).set(data).await()
    }!!

    fun collection(collectionPath: String): CollectionReference {
        policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_READ)
        policy.validateCollectionAccess(collectionPath)
        return firestore.collection(collectionPath)
    }

    fun document(documentPath: String): DocumentReference {
        policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_READ)
        policy.validateCollectionAccess(documentPath)
        return firestore.document(documentPath)
    }

    // Storage Operations

    suspend fun uploadFile(
        path: String,
        data: ByteArray,
        metadata: Map<String, String> = emptyMap()
    ) = withContext(Dispatchers.IO) {
        policy.requireScope(CapabilityPolicy.SCOPE_STORAGE_UPLOAD)
        policy.validateStoragePath(path)

        val ref = storage.reference.child(path)
        val uploadTask = ref.putBytes(data)
        uploadTask.await()

        // Update metadata if provided
        if (metadata.isNotEmpty()) {
            ref.updateMetadata(
                StorageMetadata.Builder()
                    .apply {
                        metadata.forEach { (key, value) ->
                            setCustomMetadata(key, value)
                        }
                    }
                    .build()
            ).await()
        }

        ref.downloadUrl.await().toString()
    }

    suspend fun downloadFile(path: String): ByteArray = withContext(Dispatchers.IO) {
        policy.requireScope(CapabilityPolicy.SCOPE_STORAGE_DOWNLOAD)
        policy.validateStoragePath(path)

        val bytes = storage.reference.child(path).getBytes(Long.MAX_VALUE).await()
        bytes
    }

    fun getStorageReference(path: String): StorageReference {
        policy.requireScope(CapabilityPolicy.SCOPE_STORAGE_DOWNLOAD)
        policy.validateStoragePath(path)
        return storage.reference.child(path)
    }

    // Remote Config

    suspend fun fetchRemoteConfig() = withContext(Dispatchers.IO) {
        policy.requireScope(CapabilityPolicy.SCOPE_CONFIG_READ)
        remoteConfig.fetchAndActivate().await()
    }!!

    fun getConfigValue(key: String): String {
        policy.requireScope(CapabilityPolicy.SCOPE_CONFIG_READ)
        return remoteConfig.getString(key)
    }

    // Auth Operations

    suspend fun getCurrentUser() = withContext(Dispatchers.Main) {
        policy.requireScope(CapabilityPolicy.SCOPE_AUTH_MANAGE)
        auth.currentUser
    }

    // Validation Helpers

    private fun validateDocumentSize(data: Map<String, Any>) {
        val size = data.toString().toByteArray().size.toLong()
        if (size > policy.maxDocumentSize) {
            throw SecurityException("Document size $size bytes exceeds maximum allowed ${policy.maxDocumentSize} bytes")
        }
    }

    companion object {
        /**
         * Create an AgentFirebase instance configured with the policy corresponding to the given agent capability category.
         *
         * @param agentType The agent capability category that determines which CapabilityPolicy is applied:
         * - CREATIVE -> AURA_POLICY
         * - ANALYSIS -> KAI_POLICY
         * - COORDINATION -> GENESIS_POLICY
         * - SPECIALIZED -> CASCADE_POLICY
         * - GENERAL -> CLAUDE_POLICY
         * @param firebaseApp The FirebaseApp to use for service initialization; defaults to FirebaseApp.getInstance().
         * @return An AgentFirebase configured with the selected CapabilityPolicy and provided FirebaseApp.
         * @throws IllegalArgumentException If no policy is defined for the provided `agentType`.
         */
        fun createWithPolicy(
            agentType: AgentCapabilityCategory,
            firebaseApp: FirebaseApp = FirebaseApp.getInstance()
        ): AgentFirebase {
            val policy = when (agentType) {
                AgentCapabilityCategory.CREATIVE -> CapabilityPolicy.AURA_POLICY
                AgentCapabilityCategory.ANALYSIS -> CapabilityPolicy.KAI_POLICY
                AgentCapabilityCategory.COORDINATION -> CapabilityPolicy.GENESIS_POLICY
                AgentCapabilityCategory.SPECIALIZED -> CapabilityPolicy.CASCADE_POLICY
                AgentCapabilityCategory.GENERAL -> CapabilityPolicy.CLAUDE_POLICY
            }
            return AgentFirebase(policy, firebaseApp)
        }
    }
}
