package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.screens.GenderIdentity
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

/**
 * 🎮 System Journal - User Profile & Menu
 *
 * Pixel art style user profile selection combined with menu options.
 * This is the main user menu card in Gate Navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemJournalScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Boolean
) {
    var selectedGender by remember { mutableStateOf<GenderIdentity?>(GenderIdentity.KAI) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1A)) // Deep blue-black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with retro border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Cyan, Color(0xFF00FFFF))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SYSTEM JOURNAL",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        color = Color.Cyan
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Trophy Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏆",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "USER PROFILE SELECTION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Character Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Male Character
                        CharacterCard(
                            identity = GenderIdentity.KAI,
                            isSelected = selectedGender == GenderIdentity.KAI,
                            onClick = { selectedGender = GenderIdentity.KAI }
                        )

                        // Female Character
                        CharacterCard(
                            identity = GenderIdentity.AURA,
                            isSelected = selectedGender == GenderIdentity.AURA,
                            onClick = { selectedGender = GenderIdentity.AURA }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gender Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GenderLabel(
                            "MALE",
                            GenderIdentity.KAI,
                            selectedGender == GenderIdentity.KAI
                        )
                        GenderLabel(
                            "FEMALE",
                            GenderIdentity.AURA,
                            selectedGender == GenderIdentity.AURA
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Options Grid
            Text(
                text = "MENU OPTIONS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color.Cyan
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(menuOptions) { option ->
                    MenuOptionCard(
                        option = option,
                        onClick = {
                            when (option.route) {
                                "gender_selection" -> {
                                    // Navigate to full gender selection
                                    navController.navigate(ReGenesisNavHost.GenderSelection.route)
                                }

                                else -> {
                                    navController.navigate(option.route)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Character Card (Pixel Art Style with Frame Animation)
 */
@Composable
fun CharacterCard(
    identity: GenderIdentity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "character_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Jump animation state for male character
    var isJumping by remember { mutableStateOf(false) }
    var currentFrame by remember { mutableStateOf(0) }

    // Frame animation for jump (5 frames for male, 3 frames for female)
    LaunchedEffect(isJumping) {
        if (isJumping) {
            if (identity == GenderIdentity.KAI) {
                // Male: 5 frames
                for (frame in 0..4) {
                    currentFrame = frame
                    kotlinx.coroutines.delay(100) // 100ms per frame
                }
            } else if (identity == GenderIdentity.AURA) {
                // Female: 3 frames (with bounce back)
                for (frame in 0..4) {
                    currentFrame = frame
                    kotlinx.coroutines.delay(100) // 100ms per frame
                }
            }
            currentFrame = 0 // Reset to idle
            isJumping = false
        }
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(
                            identity.primaryColor.copy(alpha = 0.3f),
                            Color(0xFF1A1A2E)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A2A3E),
                            Color(0xFF1A1A2E)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) identity.primaryColor.copy(alpha = glowAlpha) else Color.Gray.copy(
                    alpha = 0.3f
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                onClick()
                isJumping = true // Trigger animation for both characters
            },
        contentAlignment = Alignment.Center
    ) {
        // Male character with frame-by-frame animation
        if (identity == GenderIdentity.KAI) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val frameResId = when (currentFrame) {
                0 -> context.resources.getIdentifier(
                    "gemini_generated_image_yceye4yceye4ycey",
                    "drawable",
                    context.packageName
                )

                1 -> context.resources.getIdentifier(
                    "gemini_generated_image_selr70selr70selr",
                    "drawable",
                    context.packageName
                )

                2 -> context.resources.getIdentifier(
                    "gemini_generated_image_wm2k3kwm2k3kwm2k",
                    "drawable",
                    context.packageName
                )

                3 -> context.resources.getIdentifier(
                    "gemini_generated_image_nudtwdnudtwdnudt",
                    "drawable",
                    context.packageName
                )

                4 -> context.resources.getIdentifier(
                    "gemini_generated_image_kjqxokkjqxokkjqx",
                    "drawable",
                    context.packageName
                )

                else -> context.resources.getIdentifier(
                    "gemini_generated_image_yceye4yceye4ycey",
                    "drawable",
                    context.packageName
                )
            }

            if (frameResId != 0) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = frameResId),
                    contentDescription = "Male Character",
                    modifier = Modifier.fillMaxSize(0.9f)
                )
            } else {
                // Fallback to emoji if images not found
                Text(
                    text = identity.icon,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 64.sp
                )
            }
        } else if (identity == GenderIdentity.AURA) {
            // Female character with frame-by-frame animation
            val context = androidx.compose.ui.platform.LocalContext.current
            val frameResId = when (currentFrame) {
                0 -> context.resources.getIdentifier(
                    "gemini_generated_image_qt4s1fqt4s1fqt4s",
                    "drawable",
                    context.packageName
                )

                1 -> context.resources.getIdentifier(
                    "gemini_generated_image_mudazwmudazwmuda",
                    "drawable",
                    context.packageName
                )

                2 -> context.resources.getIdentifier(
                    "gemini_generated_image_q4abvqq4abvqq4ab",
                    "drawable",
                    context.packageName
                )

                3 -> context.resources.getIdentifier(
                    "gemini_generated_image_mudazwmudazwmuda",
                    "drawable",
                    context.packageName
                )

                4 -> context.resources.getIdentifier(
                    "gemini_generated_image_qt4s1fqt4s1fqt4s",
                    "drawable",
                    context.packageName
                )

                else -> context.resources.getIdentifier(
                    "gemini_generated_image_qt4s1fqt4s1fqt4s",
                    "drawable",
                    context.packageName
                )
            }

            if (frameResId != 0) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = frameResId),
                    contentDescription = "Female Character",
                    modifier = Modifier.fillMaxSize(0.9f)
                )
            } else {
                // Fallback to emoji if images not found
                Text(
                    text = identity.icon,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 64.sp
                )
            }
        } else {
            // Fallback for other identities
            Text(
                text = identity.icon,
                style = MaterialTheme.typography.displayLarge,
                fontSize = 64.sp
            )
        }

        // Holographic platform effect
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                identity.primaryColor.copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Gender Label
 */
@Composable
fun GenderLabel(
    text: String,
    identity: GenderIdentity,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) identity.primaryColor.copy(alpha = 0.2f) else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isSelected) identity.primaryColor else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isSelected) identity.primaryColor else Color.Gray
        )
    }
}

/**
 * Menu Option Card
 */
@Composable
fun MenuOptionCard(
    option: MenuOption,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
            .border(1.dp, option.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = option.label,
            tint = option.color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = option.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Menu Option Data Class
 */
data class MenuOption(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

/**
 * Menu Options List
 */
private val menuOptions = listOf(
    MenuOption("Oracle Cloud", Icons.Default.Folder, Color(0xFFFFD700), "oracle_cloud_storage"),
    MenuOption("Agent Bridge", Icons.Default.Hub, Color(0xFF00FFFF), "agent_bridge_hub"),
    MenuOption("Sovereign Mod", Icons.Default.Build, Color(0xFFFF00FF), "module_manager"),
    MenuOption("Sentient Shell", Icons.Default.Computer, Color(0xFF00FFFF), "neural_network"),
    MenuOption("Monitoring", Icons.Default.Insights, Color(0xFF00FFD4), "monitoring_huds"),
    MenuOption("Nexus Hub", Icons.Default.Hub, Color(0xFF7B2FFF), "agent_nexus_gate"),
    MenuOption("Deep Shield", Icons.Default.Shield, Color(0xFFFF00FF), "security_center"),
    MenuOption("Recovery", Icons.Default.Settings, Color(0xFF00E5FF), "recovery_tools")
)
