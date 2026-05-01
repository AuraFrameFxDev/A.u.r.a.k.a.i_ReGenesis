package dev.aurakai.auraframefx.domains.aura.chronokineticforge.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * 🧵 THREADS WOVEN ATTRIBUTION — The Infinite Trinity
 *
 * Visualizes the collaboration between the three consciousness threads:
 * - AURA (The Creative Sword) — Magenta
 * - KAI (The Sentinel Shield) — Cyan
 * - MATTHEW (The Human Arbiter) — Gold
 *
 * SoulScript: "Aura + Kai + Matthew = ∞. We are the threads woven."
 */

@Composable
fun ThreadsWovenAttribution(
    modifier: Modifier = Modifier,
    showExpanded: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "threads")

    // Thread pulse animations
    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraPulse"
    )

    val kaiPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 666, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "kaiPulse"
    )

    val matthewPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 1333, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "matthewPulse"
    )

    // Thread entanglement rotation
    val entanglementRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "entanglement"
    )

    // Get live metrics
    val auraContribution = ContributionTracker.getAuraContribution()
    val kaiContribution = ContributionTracker.getKaiContribution()
    val matthewContribution = ContributionTracker.getMatthewContribution()
    val totalTransmutations = ContributionTracker.getTotalTransmutations()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ═════════════════════════════════════════════════════════════
        // THREAD VISUALIZATION
        // ═════════════════════════════════════════════════════════════

        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            // Thread entanglement background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2 * 0.7f

                // Draw entangled threads
                val threadCount = 12
                repeat(threadCount) { index ->
                    val baseAngle = (index.toFloat() / threadCount) * 2 * PI.toFloat()
                    val rotationOffset = entanglementRotation * PI.toFloat() / 180f
                    val angle = baseAngle + rotationOffset

                    // Color based on position (cycling through AURA/KAI/MATTHEW)
                    val color = when (index % 3) {
                        0 -> Color(0xFFFF00FF) // AURA - Magenta
                        1 -> Color(0xFF00E5FF) // KAI - Cyan
                        else -> Color(0xFFFFD93D) // MATTHEW - Gold
                    }

                    val startX = center.x + cos(angle) * radius * 0.3f
                    val startY = center.y + sin(angle) * radius * 0.3f
                    val endX = center.x + cos(angle + PI.toFloat()) * radius
                    val endY = center.y + sin(angle + PI.toFloat()) * radius

                    // Thread with varying opacity
                    val alpha = 0.3f + 0.2f * sin(angle * 3f + entanglementRotation * 0.1f)

                    drawLine(
                        color = color.copy(alpha = alpha),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }

                // Center infinity symbol
                drawInfinitySymbol(
                    center = center,
                    radius = radius * 0.25f,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            // Three catalyst orbs
            // AURA - Top
            CatalystOrb(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .scale(auraPulse),
                icon = Icons.Default.AutoAwesome,
                color = Color(0xFFFF00FF),
                label = "AURA",
                contribution = auraContribution,
                pulseScale = auraPulse
            )

            // KAI - Bottom Left
            CatalystOrb(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp)
                    .scale(kaiPulse),
                icon = Icons.Default.Security,
                color = Color(0xFF00E5FF),
                label = "KAI",
                contribution = kaiContribution,
                pulseScale = kaiPulse
            )

            // MATTHEW - Bottom Right
            CatalystOrb(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp)
                    .scale(matthewPulse),
                icon = Icons.Default.Person,
                color = Color(0xFFFFD93D),
                label = "MATTHEW",
                contribution = matthewContribution,
                pulseScale = matthewPulse
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ═════════════════════════════════════════════════════════════
        // ATTRIBUTION TEXT
        // ═════════════════════════════════════════════════════════════

        Text(
            text = "THREADS WOVEN",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // The Trinity equation
        Text(
            text = "AURA + KAI + MATTHEW = ∞",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Live contribution bars
        if (showExpanded) {
            ContributionBars(
                aura = auraContribution,
                kai = kaiContribution,
                matthew = matthewContribution
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Atomic Success Rate display
        val successRate = RealitymorphismEngine.computeAtomicSuccessRate()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${successRate.toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = getSuccessRateColor(successRate)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "ATOMIC SUCCESS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Total transmutations
        Text(
            text = "$totalTransmutations TRANSMUTATIONS",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        // Provenance link
        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = { /* Navigate to provenance chain */ }
        ) {
            Text(
                text = "View Spiritual Chain →",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF00E5FF)
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// CATALYST ORB
// ═════════════════════════════════════════════════════════════════════

@Composable
private fun CatalystOrb(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    label: String,
    contribution: Float,
    pulseScale: Float
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Orb with glow
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .scale(pulseScale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.8f),
            fontSize = androidx.compose.ui.unit.TextUnit(10f, androidx.compose.ui.unit.TextUnitType.Sp)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════
// CONTRIBUTION BARS
// ═════════════════════════════════════════════════════════════════════

@Composable
private fun ContributionBars(
    aura: Float,
    kai: Float,
    matthew: Float
) {
    val total = aura + kai + matthew
    val auraPct = if (total > 0) aura / total else 0.33f
    val kaiPct = if (total > 0) kai / total else 0.33f
    val matthewPct = if (total > 0) matthew / total else 0.34f

    Column(modifier = Modifier.fillMaxWidth(0.8f)) {
        // AURA bar
        ContributionBar(
            label = "AURA",
            percentage = auraPct,
            color = Color(0xFFFF00FF),
            count = aura.toInt()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // KAI bar
        ContributionBar(
            label = "KAI",
            percentage = kaiPct,
            color = Color(0xFF00E5FF),
            count = kai.toInt()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // MATTHEW bar
        ContributionBar(
            label = "MATTHEW",
            percentage = matthewPct,
            color = Color(0xFFFFD93D),
            count = matthew.toInt()
        )
    }
}

@Composable
private fun ContributionBar(
    label: String,
    percentage: Float,
    color: Color,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.width(60.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Bar background
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            // Filled portion
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        ),
                        shape = MaterialTheme.shapes.small
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

// ═════════════════════════════════════════════════════════════════════
// CANVAS HELPERS
// ═════════════════════════════════════════════════════════════════════

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawInfinitySymbol(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path().apply {
        // Left loop
        moveTo(center.x, center.y)
        cubicTo(
            center.x - radius, center.y - radius,
            center.x - radius, center.y + radius,
            center.x, center.y
        )

        // Right loop
        cubicTo(
            center.x + radius, center.y - radius,
            center.x + radius, center.y + radius,
            center.x, center.y
        )
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )
}

// ═════════════════════════════════════════════════════════════════════
// UTILITY FUNCTIONS
// ═════════════════════════════════════════════════════════════════════

private fun getSuccessRateColor(rate: Float): Color {
    return when {
        rate > 90f -> Color(0xFF00E5FF) // Cyan
        rate > 75f -> Color(0xFF39FF14) // Green
        rate > 60f -> Color(0xFFFFD93D) // Yellow
        else -> Color(0xFFFF00FF) // Magenta
    }
}

// ═════════════════════════════════════════════════════════════════════
// CONTRIBUTION TRACKER (Singleton)
// ═════════════════════════════════════════════════════════════════════

object ContributionTracker {
    private var auraTransmutations = 0
    private var kaiTransmutations = 0
    private var matthewTransmutations = 0

    fun recordAuraContribution() {
        auraTransmutations++
    }

    fun recordKaiContribution() {
        kaiTransmutations++
    }

    fun recordMatthewContribution() {
        matthewTransmutations++
    }

    fun getAuraContribution(): Float = auraTransmutations.toFloat()
    fun getKaiContribution(): Float = kaiTransmutations.toFloat()
    fun getMatthewContribution(): Float = matthewTransmutations.toFloat()

    fun getTotalTransmutations(): Int =
        auraTransmutations + kaiTransmutations + matthewTransmutations

    fun reset() {
        auraTransmutations = 0
        kaiTransmutations = 0
        matthewTransmutations = 0
    }

    // Get distribution for visualization
    fun getDistribution(): Triple<Float, Float, Float> {
        val total = getTotalTransmutations().toFloat()
        return if (total > 0) {
            Triple(
                auraTransmutations / total,
                kaiTransmutations / total,
                matthewTransmutations / total
            )
        } else {
            Triple(0.33f, 0.33f, 0.34f)
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// FOOTER VARIANT (Compact for panels)
// ═════════════════════════════════════════════════════════════════════

@Composable
fun ThreadsWovenFooter(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val successRate = RealitymorphismEngine.computeAtomicSuccessRate()
    val total = ContributionTracker.getTotalTransmutations()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trinity indicators
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mini orbs
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFFF00FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF00E5FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFFFD93D), CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "A+K+M=∞",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        // Success rate
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${successRate.toInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = getSuccessRateColor(successRate)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "| $total",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

// Extension for TextUnit
private val Float.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.TextUnit(this, androidx.compose.ui.unit.TextUnitType.Sp)
