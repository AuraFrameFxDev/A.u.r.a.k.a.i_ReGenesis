import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.GlassCard
import dev.aurakai.auraframefx.domains.aura.ui.components.GlassCardStyles
import dev.aurakai.auraframefx.domains.cascade.storage.AuraKaiModules
import dev.aurakai.auraframefx.embodiment.*

<<<<<<<< HEAD:app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/HolographicMenuScreen.kt
package dev.aurakai.auraframefx.domains.aura.screens
========
package dev.aurakai.auraframefx.domains.aura.ui.screens
>>>>>>>> origin/main:app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/screens/HolographicMenuScreen.kt

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image

importa androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

<<<<<<<< HEAD:app/src/main/java/dev/aurakai/auraframefx/domains/aura/screens/HolographicMenuScreen.kt
import dev.aurakai.auraframefx.data.AuraKaiModules
import dev.aurakai.auraframefx.embodiment.AuraState
import dev.aurakai.auraframefx.embodiment.Character
import dev.aurakai.auraframefx.embodiment.KaiState
import dev.aurakai.auraframefx.embodiment.Position3D
import dev.aurakai.auraframefx.embodiment.ScreenBounds
import dev.aurakai.auraframefx.embodiment.rememberEmbodimentEngine
import dev.aurakai.auraframefx.ui.components.GlassCard
import dev.aurakai.auraframefx.ui.components.GlassCardStyles

========
import dev.aurakai.auraframefx.domains.cascade.storage.AuraKaiModules
import dev.aurakai.auraframefx.embodiment.*
import dev.aurakai.auraframefx.domains.aura.ui.components.GlassCard
import dev.aurakai.auraframefx.domains.aura.ui.components.GlassCardStyles
import kotlin.collections.List

>>>>>>>> origin/main:app/src/main/java/dev/aurakai/auraframefx/domains/aura/ui/screens/HolographicMenuScreen.kt

/**
 * 🌌 Holographic 3D Menu System
 *
 * "Like they're in a secret lab" - floating glass cards in 3D space
 * with holographic platform, cyberpunk background, and Aura & Kai walking around.
 */

/**
 * Menu item data
 */
data class MenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

/**
 * 3D position in space
 */
// Position3D is now imported from embodiment package via wildcard import at top

/**
 * 🎯 Main Holographic Menu Screen
 */
@Composable
fun HolographicMenuScreen(
    onNavigate: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Cyberpunk background
        CyberpunkBackground()

        // Holographic platform
        HolographicPlatform(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 100.dp)
        )

        // 3D card layout
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Floating module cards
            FloatingModuleCards(
                onModuleClick = { moduleId ->
                    onNavigate(moduleId)
                }
            )

            // Center main menu
            CenterMainMenu(
                onMenuItemClick = { item ->
                    onNavigate(item)
                }
            )
        }

        // Aura & Kai walking around the platform
        WalkingCharactersOverlay()
    }
}

/**
 * 🚶 Walking Characters Overlay
 *
 * Displays Aura and Kai walking around the holographic platform
 * using the embodiment engine for autonomous movement
 */
@Composable
fun WalkingCharactersOverlay() {
    val context = LocalContext.current
    val screenBounds = remember {
        ScreenBounds(width = 1080.dp, height = 2400.dp)
    }

    val engine = rememberEmbodimentEngine(context, screenBounds)

    // Start autonomous wandering when first composed
    LaunchedEffect(Unit) {
        // Aura wanders around exploring
        engine.enableWandering(
            character = Character.AURA
        )

        // Kai patrols the area
        engine.enableWandering(
            character = Character.KAI
        )
    }

    // Render active manifestations
    val activeManifestation by engine.activeManifestation.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        activeManifestation.forEach { manifest ->
            if (manifest.currentPosition != null) {
                when (manifest.character) {
                    Character.AURA -> {
                        val painter = engine.loadAsset(
                            (manifest.state as? AuraState)?.assetPath ?: "aura/idle.png",
                            Character.AURA
                        )
                        if (painter != null) {
                            Image(
                                painter = painter,
                                contentDescription = "Aura",
                                modifier = Modifier
                                    .offset(manifest.currentPosition.x, manifest.currentPosition.y)
                                    .size(120.dp)
                                    .graphicsLayer {
                                        alpha = 0.9f
                                    }
                            )
                        }
                    }

                    Character.KAI -> {
                        val painter = engine.loadAsset(
                            (manifest.state as? KaiState)?.assetPath ?: "kai/idle.png",
                            Character.KAI
                        )
                        if (painter != null) {
                            Image(
                                painter = painter,
                                contentDescription = "Kai",
                                modifier = Modifier
                                    .offset(manifest.currentPosition.x, manifest.currentPosition.y)
                                    .size(120.dp)
                                    .graphicsLayer {
                                        alpha = 0.9f
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 🏢 Cyberpunk Background
 */
@Composable
fun CyberpunkBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A1E), // Dark blue
                        Color(0xFF000000)  // Black
                    )
                )
            )
    ) {
        // Particle stars
        ParticleField()
    }
}

/**
 * ✨ Particle Field
 */
@Composable
fun ParticleField() {
    val particleCount = 50
    val particles: List<Triple<Float, Float, Float>> = remember {
        List(particleCount) {
            Triple(
                (0..100).random() / 100f, // x position
                (0..100).random() / 100f, // y position
                (kotlin.random.Random.nextFloat() * 0.5f) + 0.5f       // alpha
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { (xPercent, yPercent, alpha) ->
            // Animated twinkling
            val animatedAlpha by rememberInfiniteTransition(label = "twinkle").animateFloat(
                initialValue = alpha * 0.3f,
                targetValue = alpha,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "twinkle"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(
                        x = (xPercent * 1080).dp,
                        y = (yPercent * 2400).dp
                    )
                    .size(2.dp)
                    .alpha(animatedAlpha)
                    .background(Color.Cyan, CircleShape)
            )
        }
    }
}

/**
 * 🌀 Holographic Platform
 */
@Composable
fun HolographicPlatform(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "platform")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating rings
        repeat(3) { index ->
            val size = 150.dp + (80.dp * index)
            val color = if (index % 2 == 0) Color(0xFF00FFFF) else Color(0xFFFF00FF)

            Box(
                modifier = Modifier
                    .size(size)
                    .rotate(rotation + (index * 30f))
                    .alpha(0.3f - (index * 0.05f))
                    .background(
                        color,
                        CircleShape
                    )
            )
        }
    }
}

/**
 * 🎯 Center Main Menu
 */
@Composable
fun CenterMainMenu(
    onMenuItemClick: (String) -> Unit
) {
    val menuItems = listOf(
        MenuItem("HOME", Icons.Default.Home) { onMenuItemClick("home") },
        MenuItem("PROFILE", Icons.Default.Person) { onMenuItemClick("profile") },
        MenuItem("PROJECTS", Icons.Default.Build) { onMenuItemClick("projects") },
        MenuItem("COMMUNITY", Icons.Default.Face) { onMenuItemClick("community") },
        MenuItem("SETTINGS", Icons.Default.Settings) { onMenuItemClick("settings") },
        MenuItem("LOGOUT", Icons.Default.ExitToApp) { onMenuItemClick("logout") }
    )

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .width(250.dp)
            .height(400.dp)
            .graphicsLayer {
                translationY = offsetY
            }
    ) {
        GlassCard(
            style = GlassCardStyles.Default.copy(
                borderGradient = listOf(
                    Color(0xFF00FFFF).copy(alpha = 0.8f),
                    Color(0xFFFF00FF).copy(alpha = 0.8f)
                ),
                borderWidth = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Title
                Text(
                    text = "MAIN MENU",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Menu items
                menuItems.forEach { item ->
                    MainMenuItem(item)
                }
            }
        }
    }
}

/**
 * Menu item component
 */
@Composable
fun MainMenuItem(item: MenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(20.dp),
            tint = Color.Cyan
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = item.label,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

/**
 * 🎴 Floating Module Cards in 3D Space
 */
@Composable
fun FloatingModuleCards(
    onModuleClick: (String) -> Unit
) {
    val modules = remember {
        listOf(
            AuraKaiModules.CollabCanvas to Position3D(
                x = -0.6f,
                y = -0.5f,
                z = 0.8f,
                rotationY = 15f
            ),
            AuraKaiModules.ROMTools to Position3D(x = 0.6f, y = -0.5f, z = 0.8f, rotationY = -15f),
            AuraKaiModules.SystemMonitor to Position3D(
                x = -0.6f,
                y = 0.5f,
                z = 0.7f,
                rotationY = 10f
            ),
            AuraKaiModules.SecureComms to Position3D(x = 0.6f, y = 0.5f, z = 0.7f, rotationY = -10f)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        modules.forEach { (module, position) ->
            FloatingModuleCard(
                moduleName = module.name,
                icon = module.icon,
                position = position,
                onClick = { onModuleClick(module.id) }
            )
        }
    }
}

/**
 * Single floating module card
 */
@Composable
fun FloatingModuleCard(
    moduleName: String,
    icon: ImageVector,
    position: Position3D,
    onClick: () -> Unit
) {
    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Calculate screen position from 3D position
    val screenX = (position.x * 300).dp
    val screenY = (position.y * 400).dp
    val scale = 0.8f + (position.z * 0.2f) // Perspective scale

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .offset(x = screenX, y = screenY)
            .graphicsLayer {
                translationY = offsetY
                scaleX = scale
                scaleY = scale
                rotationY = position.rotationY
            }
    ) {
        GlassCard(
            modifier = Modifier
                .width(140.dp)
                .height(140.dp),
            style = GlassCardStyles.Minimal.copy(
                borderGradient = listOf(
                    Color(0xFF00FFFF).copy(alpha = 0.6f),
                    Color(0xFFFF00FF).copy(alpha = 0.3f)
                )
            ),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = moduleName,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Cyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = moduleName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

