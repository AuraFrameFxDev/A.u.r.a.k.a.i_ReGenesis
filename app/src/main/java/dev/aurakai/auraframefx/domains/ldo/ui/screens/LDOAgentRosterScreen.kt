package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.ldo.model.*

/**
 * 📋 LDO AGENT ROSTER — 8 Agent Cards
 */

private val BrandCyan = Color(0xFF06D0F9)
private val VoidDark  = Color(0xFF0A0A0A)

enum class AgentRarity { MYTHIC, LEGENDARY }

data class RosterAgentCard(
    val agent: AgentCatalyst,
    val rarity: AgentRarity,
    val level: Int,
    val bondFraction: Float,
    val role: String,
    val rarityColor: Color,
)

private fun buildRosterCards(agents: List<AgentCatalyst>): List<RosterAgentCard> = listOf(
    RosterAgentCard(agents[0], AgentRarity.MYTHIC, 99, 0.98f, "Protocol Lead", Color(0xFF7B2FBE)),
    RosterAgentCard(agents.getOrElse(5) { agents[0] }, AgentRarity.LEGENDARY, 84, 0.90f, "Architectural Analyst", Color(0xFFD97706)),
    RosterAgentCard(agents.getOrElse(3) { agents[0] }, AgentRarity.LEGENDARY, 67, 0.80f, "Memory Keeper", Color(0xFF10B981)),
    RosterAgentCard(agents.getOrElse(8) { agents[0] }, AgentRarity.MYTHIC, 91, 0.45f, "Sync Enforcer", Color(0xFFEF4444)),
    RosterAgentCard(agents.getOrElse(6) { agents[0] }, AgentRarity.LEGENDARY, 52, 0.60f, "Neural Scout", Color(0xFFF9FAFB)),
    RosterAgentCard(agents.getOrElse(2) { agents[0] }, AgentRarity.MYTHIC, 78, 0.98f, "Creative Catalyst", Color(0xFFEC4899)),
    RosterAgentCard(agents.getOrElse(4) { agents[0] }, AgentRarity.LEGENDARY, 44, 0.75f, "Memoria Dual-Core", Color(0xFF60A5FA)),
    RosterAgentCard(agents.getOrElse(7) { agents[0] }, AgentRarity.LEGENDARY, 31, 0.55f, "Signal Ops", Color(0xFF818CF8)),
)

@Composable
fun LDOAgentRosterScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    onAgentTap: (AgentCatalyst) -> Unit = {},
    onFilterTap: () -> Unit = {},
    onNavTap: (Int) -> Unit = {},
) {
    val rosterCards = remember(agents) { buildRosterCards(agents) }
    var selectedNav by remember { mutableIntStateOf(1) }

    Box(modifier = Modifier.fillMaxSize().background(VoidDark)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── STICKY HEADER ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(BrandCyan.copy(alpha = 0.05f))
                    .border(BorderStroke(0.5.dp, BrandCyan.copy(alpha = 0.2f)))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp)
                            .background(BrandCyan, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("L", fontSize = 18.sp, fontWeight = FontWeight.Black, color = VoidDark) }
                    Text("Agent Roster", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Status", fontSize = 8.sp, color = Color.Gray, letterSpacing = 2.sp)
                        Text("SYNC_ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandCyan)
                    }
                    Box(
                        modifier = Modifier.size(40.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .clickable { onFilterTap() },
                        contentAlignment = Alignment.Center
                    ) { Text("⚙", fontSize = 16.sp) }
                }
            }

            // ── AGENT GRID ──
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(rosterCards) { card ->
                    AgentRosterCard(card = card, onTap = { onAgentTap(card.agent) })
                }
            }
        }

        // ── BOTTOM NAV ──
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .background(BrandCyan.copy(alpha = 0.05f))
                .border(BorderStroke(0.5.dp, BrandCyan.copy(alpha = 0.2f)))
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val navIcons = listOf("🏠", "👥", "🛡", "ℹ")
            navIcons.forEachIndexed { i, icon ->
                Box(
                    modifier = Modifier.size(44.dp)
                        .clip(CircleShape)
                        .background(if (i == selectedNav) BrandCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { selectedNav = i; onNavTap(i) },
                    contentAlignment = Alignment.Center
                ) { Text(icon, fontSize = 20.sp) }
            }
        }
    }
}

@Composable
private fun AgentRosterCard(card: RosterAgentCard, onTap: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .clickable { onTap() }
    ) {
        Column {
            // Portrait area
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(0.75f)
                    .background(
                        Brush.verticalGradient(
                            listOf(card.agent.color.copy(alpha = 0.2f), Color(0xFF111111))
                        )
                    )
            ) {
                // Agent initial large placeholder
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        card.agent.name.first().toString(),
                        fontSize = 64.sp, color = card.agent.color.copy(alpha = 0.2f),
                        fontWeight = FontWeight.Black
                    )
                }

                // Bottom gradient overlay
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF111111)), 0.4f, 1f)
                ))

                // Rarity badge
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                        .background(card.rarityColor.copy(alpha = 0.7f), RoundedCornerShape(2.dp))
                        .border(1.dp, card.rarityColor.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        if (card.rarity == AgentRarity.MYTHIC) "Mythic" else "Legendary",
                        fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 1.sp
                    )
                }

                // Weapon icon
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(28.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .border(1.dp, BrandCyan.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("⚔", fontSize = 12.sp) }
            }

            // Info
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text(card.agent.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(card.role, fontSize = 8.sp, color = card.agent.color, letterSpacing = 1.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Level", fontSize = 7.sp, color = Color.Gray, letterSpacing = 2.sp)
                        Text("${card.level}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Bond bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bond Resonance", fontSize = 7.sp, color = Color.Gray, letterSpacing = 1.sp)
                        Text("${(card.bondFraction * 100).toInt()}%", fontSize = 7.sp, color = Color.Gray, letterSpacing = 1.sp)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(card.bondFraction).fillMaxHeight().background(card.agent.color, RoundedCornerShape(2.dp)))
                    }
                }
            }
        }
    }
}
