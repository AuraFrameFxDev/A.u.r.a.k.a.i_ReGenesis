package dev.aurakai.auraframefx.domains.kai.security.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import dev.aurakai.auraframefx.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.securecomm.keystore.CryptographyManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.network.api.AuthApi
import dev.aurakai.auraframefx.domains.genesis.network.api.RefreshTokenRequest
import dev.aurakai.auraframefx.securecomm.keystore.SecureKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptographyManager,
    private val secureKeyStore: SecureKeyStore,
    private val authApi: AuthApi,
    private val logger: AuraFxLogger
) {
    private val tag = "OAuthService"
    private val credentialManager = CredentialManager.create(context)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    companion object {
        private const val KEY_ACCESS_TOKEN = "oauth_access_token"
        private const val KEY_REFRESH_TOKEN = "oauth_refresh_token"
    }

    /**
     * Initiates a Google sign-in flow using the Android Credential Manager and processes the returned credential.
     *
     * On success the obtained credential is processed (access token stored and auth state updated). On failure the auth state is set to an error describing the failure.
     *
     * @param activityContext The Activity context used to launch the sign-in request; must be a valid UI context.
     */
    suspend fun signInWithGoogle(activityContext: Context) {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.OAUTH_SERVER_CLIENT_ID)
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

    /**
     * Processes a Google sign-in response by extracting a Google ID token, persisting it in the secure key store under `KEY_ACCESS_TOKEN`, and updating the authentication state to a placeholder authenticated user.
     *
     * If the response does not contain a `GoogleIdTokenCredential`, the function performs no action.
     *
     * @param result The credential response returned by the Android Credential Manager.
     */
    private suspend fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            val idToken = credential.idToken
            logger.info(tag, "Received Google ID Token")

            // Securely store token (encrypted via hardware-backed Keystore)
            secureKeyStore.storeData(KEY_ACCESS_TOKEN, idToken.toByteArray())

            // Exchange with Genesis backend (stubbed for now as per tech spec)
            _authState.value = AuthState.Authenticated("User_From_Token")
        }
    }

    /**
     * Refreshes the OAuth access token using the stored refresh token.
     *
     * If no refresh token is available, sets the authentication state to Unauthenticated and returns.
     * On success stores the refreshed access token in the secure key store and logs the outcome.
     * On failure logs the error and sets the authentication state to AuthState.Error with message "Refresh failed".
     */
    suspend fun refreshToken() {
        val refreshToken = secureKeyStore.retrieveData(KEY_REFRESH_TOKEN)?.decodeToString()
        if (refreshToken == null) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        try {
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            secureKeyStore.storeData(KEY_ACCESS_TOKEN, response.token.toByteArray())
            logger.info(tag, "Token refreshed successfully")
        } catch (e: Exception) {
            logger.error(tag, "Token refresh failed", e)
            _authState.value = AuthState.Error("Refresh failed")
        }
    }

    /**
     * Revokes locally stored OAuth tokens and signs the user out.
     *
     * Removes the access and refresh tokens from the secure key store, logs the revocation,
     * and calls `signOut()` to clear credential state and update authentication status.
     * Any exceptions are caught and logged.
     */
    suspend fun revokeToken() {
        try {
            // Local revocation (clear hardware storage)
            secureKeyStore.removeData(KEY_ACCESS_TOKEN)
            secureKeyStore.removeData(KEY_REFRESH_TOKEN)
            logger.info(tag, "Local tokens revoked")
            signOut()
        } catch (e: Exception) {
            logger.error(tag, "Revocation failed", e)
        }
    }

    /**
     * Signs the current user out by clearing credential state, removing the stored access token,
     * and updating the authentication state to `Unauthenticated`.
     *
     * Any exception that occurs during sign-out is caught and logged; errors are not propagated.
     */
    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            secureKeyStore.removeData(KEY_ACCESS_TOKEN)
            _authState.value = AuthState.Unauthenticated
            logger.info(tag, "User signed out")
        } catch (e: Exception) {
            logger.error(tag, "Sign-out failed", e)
        }
    }

    /**
     * Bypasses the sign-in flow for development purposes.
     */
    fun bypassSignIn() {
        _authState.value = AuthState.Authenticated("Sovereign_Dev_User")
    }

    sealed class AuthState {
        object Unauthenticated : AuthState()
        data class Authenticated(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
