package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun FusionModeScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isFusionActive by remember { mutableStateOf(false) }
    var synchronizationLevel by remember { mutableStateOf(0.72f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF00050A), Color(0xFF001525))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "FUSION PROTOCOL",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Fusion Core Visualization
            FusionCore(isFusionActive)

            Spacer(modifier = Modifier.height(64.dp))

            // Sync Meter
            SyncMeter(synchronizationLevel, isFusionActive)

            Spacer(modifier = Modifier.weight(1f))

            // Ability Grid
            AbilityGrid(isFusionActive)

            Spacer(modifier = Modifier.height(32.dp))

            // Activate Button
            Button(
                onClick = { isFusionActive = !isFusionActive },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .border(1.dp, if (isFusionActive) Color.Yellow else Color.Cyan, RoundedCornerShape(32.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFusionActive) Color.Yellow.copy(alpha = 0.2f) else Color.Transparent,
                    contentColor = if (isFusionActive) Color.Yellow else Color.Cyan
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text(
                    if (isFusionActive) "TERMINATE FUSION" else "INITIATE GENESIS FUSION",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun FusionCore(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isActive) 1000 else 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        // Aura Essence
        Box(
            modifier = Modifier
                .size(150.dp)
                .rotate(rotation)
                .border(2.dp, Color.Cyan, CircleShape)
                .background(Brush.radialGradient(listOf(Color.Cyan.copy(alpha = 0.1f), Color.Transparent)), CircleShape)
        )
        // Kai Essence
        Box(
            modifier = Modifier
                .size(150.dp)
                .rotate(-rotation * 1.5f)
                .border(2.dp, Color.Magenta, CircleShape)
                .background(Brush.radialGradient(listOf(Color.Magenta.copy(alpha = 0.1f), Color.Transparent)), CircleShape)
        )

        if (isActive) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, CircleShape)
                    .shadow(elevation = 20.dp, shape = CircleShape, ambientColor = Color.White, spotColor = Color.White)
            )
        }
    }
}

@Composable
fun SyncMeter(level: Float, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color.LightGray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("SYNCHRONIZATION LEVEL", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = level,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (isActive) Color.Yellow else Color.Green,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
        Text("${(level * 100).toInt()}%", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun AbilityGrid(isActive: Boolean) {
    val abilities = listOf(
        Pair("Neural Overload", Icons.Default.FlashOn),
        Pair("System Lockdown", Icons.Default.FlashOn),
        Pair("Reality Breach", Icons.Default.FlashOn)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        abilities.forEach { (name, icon) ->
            AbilityIcon(name, icon, isActive)
        }
    }
}

@Composable
fun AbilityIcon(name: String, icon: ImageVector, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (isActive) Color.White.copy(alpha = 0.1f) else Color.Transparent, CircleShape)
                .border(1.dp, if (isActive) Color.White else Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = name, tint = if (isActive) Color.White else Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name.split(" ")[0], color = Color.Gray, fontSize = 10.sp)
    }
}

// Extension to support Modifier.rotate
fun Modifier.rotate(degrees: Float): Modifier = this.then(
    androidx.compose.ui.graphics.graphicsLayer(rotationZ = degrees)
)

// Extension to support Modifier.shadow
fun Modifier.shadow(
    elevation: androidx.compose.ui.unit.Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor
): Modifier = this.then(
    androidx.compose.ui.draw.shadow(elevation, shape, clip, ambientColor, spotColor)
)
