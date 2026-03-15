package dev.aurakai.auraframefx.domains.aura.ui.screens.aura

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify.IconPickerViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🎨 ICONIFY HUB - 500+ System Customizations
 *
 * Aura's Domain - The Face of UI/UX
 *
 * Three-tab interface mirroring Iconify by DrDisagree:
 * - HOME: Quick access, banner, main categories
 * - TWEAKS: Deep system customizations
 * - XPOSED: LSPosed-powered features (requires root)
 *
 * Built with LDO aesthetic - neon glows, LED fonts, agent presence
 */

// ═══════════════════════════════════════════════════════════════════════════
// COLORS & THEME
// ═══════════════════════════════════════════════════════════════════════════

private object IconifyColors {
    val AuraPrimary = Color(0xFFB026FF)      // Aura's signature purple
    val AuraSecondary = Color(0xFF00E5FF)   // Cyan accent
    val NeonPink = Color(0xFFFF00FF)
    val NeonGreen = Color(0xFF00FF85)
    val Warning = Color(0xFFFF6B6B)
    val CardBg = Color(0xFF1A1A2E)
    val DarkBg = Color(0xFF0D0D1A)
    val GlowPurple = Color(0xFF9D4EDD)
}

// ═══════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════

data class IconifyCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val settingsCount: Int,
    val accentColor: Color = IconifyColors.AuraPrimary,
    val requiresXposed: Boolean = false,
    val requiresRoot: Boolean = false
)

// ═══════════════════════════════════════════════════════════════════════════
// MAIN SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconifyHubScreen(
    viewModel: IconPickerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Tweaks", "Xposed")

    // Animated background pulse
    val infiniteTransition = rememberInfiniteTransition(label = "bgPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.03f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IconifyColors.DarkBg)
            .drawBehind {
                // Animated gradient overlay
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            IconifyColors.AuraPrimary.copy(alpha = pulseAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.8f, size.height * 0.2f),
                        radius = size.width * 0.6f
                    )
                )
            }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                IconifyTopBar(onNavigateBack = onNavigateBack)
            },
            bottomBar = {
                IconifyBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    tabs = tabs
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (selectedTab) {
                    0 -> HomeTab(onNavigateToCategory = onNavigateToCategory)
                    1 -> TweaksTab(onNavigateToCategory = onNavigateToCategory)
                    2 -> XposedTab(onNavigateToCategory = onNavigateToCategory)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// TOP BAR
// ═══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconifyTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Aura's mini avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(IconifyColors.AuraPrimary, IconifyColors.NeonPink)
                            )
                        )
                        .border(1.dp, IconifyColors.AuraSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ICONIFY",
                        fontFamily = LEDFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = IconifyColors.AuraPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "500+ System Customizations",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Search */ }) {
                Icon(Icons.Default.Search, "Search", tint = Color.White)
            }
            IconButton(onClick = { /* Settings */ }) {
                Icon(Icons.Default.Settings, "Settings", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

// ═══════════════════════════════════════════════════════════════════════════
// BOTTOM NAVIGATION
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun IconifyBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>
) {
    NavigationBar(
        containerColor = IconifyColors.CardBg.copy(alpha = 0.95f),
        contentColor = Color.White
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = selectedTab == index
            val icon = when (index) {
                0 -> if (selected) Icons.Filled.Home else Icons.Default.Home
                1 -> if (selected) Icons.Filled.Tune else Icons.Default.Tune
                2 -> if (selected) Icons.Filled.Extension else Icons.Default.Extension
                else -> Icons.Default.Home
            }

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = if (selected) IconifyColors.AuraPrimary else Color.White.copy(alpha = 0.6f)
                    )
                },
                label = {
                    Text(
                        title,
                        fontFamily = LEDFontFamily,
                        fontSize = 10.sp,
                        color = if (selected) IconifyColors.AuraPrimary else Color.White.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = IconifyColors.AuraPrimary.copy(alpha = 0.2f)
                )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// HOME TAB
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeTab(onNavigateToCategory: (String) -> Unit) {
    LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner with Aura
        item {
            HeroBanner()
        }

        // Quick Stats
        item {
            QuickStatsRow()
        }

        // Main Categories
        item {
            Text(
                text = "MAIN CATEGORIES",
                fontFamily = LEDFontFamily,
                fontSize = 12.sp,
                color = IconifyColors.AuraSecondary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Category Grid
        items(homeCategories.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { category ->
                    CategoryCard(
                        category = category,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCategory(category.id) }
                    )
                }
                // Fill empty space if odd number
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// HERO BANNER
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "heroBanner")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            IconifyColors.AuraPrimary.copy(alpha = 0.3f),
                            IconifyColors.NeonPink.copy(alpha = 0.2f),
                            IconifyColors.AuraSecondary.copy(alpha = 0.3f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            IconifyColors.AuraPrimary.copy(alpha = 0.5f),
                            IconifyColors.AuraSecondary.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            // Mountain silhouette / scene background would go here
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Column {
                    Text(
                        text = "Design It Your Way",
                        fontFamily = LEDFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Unleash Your Imagination, Make It Yours",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // CTA Button
                Button(
                    onClick = { /* Quick start */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IconifyColors.AuraPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick Start", fontFamily = LEDFontFamily)
                }
            }

            // Aura silhouette hint (right side)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(100.dp)
                    .graphicsLayer { alpha = 0.3f }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                IconifyColors.AuraPrimary,
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// QUICK STATS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatChip(
            label = "Active",
            value = "23",
            color = IconifyColors.NeonGreen,
            modifier = Modifier.weight(1f)
        )
        StatChip(
            label = "Tweaks",
            value = "500+",
            color = IconifyColors.AuraPrimary,
            modifier = Modifier.weight(1f)
        )
        StatChip(
            label = "Xposed",
            value = "45",
            color = IconifyColors.Warning,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = IconifyColors.CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = LEDFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// CATEGORY CARD
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun CategoryCard(
    category: IconifyCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "scale"
    )

    Card(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = IconifyColors.CardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon with glow
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(category.accentColor.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            category.accentColor.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        category.icon,
                        contentDescription = category.name,
                        tint = category.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        fontFamily = LEDFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = category.description,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Settings count badge
            if (category.settingsCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${category.settingsCount} settings",
                    fontSize = 9.sp,
                    color = category.accentColor.copy(alpha = 0.7f)
                )
            }

            // Xposed/Root badges
            if (category.requiresXposed || category.requiresRoot) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (category.requiresXposed) {
                        Badge(text = "XPOSED", color = IconifyColors.Warning)
                    }
                    if (category.requiresRoot) {
                        Badge(text = "ROOT", color = Color(0xFFFF5722))
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// TWEAKS TAB
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun TweaksTab(onNavigateToCategory: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "SYSTEM TWEAKS",
                fontFamily = LEDFontFamily,
                fontSize = 12.sp,
                color = IconifyColors.AuraSecondary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(tweaksCategories) { category ->
            TweakListItem(
                category = category,
                onClick = { onNavigateToCategory(category.id) }
            )
        }
    }
}

@Composable
private fun TweakListItem(
    category: IconifyCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = IconifyColors.CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = category.accentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontFamily = LEDFontFamily,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = category.description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// XPOSED TAB
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun XposedTab(onNavigateToCategory: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Warning Banner
        item {
            XposedWarningBanner()
        }

        item {
            Text(
                text = "XPOSED FEATURES",
                fontFamily = LEDFontFamily,
                fontSize = 12.sp,
                color = IconifyColors.Warning,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(xposedCategories) { category ->
            TweakListItem(
                category = category,
                onClick = { onNavigateToCategory(category.id) }
            )
        }
    }
}

@Composable
private fun XposedWarningBanner() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = IconifyColors.Warning.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = IconifyColors.Warning,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "LSPosed Required",
                    fontFamily = LEDFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IconifyColors.Warning
                )
                Text(
                    text = "These features require LSPosed framework to be installed and active",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// CATEGORY DATA
// ═══════════════════════════════════════════════════════════════════════════

private val homeCategories = listOf(
    IconifyCategory(
        "icon_pack",
        "Icon Pack",
        "Change system icon pack",
        Icons.Default.Palette,
        7,
        IconifyColors.AuraPrimary
    ),
    IconifyCategory(
        "brightness_bar",
        "Brightness Bar",
        "Customize progress slider",
        Icons.Default.WbSunny,
        8,
        IconifyColors.AuraSecondary
    ),
    IconifyCategory(
        "qs_panel",
        "QS Panel Tiles",
        "Customize QS panel tiles",
        Icons.Default.GridView,
        12,
        IconifyColors.NeonPink
    ),
    IconifyCategory(
        "notification",
        "Notification",
        "Customize notification style",
        Icons.Default.Notifications,
        5,
        IconifyColors.NeonGreen
    )
)

private val tweaksCategories = listOf(
    IconifyCategory(
        "color_engine",
        "Color Engine",
        "Have control over colors",
        Icons.Default.ColorLens,
        15,
        IconifyColors.AuraPrimary
    ),
    IconifyCategory(
        "ui_roundness",
        "UI Roundness",
        "Change corner radius of interface",
        Icons.Default.RoundedCorner,
        8,
        IconifyColors.AuraSecondary
    ),
    IconifyCategory(
        "row_column",
        "Row and Column",
        "Change QS tile rows and columns",
        Icons.Default.ViewModule,
        4,
        IconifyColors.NeonPink
    ),
    IconifyCategory(
        "icon_label",
        "Icon and Label",
        "Change icon and label options",
        Icons.Default.TextFields,
        6,
        IconifyColors.NeonGreen
    ),
    IconifyCategory(
        "qs_tile_size",
        "QS Tile Size",
        "Change height of QS panel tile",
        Icons.Default.AspectRatio,
        3,
        Color(0xFFFF9800)
    ),
    IconifyCategory(
        "qs_panel_margin",
        "QS Panel Margin",
        "Modify quick settings margins",
        Icons.Default.FormatIndentIncrease,
        4,
        Color(0xFF00BCD4)
    ),
    IconifyCategory(
        "statusbar",
        "Statusbar",
        "Modify statusbar interface",
        Icons.Default.SignalCellularAlt,
        12,
        Color(0xFF9C27B0)
    ),
    IconifyCategory(
        "navigation_bar",
        "Navigation Bar",
        "Tweak navigation options",
        Icons.Default.Navigation,
        8,
        Color(0xFFE91E63)
    )
)

private val xposedCategories = listOf(
    IconifyCategory(
        "transparency_blur",
        "Transparency & Blur",
        "Enable QS blur and transparency",
        Icons.Default.BlurOn,
        6,
        IconifyColors.Warning,
        requiresXposed = true
    ),
    IconifyCategory(
        "quick_settings_x",
        "Quick Settings",
        "Tweaks related to QS panel",
        Icons.Default.SettingsApplications,
        10,
        IconifyColors.Warning,
        requiresXposed = true
    ),
    IconifyCategory(
        "themes",
        "Themes",
        "Personalize with themes",
        Icons.Default.Style,
        8,
        IconifyColors.Warning,
        requiresXposed = true
    ),
    IconifyCategory(
        "battery_style",
        "Battery Style",
        "Customize battery icon view",
        Icons.Default.BatteryFull,
        34,
        IconifyColors.Warning,
        requiresXposed = true
    ),
    IconifyCategory(
        "header_image",
        "Header Image",
        "Add custom image on QS panel",
        Icons.Default.Image,
        3,
        IconifyColors.Warning,
        requiresXposed = true
    ),
    IconifyCategory(
        "header_clock",
        "Header Clock",
        "Add custom clock on QS panel",
        Icons.Default.Schedule,
        12,
        IconifyColors.Warning,
        requiresXposed = true
    ),
    IconifyCategory(
        "lockscreen_clock",
        "Lockscreen Clock",
        "Add custom clock on lockscreen",
        Icons.Default.Lock,
        15,
        IconifyColors.Warning,
        requiresXposed = true
    )
)

