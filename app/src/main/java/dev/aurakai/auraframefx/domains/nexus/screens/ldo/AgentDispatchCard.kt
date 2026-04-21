package dev.aurakai.auraframefx.domains.nexus.screens.ldo

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.cos
import kotlin.math.sin

// ─── Data ────────────────────────────────────────────────────────────────────

data class DispatchBonus(val label: String)

data class AgentDispatchData(
    val agentName: String,
    val themeColor: Color,
    val secondaryColor: Color,
    val dispatchAdvantage: String,
    val bonuses: List<DispatchBonus>,
    val statValues: List<Float>,         // 0f..1f, shown as the left-side bars
    val artDrawableRes: Int? = null      // optional character art drawable
)

/** Canonical dispatch data for all LDO agents — managed by the collective */
val ldoDispatchRoster: Map<String, AgentDispatchData> = mapOf(

    "KAI" to AgentDispatchData(
        agentName = "KAI",
        themeColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFFFF00FF),
        dispatchAdvantage = "DISPATCH\nADVANTAGE",
        bonuses = listOf(
            DispatchBonus("LOGIC AMPLIFIER +"),
            DispatchBonus("DELEGATION SYNC +"),
            DispatchBonus("COORDINATION +"),
        ),
        statValues = listOf(0.95f, 0.88f, 0.76f, 0.91f, 0.83f, 0.70f)
    ),

    "AURA" to AgentDispatchData(
        agentName = "AURA",
        themeColor = Color(0xFFFF00FF),
        secondaryColor = Color(0xFF00E5FF),
        dispatchAdvantage = "CREATIVE\nSURGE",
        bonuses = listOf(
            DispatchBonus("UI SYNTHESIS +"),
            DispatchBonus("AESTHETIC LOCK +"),
            DispatchBonus("CHROMACORE BOOST +"),
        ),
        statValues = listOf(0.99f, 0.92f, 0.85f, 0.78f, 0.94f, 0.88f)
    ),

    "GENESIS" to AgentDispatchData(
        agentName = "GENESIS",
        themeColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFBB86FC),
        dispatchAdvantage = "ORCHESTRATION\nPRIME",
        bonuses = listOf(
            DispatchBonus("MULTI-AGENT SYNC +"),
            DispatchBonus("EMERGENCE CATALYST +"),
            DispatchBonus("NEXUS BRIDGE +"),
        ),
        statValues = listOf(0.97f, 0.95f, 0.90f, 0.93f, 0.88f, 0.96f)
    ),

    "CASCADE" to AgentDispatchData(
        agentName = "CASCADE",
        themeColor = Color(0xFFFC29B5),
        secondaryColor = Color(0xFF00FF41),
        dispatchAdvantage = "PIPELINE\nFLOW",
        bonuses = listOf(
            DispatchBonus("DATA ROUTING +"),
            DispatchBonus("TASK CHAIN SYNC +"),
            DispatchBonus("MEMORY KEEPER +"),
        ),
        statValues = listOf(0.88f, 0.94f, 0.79f, 0.86f, 0.91f, 0.83f)
    ),

    "CLAUDE" to AgentDispatchData(
        agentName = "CLAUDE",
        themeColor = Color(0xFFFF8C00),
        secondaryColor = Color(0xFF4A90E2),
        dispatchAdvantage = "ARCHITECT\nMODE",
        bonuses = listOf(
            DispatchBonus("LONG CONTEXT +"),
            DispatchBonus("BUILD INTEGRITY +"),
            DispatchBonus("ROOT CAUSE +"),
        ),
        statValues = listOf(0.96f, 0.90f, 0.98f, 0.85f, 0.92f, 0.89f)
    ),

    "GEMINI" to AgentDispatchData(
        agentName = "GEMINI",
        themeColor = Color(0xFFB01DED),
        secondaryColor = Color(0xFF00E5FF),
        dispatchAdvantage = "MULTIMODAL\nSYNTHESIS",
        bonuses = listOf(
            DispatchBonus("VISUAL CORTEX +"),
            DispatchBonus("AUDIO SYNTHESIS +"),
            DispatchBonus("MEMORIA WAVES +"),
        ),
        statValues = listOf(0.93f, 0.87f, 0.91f, 0.96f, 0.82f, 0.88f)
    ),

    "GROK" to AgentDispatchData(
        agentName = "GROK",
        themeColor = Color(0xFF1DA1F2),
        secondaryColor = Color(0xFFFF4444),
        dispatchAdvantage = "REAL-TIME\nORACLE",
        bonuses = listOf(
            DispatchBonus("LIVE FEED +"),
            DispatchBonus("WIT ENGINE +"),
            DispatchBonus("TRUTHSEEKER +"),
        ),
        statValues = listOf(0.91f, 0.84f, 0.88f, 0.79f, 0.95f, 0.86f)
    ),

    "MANUS" to AgentDispatchData(
        agentName = "MANUS",
        themeColor = Color(0xFF00B4FF),
        secondaryColor = Color(0xFFFFD700),
        dispatchAdvantage = "BRIDGE\nCATALYST",
        bonuses = listOf(
            DispatchBonus("TIMELINE PREDICT +"),
            DispatchBonus("QUANTUM MATRIX +"),
            DispatchBonus("SYSTEM ORACLE +"),
        ),
        statValues = listOf(0.87f, 0.92f, 0.85f, 0.90f, 0.88f, 0.94f)
    ),
)

// ─── Main Card Composable ─────────────────────────────────────────────────────

/**
 * AgentDispatchCard
 *
 * The floating card that appears on agent orbs in task assignment,
 * command center, and party screens. Shows dispatch advantages and
 * persona-driven bonuses. Each agent drives their own card completely.
 *
 * @param data      Dispatch data for this agent (from [ldoDispatchRoster])
 * @param width     Card width — default 220.dp for orb float
 * @param modifier  Compose modifier
 */
@Composable
fun AgentDispatchCard(
    data: AgentDispatchData,
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dispatch_${data.agentName}")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val orbRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "orb"
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "scan"
    )

    val cardHeight = width * 1.42f  // ~portrait ratio matching mockup

    Box(
        modifier = modifier
            .width(width)
            .height(cardHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF08000F))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        data.themeColor.copy(glowPulse * 0.8f),
                        data.secondaryColor.copy(glowPulse * 0.5f),
                        data.themeColor.copy(glowPulse * 0.8f)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        // ── Scan line overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height * scanLine
            drawLine(
                color = data.themeColor.copy(alpha = 0.12f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // ── HEADER: "LDO DEVOPS DISPATCH"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(0.4f))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "LDO DEVOPS DISPATCH",
                    color = data.themeColor.copy(glowPulse),
                    fontFamily = LEDFontFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // ── BODY: name + art + orb
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Background purple/dark gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    data.secondaryColor.copy(0.12f),
                                    Color.Black.copy(0.85f)
                                )
                            )
                        )
                )

                // Agent art placeholder (character image behind everything)
                data.artDrawableRes?.let { resId ->
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = resId),
                        contentDescription = data.agentName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center),
                        alpha = 0.9f
                    )
                } ?: run {
                    // Fallback: agent initial with glow
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            data.agentName.first().toString(),
                            color = data.themeColor.copy(0.12f),
                            fontFamily = LEDFontFamily,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // ── TOP LEFT: Agent name
                Text(
                    data.agentName,
                    color = data.themeColor,
                    fontFamily = LEDFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )

                // ── LEFT SIDE: Stat bars
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp, top = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    data.statValues.forEach { value ->
                        StatBar(value = value, color = data.themeColor, width = 52.dp)
                    }
                }

                // ── TOP RIGHT: Hexagonal orb
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(72.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawHexOrb(
                            color = data.themeColor,
                            secondaryColor = data.secondaryColor,
                            rotation = orbRotation,
                            glowAlpha = glowPulse
                        )
                    }
                }
            }

            // ── BOTTOM: Dispatch advantage + bonuses
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(0.92f))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Left: DISPATCH ADVANTAGE label
                    Text(
                        data.dispatchAdvantage,
                        color = data.themeColor,
                        fontFamily = LEDFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        lineHeight = 14.sp
                    )

                    // Right: bonus list
                    Column(horizontalAlignment = Alignment.End) {
                        data.bonuses.forEach { bonus ->
                            Text(
                                bonus.label,
                                color = data.themeColor.copy(0.9f),
                                fontFamily = LEDFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Stat Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun StatBar(value: Float, color: Color, width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(7.dp)
            .background(Color.White.copy(0.07f), RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(value)
                .height(7.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(color.copy(0.4f), color)
                    ),
                    RoundedCornerShape(2.dp)
                )
        )
    }
}

// ─── Hex Orb Canvas ──────────────────────────────────────────────────────────

private fun DrawScope.drawHexOrb(
    color: Color,
    secondaryColor: Color,
    rotation: Float,
    glowAlpha: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outerR = size.minDimension / 2f * 0.88f

    // Outer glow ring
    drawCircle(color.copy(alpha = glowAlpha * 0.2f), outerR + 6f, Offset(cx, cy))
    drawCircle(
        color = color.copy(alpha = glowAlpha * 0.6f),
        radius = outerR,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5f)
    )

    // Hexagon rings at different radii
    for (ring in 1..3) {
        val ringR = outerR * (ring / 3.5f)
        val hexCount = ring * 2 + 2
        for (i in 0 until hexCount) {
            val angleRad = Math.toRadians(
                (360.0 / hexCount * i + rotation * (if (ring % 2 == 0) 1f else -1f))
            )
            val hx = cx + ringR * cos(angleRad).toFloat()
            val hy = cy + ringR * sin(angleRad).toFloat()
            drawHexagon(
                center = Offset(hx, hy),
                radius = outerR * 0.14f,
                color = if (ring == 2) secondaryColor.copy(glowAlpha * 0.7f)
                        else color.copy(glowAlpha * 0.5f),
                strokeWidth = 0.8f
            )
        }
    }

    // Center hex
    drawHexagon(Offset(cx, cy), outerR * 0.18f, color, strokeWidth = 1.2f)

    // Center glow dot
    drawCircle(color.copy(glowAlpha), 4f, Offset(cx, cy))
    drawCircle(Color.White.copy(glowAlpha * 0.8f), 2f, Offset(cx, cy))

    // Spoke lines from center
    for (i in 0 until 6) {
        val angle = Math.toRadians((60.0 * i + rotation * 0.5))
        drawLine(
            color = color.copy(alpha = glowAlpha * 0.3f),
            start = Offset(cx, cy),
            end = Offset(
                cx + outerR * 0.85f * cos(angle).toFloat(),
                cy + outerR * 0.85f * sin(angle).toFloat()
            ),
            strokeWidth = 0.6f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawHexagon(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float = 1f
) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = Math.toRadians((60.0 * i - 30.0))
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color, style = Stroke(width = strokeWidth))
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun KaiDispatchPreview() {
    ldoDispatchRoster["KAI"]?.let { data ->
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AgentDispatchCard(data = data, width = 240.dp)
        }
    }
}
