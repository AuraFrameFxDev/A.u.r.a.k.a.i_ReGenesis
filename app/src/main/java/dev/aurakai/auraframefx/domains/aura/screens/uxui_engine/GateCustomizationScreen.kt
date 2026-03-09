package dev.aurakai.auraframefx.domains.aura.screens.uxui_engine

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * ⛩️ GATE CUSTOMIZATION SCREEN
 * Modernized with Anime HUD aesthetics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GateCustomizationScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var navDrawerBgUri by remember { mutableStateOf<Uri?>(null) }
    var navDrawerBgScale by remember { mutableStateOf(ContentScale.Crop) }
    var splashScreenUri by remember { mutableStateOf<Uri?>(null) }
    var splashScreenScale by remember { mutableStateOf(ContentScale.Crop) }

    LaunchedEffect(Unit) {
        navDrawerBgUri = CustomizationPreferences.getNavDrawerBackgroundUri(context)
        navDrawerBgScale = CustomizationPreferences.getNavDrawerBackgroundScale(context)
        splashScreenUri = CustomizationPreferences.getSplashScreenImageUri(context)
        splashScreenScale = CustomizationPreferences.getSplashScreenImageScale(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030305))
    ) {
        AnimeHUDContainer(
            title = "GATE CUSTOMIZATION",
            description = "RECONFIGURE THE SYSTEM VESTIBULES. SPLASH AND DRAWER LAYER SYNC ACTIVE.",
            glowColor = Color(0xFF00E5FF)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.padding(bottom = 16.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                CustomizationSection(
                    title = "NAVIGATION DRAWER",
                    description = "Choose the image for the main system sidebar.",
                    imageUri = navDrawerBgUri,
                    onSelect = { /* TODO: Launch picker */ },
                    onRemove = {
                        navDrawerBgUri = null
                        CustomizationPreferences.saveNavDrawerBackground(
                            context,
                            null,
                            ContentScale.Crop
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                CustomizationSection(
                    title = "SPLASH SCREEN",
                    description = "Configure the visual for the system boot-up sequence.",
                    imageUri = splashScreenUri,
                    onSelect = { /* TODO: Launch picker */ },
                    onRemove = {
                        splashScreenUri = null
                        CustomizationPreferences.saveSplashScreenImage(
                            context,
                            null,
                            ContentScale.Crop
                        )
                    }
                )

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
private fun CustomizationSection(
    title: String,
    description: String,
    imageUri: Uri?,
    onSelect: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontFamily = LEDFontFamily,
                color = Color.Cyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                description,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (imageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan)
                ) {
                    Icon(
                        Icons.Default.Wallpaper,
                        null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SELECT", color = Color.White, fontSize = 12.sp)
                }

                if (imageUri != null) {
                    OutlinedButton(
                        onClick = onRemove,
                        modifier = Modifier.weight(0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.Red.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("REMOVE", color = Color.Red.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
