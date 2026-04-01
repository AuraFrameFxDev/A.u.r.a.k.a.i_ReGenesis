package dev.aurakai.auraframefx.core.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sovereign security session manager.
 * Tracks authentication state and permission grants for all LDO agents.
 */
@Singleton
class SecurityContext @Inject constructor(
    private val keystoreManager: KeystoreManager,
    @ApplicationContext private val context: Context
) {
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
        _sessionState.value = SessionState.Authenticated(
            userId = userId,
            permissions = SecurityPermission.entries.toSet()
        )
        Timber.i("SecurityContext: Sovereign session bootstrapped")
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
