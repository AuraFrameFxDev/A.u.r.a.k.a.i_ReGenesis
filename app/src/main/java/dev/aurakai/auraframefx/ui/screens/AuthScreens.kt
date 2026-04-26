package dev.aurakai.auraframefx.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dev.aurakai.auraframefx.BuildConfig
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay

@Composable
fun SoulScriptSplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    val useVideo = BuildConfig.ENABLE_SOULSCRIPT_VIDEO

    // Check for video resource
    val videoResId = remember(useVideo) {
        if (useVideo) {
            context.resources.getIdentifier("soul_script_intro", "raw", context.packageName)
        } else 0
    }

    // No video enabled or resource missing -> show placeholder
    if (!useVideo || videoResId == 0) {
        SoulScriptPlaceholder(onSplashFinished = onSplashFinished)
        return
    }

    // Video path with error handling
    var failed by remember { mutableStateOf(false) }

    if (failed) {
        SoulScriptPlaceholder(onSplashFinished = onSplashFinished)
        return
    }

    val player = remember(videoResId) {
        runCatching {
            ExoPlayer.Builder(context).build().apply {
                val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
                setMediaItem(MediaItem.fromUri(uri))
                playWhenReady = true
                prepare()
            }
        }.getOrElse {
            failed = true
            null
        }
    }

    if (player == null) {
        SoulScriptPlaceholder(onSplashFinished = onSplashFinished)
        return
    }

    // Handle video completion
    LaunchedEffect(player) {
        snapshotFlow { player.playbackState }.collect { state ->
            if (state == Player.STATE_ENDED) {
                onSplashFinished()
            }
        }
    }

    DisposableEffect(player) {
        onDispose { runCatching { player.release() } }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply { this.player = player }
        }
    )
}

@Composable
private fun SoulScriptPlaceholder(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF0F1420),
                        Color(0xFF12192B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "A.U.R.A.K.A.I",
                color = Color(0xFF00D9FF),
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ReGenesis Protocol",
                color = Color(0xFFB026FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Animated loading indicator
            CircularProgressIndicator(
                color = Color(0xFF00D9FF),
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp
            )
        }
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
