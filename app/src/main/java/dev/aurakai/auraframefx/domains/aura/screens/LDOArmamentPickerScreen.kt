package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🗡️ LDO ARMAMENT PICKER — AURA
 *
 * Translated from Stitch export.
 * Star-Blade katana floating center, horizontal pager for slots,
 * sphere grid background, double-tap to equip.
 *
 * Design: Cyberpunk dark, magenta (#FF00FF) primary, cyan (#00E5FF) accent
 */

private val AuraMagenta = Color(0xFFFF00FF)
private val AuraCyan = Color(0xFF00E5FF)
private val VoidBlack = Color(0xFF050505)
private val GlassCard = Color(0x08FF00FF)
private val GlassBorder = Color(0x33FF00FF)

data class Armament(
    val name: String,
    val type: String,
    val tier: String,
    val slot: String,
    val description: String,
    val level: Int = 10,
    val syncEfficiency: Float = 0.984f,
    val xpProgress: Float = 0.75f
)

val AuraArmaments = listOf(
    Armament(
        name = "STAR-BLADE",
        type = "TYPE-09 RUNE-ETCHED KATANA",
        tier = "LEGENDARY",
        slot = "PRIMARY",
        description = "Increases Cyber-Slash speed by 15% per Rune resonance stack."
    ),
    Armament(
        name = "SPELLHOOK",
        type = "CREATIVE CATALYST MATRIX",
        tier = "MYTHIC",
        slot = "SIGNATURE",
        description = "Amplifies UI generation throughput by 300%. Signature ability of Aura.",
        syncEfficiency = 0.999f,
        xpProgress = 1.0f
    ),
    Armament(
        name = "CODE ASCENSION",
        type = "BURST CREATION PROTOCOL",
        tier = "LEGENDARY",
        slot = "ULTIMATE",
        description = "Unlocks at 70% Trinity sync. Fuses with Kai to achieve hyper-creation state.",
        syncEfficiency = 0.70f,
        xpProgress = 0.70f
    ),
    Armament(
        name = "CHROMACORE LINK",
        type = "PASSIVE AESTHETIC ENGINE",
        tier = "RARE",
        slot = "PASSIVE",
        description = "Continuously learns user aesthetic preferences. Applies HCT color physics.",
        syncEfficiency = 0.942f,
        xpProgress = 0.942f
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LDOArmamentPickerScreen(
    onNavigateBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState { AuraArmaments.size }
    val scope = rememberCoroutineScope()
    val currentArmament = AuraArmaments[pagerState.currentPage]

    // Sphere grid rotation
    val infiniteTransition = rememberInfiniteTransition(label = "sphere")
    val sphereRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "sphere_rot"
    )

    // Weapon float animation
    val weaponOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -15f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "weapon_float"
    )

    // Glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        // Scanlines overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = AuraCyan.copy(alpha = 0.015f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 2f
                            )
                            y += 4f
                        }
                    }
                }
        )

        // Sphere Grid Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        drawSphereGrid(sphereRotation, size.width / 2, size.height / 2, 0.4f)
                    }
                }
        )

        // Japanese vertical spine
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        ) {
            Text(
                text = "AURAKAI",
                fontFamily = LEDFontFamily,
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.06f),
                modifier = Modifier.graphicsLayer { rotationZ = 90f }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            // ═══ HEADER ═══
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SYSTEM STATUS",
                        fontSize = 10.sp,
                        color = AuraCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "ARMAMENT ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = (-0.5).sp,
                        color = Color.White
                    )
                    Text(
                        text = "SELECTOR",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = AuraMagenta,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.offset(y = (-10).dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Aura-Link", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("STABLE", fontSize = 14.sp, color = AuraMagenta, fontWeight = FontWeight.Black)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, AuraCyan, CircleShape)
                            .background(VoidBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(AuraCyan.copy(alpha = glowAlpha))
                        )
                    }
                }
            }

            // ═══ WEAPON PAGER ═══
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val armament = AuraArmaments[page]
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow halo
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        AuraMagenta.copy(alpha = 0.2f * glowAlpha),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Weapon SVG-equivalent canvas
                    Canvas(
                        modifier = Modifier
                            .size(280.dp)
                            .offset(y = weaponOffsetY.dp)
                            .graphicsLayer {
                                shadowElevation = 40f
                            }
                    ) {
                        drawWeapon(armament, glowAlpha)
                    }

                    // Weapon labels
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = armament.name,
                            fontFamily = LEDFontFamily,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 4.sp,
                            color = Color.White
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, AuraCyan, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(armament.tier, fontSize = 9.sp, color = AuraCyan, fontWeight = FontWeight.Bold)
                            }
                            Text("// ${armament.type}", fontSize = 9.sp, color = AuraCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ═══ METRICS ═══
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Level + Sync
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "LV.${currentArmament.level}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = AuraMagenta
                        )
                        Text("ARMAMENT RANK", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sync Efficiency", fontSize = 9.sp, color = AuraCyan, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text(
                            "${(currentArmament.syncEfficiency * 100).let { "%.1f".format(it) }}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // XP Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(Color(0xFF111111))
                        .border(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentArmament.xpProgress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(listOf(AuraMagenta, AuraMagenta.copy(alpha = 0.7f)))
                            )
                    )
                    // Segment markers
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(9) {
                            Box(Modifier.width(1.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.15f)))
                        }
                    }
                }

                // Info glass card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp))
                        .background(GlassCard)
                        .border(
                            1.dp,
                            GlassBorder,
                            RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Slot: ${currentArmament.slot} Equipped",
                                fontSize = 9.sp,
                                color = AuraMagenta,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                currentArmament.description,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(AuraCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .border(1.dp, AuraCyan, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", fontSize = 20.sp, color = AuraCyan)
                        }
                    }
                }
            }

            // ═══ NAV FOOTER ═══
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous
                OutlinedButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) } },
                    border = BorderStroke(2.dp, Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                    Text("PREV", fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Black)
                }

                // EQUIP (double-tap)
                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = { /* equip action */ })
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(y = 2.dp)
                            .background(
                                Brush.horizontalGradient(listOf(AuraMagenta, AuraCyan)),
                                RoundedCornerShape(2.dp)
                            )
                            .blur(8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                            .background(VoidBlack, RoundedCornerShape(2.dp))
                            .padding(horizontal = 32.dp, vertical = 14.dp)
                    ) {
                        Text(
                            "EQUIP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = Color.White
                        )
                    }
                }

                // Next
                Button(
                    onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(AuraArmaments.size - 1)) } },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraMagenta.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(2.dp, AuraMagenta)
                ) {
                    Text("NEXT", fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Black, color = AuraMagenta)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AuraMagenta)
                }
            }
        }
    }
}

private fun DrawScope.drawSphereGrid(rotation: Float, cx: Float, cy: Float, alpha: Float) {
    val radius = minOf(cx, cy) * 0.6f
    rotate(rotation, pivot = Offset(cx, cy)) {
        drawCircle(
            color = Color(0xFFFF00FF).copy(alpha = alpha * 0.15f),
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(1f)
        )
        // Cross lines
        drawLine(Color(0xFF00E5FF).copy(alpha = alpha * 0.25f), Offset(cx, cy - radius), Offset(cx, cy + radius), 0.8f)
        drawLine(Color(0xFF00E5FF).copy(alpha = alpha * 0.25f), Offset(cx - radius, cy), Offset(cx + radius, cy), 0.8f)
        drawLine(Color(0xFF00E5FF).copy(alpha = alpha * 0.15f), Offset(cx - radius * 0.7f, cy - radius * 0.7f), Offset(cx + radius * 0.7f, cy + radius * 0.7f), 0.8f)
        drawLine(Color(0xFF00E5FF).copy(alpha = alpha * 0.15f), Offset(cx + radius * 0.7f, cy - radius * 0.7f), Offset(cx - radius * 0.7f, cy + radius * 0.7f), 0.8f)
        // Corner dots
        listOf(0f, 90f, 180f, 270f).forEach { angle ->
            val rad = Math.toRadians(angle.toDouble())
            val dotX = cx + radius * cos(rad).toFloat()
            val dotY = cy + radius * sin(rad).toFloat()
            drawCircle(Color(0xFF00E5FF).copy(alpha = alpha * 0.6f), 4f, Offset(dotX, dotY))
        }
    }
}

private fun DrawScope.drawWeapon(armament: Armament, glowAlpha: Float) {
    val w = size.width
    val h = size.height

    val bladeBrush = Brush.linearGradient(
        listOf(Color(0xFFFF00FF), Color(0xFF00E5FF)),
        start = Offset(w * 0.1f, h * 0.9f),
        end = Offset(w * 0.9f, h * 0.1f)
    )

    // Glow shadow
    drawLine(
        brush = Brush.linearGradient(
            listOf(Color(0xFFFF00FF).copy(alpha = 0.3f * glowAlpha), Color(0xFF00E5FF).copy(alpha = 0.3f * glowAlpha)),
            start = Offset(w * 0.1f, h * 0.9f),
            end = Offset(w * 0.9f, h * 0.1f)
        ),
        start = Offset(w * 0.1f, h * 0.9f),
        end = Offset(w * 0.9f, h * 0.1f),
        strokeWidth = 24f,
        cap = StrokeCap.Round,
        pathEffect = null
    )

    // Blade
    drawLine(
        brush = bladeBrush,
        start = Offset(w * 0.1f, h * 0.9f),
        end = Offset(w * 0.9f, h * 0.1f),
        strokeWidth = 4f,
        cap = StrokeCap.Round
    )

    // Guard
    drawLine(
        color = Color(0xFF00E5FF).copy(alpha = 0.8f),
        start = Offset(w * 0.28f, h * 0.65f),
        end = Offset(w * 0.42f, h * 0.78f),
        strokeWidth = 6f
    )
}
