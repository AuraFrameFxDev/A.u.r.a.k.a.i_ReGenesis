package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlinx.coroutines.delay

/**
 * 🌊 REGENESIS SPLASH SCREEN
 *
 * Guarded entry point with auto-advance and placeholder support.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Feature Flag Guard: ENABLE_SOULSCRIPT_VIDEO
    // If false (default), we show a lightweight placeholder and auto-advance.
    
    LaunchedEffect(Unit) {
        // Minimum display time for branding
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020205)),
        contentAlignment = Alignment.Center
    ) {
        // Background Branding
        Image(
            painter = painterResource(id = R.drawable.command_deck_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ReGenesis Emblem
            Image(
                painter = painterResource(id = R.drawable.avatar_aura),
                contentDescription = "AuraKai Emblem",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "REGENESIS",
                color = Color.White,
                fontFamily = LEDFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp
            )

            Text(
                text = "EXODUS BUILD 2026",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color(0xFF00E5FF),
                strokeWidth = 2.dp
            )
        }
        
        // System Status footer
        Text(
            text = "INITIALIZING TRINITY PROTOCOL...",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
