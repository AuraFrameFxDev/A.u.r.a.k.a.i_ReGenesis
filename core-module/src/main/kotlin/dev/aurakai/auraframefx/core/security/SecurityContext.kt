package dev.aurakai.auraframefx.core.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class SecurityStatus(
    val threatLevel: KaiSentinelBus.ThreatLevel = KaiSentinelBus.ThreatLevel.NOMINAL,
    val detectedThreats: List<String> = emptyList(),
    val lastScanTime: Long = 0L
)

/**
 * Sovereign security session manager.
 * Tracks authentication state and permission grants for all LDO agents.
 */
@Singleton
class SecurityContext @Inject constructor(
    private val keystoreManager: KeystoreManager,
    @ApplicationContext private val context: Context
) {
    private val _securityState = MutableStateFlow(SecurityStatus())
    val securityState: StateFlow<SecurityStatus> = _securityState.asStateFlow()

    private val _threatDetectionActive = MutableStateFlow(false)
    val threatDetectionActive: StateFlow<Boolean> = _threatDetectionActive.asStateFlow()

    fun startThreatDetection() {
        _threatDetectionActive.value = true
        Timber.i("SecurityContext: Threat detection started")
    }

    fun stopThreatDetection() {
        _threatDetectionActive.value = false
        Timber.i("SecurityContext: Threat detection stopped")
    }

    fun updateSecurityStatus(status: SecurityStatus) {
        _securityState.value = status
    }

    fun validateRequest(type: String, details: String) {
        Timber.d("SecurityContext: Validating request — type=$type")
        
        // 🛡️ Basic Sovereign Validation Rules
        val session = _sessionState.value
        if (session !is SessionState.Authenticated) {
            Timber.w("SecurityContext: Request rejected — Unauthenticated")
            throw SecurityException("Unauthenticated access attempt for operation: $type")
        }

        // Rule 1: Root-level operations require SOVEREIGN_ACCESS
        if (type.contains("root", ignoreCase = true) || type.contains("system", ignoreCase = true)) {
            if (!session.permissions.contains(SecurityPermission.SOVEREIGN_ACCESS)) {
                Timber.e("SecurityContext: VETO — High-risk operation $type blocked for user ${session.userId}")
                throw SecurityException("Security VETO: Sovereign permission required for root operations")
            }
        }

        // Rule 2: Standard write operations
        if (type.startsWith("write") || type.contains("delete")) {
            if (!session.permissions.contains(SecurityPermission.WRITE_STANDARD)) {
                throw SecurityException("Permission denied: WRITE_STANDARD required for $type")
            }
        }

        Timber.i("SecurityContext: Request validated — $type")
    }

    sealed class SessionState {
        object Unauthenticated : SessionState()
        data class Authenticated(
            val userId: String,
            val permissions: Set<SecurityPermission>
        ) : SessionState()
        object Locked : SessionState()
    }

    enum class SecurityPermission {
        READ_ONLY,
        WRITE_STANDARD,
        AGENT_CONTROL,
        SYSTEM_HOOK,
        SOVEREIGN_ACCESS,
        PANDORA_UNLOCK,
        FIREBASE_WRITE
    }

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Unauthenticated)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun authenticate(userId: String, token: String): Boolean {
        return if (keystoreManager.validateToken(token)) {
            _sessionState.value = SessionState.Authenticated(
                userId = userId,
                permissions = SecurityPermission.entries.toSet()
            )
            Timber.i("SecurityContext: Session established — userId=$userId")
            true
        } else {
            Timber.w("SecurityContext: Authentication rejected for userId=$userId")
            false
        }
    }

    fun bootstrapSovereignSession(userId: String = "LDO_SOVEREIGN") {
        // 🛡️ High-Security Hardening: Require a system property or environment variable
        // to prevent unauthorized god-mode bootstrapping in production builds.
        val isDevMode = System.getProperty("aurakai.dev_mode") == "true"
        if (!isDevMode && !userId.startsWith("SYSTEM_")) {
            Timber.e("SecurityContext: UNAUTHORIZED SOVEREIGN BOOTSTRAP ATTEMPT — userId=$userId")
            throw SecurityException("Unauthorized sovereign bootstrap attempt")
        }

        _sessionState.value = SessionState.Authenticated(
            userId = userId,
            permissions = SecurityPermission.entries.toSet()
        )
        Timber.i("SecurityContext: Sovereign session bootstrapped — userId=$userId")
    }

    fun hasPermission(permission: SecurityPermission): Boolean {
        return (_sessionState.value as? SessionState.Authenticated)
            ?.permissions?.contains(permission) == true
    }

    fun requirePermission(permission: SecurityPermission) {
        check(hasPermission(permission)) {
            "SecurityContext: Permission denied — $permission required"
        }
    }

    fun getCurrentUserId(): String? =
        (_sessionState.value as? SessionState.Authenticated)?.userId

    fun isAuthenticated(): Boolean = _sessionState.value is SessionState.Authenticated

    fun lock() {
        Timber.w("SecurityContext: Session locked")
        _sessionState.value = SessionState.Locked
    }

    fun revoke() {
        Timber.w("SecurityContext: Session revoked — returning to unauthenticated")
        _sessionState.value = SessionState.Unauthenticated
    }
}
