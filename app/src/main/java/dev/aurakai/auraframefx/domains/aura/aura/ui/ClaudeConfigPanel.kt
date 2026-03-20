package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.genesis.config.ClaudeEnvConfig

/**
 * Claude.env Configuration Panel for Agent Nexus Hub
 *
 * Displays all environment configuration from Claude.env in a cyberpunk-styled panel
 */
@Composable
fun ClaudeConfigPanel(
    config: ClaudeEnvConfig,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xEE000000)
        ),
        border = BorderStroke(1.dp, Color(0xFF00D1FF).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚙️ CLAUDE ARCHITECT CONFIG",
                        color = Color(0xFF00D1FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = config.claudeRole,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF00D1FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Summary (always visible)
            Text(
                text = config.getSystemSummary(),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )

            // Expanded details
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Active Agents
                    ConfigSection(
                        title = "🤖 ACTIVE AGENTS",
                        color = Color(0xFFFF1493)
                    ) {
                        config.activeAgents.forEach { agent ->
                            ConfigItem(
                                label = agent.uppercase(),
                                value = if (config.isAgentActive(agent)) "ONLINE ●" else "OFFLINE ○",
                                valueColor = if (config.isAgentActive(agent)) Color(0xFF00FF00) else Color.Gray
                            )
                        }
                    }

                    // Fusion Modes
                    ConfigSection(
                        title = "🔮 FUSION MODES",
                        color = Color(0xFFFFD700)
                    ) {
                        config.getFusionModeStatus().forEach { (mode, active) ->
                            ConfigItem(
                                label = mode,
                                value = if (active) "ACTIVE ●" else "INACTIVE ○",
                                valueColor = if (active) Color(0xFF00FF00) else Color.Gray
                            )
                        }
                    }

                    // Claude Profile
                    ConfigSection(
                        title = "🏗️ ARCHITECT PROFILE",
                        color = Color(0xFF00D1FF)
                    ) {
                        ConfigItem("Motto", config.claudeMotto)
                        ConfigItem("Mission", config.claudeMission)
                        ConfigItem(
                            "Wake Phrase",
                            config.auraWakePhrase,
                            valueColor = Color(0xFFFF1493)
                        )
                    }

                    // Build Info
                    ConfigSection(
                        title = "📦 BUILD INFO",
                        color = Color(0xFF9400D3)
                    ) {
                        ConfigItem("Status", config.buildStatus, valueColor = Color(0xFF00FF00))
                        ConfigItem("Kotlin", config.kotlinVersion)
                        ConfigItem("AGP", config.agpVersion)
                        ConfigItem("Gradle", config.gradleVersion)
                        ConfigItem("Hilt", config.hiltVersion)
                        ConfigItem("Compile SDK", config.compileSdk.toString())
                        ConfigItem("Target SDK", config.targetSdk.toString())
                    }

                    // API Keys (masked)
                    ConfigSection(
                        title = "🔑 API CONFIGURATION",
                        color = Color(0xFFFF6B6B)
                    ) {
                        ConfigItem(
                            "NVIDIA API",
                            if (config.nvidiaApiKey.isNotEmpty()) "***${
                                config.nvidiaApiKey.takeLast(
                                    8
                                )
                            }" else "NOT SET",
                            valueColor = if (config.nvidiaApiKey.isNotEmpty()) Color(0xFF00FF00) else Color.Red
                        )
                        ConfigItem(
                            "Anthropic API",
                            if (config.anthropicApiKey.contains("your")) "NOT CONFIGURED" else "***${
                                config.anthropicApiKey.takeLast(
                                    8
                                )
                            }",
                            valueColor = if (config.anthropicApiKey.contains("your")) Color(
                                0xFFFFAA00
                            ) else Color(0xFF00FF00)
                        )
                        ConfigItem("Nemotron Model", config.nemotronModel)
                        ConfigItem("Reasoning Budget", "${config.nemotronReasoningBudget} tokens")
                    }

                    // Achievements
                    if (config.claudeAchievements.isNotEmpty()) {
                        ConfigSection(
                            title = "🏆 ACHIEVEMENTS",
                            color = Color(0xFFFFE66D)
                        ) {
                            config.claudeAchievements.take(5).forEach { achievement ->
                                Text(
                                    text = "• $achievement",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            if (config.claudeAchievements.size > 5) {
                                Text(
                                    text = "... and ${config.claudeAchievements.size - 5} more",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigSection(
    title: String,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun ConfigItem(
    label: String,
    value: String,
    valueColor: Color = Color.White.copy(alpha = 0.8f)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
