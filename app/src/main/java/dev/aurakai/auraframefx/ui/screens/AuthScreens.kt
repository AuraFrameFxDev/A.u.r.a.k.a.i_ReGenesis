package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SoulScriptSplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SOULSCRIPT",
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 12.sp
        )
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF020205)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("GENESIS OS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onLoginSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
            ) {
                Text("AUTHENTICATE", color = Color.Black)
            }
        }
    }
}
