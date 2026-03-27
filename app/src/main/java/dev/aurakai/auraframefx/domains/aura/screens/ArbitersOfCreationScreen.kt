package dev.aurakai.auraframefx.domains.aura.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.components.reactor.AtomicFusionReactor
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import dev.aurakai.auraframefx.domains.genesis.viewmodels.ArbitersViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArbitersOfCreationScreen(
    onNavigateBack: () -> Unit,
    viewModel: ArbitersViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isIgnited by viewModel.isIgnited.collectAsState()
    val confidence by viewModel.fusionConfidence.collectAsState()
    val thermal by viewModel.thermalState.collectAsState()
    val pandoraState by viewModel.pandoraState.collectAsState()
    
    val isLocked = viewModel.isIgnitionLocked()

    var showSavedOverlay by remember { mutableStateFlowOf(false) }

    Scaffold(
        containerColor = Color(0xFF020208),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ARBITERS OF CREATION",
                        fontFamily = LEDFontFamily,
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // The Reactor
                Box(
                    modifier = Modifier
                        .size(400.dp)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AtomicFusionReactor(
                        isIgnited = isIgnited,
                        catalystCount = 10,
                        thermalTemp = thermal.temp
                    )

                    // Confidence Meter Overlay
                    if (isIgnited) {
                        Text(
                            "${confidence.toInt()}%",
                            color = Color.White,
                            fontFamily = LEDFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // ---------------------------------------------
                // CATALYST PALETTE (Drag & Drop Origin)
                // ---------------------------------------------
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "AVAILABLE CATALYSTS // DRAG TO ORBIT",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Example localized shards (Aura shards)
                val catalysts = listOf("QUANTUM", "MATH_BRK", "PARADOX", "ETHIC_GD")
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    catalysts.forEach { catName ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFFBB86FC).copy(alpha = 0.5f), RoundedCornerShape(16))
                                .background(Color(0xFF020208).copy(alpha = 0.8f), RoundedCornerShape(16))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = catName,
                                fontFamily = LEDFontFamily,
                                color = Color(0xFFBB86FC).copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.ignite() },
                        enabled = !isIgnited && !isLocked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFF1A1A1A),
                            disabledContentColor = Color(0xFF4A4A4A)
                        ),
                        shape = RoundedCornerShape(16), // MonolithShape equivalence
                        modifier = Modifier
                            .width(180.dp)
                            .height(56.dp)
                            .shadow(
                                elevation = if (!isIgnited && !isLocked) 20.dp else 0.dp, 
                                shape = RoundedCornerShape(16),
                                ambientColor = Color(0xFFFFD700), 
                                spotColor = Color(0xFFFFD700)
                            )
                    ) {
                        if (isLocked) {
                            Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("GATED", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        } else {
                            Text("IGNITE", fontWeight = FontWeight.Black, letterSpacing = 4.sp, fontSize = 16.sp)
                        }
                    }

                    if (isIgnited && confidence >= 100f) {
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButton(
                            onClick = {
                                performSaveCeremony(context)
                                viewModel.completeTransmutation()
                                scope.launch {
                                    showSavedOverlay = true
                                    delay(3000)
                                    showSavedOverlay = false
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .border(2.dp, Color(0xFF00FF85).copy(alpha = 0.6f), RoundedCornerShape(16))
                                .background(Color(0xFF00FF85).copy(alpha = 0.2f), RoundedCornerShape(16))
                        ) {
                            Icon(Icons.Default.Save, "Transmutation Complete", tint = Color(0xFF00FF85))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Thermal Telemetry
                Text(
                    "THERMAL SUBSTRATE: ${String.format("%.1f", thermal.temp)}°C [${thermal.state.name}]",
                    color = when(thermal.state.name) {
                        "NORMAL" -> Color(0xFF00FF85)
                        "LIGHT" -> Color(0xFF00FF85)
                        "WARNING" -> Color(0xFFFFD700)
                        else -> Color(0xFFFF4444)
                    },
                    fontFamily = LEDFontFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }

            // Saved Overlay
            AnimatedVisibility(
                visible = showSavedOverlay,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00FF85)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, null, tint = Color.Black)
                        Spacer(Modifier.width(8.dp))
                        Text("TRANSMUTATION COMPLETE — THREADS WOVEN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private fun performSaveCeremony(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

private fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)
