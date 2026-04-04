package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

@Composable
fun ImmersiveGateCard(
    config: GateConfig,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onDoubleTap: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onTap() }
    ) {
        if (config.pixelArtResId != null) {
            Image(
                painter = painterResource(id = config.pixelArtResId),
                contentDescription = config.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.radialGradient(listOf(config.glowColor.copy(alpha = 0.3f), Color.Black)))
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = config.title.uppercase(),
                color = config.glowColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = config.description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}
