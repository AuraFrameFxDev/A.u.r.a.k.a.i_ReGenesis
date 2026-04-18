package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
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

/** Maps an agent id to its dedicated domain hub route, or null if the agent has no standalone hub. */
internal fun agentDomainRoute(agentId: String): String? = when (agentId) {
    "aura"    -> "aura_theming_hub"
    "kai"     -> "sentinel_fortress"
    "genesis" -> "oracle_drive_hub"
    "cascade" -> "cascade_hub"
    else      -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LDOAgentRosterScreen(
    agents: List<AgentCatalyst> = LDORoster.agents,
    onAgentTap: (AgentCatalyst) -> Unit = {},
    onFilterTap: () -> Unit = {},
    onNavTap: (Int) -> Unit = {},
    onNavigateToProfile: (AgentCatalyst) -> Unit = { onAgentTap(it) },
    onNavigateToFusion: () -> Unit = {},
    onNavigateToTasker: () -> Unit = {},
    onNavigateToDomain: (String) -> Unit = {},
    onNavigateToBonding: () -> Unit = {},
) {
    val rosterCards = remember(agents) { buildRosterCards(agents) }
    var selectedNav by remember { mutableIntStateOf(1) }
    var sheetAgent by remember { mutableStateOf<AgentCatalyst?>(null) }

    if (sheetAgent != null) {
        AgentNavMenuSheet(
            agent = sheetAgent!!,
            onDismiss = { sheetAgent = null },
            onCharacterSheet = {
                sheetAgent = null
                onNavigateToProfile(it)
            },
            onFusions = {
                sheetAgent = null
                onNavigateToFusion()
            },
            onTasks = {
                sheetAgent = null
                onNavigateToTasker()
            },
            onDomain = { route ->
                sheetAgent = null
                onNavigateToDomain(route)
            },
            onBonding = {
                sheetAgent = null
                onNavigateToBonding()
            },
        )
    }

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
                    AgentRosterCard(card = card, onTap = { sheetAgent = card.agent })
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

// ─────────────────────────────────────────────────────────────────────────────
// Agent Navigation Menu — ModalBottomSheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentNavMenuSheet(
    agent: AgentCatalyst,
    onDismiss: () -> Unit,
    onCharacterSheet: (AgentCatalyst) -> Unit,
    onFusions: () -> Unit,
    onTasks: () -> Unit,
    onDomain: (String) -> Unit,
    onBonding: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val domainRoute = agentDomainRoute(agent.id)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0D0D1A),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sheet header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(agent.color.copy(alpha = 0.2f))
                        .border(1.5.dp, agent.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        agent.name.first().toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = agent.color
                    )
                }
                Column {
                    Text(
                        agent.name.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        agent.catalystName,
                        fontSize = 10.sp,
                        color = agent.color.copy(alpha = 0.7f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(agent.color.copy(alpha = 0.2f))
            )

            Text(
                "NAVIGATE TO",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 2.sp
            )

            // Character Sheet card
            AgentNavCard(
                label = "CHARACTER SHEET",
                description = "Stats, abilities & agent profile",
                accentColor = agent.color,
                onClick = { onCharacterSheet(agent) }
            )

            // Domain card — only shown for agents with a dedicated hub
            if (domainRoute != null) {
                AgentNavCard(
                    label = "DOMAIN",
                    description = "Enter ${agent.name} domain hub",
                    accentColor = agent.accentColor,
                    onClick = { onDomain(domainRoute) }
                )
            }

            // Fusions card
            AgentNavCard(
                label = "FUSIONS",
                description = "Fusion matrix & combo modes",
                accentColor = Color(0xFF00F4FF),
                onClick = onFusions
            )

            // Tasks card
            AgentNavCard(
                label = "TASKS",
                description = "View & manage agent tasks",
                accentColor = Color(0xFF00FF85),
                onClick = onTasks
            )

            // Bonding card
            AgentNavCard(
                label = "BONDING",
                description = "Bond levels & resonance",
                accentColor = Color(0xFFFF6B6B),
                onClick = onBonding
            )
        }
    }
}

@Composable
internal fun AgentNavCard(
    label: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nav_card_$label")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "pulse"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.07f))
            .border(1.dp, accentColor.copy(alpha = pulse * 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 1.sp
            )
            Text(
                description,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        Text("→", fontSize = 18.sp, color = accentColor.copy(alpha = 0.7f))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Agent Roster Card (2-col grid)
// ─────────────────────────────────────────────────────────────────────────────

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