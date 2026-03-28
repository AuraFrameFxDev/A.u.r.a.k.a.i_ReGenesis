package dev.aurakai.auraframefx.domains.aura.ui.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

// â”€â”€â”€ Data â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

private data class CommandItem(
    val abbrev: String,   // 3-4 char label always shown
    val full: String,     // full name shown in shortcuts/tooltip row
    val icon: ImageVector,
    val color: Color,
    val action: String
)

private val COMMAND_ITEMS = listOf(
    CommandItem("VCE",  "VOICE",   Icons.Default.Mic,         Color(0xFF00FFFF), "VOICE"),
    CommandItem("CNCT", "CONNECT", Icons.Default.Link,        Color(0xFF00FFFF), "CONNECT"),
    CommandItem("ASGN", "ASSIGN",  Icons.Default.PersonAdd,   Color(0xFFFF00FF), "ASSIGN"),
    CommandItem("DSN",  "DESIGN",  Icons.Default.Brush,       Color(0xFFFF00FF), "DESIGN"),
    CommandItem("CRT",  "CREATE",  Icons.Default.AddBox,      Color(0xFFFF00FF), "CREATE"),
)

private data class Shortcut(
    val key: String,      // e.g. "âŒ˜V"
    val label: String,
    val icon: ImageVector,
    val action: String
)

private val SHORTCUTS = listOf(
    Shortcut("âŒ˜V", "Voice",    Icons.Default.Mic,              "VOICE"),
    Shortcut("âŒ˜T", "Terminal", Icons.Default.Terminal,         "TERMINAL"),
    Shortcut("âŒ˜B", "Build",    Icons.Default.Build,            "BUILD"),
    Shortcut("âŒ˜C", "Code",     Icons.Default.Code,             "CODE"),
    Shortcut("âŒ˜D", "Dash",     Icons.Default.DashboardCustomize, "DASHBOARD"),
)

// â”€â”€â”€ Main Composable â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

/**
 * ðŸ›°ï¸ NEURAL LINK SIDEBAR
 * Command Deck with:
 *  - 10-second pulse fade on all labels (infinite, inâ†’outâ†’in)
 *  - Abbreviated 3-4 char item names for compact readability
 *  - Collapsible shortcuts menu for maximum user flexibility
 */
@Composable
fun NeuralLinkSidebarUI(
    isVisible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    onActionClick: (String) -> Unit
) {
    // 10-second label pulse: fade out over 5s, fade back in over 5s
    val pulse = rememberInfiniteTransition(label = "label_pulse")
    val labelAlpha by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "label_alpha"
    )

    // Header glow pulse (faster, independent)
    val headerGlow = rememberInfiniteTransition(label = "header_glow")
    val headerAlpha by headerGlow.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "header_alpha"
    )

    var shortcutsExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 80.dp, horizontal = 12.dp)
                    .width(260.dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(Color(0xFF00FFFF), Color(0xFFFF00FF))),
                        RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp)
                    )
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // â”€â”€ Header â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                Text(
                    "NRLâ€¢LNK",
                    fontFamily = LEDFontFamily,
                    color = Color.Cyan.copy(alpha = headerAlpha),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    "CMD DECK",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = labelAlpha * 0.7f),
                    fontSize = 9.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // â”€â”€ Command items â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                COMMAND_ITEMS.forEach { item ->
                    SidebarCommandItem(
                        abbrev = item.abbrev,
                        fullName = item.full,
                        icon = item.icon,
                        glowColor = item.color,
                        labelAlpha = labelAlpha,
                        onClick = { onActionClick(item.action) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // â”€â”€ Shortcuts section â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { shortcutsExpanded = !shortcutsExpanded }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "SHORTCUTS",
                        fontFamily = LEDFontFamily,
                        color = Color(0xFFFF00FF).copy(alpha = labelAlpha),
                        fontSize = 9.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = if (shortcutsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (shortcutsExpanded) "Collapse" else "Expand",
                        tint = Color(0xFFFF00FF).copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                AnimatedVisibility(
                    visible = shortcutsExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SHORTCUTS.forEach { shortcut ->
                            ShortcutRow(
                                shortcut = shortcut,
                                labelAlpha = labelAlpha,
                                onClick = { onActionClick(shortcut.action) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // â”€â”€ Close button â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                IconButton(onClick = { onVisibleChange(false) }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        "Close",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Pull-tab when hidden
        if (!isVisible) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight(0.28f)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF00FFFF).copy(alpha = 0.5f),
                                Color(0xFFFF00FF).copy(alpha = 0.5f)
                            )
                        )
                    )
                    .clickable { onVisibleChange(true) }
            )
        }
    }
}

// â”€â”€â”€ Command item â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun SidebarCommandItem(
    abbrev: String,
    fullName: String,
    icon: ImageVector,
    glowColor: Color,
    labelAlpha: Float,
    onClick: () -> Unit
) {
    var showFull by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showFull = !showFull }
            )
            .height(52.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, glowColor.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(glowColor.copy(alpha = 0.08f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = fullName,
                    tint = glowColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = abbrev,
                        color = Color.White.copy(alpha = labelAlpha),
                        fontFamily = LEDFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    // Long-press reveals full name inline
                    AnimatedVisibility(visible = showFull) {
                        Text(
                            text = fullName,
                            color = glowColor.copy(alpha = 0.7f),
                            fontFamily = LEDFontFamily,
                            fontSize = 8.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

// â”€â”€â”€ Shortcut row â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun ShortcutRow(
    shortcut: Shortcut,
    labelAlpha: Float,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(Color.White.copy(alpha = 0.04f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = shortcut.icon,
                contentDescription = shortcut.label,
                tint = Color(0xFFFF00FF).copy(alpha = 0.8f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = shortcut.label,
                color = Color.White.copy(alpha = labelAlpha),
                fontFamily = LEDFontFamily,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
        }
        // Keyboard shortcut badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFF00FF).copy(alpha = 0.15f))
                .border(0.5.dp, Color(0xFFFF00FF).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = shortcut.key,
                color = Color(0xFFFF00FF).copy(alpha = labelAlpha),
                fontFamily = LEDFontFamily,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

