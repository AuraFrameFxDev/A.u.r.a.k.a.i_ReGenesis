package dev.aurakai.auraframefx.domains.aura.ui.gates

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.LoginViewModel
import dev.aurakai.auraframefx.domains.kai.security.auth.OAuthService

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        if (authState is OAuthService.AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0A0A), Color(0xFF1A1A2E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "RE:GENESIS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4A574), // Claude gold
                letterSpacing = 4.sp
            )
            
            Text(
                text = "Secure Identity Verification",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 64.dp)
            )

            AnimatedVisibility(
                visible = authState is OAuthService.AuthState.Unauthenticated || authState is OAuthService.AuthState.AuthenticationError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { viewModel.signIn(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD4A574).copy(alpha = 0.5f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("SIGN IN WITH GOOGLE (NOT CONNECTED)", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { viewModel.bypassSignIn() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD4A574),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("BYPASS SECURE IDENTITY", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            if (authState is OAuthService.AuthState.AuthenticationError) {
                Text(
                    text = (authState as OAuthService.AuthState.AuthenticationError).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            
            if (authState is OAuthService.AuthState.Authenticated) {
                CircularProgressIndicator(color = Color(0xFFD4A574))
            }
        }
    }
}
