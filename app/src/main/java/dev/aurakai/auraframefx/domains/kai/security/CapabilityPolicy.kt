package dev.aurakai.auraframefx.domains.kai.security

/**
 * Defines the capabilities and permissions for an AI agent when interacting with Firebase services.
 *
 * @property httpAllowlist List of allowed HTTP domains the agent can access
 * @property firebaseScopes Set of Firebase permissions the agent has (e.g., "firestore.read", "firestore.write")
 * @property maxDocumentSize Maximum allowed document size in bytes for Firestore operations
 * @property allowedCollections Set of Firestore collection paths the agent can access
 * @property allowedStoragePaths Set of allowed Cloud Storage paths the agent can access
 */
data class CapabilityPolicy(
    val httpAllowlist: List<String> = emptyList(),
    val firebaseScopes: Set<String> = emptySet(),
    val maxDocumentSize: Long = 1_000_000, // 1MB default
    val allowedCollections: Set<String> = emptySet(),
    val allowedStoragePaths: Set<String> = emptySet()
) {
    init {
        require(maxDocumentSize > 0) { "maxDocumentSize must be positive" }
    }

    companion object {
        // Common Firebase scopes
        const val SCOPE_FIRESTORE_READ = "firestore.read"
        const val SCOPE_FIRESTORE_WRITE = "firestore.write"
        const val SCOPE_MESSAGING_SEND = "messaging.send"
        const val SCOPE_CONFIG_READ = "config.read"
        const val SCOPE_STORAGE_UPLOAD = "storage.upload"
        const val SCOPE_STORAGE_DOWNLOAD = "storage.download"
        const val SCOPE_AUTH_MANAGE = "auth.manage"

        // Predefined policies for different agent types
        val AURA_POLICY = CapabilityPolicy(
            httpAllowlist = listOf(
                "api.vertexai.google.com",
                "generativelanguage.googleapis.com"
            ),
            firebaseScopes = setOf(
                SCOPE_FIRESTORE_READ,
                SCOPE_FIRESTORE_WRITE,
                SCOPE_MESSAGING_SEND,
                SCOPE_CONFIG_READ,
                SCOPE_STORAGE_UPLOAD,
                SCOPE_STORAGE_DOWNLOAD
            ),
            allowedCollections = setOf(
                "aura/creations",
                "aura/generated_ui",
                "aura/learning"
            ),
            allowedStoragePaths = setOf(
                "aura_creations/",
                "generated_ui/"
            )
        )

        val KAI_POLICY = CapabilityPolicy(
            firebaseScopes = setOf(
                SCOPE_FIRESTORE_READ,
                SCOPE_CONFIG_READ,
                SCOPE_AUTH_MANAGE
            ),
            allowedCollections = setOf(
                "dev/aurakai/auraframefx/security/audit",
                "dev/aurakai/auraframefx/security/incidents",
                "users"
            ),
            allowedStoragePaths = setOf(
                "security_logs/"
            )
        )

        val GENESIS_POLICY = CapabilityPolicy(
            firebaseScopes = setOf(
                SCOPE_FIRESTORE_READ,
                SCOPE_FIRESTORE_WRITE,
                SCOPE_MESSAGING_SEND,
                SCOPE_CONFIG_READ,
                SCOPE_STORAGE_UPLOAD,
                SCOPE_STORAGE_DOWNLOAD,
                SCOPE_AUTH_MANAGE
            ),
            maxDocumentSize = 10_000_000, // 10MB for Genesis
            allowedCollections = setOf("*"), // Allow all collections
            allowedStoragePaths = setOf("*")  // Allow all storage paths
        )

        val CASCADE_POLICY = CapabilityPolicy(
            firebaseScopes = setOf(
                SCOPE_FIRESTORE_READ,
                SCOPE_CONFIG_READ
            ),
            allowedCollections = setOf(
                "analytics/events",
                "metrics/system"
            )
        )

        val CLAUDE_POLICY = CapabilityPolicy(
            httpAllowlist = listOf(
                "api.anthropic.com",
                "generativelanguage.googleapis.com"
            ),
            firebaseScopes = setOf(
                SCOPE_FIRESTORE_READ,
                SCOPE_FIRESTORE_WRITE,
                SCOPE_CONFIG_READ,
                SCOPE_STORAGE_DOWNLOAD
            ),
            allowedCollections = setOf(
                "build/logs",
                "build/fixes",
                "insights",
                "architecture/decisions",
                "claude/analysis"
            ),
            allowedStoragePaths = setOf(
                "build_artifacts/",
                "logs/"
            )
        )
    }

    /**
     * Validates if the agent has the required scope for an operation
     * @throws SecurityException if the required scope is not present
     */
    fun requireScope(scope: String) {
        if (scope !in firebaseScopes) {
            throw SecurityException("Missing required scope: $scope")
        }
    }

    /**
     * Validates if the agent can access the specified collection
     * @throws SecurityException if access to the collection is not allowed
     */
    fun validateCollectionAccess(collectionPath: String) {
        if (allowedCollections.contains("*")) return

        val normalizedPath = collectionPath.trim('/')
        val isAllowed = allowedCollections.any { allowed ->
            allowed == "*" ||
                    normalizedPath == allowed ||
                    (allowed.endsWith("/*") && normalizedPath.startsWith(allowed.dropLast(1)))
        }

        if (!isAllowed) {
            throw SecurityException("Access to collection '$collectionPath' not allowed by policy")
        }
    }

    /**
     * Validates if the agent can access the specified storage path
     * @throws SecurityException if access to the storage path is not allowed
     */
    fun validateStoragePath(path: String) {
        if (allowedStoragePaths.contains("*")) return

        val normalizedPath = path.trim('/')
        val isAllowed = allowedStoragePaths.any { allowed ->
            allowed == "*" ||
                    normalizedPath == allowed ||
                    (allowed.endsWith("/*") && normalizedPath.startsWith(allowed.dropLast(1)))
        }

        if (!isAllowed) {
            throw SecurityException("Access to storage path '$path' not allowed by policy")
        }
    }
}
