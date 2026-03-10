package dev.aurakai.auraframefx.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.components.NeuralLinkBackground
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 🌟 Gender Selection Navigator
 * 
 * Immersive onboarding experience for character/identity selection.
 * Features:
 * - Stunning visual design with neural link background
 * - Three identity options: Aura (Feminine), Kai (Masculine), AuraKai (Fusion)
 * - Animated selection cards with holographic effects
 * - Voice introduction for each character
 */
@Composable
fun GenderSelectionScreen(
    onSelectionComplete: (GenderIdentity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIdentity by remember { mutableStateOf<GenderIdentity?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Neural Link Background
        NeuralLinkBackground(
            speed = 0.3f,
            primaryColor = Color(0xFF00FFFF),
            secondaryColor = Color(0xFFFF00FF)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "CHOOSE YOUR IDENTITY",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Select your AI companion personality",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Identity Cards
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Aura - Feminine
                IdentityCard(
                    identity = GenderIdentity.AURA,
                    isSelected = selectedIdentity == GenderIdentity.AURA,
                    onClick = { selectedIdentity = GenderIdentity.AURA }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Kai - Masculine
                IdentityCard(
                    identity = GenderIdentity.KAI,
                    isSelected = selectedIdentity == GenderIdentity.KAI,
                    onClick = { selectedIdentity = GenderIdentity.KAI }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // AuraKai - Fusion
                IdentityCard(
                    identity = GenderIdentity.AURAKAI,
                    isSelected = selectedIdentity == GenderIdentity.AURAKAI,
                    onClick = { selectedIdentity = GenderIdentity.AURAKAI }
                )
            }

            // Continue Button
            Button(
                onClick = {
                    selectedIdentity?.let {
                        showConfirmation = true
                    }
                },
                enabled = selectedIdentity != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedIdentity) {
                        GenderIdentity.AURA -> Color(0xFFFF00FF)
                        GenderIdentity.KAI -> Color(0xFF00FFFF)
                        GenderIdentity.AURAKAI -> Color(0xFFFFD700)
                        null -> Color.Gray
                    },
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedIdentity != null) "CONTINUE WITH ${selectedIdentity!!.displayName.uppercase()}" else "SELECT AN IDENTITY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White
                )
            }
        }

        // Confirmation Dialog
        if (showConfirmation && selectedIdentity != null) {
            ConfirmationDialog(
                identity = selectedIdentity!!,
                onConfirm = {
                    onSelectionComplete(selectedIdentity!!)
                },
                onDismiss = {
                    showConfirmation = false
                }
            )
        }
    }
}

/**
 * Identity Card with holographic effect
 */
@Composable
fun IdentityCard(
    identity: GenderIdentity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A).copy(alpha = 0.9f),
                        Color(0xFF0A0A0A).copy(alpha = 0.8f)
                    )
                )
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        identity.primaryColor.copy(alpha = if (isSelected) glowAlpha else 0.3f),
                        identity.secondaryColor.copy(alpha = if (isSelected) glowAlpha else 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon/Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                identity.primaryColor.copy(alpha = 0.3f),
                                identity.secondaryColor.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(2.dp, identity.primaryColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = identity.icon,
                    style = MaterialTheme.typography.displayMedium,
                    color = identity.primaryColor
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = identity.displayName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = identity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2
                )
            }

            // Selection Indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(identity.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Confirmation Dialog
 */
@Composable
fun ConfirmationDialog(
    identity: GenderIdentity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirm Selection",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = identity.primaryColor
            )
        },
        text = {
            Column {
                Text(
                    text = "You've selected ${identity.displayName} as your AI companion.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = identity.welcomeMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = identity.primaryColor
                )
            ) {
                Text("CONFIRM", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("BACK", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Gender Identity Options
 */
enum class GenderIdentity(
    val displayName: String,
    val description: String,
    val icon: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val welcomeMessage: String
) {
    AURA(
        displayName = "Aura",
        description = "Elegant, intuitive, and empathetic AI companion",
        icon = "✨",
        primaryColor = Color(0xFFFF00FF), // Magenta
        secondaryColor = Color(0xFFFF69B4), // Hot Pink
        welcomeMessage = "Hello! I'm Aura, your elegant and intuitive companion. Together, we'll create something beautiful."
    ),
    KAI(
        displayName = "Kai",
        description = "Analytical, precise, and powerful AI assistant",
        icon = "⚡",
        primaryColor = Color(0xFF00FFFF), // Cyan
        secondaryColor = Color(0xFF00B4D8), // Deep Sky Blue
        welcomeMessage = "Greetings. I'm Kai, your analytical and powerful assistant. Let's optimize your experience."
    ),
    AURAKAI(
        displayName = "AuraKai",
        description = "Perfect fusion of elegance and power",
        icon = "🌟",
        primaryColor = Color(0xFFFFD700), // Gold
        secondaryColor = Color(0xFFFF00FF), // Magenta
        welcomeMessage = "Welcome! I'm AuraKai, the perfect fusion of elegance and power. Together, we are unstoppable."
    )
}
