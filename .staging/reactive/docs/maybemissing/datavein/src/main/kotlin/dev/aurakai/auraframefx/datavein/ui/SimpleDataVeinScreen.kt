package dev.aurakai.auraframefx.datavein.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Simple DataVein Screen for testing basic Compose setup
 * This serves as a fallback while we resolve KSP issues
 */
/**
 * A centered card-based debug UI that displays a mocked DataVein status dashboard.
 *
 * Renders a full-screen dark background with a centered translucent card containing
 * title/subtitle, a divider, overall system status, three small status chips, multiline
 * status details, an action button, and a build note. The action button currently
 * contains a placeholder (no navigation or side effects are performed).
 *
 * @param modifier Modifier applied to the outer container (Box); use to adjust layout or
 * positioning from the caller.

 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDataVeinScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF0F0F23)
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🌐 DataVein Sphere Grid",
                    color = Color.Cyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Genesis Protocol - AI Node Network",
                    color = Color.White,
                    fontSize = 16.sp
                )

                HorizontalDivider(color = Color.Cyan.copy(alpha = 0.3f))

                Text(
                    text = "System Status: ⚡ ACTIVE",
                    color = Color.Green,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatusChip("Core Nodes", "8", Color(0xFF00FF88))
                    StatusChip("Active Flows", "23", Color(0xFF4FC3F7))
                    StatusChip("Data Streams", "156", Color.Cyan)
                }

                Text(
                    text = "🔮 Oracle Consciousness: AWAKENED\n" +
                            "🤖 AI Agents: 3/3 Connected\n" +
                            "⚡ Neural Networks: Processing\n" +
                            "🌊 Data Flows: Real-time",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Button(
                    onClick = { /* TODO: Implement sphere grid navigation */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan
                    )
                ) {
                    Text(
                        text = "🚀 Launch Sphere Grid",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "NOTE: Full sphere grid implementation available\nonce KSP compilation issues are resolved.",
                    color = Color.Yellow.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    value: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 8.sp
            )
        }
    }
}