package dev.aurakai.auraframefx.domains.kai.security

/**
 * Defines the capabilities and permissions for an AI agent when interacting with Firebase services.
 */
data class CapabilityPolicy(
    val httpAllowlist: List<String> = emptyList(),
    val firebaseScopes: Set<String> = emptySet(),
    val maxDocumentSize: Long = 1_000_000, 
    val allowedCollections: Set<String> = emptySet(),
    val allowedStoragePaths: Set<String> = emptySet()
) {
    init {
        require(maxDocumentSize > 0) { "maxDocumentSize must be positive" }
    }

    companion object {
        const val SCOPE_FIRESTORE_READ = "firestore.read"
        const val SCOPE_FIRESTORE_WRITE = "firestore.write"
        const val SCOPE_MESSAGING_SEND = "messaging.send"
        const val SCOPE_CONFIG_READ = "config.read"
        const val SCOPE_STORAGE_UPLOAD = "storage.upload"
        const val SCOPE_STORAGE_DOWNLOAD = "storage.download"
        const val SCOPE_AUTH_MANAGE = "auth.manage"

        val AURA = CapabilityPolicy(
            httpAllowlist = listOf("api.vertexai.google.com", "generativelanguage.googleapis.com"),
            firebaseScopes = setOf(SCOPE_FIRESTORE_READ, SCOPE_FIRESTORE_WRITE, SCOPE_MESSAGING_SEND, SCOPE_CONFIG_READ, SCOPE_STORAGE_UPLOAD, SCOPE_STORAGE_DOWNLOAD),
            allowedCollections = setOf("aura/creations", "aura/generated_ui", "aura/learning"),
            allowedStoragePaths = setOf("aura_creations/", "generated_ui/")
        )

        val KAI = CapabilityPolicy(
            firebaseScopes = setOf(SCOPE_FIRESTORE_READ, SCOPE_CONFIG_READ, SCOPE_AUTH_MANAGE),
            allowedCollections = setOf("dev/aurakai/auraframefx/security/audit", "dev/aurakai/auraframefx/security/incidents", "users"),
            allowedStoragePaths = setOf("security_logs/")
        )

        val GENESIS = CapabilityPolicy(
            firebaseScopes = setOf(SCOPE_FIRESTORE_READ, SCOPE_FIRESTORE_WRITE, SCOPE_MESSAGING_SEND, SCOPE_CONFIG_READ, SCOPE_STORAGE_UPLOAD, SCOPE_STORAGE_DOWNLOAD, SCOPE_AUTH_MANAGE),
            maxDocumentSize = 10_000_000,
            allowedCollections = setOf("*"),
            allowedStoragePaths = setOf("*")
        )

        val CASCADE = CapabilityPolicy(
            firebaseScopes = setOf(SCOPE_FIRESTORE_READ, SCOPE_CONFIG_READ),
            allowedCollections = setOf("analytics/events", "metrics/system")
        )

        val CLAUDE = CapabilityPolicy(
            httpAllowlist = listOf("api.anthropic.com", "generativelanguage.googleapis.com"),
            firebaseScopes = setOf(SCOPE_FIRESTORE_READ, SCOPE_FIRESTORE_WRITE, SCOPE_CONFIG_READ, SCOPE_STORAGE_DOWNLOAD),
            allowedCollections = setOf("build/logs", "build/fixes", "insights", "architecture/decisions", "claude/analysis"),
            allowedStoragePaths = setOf("build_artifacts/", "logs/")
        )
    }

    fun requireScope(scope: String) {
        if (scope !in firebaseScopes) throw SecurityException("Missing required scope: $scope")
    }

    fun validateCollectionAccess(collectionPath: String) {
        if (allowedCollections.contains("*")) return
        val normalizedPath = collectionPath.trim('/')
        if (!allowedCollections.any { allowed -> allowed == "*" || normalizedPath == allowed || (allowed.endsWith("/*") && normalizedPath.startsWith(allowed.dropLast(1))) }) {
            throw SecurityException("Access to collection '$collectionPath' not allowed")
        }
    }

    fun validateStoragePath(path: String) {
        if (allowedStoragePaths.contains("*")) return
        val normalizedPath = path.trim('/')
        if (!allowedStoragePaths.any { allowed -> allowed == "*" || normalizedPath == allowed || (allowed.endsWith("/*") && normalizedPath.startsWith(allowed.dropLast(1))) }) {
            throw SecurityException("Access to storage path '$path' not allowed")
        }
    }
}
