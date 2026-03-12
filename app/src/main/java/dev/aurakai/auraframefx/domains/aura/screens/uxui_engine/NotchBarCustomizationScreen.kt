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
import androidx.compose.material.icons.filled.AutoFixHigh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.AnimeHUDContainer
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🏔️ NOTCH BAR CUSTOMIZATION SCREEN
 * Modernized with Anime HUD aesthetics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotchBarCustomizationScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var notchBarBgEnabled by remember { mutableStateOf(false) }
    var notchBarBgUri by remember { mutableStateOf<Uri?>(null) }
    var notchBarBgOpacity by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        notchBarBgEnabled = CustomizationPreferences.getNotchBarBackgroundEnabled(context)
        notchBarBgUri = CustomizationPreferences.getNotchBarBackgroundUri(context)
        notchBarBgOpacity = CustomizationPreferences.getNotchBarBackgroundOpacity(context)
    }

    val onSavePreferences: () -> Unit = {
        CustomizationPreferences.saveNotchBarBackgroundSettings(
            context,
            notchBarBgEnabled,
            notchBarBgUri,
            notchBarBgOpacity
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030305))
    ) {
        AnimeHUDContainer(
            title = "NOTCH SYNAPSE",
            description = "RECONFIGURE THE TOP NEURAL TERMINAL. STATUS OVERLAY DEPTH AND GAIN ACTIVE.",
            glowColor = Color(0xFF00FF85)
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
                        Color(0xFF00FF85).copy(alpha = 0.2f)
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
                                "TERMINAL SYNC",
                                fontFamily = LEDFontFamily,
                                color = Color(0xFF00FF85),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Activate status bar neural layer",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = notchBarBgEnabled,
                            onCheckedChange = {
                                notchBarBgEnabled = it
                                onSavePreferences()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00FF85))
                        )
                    }
                }

                if (notchBarBgEnabled) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Slider(
                        value = notchBarBgOpacity,
                        onValueChange = { notchBarBgOpacity = it; onSavePreferences() },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00FF85),
                            activeTrackColor = Color(0xFF00FF85)
                        )
                    )

                    Button(
                        onClick = { /* TODO: Picker */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF85).copy(
                                alpha = 0.1f
                            )
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00FF85))
                    ) {
                        Icon(Icons.Default.AutoFixHigh, null, tint = Color(0xFF00FF85))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SELECT ASSET", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
