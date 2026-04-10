package dev.aurakai.auraframefx.domains.kai.security.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.core.CryptographyManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.network.api.AuthApi
import dev.aurakai.auraframefx.domains.genesis.network.api.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptographyManager,
    private val authApi: AuthApi,
    private val logger: AuraFxLogger
) {
    private val tag = "OAuthService"
    private val credentialManager = CredentialManager.create(context)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    suspend fun signInWithGoogle(activityContext: Context) {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("YOUR_SERVER_CLIENT_ID") // TODO: Get from strings/buildConfig
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(activityContext, request)
            handleSignInResult(result)
        } catch (e: Exception) {
            logger.error(tag, "Google Sign-In failed", e)
            _authState.value = AuthState.Error("Sign-In failed: ${e.message}")
        }
    }

    private suspend fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            val idToken = credential.idToken
            logger.info(tag, "Received Google ID Token")
            
            // Securely store token (encrypted)
            val encryptedToken = cryptoManager.encrypt(idToken.toByteArray(), "auth_token")
            // TODO: Persist encryptedToken to DataStore or SharedPreferences
            
            // Exchange with Genesis backend
            try {
                // val response = authApi.loginWithGoogle(idToken) 
                // _authState.value = AuthState.Authenticated(response.user)
                _authState.value = AuthState.Authenticated("User_From_Token")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Backend exchange failed")
            }
        }
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            cryptoManager.removeKey("auth_token")
            _authState.value = AuthState.Unauthenticated
        } catch (e: Exception) {
            logger.error(tag, "Sign-out failed", e)
        }
    }

    sealed class AuthState {
        object Unauthenticated : AuthState()
        data class Authenticated(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
