package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.security.auth.OAuthService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val oauthService: OAuthService
) : ViewModel() {

    val authState: StateFlow<OAuthService.AuthState> = oauthService.authState

    fun signIn(activityContext: Context) {
        viewModelScope.launch {
            oauthService.signInWithGoogle(activityContext)
        }
    }

    fun bypassSignIn() {
        oauthService.bypassSignIn()
    }

    fun signOut() {
        viewModelScope.launch {
            oauthService.signOut()
        }
    }
}
