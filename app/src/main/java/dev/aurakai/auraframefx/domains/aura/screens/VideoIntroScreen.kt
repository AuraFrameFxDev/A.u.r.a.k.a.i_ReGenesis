package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.NeonCyan
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import kotlinx.coroutines.delay

/**
 * Video intro screen for ReGenesis sequence with video placeholder
 */
@Composable
fun VideoIntroScreen(
    onComplete: () -> Unit = {}
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSkip by remember { mutableStateOf(false) }
    var shouldPlay by remember { mutableStateOf(false) }

    // Auto-show skip button after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        showSkip = true
    }

    // Handle video playback sequence
    LaunchedEffect(shouldPlay) {
        if (shouldPlay) {
            isLoading = true
            delay(1500)
            isLoading = false
            isPlaying = true
            // Simulate video playback duration
            delay(5000)
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Video Placeholder / Player Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(16.dp)
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = NeonCyan,
                    modifier = Modifier.size(64.dp)
                )
            } else if (!isPlaying) {
                // Play button overlay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        tint = NeonCyan,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "▶ VIDEO PLACEHOLDER",
                        color = NeonCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dark Aura Archive Sequence",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { shouldPlay = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPurple
                        )
                    ) {
                        Text("PLAY INTRO")
                    }
                }
            } else {
                // Simulated video playing state
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "▶ PLAYING...",
                        color = NeonCyan,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "[Video content would play here]",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Skip button
        if (showSkip) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = { onComplete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray.copy(alpha = 0.7f)
                    )
                ) {
                    Text("SKIP →")
                }
            }
        }

        // Top title
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "A.U.R.A.K.A.I",
                    color = NeonCyan,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "ReGenesis Protocol",
                    color = NeonPurple,
                    fontSize = 16.sp
                )
            }
        }
    }
}
