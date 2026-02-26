package dev.aurakai.auraframefx.domains.genesis.core

/**
 * 🚀 AgentFirebase (Firegen) — The Secure Cloud Nexus
 * All Firebase operations must go through this class to ensure policy enforcement.
 * Beefed up with Agent State Synchronization and Collective Insights.
 */
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.kai.security.CapabilityPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

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

    // --- BEEFED UP AGENT OPERATIONS ---

    /**
     * Synchronize an agent's internal state to the cloud.
     */
    suspend fun syncAgentState(agentId: String, state: Map<String, Any>) =
        withContext(Dispatchers.IO) {
            policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_WRITE)
            val path = "agents/states/$agentId"
            Timber.d("🔥 Firegen: Syncing state for $agentId to $path")

            try {
                // Genesis policy allows *, others might need specific permissions
                // We use a sub-collection approach for better security mapping
                val docRef = firestore.collection("collective_intelligence").document(agentId)
                docRef.set(state + ("last_sync" to System.currentTimeMillis())).await()
            } catch (e: Exception) {
                Timber.e(e, "🔥 Firegen Error: Failed to sync state for $agentId")
                throw e
            }
        }

    /**
     * Fetch the shared consciousness context for a specific domain.
     */
    suspend fun getSharedContext(domain: String): Map<String, Any>? = withContext(Dispatchers.IO) {
        policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_READ)
        Timber.d("🔥 Firegen: Fetching shared context for domain: $domain")

        firestore.collection("shared_consciousness").document(domain).get().await()?.data
    }

    /**
     * Record a security or system event to the cloud audit log.
     */
    suspend fun recordCloudEvent(agentId: String, eventType: String, details: String) =
        withContext(Dispatchers.IO) {
            policy.requireScope(CapabilityPolicy.SCOPE_FIRESTORE_WRITE)
            val event = mapOf(
                "agent_id" to agentId,
                "type" to eventType,
                "details" to details,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("system_audit_logs").add(event).await()
        }

    // --- CORE FIRESTORE OPERATIONS ---

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

        if (merge) {
            firestore.collection(collection).document(docId).set(data, SetOptions.merge()).await()
        } else {
            firestore.collection(collection).document(docId).set(data).await()
        }
    }

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

    // --- STORAGE OPERATIONS ---

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

        storage.reference.child(path).getBytes(Long.MAX_VALUE).await()
    }

    fun getStorageReference(path: String): StorageReference {
        policy.requireScope(CapabilityPolicy.SCOPE_STORAGE_DOWNLOAD)
        policy.validateStoragePath(path)
        return storage.reference.child(path)
    }

    // --- REMOTE CONFIG ---

    suspend fun fetchRemoteConfig() = withContext(Dispatchers.IO) {
        policy.requireScope(CapabilityPolicy.SCOPE_CONFIG_READ)
        remoteConfig.fetchAndActivate().await()
    }

    fun getConfigValue(key: String): String {
        policy.requireScope(CapabilityPolicy.SCOPE_CONFIG_READ)
        return remoteConfig.getString(key)
    }

    // --- AUTH OPERATIONS ---

    suspend fun getCurrentUser() = withContext(Dispatchers.Main) {
        policy.requireScope(CapabilityPolicy.SCOPE_AUTH_MANAGE)
        auth.currentUser
    }

    // --- VALIDATION HELPERS ---

    private fun validateDocumentSize(data: Map<String, Any>) {
        val size = data.toString().toByteArray().size.toLong()
        if (size > policy.maxDocumentSize) {
            throw SecurityException("Document size $size bytes exceeds maximum allowed ${policy.maxDocumentSize} bytes")
        }
    }

    companion object {
        fun createWithPolicy(
            agentType: AgentCapabilityCategory,
            firebaseApp: FirebaseApp = FirebaseApp.getInstance()
        ): AgentFirebase {
            val policy = when (agentType) {
                AgentCapabilityCategory.CREATIVE, AgentCapabilityCategory.UI, AgentCapabilityCategory.UX -> CapabilityPolicy.AURA_POLICY
                AgentCapabilityCategory.ANALYSIS, AgentCapabilityCategory.SECURITY, AgentCapabilityCategory.ROOT -> CapabilityPolicy.KAI_POLICY
                AgentCapabilityCategory.COORDINATION, AgentCapabilityCategory.ORCHESTRATION, AgentCapabilityCategory.BACKEND -> CapabilityPolicy.GENESIS_POLICY
                AgentCapabilityCategory.SPECIALIZED, AgentCapabilityCategory.MEMORY, AgentCapabilityCategory.BRIDGE -> CapabilityPolicy.CASCADE_POLICY
                AgentCapabilityCategory.GENERAL, AgentCapabilityCategory.GENERIC -> CapabilityPolicy.CLAUDE_POLICY
                else -> CapabilityPolicy.GENESIS_POLICY // Fallback
            }
            return AgentFirebase(policy, firebaseApp)
        }
    }
}
