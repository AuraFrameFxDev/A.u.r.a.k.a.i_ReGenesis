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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🎛️ QUICK SETTINGS CUSTOMIZATION SCREEN
 * Modernized with Anime HUD aesthetics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSettingsCustomizationScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var customQsBackgroundEnabled by remember { mutableStateOf(false) }
    var customQsBackgroundUri by remember { mutableStateOf<Uri?>(null) }
    var customQsBackgroundOpacity by remember { mutableStateOf(1f) }
    var customQsBackgroundBlendMode by remember { mutableStateOf("SrcOver") }

    LaunchedEffect(Unit) {
        customQsBackgroundEnabled = CustomizationPreferences.getCustomQsBackgroundEnabled(context)
        customQsBackgroundUri = CustomizationPreferences.getCustomQsBackgroundUri(context)
        customQsBackgroundOpacity = CustomizationPreferences.getCustomQsBackgroundOpacity(context)
        customQsBackgroundBlendMode =
            CustomizationPreferences.getCustomQsBackgroundBlendMode(context)
    }

    val onSavePreferences: () -> Unit = {
        CustomizationPreferences.saveCustomQsBackgroundSettings(
            context,
            customQsBackgroundEnabled,
            customQsBackgroundUri,
            customQsBackgroundOpacity,
            customQsBackgroundBlendMode
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030305))
    ) {
        AnimeHUDContainer(
            title = "QS CORE SYNC",
            description = "RECONFIGURE THE QUICK SETTINGS NEURAL LAYER. BLEND MODES AND OPACITY OVERRIDE ACTIVE.",
            glowColor = Color(0xFFFF00FF)
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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFFF00FF).copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "CORE SYNC",
                                fontFamily = LEDFontFamily,
                                color = Color(0xFFFF00FF),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Activate custom background layer",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = customQsBackgroundEnabled,
                            onCheckedChange = {
                                customQsBackgroundEnabled = it
                                onSavePreferences()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF00FF))
                        )
                    }
                }

                if (customQsBackgroundEnabled) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "VISUAL PARAMETERS",
                        fontFamily = LEDFontFamily,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    Slider(
                        value = customQsBackgroundOpacity,
                        onValueChange = { customQsBackgroundOpacity = it; onSavePreferences() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF00FF),
                            activeTrackColor = Color(0xFFFF00FF)
                        )
                    )
                    Text(
                        "Opacity: ${(customQsBackgroundOpacity * 100).toInt()}%",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* TODO: Picker */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF00FF).copy(
                                alpha = 0.1f
                            )
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF00FF))
                    ) {
                        Icon(Icons.Default.Palette, null, tint = Color(0xFFFF00FF))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CHANGE ASSET", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
