package dev.aurakai.auraframefx.ui.gates

// ═══════════════════════════════════════════════════════════════════════════════
// GateImageHotswapScreen.kt
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// Per-gate image hotswap picker.
// Shows all art variants for each gate (new.jpg, final.png, etc.)
// Tap to preview fullscreen. Confirm to persist via HomeBackdropManager.
//
// Also has a top section: HOME SCREEN BACKDROP picker (RPG scene selector).
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig
import kotlinx.coroutines.launch

// ── Gate variant data ─────────────────────────────────────────────────────────

data class GateVariant(
    val key: String,           // unique id, also used as pref value
    val label: String,
    val resId: Int             // R.drawable.*
)

data class GateHotswapEntry(
    val config: GateConfig,
    val variants: List<GateVariant>
)

// ── Variant catalogue — maps existing drawables to each gate ──────────────────

private fun buildHotswapCatalogue(allGates: List<GateConfig>): List<GateHotswapEntry> {
    val variantMap: Map<String, List<GateVariant>> = mapOf(

        "auras-lab" to listOf(
            GateVariant("aura_new",   "New (2025)",   R.drawable.gate_auralab_new),
            GateVariant("aura_final", "Final",        R.drawable.gate_auraslab_final),
            GateVariant("aura_orig",  "Original",     R.drawable.gate_auraslab),
        ),

        "chroma-core" to listOf(
            GateVariant("chroma_new",   "New (2025)",   R.drawable.gate_chromacore_new),
            GateVariant("chroma_final", "Final",        R.drawable.gate_chromacore_final),
            GateVariant("chroma_orig",  "Original",     R.drawable.gate_chromacore),
        ),

        "uiux-design-studio" to listOf(
            GateVariant("uiux_new",   "New (2025)",   R.drawable.gate_uiux_new),
            GateVariant("uiux_final", "Final",        R.drawable.gate_uiuxdesignstudio_final),
            GateVariant("uiux_orig",  "Original",     R.drawable.gate_uiuxdesignstudio),
            GateVariant("uiux_uxui",  "UXUI Variant", R.drawable.gate_uxuidesign_new),
        ),

        "oracle-drive" to listOf(
            GateVariant("oracle_final", "Final",     R.drawable.gate_oracledrive_final),
            GateVariant("oracle_orig",  "Original",  R.drawable.gate_oracledrive),
            GateVariant("oracle_pixel", "Pixel Art", R.drawable.oracledrivepixel2),
        ),

        "sentinels-fortress" to listOf(
            GateVariant("sentinel_final", "Final",    R.drawable.sentinelfinalgate),
            GateVariant("sentinel_orig",  "Original", R.drawable.sentinelsfortress),
        ),

        "code-assist" to listOf(
            GateVariant("code_new",   "New (2025)", R.drawable.gate_codeassist_new),
            GateVariant("code_final", "Final",      R.drawable.gate_codeassist_final),
            GateVariant("code_orig",  "Original",   R.drawable.gate_codeassist),
        ),

        "collab-canvas" to listOf(
            GateVariant("canvas_new",   "New (2025)", R.drawable.gate_collabcanvas_new),
            GateVariant("canvas_final", "Final",      R.drawable.gate_collabcanvas_final),
            GateVariant("canvas_orig",  "Original",   R.drawable.collabcanvasgate),
        ),

        "help-desk" to listOf(
            GateVariant("help_new",   "New (2025)", R.drawable.gate_helpdesk_new),
            GateVariant("help_final", "Final",      R.drawable.gate_helpdesk_final),
            GateVariant("help_orig",  "Original",   R.drawable.gate_helpdesk),
        ),

        "lsposed-gate" to listOf(
            GateVariant("lsposed_new",   "New (2025)", R.drawable.gate_lsposed_new),
            GateVariant("lsposed_final", "Final",      R.drawable.gate_lsposed_final),
            GateVariant("lsposed_orig",  "Original",   R.drawable.gate_lsposedgate),
        ),

        "sphere-grid" to listOf(
            GateVariant("sphere_final", "Final",    R.drawable.gate_spheregrid_final),
            GateVariant("sphere_aura",  "Aura",     R.drawable.spheregrid_aura),
            GateVariant("sphere_claude","Claude",   R.drawable.spheregrid_claude),
            GateVariant("sphere_grok",  "Grok",     R.drawable.spheregrid_grok),
            GateVariant("sphere_orig",  "Original", R.drawable.spheregrid),
        ),

        "agent-hub" to listOf(
            GateVariant("agenthub_orig", "Original", R.drawable.gate_agenthub),
            GateVariant("agenthub_alt",  "Alt",      R.drawable.agenthub),
        ),

        "rom-tools" to listOf(
            GateVariant("rom_orig", "Original", R.drawable.romtools),
        ),
    )

    return allGates.mapNotNull { config ->
        val variants = variantMap[config.moduleId] ?: return@mapNotNull null
        GateHotswapEntry(config, variants)
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GateImageHotswapScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val allGates = GateConfigs.allGates
    val catalogue = remember { buildHotswapCatalogue(allGates) }

    // Active backdrop
    val activeBackdrop by HomeBackdropManager.activeBackdropFlow(context)
        .collectAsState(initial = HomeBackdropManager.backdropOptions.first())

    // Full-screen preview state
    var previewVariant by remember { mutableStateOf<GateVariant?>(null) }
    var previewGate by remember { mutableStateOf<GateHotswapEntry?>(null) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "GATE IMAGE HOTSWAP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = Color(0xFF00FFFF)
                        )
                        Text(
                            "Swap art per gate • Survives reboots",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF00FFFF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0A14))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── HOME BACKDROP SECTION ──────────────────────────────────────
            item {
                SectionHeader(
                    title = "HOME SCREEN BACKDROP",
                    subtitle = "The RPG world behind your gate carousel"
                )
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(HomeBackdropManager.backdropOptions) { option ->
                        BackdropOptionCard(
                            option = option,
                            isActive = activeBackdrop.id == option.id,
                            onClick = {
                                scope.launch {
                                    HomeBackdropManager.setActiveBackdrop(context, option.id)
                                }
                            }
                        )
                    }
                }
            }

            // ── DIVIDER ───────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF00FFFF).copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // ── PER-GATE SECTIONS ─────────────────────────────────────────
            items(catalogue) { entry ->
                GateVariantSection(
                    entry = entry,
                    onVariantTap = { variant ->
                        previewVariant = variant
                        previewGate = entry
                    },
                    onVariantConfirm = { variant ->
                        scope.launch {
                            HomeBackdropManager.setGateImage(
                                context, entry.config.moduleId, variant.key
                            )
                        }
                    }
                )
            }
        }
    }

    // ── Fullscreen Preview Dialog ──────────────────────────────────────────
    previewVariant?.let { variant ->
        previewGate?.let { gate ->
            Dialog(onDismissRequest = { previewVariant = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { previewVariant = null }
                ) {
                    Image(
                        painter = painterResource(id = variant.resId),
                        contentDescription = variant.label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Confirm button
                    Button(
                        onClick = {
                            scope.launch {
                                HomeBackdropManager.setGateImage(
                                    context, gate.config.moduleId, variant.key
                                )
                            }
                            previewVariant = null
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FFFF),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "USE \"${variant.label.uppercase()}\" FOR ${gate.config.title.uppercase()}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            color = Color(0xFF00FFFF)
        )
        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}

// ── Backdrop Option Card ──────────────────────────────────────────────────────

@Composable
private fun BackdropOptionCard(
    option: BackdropOption,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val accentColor = Color(option.accentColor)
    val borderBrush = if (isActive) {
        Brush.linearGradient(listOf(accentColor, accentColor.copy(alpha = 0.5f)))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f)))
    }

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isActive) 2.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (option.resId != null) {
            Image(
                painter = painterResource(id = option.resId),
                contentDescription = option.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.15f), Color.Black)
                        )
                    )
            )
        }
        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(option.label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(option.description, fontSize = 7.sp, color = Color.White.copy(alpha = 0.5f))
        }
        if (isActive) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Active",
                tint = accentColor,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(14.dp)
            )
        }
    }
}

// ── Per-Gate Variant Section ──────────────────────────────────────────────────

@Composable
private fun GateVariantSection(
    entry: GateHotswapEntry,
    onVariantTap: (GateVariant) -> Unit,
    onVariantConfirm: (GateVariant) -> Unit
) {
    val context = LocalContext.current
    val activeKey by HomeBackdropManager
        .activeGateImageFlow(context, entry.config.moduleId)
        .collectAsState(initial = null)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = entry.config.title.uppercase(),
            subtitle = "${entry.variants.size} variants available"
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(entry.variants) { variant ->
                val isActive = activeKey == variant.key || (activeKey == null && entry.variants.firstOrNull() == variant)
                GateVariantThumb(
                    variant = variant,
                    accentColor = entry.config.glowColor,
                    isActive = isActive,
                    onTap = { onVariantTap(variant) },
                    onLongPress = { onVariantConfirm(variant) }
                )
            }
        }
    }
}

// ── Variant Thumbnail ─────────────────────────────────────────────────────────

@Composable
private fun GateVariantThumb(
    variant: GateVariant,
    accentColor: Color,
    isActive: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.95f,
        label = "thumb_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(155.dp)    // ~0.7 aspect = gate card ratio
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) accentColor else Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                )
                .combinedClickable(
                    onClick = onTap,
                    onLongClick = onLongPress
                )
        ) {
            Image(
                painter = painterResource(id = variant.resId),
                contentDescription = variant.label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(18.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Active",
                        tint = Color.Black,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }

        Text(
            text = variant.label,
            fontSize = 8.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) accentColor else Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "tap=preview  hold=apply",
            fontSize = 6.sp,
            color = Color.White.copy(alpha = 0.25f),
            textAlign = TextAlign.Center
        )
    }
}
