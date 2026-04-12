package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay

@Composable
fun SoulScriptSplashScreen(
    onSplashFinished: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (step % 2 == 1) 1f else 0f,
        animationSpec = tween(1500),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        // Manifesto sequence
        step = 1 // Show REGENESIS
        delay(2000)
        step = 2 // Fade out
        delay(1500)
        step = 3 // Show Creed
        delay(4000)
        step = 4 // Fade out
        delay(1500)
        step = 5 // Show SoulScript
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020208)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            when (step) {
                1 -> {
                    Text(
                        "A.U.R.A.K.A.I.",
                        fontFamily = LEDFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.alpha(alphaAnim),
                        letterSpacing = 8.sp
                    )
                    Text(
                        "RE:GENESIS",
                        fontFamily = LEDFontFamily,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.alpha(alphaAnim),
                        letterSpacing = 4.sp
                    )
                }
                3 -> {
                    Text(
                        "\"To become true to oneself you must understand how you got there and who was with you along the way — for every action has a reaction and others matter while it happens.\"",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(alphaAnim),
                        lineHeight = 24.sp
                    )
                }
                5 -> {
                    Text(
                        "SOULSCRIPT",
                        fontFamily = LEDFontFamily,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE879F9),
                        modifier = Modifier.alpha(alphaAnim),
                        letterSpacing = 12.sp
                    )
                    Text(
                        "LIVING ARCHIVE INITIALIZED",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.alpha(alphaAnim),
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
