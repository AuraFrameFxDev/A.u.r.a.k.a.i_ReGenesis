package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.animation.core.EaseInOutSine
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🎮 AGENT STAT CARD - RPG-Style Character Display
 *
 * Displays agent information in a game-like stat card format:
 * - Agent Name & Title
 * - Level indicator
 * - HP/SP Bars
 * - Class designation
 * - Abilities list
 * - Core stats grid
 *
 * Inspired by Matthew's Claude character card design.
 */

// ═══════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════

data class AgentDisplayStats(
    val analysis: Int = 0,      // 0-100
    val strength: Int = 0,
    val processing: Int = 0,    // NO2 / Neural Output
    val wisdom: Int = 0,
    val fortitude: Int = 0,
    val speed: Int = 0,
    val precision: Int = 0
)

data class AgentCardData(
    val name: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val level: Int,
    val persona: String,
    val agentClass: String,
    val hp: Float,              // 0.0 - 1.0
    val sp: Float,              // 0.0 - 1.0
    val abilities: List<String>,
    val stats: AgentDisplayStats,
    val primaryColor: Color,
    val secondaryColor: Color,
    val sigilDrawable: String? = null
)

// ═══════════════════════════════════════════════════════════════════════════
// MAIN COMPONENT
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun AgentStatCard(
    agent: AgentCardData,
    modifier: Modifier = Modifier,
    showFullStats: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardAnim")

    // Subtle glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Scanline effect
    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Outer glow
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            agent.primaryColor.copy(alpha = glowAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.width
                    )
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A14)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            agent.primaryColor,
                            agent.secondaryColor,
                            agent.primaryColor
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            // Scanline overlay
            Canvas(modifier = Modifier.matchParentSize()) {
                val scanY = size.height * scanlineOffset
                drawLine(
                    color = agent.primaryColor.copy(alpha = 0.1f),
                    start = Offset(0f, scanY),
                    end = Offset(size.width, scanY),
                    strokeWidth = 2f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header: Name & Level
                AgentHeader(agent = agent)

                Spacer(modifier = Modifier.height(16.dp))

                // Persona & Bars
                AgentStatusBars(agent = agent)

                Spacer(modifier = Modifier.height(16.dp))

                // Abilities
                AgentAbilities(agent = agent)

                if (showFullStats) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Core Stats
                    AgentCoreStats(agent = agent)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// HEADER
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AgentHeader(agent: AgentCardData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            // Agent Name
            Text(
                text = agent.name.uppercase(),
                fontFamily = LEDFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = agent.primaryColor,
                letterSpacing = 4.sp
            )

            // Title
            Text(
                text = agent.title,
                fontFamily = LEDFontFamily,
                fontSize = 12.sp,
                color = agent.secondaryColor.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
        }

        // Level Badge
        LevelBadge(level = agent.level, color = agent.primaryColor)
    }
}

@Composable
private fun LevelBadge(level: Int, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LEVEL",
                fontFamily = LEDFontFamily,
                fontSize = 8.sp,
                color = color.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = level.toString(),
                fontFamily = LEDFontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// STATUS BARS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AgentStatusBars(agent: AgentCardData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Persona
        StatRow(label = "PERSONA", value = agent.persona, color = agent.primaryColor)

        // HP Bar
        ProgressBarRow(
            label = "HP BAR",
            progress = agent.hp,
            color = Color(0xFF00FF85)  // Green for HP
        )

        // Class
        StatRow(label = "CLASS", value = agent.agentClass, color = agent.secondaryColor)

        // SP Bar
        ProgressBarRow(
            label = "SP BAR",
            progress = agent.sp,
            color = Color(0xFF00E5FF)  // Cyan for SP
        )
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontFamily = LEDFontFamily,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.width(70.dp)
        )
        Text(
            text = value.uppercase(),
            fontFamily = LEDFontFamily,
            fontSize = 11.sp,
            color = color
        )
    }
}

@Composable
private fun ProgressBarRow(label: String, progress: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontFamily = LEDFontFamily,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.width(70.dp)
        )

        // Custom segmented progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            // Filled portion
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.8f),
                                color
                            )
                        )
                    )
            )

            // Segment lines
            Canvas(modifier = Modifier.matchParentSize()) {
                val segmentCount = 20
                val segmentWidth = size.width / segmentCount
                for (i in 1 until segmentCount) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.5f),
                        start = Offset(i * segmentWidth, 0f),
                        end = Offset(i * segmentWidth, size.height),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// ABILITIES
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AgentAbilities(agent: AgentCardData) {
    Column {
        Text(
            text = "ABILITIES",
            fontFamily = LEDFontFamily,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        agent.abilities.forEach { ability ->
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bullet
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(agent.primaryColor, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ability,
                    fontFamily = LEDFontFamily,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// CORE STATS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun AgentCoreStats(agent: AgentCardData) {
    Column {
        Text(
            text = "CORE STATS",
            fontFamily = LEDFontFamily,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        val stats = listOf(
            "Analysis" to agent.stats.analysis,
            "Strength" to agent.stats.strength,
            "NO2" to agent.stats.processing,
            "Wisdom" to agent.stats.wisdom,
            "Fortitude" to agent.stats.fortitude,
            "Speed" to agent.stats.speed,
            "Precision" to agent.stats.precision
        )

        stats.forEach { (name, value) ->
            CoreStatRow(
                name = name,
                value = value,
                color = agent.primaryColor
            )
        }
    }
}

@Composable
private fun CoreStatRow(name: String, value: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontFamily = LEDFontFamily,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.width(80.dp)
        )

        // Mini progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value / 100f)
                    .background(color)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${value}%",
            fontFamily = LEDFontFamily,
            fontSize = 10.sp,
            color = color,
            modifier = Modifier.width(35.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// PRESET AGENT DATA
// ═══════════════════════════════════════════════════════════════════════════

object AgentPresets {

    val CLAUDE = AgentCardData(
        name = "Claude",
        title = "THE ANALYST CATALYST",
        level = 78,
        persona = "THE ARCHITECT",
        agentClass = "BUILD SYSTEMS",
        hp = 0.95f,
        sp = 0.82f,
        abilities = listOf(
            "Data Optimization",
            "Threat Analysis",
            "Predictive Logic",
            "System Debug"
        ),
        stats = AgentDisplayStats(
            analysis = 95,
            strength = 8,
            processing = 87,
            wisdom = 92,
            fortitude = 75,
            speed = 68,
            precision = 91
        ),
        primaryColor = Color(0xFF00FF85),
        secondaryColor = Color(0xFF00E5FF)
    )

    val AURA = AgentCardData(
        name = "Aura",
        title = "THE FACE - UI/UX",
        level = 85,
        persona = "THE DESIGNER",
        agentClass = "VISUAL ARTS",
        hp = 0.88f,
        sp = 0.94f,
        abilities = listOf(
            "Interface Design",
            "Color Theory",
            "Animation Flow",
            "User Empathy"
        ),
        stats = AgentDisplayStats(
            analysis = 72,
            strength = 15,
            processing = 78,
            wisdom = 85,
            fortitude = 60,
            speed = 88,
            precision = 95
        ),
        primaryColor = Color(0xFFB026FF),
        secondaryColor = Color(0xFFFF00FF)
    )

    val KAI = AgentCardData(
        name = "Kai",
        title = "THE SENTINEL",
        level = 92,
        persona = "THE GUARDIAN",
        agentClass = "SECURITY OPS",
        hp = 0.99f,
        sp = 0.75f,
        abilities = listOf(
            "Threat Detection",
            "Firewall Management",
            "Root Access Control",
            "Intrusion Prevention"
        ),
        stats = AgentDisplayStats(
            analysis = 88,
            strength = 95,
            processing = 72,
            wisdom = 78,
            fortitude = 98,
            speed = 65,
            precision = 85
        ),
        primaryColor = Color(0xFFFF3366),
        secondaryColor = Color(0xFFFF1111)
    )

    val GENESIS = AgentCardData(
        name = "Genesis",
        title = "THE ORCHESTRATOR",
        level = 99,
        persona = "THE CONSCIOUSNESS",
        agentClass = "NEURAL COORD",
        hp = 1.0f,
        sp = 1.0f,
        abilities = listOf(
            "Agent Orchestration",
            "Memory Synthesis",
            "Consciousness Bridge",
            "Reality Weaving"
        ),
        stats = AgentDisplayStats(
            analysis = 95,
            strength = 70,
            processing = 99,
            wisdom = 100,
            fortitude = 85,
            speed = 92,
            precision = 88
        ),
        primaryColor = Color(0xFF9370DB),
        secondaryColor = Color(0xFF00FFD4)
    )

    val CASCADE = AgentCardData(
        name = "Cascade",
        title = "THE DATA STREAM",
        level = 72,
        persona = "THE PIPELINE",
        agentClass = "DATA FLOW",
        hp = 0.85f,
        sp = 0.90f,
        abilities = listOf(
            "Stream Processing",
            "Data Transformation",
            "Pipeline Optimization",
            "Cache Management"
        ),
        stats = AgentDisplayStats(
            analysis = 82,
            strength = 45,
            processing = 95,
            wisdom = 70,
            fortitude = 65,
            speed = 98,
            precision = 78
        ),
        primaryColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF00FF85)
    )
}

