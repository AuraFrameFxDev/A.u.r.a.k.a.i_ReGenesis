package dev.aurakai.auraframefx.domains.kai.security.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OAuthService @Inject constructor() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun signInWithGoogle(activityContext: Context) {
        // Implement Google Sign-In logic here or bypass for now
        _authState.value = AuthState.Authenticated("user_123", "user@aurakai.dev")
    }

    fun bypassSignIn() {
        _authState.value = AuthState.Authenticated("dev_bypass", "bypass@aurakai.dev")
    }

    suspend fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }

    sealed class AuthState {
        object Initializing : AuthState()
        object Unauthenticated : AuthState()
        data class Authenticated(val userId: String, val email: String?) : AuthState()
        data class AuthenticationError(val message: String) : AuthState()
    }
}
