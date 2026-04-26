package dev.aurakai.auraframefx.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🎮 BOTTOM JOYSTICK NAVIGATION
 * 
 * Gaming-inspired navigation with:
 * - Left/Right swipe to switch tabs
 * - Visual joystick indicator at bottom
 * - Quick-access icons for each LDO domain
 */

@Composable
fun BottomJoystickNavigation(
    selectedIndex: Int,
    tabs: List<String>,
    accentColor: Color,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    // Tab icons mapping
    val tabIcons = listOf(
        Icons.Default.Dashboard,      // 0: Dashboard
        Icons.Default.Code,           // 1: LDO DevOps
        Icons.Default.Palette,        // 2: UXUI Design Studio
        Icons.Default.Security,       // 3: Sentinels Fortress
        Icons.Default.Hub,            // 4: OracleDrive
        Icons.Default.Memory,         // 5: Cascade Memory
        Icons.Default.Groups          // 6: Agent Nexus
    )
    
    // Short labels for bottom nav
    val shortLabels = listOf(
        "DASH", "LDO", "AURA", "KAI", "GEN", "CASC", "NEXUS"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f),
                        Color.Black.copy(alpha = 0.95f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount.x
                    },
                    onDragEnd = {
                        when {
                            dragOffset > 100f -> {
                                // Swipe right = previous tab
                                if (selectedIndex > 0) onTabSelected(selectedIndex - 1)
                            }
                            dragOffset < -100f -> {
                                // Swipe left = next tab
                                if (selectedIndex < tabs.size - 1) onTabSelected(selectedIndex + 1)
                            }
                        }
                        dragOffset = 0f
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Joystick visual indicator
            JoystickIndicator(
                selectedIndex = selectedIndex,
                totalTabs = tabs.size,
                accentColor = accentColor
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Tab icons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, _ ->
                    val isSelected = index == selectedIndex
                    val tabColor = when(index) {
                        0 -> Color(0xFFFFD700)
                        1 -> Color(0xFF00E5FF)
                        2 -> Color(0xFFFF00FF)
                        3 -> Color(0xFF00FF88)
                        4 -> Color(0xFFFFAA00)
                        5 -> Color(0xFF8B5CF6)
                        6 -> Color(0xFF00D6FF)
                        else -> Color.White
                    }
                    
                    BottomNavItem(
                        icon = tabIcons[index],
                        label = shortLabels[index],
                        isSelected = isSelected,
                        color = tabColor,
                        onClick = { onTabSelected(index) }
                    )
                }
            }
        }
        
        // Swipe hint animation
        if (dragOffset != 0f) {
            SwipeHintOverlay(dragOffset, accentColor)
        }
    }
}

@Composable
fun JoystickIndicator(
    selectedIndex: Int,
    totalTabs: Int,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "joystick_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(alpha = 0.2f))
    ) {
        // Active position indicator
        val indicatorPosition = selectedIndex / (totalTabs - 1).toFloat()
        
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .graphicsLayer {
                    translationX = (indicatorPosition * (200.dp.toPx() - 40.dp.toPx())).coerceIn(0f, (200.dp.toPx() - 40.dp.toPx()))
                }
                .clip(RoundedCornerShape(2.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00FFFF), // Cyan
                            Color(0xFFFF00FF)  // Magenta
                        )
                    )
                )
                .scale(pulse)
        )
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 44.dp else 36.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    } else {
                        SolidColor(Color.Transparent)
                    }
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF00FFFF), Color(0xFFFF00FF))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) color else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(if (isSelected) 24.dp else 20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = label,
            color = if (isSelected) color else Color.White.copy(alpha = 0.5f),
            fontSize = 8.sp,
            fontFamily = LEDFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SwipeHintOverlay(
    dragOffset: Float,
    accentColor: Color
) {
    val direction = when {
        dragOffset > 0 -> "← PREV"
        dragOffset < 0 -> "NEXT →"
        else -> ""
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = if (dragOffset > 0) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Text(
            text = direction,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LEDFontFamily,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

/**
 * Floating joystick button for quick navigation
 */
@Composable
fun FloatingJoystickButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_pulse"
    )
    
    Box(
        modifier = modifier
            .size(60.dp * pulse)
            .clip(CircleShape)
            .background(
                color = Color(0xFF00FFFF),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF00FFFF),
                        Color(0xFFFF00FF),
                        Color(0xFF00FFFF)
                    )
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
