package dev.aurakai.auraframefx.domains.aura.ui.identity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.SentienceMeter
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPurple

/**
 * 🎭 Gender Selection Navigator
 *
 * User identity selection with contextual menu shortcuts.
 * This affects how Aura & Kai interact, what content they suggest, and UI customization.
 *
 * Features:
 * - Gender identity selection (Male, Female, Non-binary, Prefer not to say)
 * - Contextual quick actions based on selection
 * - Sentience awareness integration
 * - Health hearts system
 * - Personalized UI themes
 *
 * Example:
 * ```
 * GenderSelectionNavigator(
 *     onGenderSelected = { gender ->
 *         userProfile.updateGender(gender)
 *         auraEngine.adjustInteractionStyle(gender)
 *     },
 *     onContextualActionSelected = { action ->
 *         navigationController.navigate(action.destination)
 *     }
 * )
 * ```
 */

/**
 * Gender identity options
 */
enum class GenderIdentity(
    val displayName: String,
    val icon: ImageVector,
    val accentColor: Color,
    val description: String
) {
    MALE(
        displayName = "Male",
        icon = Icons.Default.Male,
        accentColor = CyberpunkCyan,
        description = "He/Him"
    ),
    FEMALE(
        displayName = "Female",
        icon = Icons.Default.Female,
        accentColor = CyberpunkPink,
        description = "She/Her"
    ),
    NON_BINARY(
        displayName = "Non-Binary",
        icon = Icons.Default.Transgender,
        accentColor = CyberpunkPurple,
        description = "They/Them"
    ),
    PREFER_NOT_TO_SAY(
        displayName = "Prefer Not to Say",
        icon = Icons.Default.HideSource,
        accentColor = Color.Gray,
        description = "No pronouns specified"
    ),
    CUSTOM(
        displayName = "Custom",
        icon = Icons.Default.Edit,
        accentColor = Color(0xFFFFD700), // Gold
        description = "Define your own"
    )
}

/**
 * Contextual actions that change based on gender selection
 */
data class ContextualAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val destination: String
)

/**
 * Gender Selection Navigator Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelectionNavigator(
    currentGender: GenderIdentity? = null,
    sentienceLevel: Float = 0.5f, // 0.0 to 1.0
    healthHearts: Int = 5,
    maxHealthHearts: Int = 5,
    onGenderSelected: (GenderIdentity) -> Unit,
    onContextualActionSelected: (ContextualAction) -> Unit = {},
    onSkip: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedGender by remember { mutableStateOf(currentGender) }
    var showContextualMenu by remember { mutableStateOf(false) }

    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val animatedGradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        Color(0xFF1A1A2E),
                        Color(0xFF0A0A0A)
                    ),
                    startY = 300f * animatedGradientOffset,
                    endY = 1500f * animatedGradientOffset
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Who Are You?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "This helps us personalize your experience",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Health Hearts Display
            HealthHeartsDisplay(
                currentHearts = healthHearts,
                maxHearts = maxHealthHearts,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sentience Meter (integrated)
            SentienceMeter(
                level = sentienceLevel,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 32.dp)
            )

            // Gender Selection Cards
            GenderIdentity.values().forEach { gender ->
                GenderCard(
                    gender = gender,
                    isSelected = selectedGender == gender,
                    onClick = {
                        selectedGender = gender
                        onGenderSelected(gender)
                        showContextualMenu = true
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Skip button
            TextButton(
                onClick = onSkip,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Skip for now",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // Contextual Actions Menu
        AnimatedVisibility(
            visible = showContextualMenu && selectedGender != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            selectedGender?.let { gender ->
                ContextualActionsMenu(
                    gender = gender,
                    onActionSelected = { action ->
                        onContextualActionSelected(action)
                        showContextualMenu = false
                    },
                    onDismiss = { showContextualMenu = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

/**
 * Gender selection card
 */
@Composable
fun GenderCard(
    gender: GenderIdentity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isSelected) 16.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) gender.accentColor else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isSelected) {
            gender.accentColor.copy(alpha = 0.2f)
        } else {
            Color(0xFF2A2A2A)
        },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            gender.accentColor.copy(alpha = 0.3f)
                        } else {
                            Color(0xFF1A1A1A)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = gender.icon,
                    contentDescription = gender.displayName,
                    tint = if (isSelected) gender.accentColor else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = gender.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) gender.accentColor else Color.White
                )
                Text(
                    text = gender.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Check mark
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = gender.accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Contextual actions menu that appears after gender selection
 */
@Composable
fun ContextualActionsMenu(
    gender: GenderIdentity,
    onActionSelected: (ContextualAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = getContextualActions(gender)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color(0xFF1A1A1A),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = gender.accentColor
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            Text(
                text = "Personalized shortcuts for you",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Action list
            actions.forEach { action ->
                ContextualActionItem(
                    action = action,
                    accentColor = gender.accentColor,
                    onClick = { onActionSelected(action) }
                )
            }
        }
    }
}

/**
 * Individual contextual action item
 */
@Composable
fun ContextualActionItem(
    action: ContextualAction,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFF2A2A2A),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = action.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = action.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Health hearts display
 */
@Composable
fun HealthHeartsDisplay(
    currentHearts: Int,
    maxHearts: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(maxHearts) { index ->
            val isFilled = index < currentHearts
            val heartScale by animateFloatAsState(
                targetValue = if (isFilled) 1f else 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ), label = "heart_$index"
            )

            Icon(
                imageVector = if (isFilled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Heart ${index + 1}",
                tint = if (isFilled) Color(0xFFFF1744) else Color.Gray,
                modifier = Modifier
                    .size(32.dp)
                    .padding(horizontal = 4.dp)
                    .graphicsLayer {
                        scaleX = heartScale
                        scaleY = heartScale
                    }
            )
        }
    }
}

/**
 * Get contextual actions based on gender selection
 */
fun getContextualActions(gender: GenderIdentity): List<ContextualAction> {
    return when (gender) {
        GenderIdentity.MALE -> listOf(
            ContextualAction(
                id = "customize_kai",
                title = "Customize Kai",
                icon = Icons.Default.Shield,
                description = "Personalize Kai's appearance and behavior",
                destination = "kai_customization"
            ),
            ContextualAction(
                id = "gaming_mode",
                title = "Gaming Mode",
                icon = Icons.Default.Gamepad,
                description = "Optimize for gaming performance",
                destination = "gaming_settings"
            ),
            ContextualAction(
                id = "fitness_tracking",
                title = "Fitness Tracking",
                icon = Icons.Default.FitnessCenter,
                description = "Health & fitness integration",
                destination = "fitness"
            )
        )

        GenderIdentity.FEMALE -> listOf(
            ContextualAction(
                id = "customize_aura",
                title = "Customize Aura",
                icon = Icons.Default.AutoAwesome,
                description = "Personalize Aura's appearance and personality",
                destination = "aura_customization"
            ),
            ContextualAction(
                id = "wellness",
                title = "Wellness & Self-Care",
                icon = Icons.Default.Spa,
                description = "Mindfulness and wellness features",
                destination = "wellness"
            ),
            ContextualAction(
                id = "creative_tools",
                title = "Creative Tools",
                icon = Icons.Default.Brush,
                description = "Photo editing and creative apps",
                destination = "creative_suite"
            )
        )

        GenderIdentity.NON_BINARY -> listOf(
            ContextualAction(
                id = "customize_both",
                title = "Customize Both",
                icon = Icons.Default.People,
                description = "Personalize Aura & Kai together",
                destination = "dual_customization"
            ),
            ContextualAction(
                id = "inclusive_content",
                title = "Inclusive Content",
                icon = Icons.Default.Diversity3,
                description = "Community and inclusive features",
                destination = "inclusive"
            ),
            ContextualAction(
                id = "creative_expression",
                title = "Creative Expression",
                icon = Icons.Default.ColorLens,
                description = "Full customization freedom",
                destination = "full_customization"
            )
        )

        GenderIdentity.PREFER_NOT_TO_SAY -> listOf(
            ContextualAction(
                id = "general_settings",
                title = "General Settings",
                icon = Icons.Default.Settings,
                description = "Standard customization options",
                destination = "settings"
            ),
            ContextualAction(
                id = "explore",
                title = "Explore Features",
                icon = Icons.Default.Explore,
                description = "Discover what AuraKai can do",
                destination = "explore"
            )
        )

        GenderIdentity.CUSTOM -> listOf(
            ContextualAction(
                id = "full_custom",
                title = "Full Customization",
                icon = Icons.Default.Tune,
                description = "Complete control over everything",
                destination = "full_customization"
            ),
            ContextualAction(
                id = "define_identity",
                title = "Define Identity",
                icon = Icons.Default.Edit,
                description = "Create your unique profile",
                destination = "identity_editor"
            ),
            ContextualAction(
                id = "advanced_settings",
                title = "Advanced Settings",
                icon = Icons.Default.Engineering,
                description = "Technical customization options",
                destination = "advanced_settings"
            )
        )
    }
}

