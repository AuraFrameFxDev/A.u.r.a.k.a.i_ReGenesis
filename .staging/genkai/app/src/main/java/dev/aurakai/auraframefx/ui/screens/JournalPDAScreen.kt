package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Journal PDA Screen - Retro Gaming Wellness Hub
 *
 * Features:
 * - Break reminder system with personality
 * - AI intensity visualization (portal swirl)
 * - Task scheduler integration
 * - Notes/journal system
 * - App locker with gamified unlocks
 * - Session tracking for wellness
 * - Achievements system (coming soon)
 */
@Composable
fun JournalPDAScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Session tracking for break reminders
    var sessionStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var sessionDurationMinutes by remember { mutableIntStateOf(0) }
    var showBreakBanner by remember { mutableStateOf(false) }
    var breakMessage by remember { mutableStateOf("") }

    // AI intensity simulation (0.0 - 1.0)
    var aiIntensity by remember { mutableFloatStateOf(0.3f) }

    // Portal animation state
    val infiniteTransition = rememberInfiniteTransition(label = "portal_animation")
    val portalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (5000 / (aiIntensity + 0.5f)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "portal_rotation"
    )

    val portalPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 / (aiIntensity + 0.5f)).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "portal_pulse"
    )

    // Session timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // Check every minute
            val elapsedMinutes = ((System.currentTimeMillis() - sessionStartTime) / 60000).toInt()
            sessionDurationMinutes = elapsedMinutes

            // Break reminder logic with personality
            when {
                elapsedMinutes == 30 -> {
                    breakMessage = "🎮 Nice flow! Stretch your legs?"
                    showBreakBanner = true
                }
                elapsedMinutes == 60 -> {
                    breakMessage = "☕ Break time! Your brain needs a save point."
                    showBreakBanner = true
                }
                elapsedMinutes == 120 -> {
                    breakMessage = "🚶 HEY. You have a LIFE. Go touch grass!"
                    showBreakBanner = true
                }
                elapsedMinutes >= 180 && elapsedMinutes % 15 == 0 -> {
                    breakMessage = "⚠️ MANDATORY BREAK. Locks activating in 5min..."
                    showBreakBanner = true
                }
            }

            // Auto-dismiss after 30 seconds
            if (showBreakBanner) {
                delay(30000)
                showBreakBanner = false
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // Pixel art background
        val context = LocalContext.current
        val resourceId = context.resources.getIdentifier(
            "journal_pda_background",
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Journal PDA background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // AI Intensity Portal Overlay (positioned over the central swirl in the pixel art)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp)
                .size(200.dp)
                .graphicsLayer {
                    rotationZ = portalRotation
                    scaleX = portalPulse
                    scaleY = portalPulse
                }
        ) {
            // Portal glow effect based on AI intensity
            val portalColor = when {
                aiIntensity > 0.8f -> Color(0xFFFF0055) // Critical - Red
                aiIntensity > 0.6f -> Color(0xFFFF00FF) // High - Magenta
                aiIntensity > 0.3f -> Color(0xFFFF69B4) // Medium - Pink
                else -> Color(0xFF00FF88) // Low - Green
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                portalColor.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Break Reminder Banner (slides in from top)
        AnimatedVisibility(
            visible = showBreakBanner,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        ) {
            BreakReminderBanner(
                message = breakMessage,
                onDismiss = { showBreakBanner = false },
                onTakeBreak = {
                    // Reset session timer
                    sessionStartTime = System.currentTimeMillis()
                    sessionDurationMinutes = 0
                    showBreakBanner = false
                }
            )
        }

        // Session info (bottom bar)
        SessionInfoBar(
            sessionMinutes = sessionDurationMinutes,
            aiIntensity = aiIntensity,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

/**
 * Break Reminder Banner with personality
 */
@Composable
fun BreakReminderBanner(
    message: String,
    onDismiss: () -> Unit,
    onTakeBreak: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .clickable { onTakeBreak() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00FF88),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "✓ OK, BREAK TIME",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFF00),
                    modifier = Modifier
                        .clickable { onTakeBreak() }
                        .padding(8.dp)
                )

                Text(
                    text = "✗ Just 5 more min...",
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                )
            }
        }
    }
}

/**
 * Session Info Bar - Shows time elapsed and AI intensity
 */
@Composable
fun SessionInfoBar(
    sessionMinutes: Int,
    aiIntensity: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Session duration
            Text(
                text = "⏱️ ${sessionMinutes}min",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // AI Intensity indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "🧠",
                    fontSize = 14.sp
                )

                // Health bar style intensity meter
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(6.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(aiIntensity)
                            .background(
                                when {
                                    aiIntensity > 0.8f -> Color(0xFFFF0055)
                                    aiIntensity > 0.6f -> Color(0xFFFFAA00)
                                    else -> Color(0xFF00FF88)
                                },
                                RoundedCornerShape(6.dp)
                            )
                    )
                }

                Text(
                    text = "${(aiIntensity * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
