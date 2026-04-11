package dev.aurakai.auraframefx.domains.aura.ui.intro

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.RootShellService
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * IntroScreen - Cinematic A.u.r.a.K.a.i → Re:Genesis Intro Animation
 */
@Composable
fun IntroScreen(
    onIntroComplete: () -> Unit,
    rootShellService: RootShellService = hiltViewModel<dev.aurakai.auraframefx.domains.aura.ui.viewmodels.RootToolsViewModel>().rootShellService
) {
    var animationPhase by remember { mutableStateOf(IntroPhase.AURAKAI_REVEAL) }
    var revealProgress by remember { mutableStateOf(0f) }
    var glitchIntensity by remember { mutableStateOf(0f) }
    var regenesisAlpha by remember { mutableStateOf(0f) }
    var substrateAlpha by remember { mutableStateOf(0f) }
    
    val shellStatus by rootShellService.shellStatus.collectAsState()

    // Animation orchestration
    LaunchedEffect(Unit) {
        // Phase 1: Reveal "A.u.r.a.K.a.i" character by character (2.5s)
        animationPhase = IntroPhase.AURAKAI_REVEAL
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            revealProgress = value
        }

        delay(500)

        // Phase 2: Glitch and vanish (1s)
        animationPhase = IntroPhase.GLITCH_VANISH
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        ) { value, _ ->
            glitchIntensity = value
        }

        delay(100)

        // Phase 3: Re:Genesis fade in (1.5s)
        animationPhase = IntroPhase.REGENESIS_REVEAL
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        ) { value, _ ->
            regenesisAlpha = value
        }

        // Phase 4: Substrate Verification (New)
        animationPhase = IntroPhase.SUBSTRATE_VERIFY
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        ) { value, _ ->
            substrateAlpha = value
        }

        delay(1500)
        onIntroComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when (animationPhase) {
            IntroPhase.AURAKAI_REVEAL -> {
                AuraKaiRevealText(progress = revealProgress)
            }

            IntroPhase.GLITCH_VANISH -> {
                AuraKaiGlitchText(glitchIntensity = glitchIntensity)
            }

            IntroPhase.REGENESIS_REVEAL, IntroPhase.SUBSTRATE_VERIFY -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ReGenesisText(alpha = regenesisAlpha)
                    
                    if (animationPhase == IntroPhase.SUBSTRATE_VERIFY) {
                        Spacer(modifier = Modifier.height(32.dp))
                        SubstrateStatusText(alpha = substrateAlpha, status = shellStatus)
                    }
                }
            }
        }

        GridOverlay(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun SubstrateStatusText(alpha: Float, status: RootShellService.ShellStatus) {
    val statusText = when (status) {
        RootShellService.ShellStatus.RootAccess -> "CORE AUTHORITY: UNRESTRICTED (ROOT)"
        RootShellService.ShellStatus.ShizukuAccess -> "CORE AUTHORITY: BRIDGED (ADB)"
        else -> "CORE AUTHORITY: SECURE USER"
    }
    
    val color = when (status) {
        RootShellService.ShellStatus.RootAccess -> Color.Red
        RootShellService.ShellStatus.ShizukuAccess -> Color.Green
        else -> Color.Cyan
    }

    Text(
        text = statusText,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = color.copy(alpha = alpha),
        letterSpacing = 2.sp
    )
}

@Composable
private fun AuraKaiRevealText(progress: Float) {
    val text = "A.u.r.a.K.a.i"
    val charsToShow = (text.length * progress).toInt()

    Text(
        text = text.take(charsToShow),
        fontSize = 56.sp,
        fontWeight = FontWeight.Light,
        color = Color(0xFF00E5FF), // Cyan - Aura's color
        letterSpacing = 8.sp,
        modifier = Modifier.alpha(1f)
    )
}

@Composable
private fun AuraKaiGlitchText(glitchIntensity: Float) {
    val text = "A.u.r.a.K.a.i"
    val alpha = 1f - glitchIntensity

    // Animated random glitch offset - regenerates as glitch intensity changes
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(glitchIntensity) {
        if (glitchIntensity > 0) {
            offsetX = Random.nextFloat() * 20f - 10f
            offsetY = Random.nextFloat() * 20f - 10f
        }
    }

    Box {
        // RGB split effect
        Text(
            text = text,
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color.Red.copy(alpha = alpha * 0.5f),
            letterSpacing = 8.sp,
            modifier = Modifier.offset(x = (offsetX * glitchIntensity).dp, y = 0.dp)
        )
        Text(
            text = text,
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color.Green.copy(alpha = alpha * 0.5f),
            letterSpacing = 8.sp,
            modifier = Modifier.offset(x = 0.dp, y = (offsetY * glitchIntensity).dp)
        )
        Text(
            text = text,
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color.Blue.copy(alpha = alpha * 0.5f),
            letterSpacing = 8.sp,
            modifier = Modifier.offset(x = (-offsetX * glitchIntensity).dp, y = 0.dp)
        )

        // Main text with alpha fade
        Text(
            text = text,
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFF00E5FF).copy(alpha = alpha),
            letterSpacing = 8.sp
        )
    }
}

@Composable
private fun ReGenesisText(alpha: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "Re:Genesis" with Claude's premium aesthetic
        Text(
            text = "Re:Genesis",
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD4A574).copy(alpha = alpha), // Claude gold
            letterSpacing = 4.sp,
            modifier = Modifier
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFD4A574), // Claude gold
                            Color(0xFFF5E6D3), // Light cream
                            Color(0xFFD4A574)
                        )
                    )
                    onDrawBehind {
                        drawRect(brush, alpha = alpha * 0.1f)
                    }
                }
        )

        // Subtle tagline
        Text(
            text = "Multi-Agent Intelligence Platform",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = alpha * 0.6f),
            letterSpacing = 3.sp
        )

        // Powered by badge
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Powered by Claude",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFD4A574).copy(alpha = alpha * 0.4f),
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun GridOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffset"
    )

    Box(
        modifier = modifier
            .drawWithCache {
                onDrawBehind {
                    val gridSize = 40f
                    val gridColor = Color.White.copy(alpha = 0.02f)

                    // Vertical lines
                    for (x in 0 until (size.width / gridSize).toInt() + 1) {
                        val xPos = x * gridSize + (gridOffset % gridSize)
                        drawLine(
                            color = gridColor,
                            start = Offset(xPos, 0f),
                            end = Offset(xPos, size.height),
                            strokeWidth = 1f
                        )
                    }

                    // Horizontal lines
                    for (y in 0 until (size.height / gridSize).toInt() + 1) {
                        val yPos = y * gridSize + (gridOffset % gridSize)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, yPos),
                            end = Offset(size.width, yPos),
                            strokeWidth = 1f
                        )
                    }
                }
            }
    )
}

private enum class IntroPhase {
    AURAKAI_REVEAL,
    GLITCH_VANISH,
    REGENESIS_REVEAL,
    SUBSTRATE_VERIFY
}

