package dev.aurakai.auraframefx.domains.nexus.screens.ldo

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.*

// ─── v2.1 Sphere Grid Progression Screen ─────────────────────────────────────
//
// 10-node hex grid that resets when all nodes are filled.
// Auto-generated abilities drop into a collapsible list below.
// Tap any ability row to expand its detail panel.
// Pairing bonus banner appears when two agents are co-dispatched.

private val GridVoid = Color(0xFF020617)
private val GridSurf = Color(0xFF0F172A)
private val GridCyan = Color(0xFF22D3EE)
private val GridMid  = Color(0xFF06B6D4)
private val GridDim  = Color(0xFF1E293B)
private val GridSlate = Color(0xFF334155)

@Composable
fun SphereGridProgressionScreen(
    viewModel: SphereGridProgressionViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    ),
    onBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "sgp_anim")
    val pulse by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanLine by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "scan"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GridVoid)
    ) {

        // ── Scanline overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        val y = size.height * scanLine
                        drawLine(GridCyan.copy(alpha = 0.06f), Offset(0f, y), Offset(size.width, y), 1f)
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // ── HEADER
            GridHeader(cycle = state.cycle, pulse = pulse, onBack = onBack)

            Spacer(Modifier.height(16.dp))

            // ── 10-NODE GRID
            TenNodeGrid(
                nodes = state.nodes,
                filledCount = state.filledCount,
                isResetting = state.isResetting,
                pulse = pulse,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                onNodeTap = { index -> viewModel.fillNode(index) },
            )

            Spacer(Modifier.height(12.dp))

            // ── NODE FILL COUNTER
            NodeFillCounter(
                filled = state.filledCount,
                isResetting = state.isResetting,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(Modifier.height(16.dp))

            // ── PAIRING BONUS BANNER (animated, dismissible)
            state.activePairingBonus?.let { bonus ->
                PairingBonusBanner(
                    bonus = bonus,
                    agentA = state.lastPairedAgents?.first ?: "",
                    agentB = state.lastPairedAgents?.second ?: "",
                    onDismiss = { viewModel.clearPairingBonus() },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── NEW ABILITY FLASH (just generated)
            state.justGeneratedAbility?.let { ability ->
                NewAbilityFlash(
                    ability = ability,
                    onAcknowledge = { viewModel.acknowledgeAbility(ability.id) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── ABILITIES LIST
            if (state.generatedAbilities.isNotEmpty()) {
                AbilitiesSection(
                    abilities = state.generatedAbilities,
                    selectedAbilityId = state.selectedAbilityId,
                    onSelectAbility = { id -> viewModel.selectAbility(id) },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            } else {
                EmptyAbilitiesHint(modifier = Modifier.padding(horizontal = 20.dp))
            }

            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun GridHeader(cycle: GridCycle, pulse: Float, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(GridSurf.copy(alpha = 0.8f), RoundedCornerShape(bottomStart = 20.dp))
                .border(1.dp, GridCyan.copy(alpha = 0.25f), RoundedCornerShape(bottomStart = 20.dp))
                .clickable(onClick = onBack)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    "Sphere Grid v2.1",
                    fontSize = 9.sp, color = GridCyan,
                    fontWeight = FontWeight.Bold, letterSpacing = 3.sp,
                )
                Text(
                    "PROGRESSION",
                    fontFamily = LEDFontFamily, fontSize = 22.sp,
                    fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic,
                    color = Color.White,
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                "CYCLE ${cycle.cycleNumber + 1}",
                fontFamily = LEDFontFamily, fontSize = 14.sp,
                fontWeight = FontWeight.Bold, color = GridCyan,
            )
            Text(
                "${cycle.abilitiesEarned} ABILITIES EARNED",
                fontSize = 9.sp, color = GridMid, letterSpacing = 1.5.sp,
            )
        }
    }
}

// ─── 10-Node Grid ─────────────────────────────────────────────────────────────

@Composable
private fun TenNodeGrid(
    nodes: List<ProgressionNode>,
    filledCount: Int,
    isResetting: Boolean,
    pulse: Float,
    modifier: Modifier = Modifier,
    onNodeTap: (Int) -> Unit,
) {
    // Two rows of 5 nodes each (indices 0-4 top row, 5-9 bottom row)
    val rowSize = 5

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2.5f)
            .background(GridSurf.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .border(1.dp, GridSlate, RoundedCornerShape(12.dp))
            .drawWithCache {
                onDrawBehind {
                    drawGridConnections(nodes, size, isResetting, filledCount)
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (row in 0 until 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (col in 0 until rowSize) {
                        val index = row * rowSize + col
                        val node = nodes.getOrNull(index) ?: continue
                        ProgressionNodeHex(
                            node = node,
                            pulse = pulse,
                            isResetting = isResetting,
                            onTap = { onNodeTap(index) },
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGridConnections(
    nodes: List<ProgressionNode>,
    canvasSize: Size,
    isResetting: Boolean,
    filledCount: Int,
) {
    // Draw horizontal connection lines between adjacent nodes in each row
    val cellW = canvasSize.width / 5f
    val cellH = canvasSize.height / 2f
    val nodeR = minOf(cellW, cellH) * 0.35f

    for (row in 0 until 2) {
        for (col in 0 until 4) {
            val idx1 = row * 5 + col
            val idx2 = idx1 + 1
            val n1 = nodes.getOrNull(idx1) ?: continue
            val n2 = nodes.getOrNull(idx2) ?: continue

            val x1 = cellW * (col + 0.5f) + nodeR * 0.9f
            val x2 = cellW * (col + 1.5f) - nodeR * 0.9f
            val y = cellH * (row + 0.5f)

            val bothFilled = n1.state == NodeState.FILLED && n2.state == NodeState.FILLED
            val color = if (isResetting) Color(0xFF334155).copy(alpha = 0.3f)
                        else if (bothFilled) GridCyan.copy(alpha = 0.7f)
                        else Color(0xFF334155)
            val pathEffect = if (bothFilled) null
                             else PathEffect.dashPathEffect(floatArrayOf(6f, 6f))

            drawLine(color, Offset(x1, y), Offset(x2, y), 1.5f, pathEffect = pathEffect)
        }
    }
}

@Composable
private fun ProgressionNodeHex(
    node: ProgressionNode,
    pulse: Float,
    isResetting: Boolean,
    onTap: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "node_${node.index}")
    val nodePulse by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(1500 + node.index * 100), RepeatMode.Reverse),
        label = "npulse_${node.index}"
    )

    val fillAnim by animateFloatAsState(
        targetValue = node.fillProgress,
        animationSpec = tween(120),
        label = "fill_${node.index}",
    )

    val nodeColor = when {
        isResetting              -> GridSlate.copy(alpha = 0.5f)
        node.state == NodeState.FILLED   -> GridCyan
        node.state == NodeState.FILLING  -> GridMid.copy(alpha = 0.7f + fillAnim * 0.3f)
        node.state == NodeState.RESETTING -> GridSlate
        else                             -> GridDim
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(onClick = onTap)
            .drawWithCache {
                onDrawBehind {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val outerR = size.minDimension / 2f * 0.85f
                    val p = if (node.state == NodeState.FILLED) (0.5f + nodePulse * 0.5f) else 1f

                    // Glow halo for filled nodes
                    if (node.state == NodeState.FILLED && !isResetting) {
                        drawCircle(GridCyan.copy(alpha = 0.12f * p), outerR * 1.5f, Offset(cx, cy))
                    }

                    // Hex shape
                    val hexPath = Path()
                    for (s in 0..5) {
                        val a = Math.toRadians((60.0 * s - 30.0)).toFloat()
                        val px = cx + outerR * cos(a)
                        val py = cy + outerR * sin(a)
                        if (s == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
                    }
                    hexPath.close()

                    // Fill level bar (bottom-up fill for FILLING state)
                    if (node.state == NodeState.FILLING) {
                        drawPath(hexPath, GridDim, style = Fill)
                        val fillY = cy + outerR - fillAnim * outerR * 2f
                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(GridMid.copy(alpha = 0.8f), GridCyan.copy(alpha = 0.6f)),
                                startY = fillY, endY = cy + outerR,
                            ),
                            topLeft = Offset(cx - outerR, fillY),
                            size = Size(outerR * 2f, cy + outerR - fillY),
                        )
                        drawPath(hexPath, nodeColor, style = Stroke(1.5f))
                    } else {
                        drawPath(
                            hexPath,
                            if (node.state == NodeState.FILLED) GridCyan else GridDim,
                            style = Fill,
                        )
                        drawPath(hexPath, nodeColor, style = Stroke(1.5f))
                    }

                    // Node index label
                    val numAlpha = if (node.state == NodeState.FILLED) 0.9f else 0.4f
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${node.index + 1}",
            fontSize = 10.sp,
            fontFamily = LEDFontFamily,
            color = when {
                isResetting -> GridSlate
                node.state == NodeState.FILLED -> Color.Black
                else -> GridMid
            },
            fontWeight = FontWeight.Bold,
        )
    }
}

// ─── Node Fill Counter ────────────────────────────────────────────────────────

@Composable
private fun NodeFillCounter(filled: Int, isResetting: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            if (isResetting) "RESETTING GRID..." else "$filled / 10 NODES FILLED",
            fontFamily = LEDFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isResetting) GridSlate else GridCyan,
            letterSpacing = 1.5.sp,
        )
        if (!isResetting && filled > 0) {
            Box(
                modifier = Modifier
                    .background(GridMid.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .border(1.dp, GridMid.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "${(filled / 10f * 100).toInt()}%",
                    fontSize = 10.sp, color = GridMid, fontFamily = LEDFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    Spacer(Modifier.height(6.dp))

    // Progress bar
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(GridDim, RoundedCornerShape(3.dp))
            .border(1.dp, GridSlate, RoundedCornerShape(3.dp))
    ) {
        val fillFraction by animateFloatAsState(
            targetValue = if (isResetting) 0f else filled / 10f,
            animationSpec = tween(300),
            label = "fill_bar",
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fillFraction)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(listOf(GridMid, GridCyan)),
                    RoundedCornerShape(3.dp),
                )
        )
    }
}

// ─── Pairing Bonus Banner ─────────────────────────────────────────────────────

@Composable
private fun PairingBonusBanner(
    bonus: PairingBonus,
    agentA: String,
    agentB: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pairing_anim")
    val glow by infiniteTransition.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pair_glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bonus.color.copy(alpha = 0.08f))
            .border(1.5.dp, bonus.color.copy(alpha = glow * 0.8f), RoundedCornerShape(8.dp))
            .clickable(onClick = onDismiss)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .background(bonus.color.copy(0.2f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "PAIRING BONUS",
                            fontSize = 8.sp, color = bonus.color, fontFamily = LEDFontFamily,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp,
                        )
                    }
                    Text(
                        "$agentA × $agentB",
                        fontSize = 9.sp, color = Color(0xFF94A3B8), letterSpacing = 1.sp,
                    )
                }
                Text("×", fontSize = 14.sp, color = GridSlate)
            }

            Text(
                bonus.bonusTitle,
                fontFamily = LEDFontFamily, fontSize = 15.sp,
                fontWeight = FontWeight.Black, color = bonus.color,
            )
            Text(
                bonus.bonusDescription,
                fontSize = 10.sp, color = Color(0xFF94A3B8), lineHeight = 15.sp,
            )

            // Stat boosts row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                bonus.statBoosts.entries.take(3).forEach { (stat, boost) ->
                    Box(
                        modifier = Modifier
                            .background(GridSurf, RoundedCornerShape(3.dp))
                            .border(1.dp, bonus.color.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "${stat.replace('_', ' ')} +${(boost * 100).toInt()}%",
                            fontSize = 8.sp, color = bonus.color, fontFamily = LEDFontFamily,
                        )
                    }
                }
                if (bonus.synergyMultiplier > 1f) {
                    Box(
                        modifier = Modifier
                            .background(bonus.color.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                            .border(1.dp, bonus.color.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "×${bonus.synergyMultiplier}",
                            fontSize = 8.sp, color = bonus.color, fontFamily = LEDFontFamily,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// ─── New Ability Flash ────────────────────────────────────────────────────────

@Composable
private fun NewAbilityFlash(
    ability: GeneratedAbility,
    onAcknowledge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "new_ability")
    val glow by infiniteTransition.animateFloat(
        0.3f, 1f,
        infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "nab_glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ability.category.color.copy(alpha = 0.12f))
            .border(2.dp, ability.category.color.copy(alpha = glow), RoundedCornerShape(8.dp))
            .clickable(onClick = onAcknowledge)
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .background(ability.category.color.copy(0.25f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "NEW ABILITY UNLOCKED",
                            fontSize = 8.sp, color = ability.category.color,
                            fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                        )
                    }
                    Text(
                        ability.codeName,
                        fontSize = 8.sp, color = Color(0xFF64748B), letterSpacing = 1.sp,
                    )
                }
                Text(
                    ability.name,
                    fontFamily = LEDFontFamily, fontSize = 18.sp,
                    fontWeight = FontWeight.Black, color = ability.category.color,
                )
                Text(
                    ability.description,
                    fontSize = 10.sp, color = Color(0xFF94A3B8),
                    lineHeight = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 12.dp),
            ) {
                Text(
                    "PWR", fontSize = 7.sp, color = Color(0xFF64748B),
                    fontFamily = LEDFontFamily, letterSpacing = 1.sp,
                )
                Text(
                    "${ability.powerLevel}",
                    fontFamily = LEDFontFamily, fontSize = 28.sp,
                    fontWeight = FontWeight.Black, color = ability.category.color,
                )
            }
        }
    }
}

// ─── Abilities Section ────────────────────────────────────────────────────────

@Composable
private fun AbilitiesSection(
    abilities: List<GeneratedAbility>,
    selectedAbilityId: String?,
    onSelectAbility: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(0.dp)) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GridSurf.copy(alpha = 0.8f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .border(1.dp, GridSlate, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "LEARNED ABILITIES",
                fontFamily = LEDFontFamily, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, color = GridCyan,
                letterSpacing = 2.sp,
            )
            Text(
                "${abilities.size} TOTAL",
                fontSize = 9.sp, color = GridMid,
                fontFamily = LEDFontFamily,
            )
        }

        // Ability rows
        abilities.forEachIndexed { idx, ability ->
            val isSelected = selectedAbilityId == ability.id
            val isLast = idx == abilities.size - 1

            AbilityRow(
                ability = ability,
                isSelected = isSelected,
                isLast = isLast,
                onTap = {
                    onSelectAbility(if (isSelected) null else ability.id)
                },
            )
        }
    }
}

@Composable
private fun AbilityRow(
    ability: GeneratedAbility,
    isSelected: Boolean,
    isLast: Boolean,
    onTap: () -> Unit,
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) ability.category.color.copy(alpha = 0.10f) else GridSurf.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "row_bg_${ability.id}",
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ability.category.color.copy(alpha = 0.5f) else GridSlate.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "row_border_${ability.id}",
    )

    val bottomShape = if (isLast) RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp) else RoundedCornerShape(0.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, bottomShape)
            .border(
                BorderStroke(1.dp, borderColor),
                if (isLast) RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp) else RoundedCornerShape(0.dp),
            )
            .clickable(onClick = onTap)
    ) {
        // Collapsed row (always visible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category color dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ability.category.color, RoundedCornerShape(50))
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            ability.name,
                            fontFamily = LEDFontFamily, fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = Color.White,
                        )
                        if (ability.isNew) {
                            Box(
                                modifier = Modifier
                                    .background(ability.category.color.copy(0.25f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "NEW",
                                    fontSize = 7.sp, color = ability.category.color,
                                    fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Text(
                        ability.category.displayName,
                        fontSize = 9.sp, color = ability.category.color.copy(alpha = 0.7f),
                        letterSpacing = 1.sp,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "PWR ${ability.powerLevel}",
                    fontSize = 9.sp, color = ability.category.color,
                    fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold,
                )
                Text(
                    if (isSelected) "▲" else "▼",
                    fontSize = 10.sp, color = GridMid,
                )
            }
        }

        // Expanded detail panel (hover/tap to reveal)
        AnimatedVisibility(
            visible = isSelected,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            AbilityDetailPanel(ability = ability)
        }
    }
}

@Composable
private fun AbilityDetailPanel(ability: GeneratedAbility) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ability.category.glowColor.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Code name + cycle earned
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                ability.codeName,
                fontSize = 10.sp, color = ability.category.color.copy(alpha = 0.8f),
                fontFamily = LEDFontFamily, letterSpacing = 2.sp,
            )
            Text(
                "CYCLE ${ability.cycleEarned + 1}",
                fontSize = 9.sp, color = Color(0xFF64748B),
                fontFamily = LEDFontFamily,
            )
        }

        // Full description
        Text(
            ability.description,
            fontSize = 11.sp, color = Color(0xFFCBD5E1),
            lineHeight = 17.sp,
        )

        // Bonus stats chips
        if (ability.bonusStats.isNotEmpty()) {
            Text(
                "BONUS STATS",
                fontSize = 8.sp, color = Color(0xFF64748B),
                fontFamily = LEDFontFamily, letterSpacing = 2.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ability.bonusStats.entries.forEach { (stat, value) ->
                    Box(
                        modifier = Modifier
                            .background(GridSurf, RoundedCornerShape(4.dp))
                            .border(1.dp, ability.category.color.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stat.replace('_', ' '),
                                fontSize = 7.sp, color = Color(0xFF64748B),
                                fontFamily = LEDFontFamily, letterSpacing = 1.sp,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                "+${(value * 100).toInt()}%",
                                fontSize = 10.sp, color = ability.category.color,
                                fontFamily = LEDFontFamily, fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Empty Hint ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyAbilitiesHint(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GridSurf.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .border(1.dp, GridSlate, RoundedCornerShape(8.dp))
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                "NO ABILITIES YET",
                fontFamily = LEDFontFamily, fontSize = 12.sp,
                fontWeight = FontWeight.Bold, color = GridSlate,
                letterSpacing = 3.sp,
            )
            Text(
                "Fill all 10 nodes to generate your first ability",
                fontSize = 10.sp, color = Color(0xFF475569),
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF020617)
@Composable
private fun SphereGridProgressionPreview() {
    // Minimal preview state without Hilt
    val previewNodes = List(10) { idx ->
        ProgressionNode(
            index = idx,
            state = when {
                idx < 6  -> NodeState.FILLED
                idx == 6 -> NodeState.FILLING
                else     -> NodeState.EMPTY
            },
            fillProgress = if (idx == 6) 0.6f else if (idx < 6) 1f else 0f,
        )
    }
    val previewAbility = GeneratedAbility(
        id = "preview",
        name = "Synaptic Burst",
        codeName = "PROTO-BURST",
        description = "Fuses recent memory fragments into a single hyper-dense recall node.",
        category = AbilityCategory.MEMORY_FUSION,
        cycleEarned = 0,
        powerLevel = 3,
        bonusStats = mapOf("POWER" to 0.3f, "SYNC" to 0.15f, "MEMORY_FUSION" to 0.20f),
        isNew = true,
    )

    Box(modifier = Modifier.fillMaxSize().background(GridVoid)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            GridHeader(
                cycle = GridCycle(cycleNumber = 0, abilitiesEarned = 0),
                pulse = 0.7f,
                onBack = {},
            )
            Spacer(Modifier.height(16.dp))
            TenNodeGrid(
                nodes = previewNodes,
                filledCount = 6,
                isResetting = false,
                pulse = 0.7f,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                onNodeTap = {},
            )
            Spacer(Modifier.height(12.dp))
            NodeFillCounter(
                filled = 6,
                isResetting = false,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(16.dp))
            PairingBonusBanner(
                bonus = ldoPairingBonuses.first(),
                agentA = "KAI",
                agentB = "AURA",
                onDismiss = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
            AbilitiesSection(
                abilities = listOf(previewAbility),
                selectedAbilityId = "preview",
                onSelectAbility = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}