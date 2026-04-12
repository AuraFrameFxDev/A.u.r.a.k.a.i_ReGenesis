package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.core.NativeLib
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LdoDbgVerifierScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var isVerifying by remember { mutableStateOf(false) }
    var verificationStatus by remember { mutableStateOf("IDLE") }
    var statusMessage by remember { mutableStateOf("Pending sovereignty check...") }
    
    val infiniteTransition = rememberInfiniteTransition(label = "verify")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "pulse"
    )

    fun startVerification() {
        scope.launch {
            isVerifying = true
            verificationStatus = "ANALYZING"
            statusMessage = "Hooking native process space..."
            delay(1500)
            
            NativeLib.enableNativeHooksSafe()
            
            statusMessage = "Checking kernel shield substrate..."
            delay(1000)
            
            val isKernelActive = NativeLib.isKernelShieldActive()
            
            if (isKernelActive) {
                verificationStatus = "VERIFIED"
                statusMessage = "SOVEREIGNTY CONFIRMED. LDO-DBG Active."
            } else {
                verificationStatus = "WARNING"
                statusMessage = "Kernel Shield inactive. Proceed with caution."
            }
            isVerifying = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020208))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF00FF41))
                }
                Text(
                    "LDO-DBG VERIFIER",
                    fontFamily = LEDFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF41)
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(64.dp))

            // Central Shield Icon
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                when (verificationStatus) {
                                    "VERIFIED" -> Color(0xFF00FF41).copy(alpha = 0.2f)
                                    "WARNING" -> Color.Yellow.copy(alpha = 0.2f)
                                    "ANALYZING" -> Color.Cyan.copy(alpha = 0.2f)
                                    else -> Color.Gray.copy(alpha = 0.1f)
                                },
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        2.dp,
                        when (verificationStatus) {
                            "VERIFIED" -> Color(0xFF00FF41)
                            "WARNING" -> Color.Yellow
                            "ANALYZING" -> Color.Cyan
                            else -> Color.Gray.copy(alpha = 0.5f)
                        }.copy(alpha = pulse),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = when (verificationStatus) {
                        "VERIFIED" -> Color(0xFF00FF41)
                        "WARNING" -> Color.Yellow
                        "ANALYZING" -> Color.Cyan
                        else -> Color.Gray
                    }
                )
            }

            Spacer(Modifier.height(48.dp))

            Text(
                verificationStatus,
                fontFamily = LEDFontFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = when (verificationStatus) {
                    "VERIFIED" -> Color(0xFF00FF41)
                    "WARNING" -> Color.Yellow
                    "ANALYZING" -> Color.Cyan
                    else -> Color.White
                }
            )

            Text(
                statusMessage,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(Modifier.weight(1f))

            if (!isVerifying && verificationStatus != "VERIFIED") {
                Button(
                    onClick = { startVerification() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF41).copy(alpha = 0.1f),
                        contentColor = Color(0xFF00FF41)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color(0xFF00FF41), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("INITIATE VERIFICATION", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            } else if (isVerifying) {
                CircularProgressIndicator(color = Color(0xFF00FF41))
            } else if (verificationStatus == "VERIFIED") {
                Text(
                    "SYSTEM VERIFIED",
                    color = Color(0xFF00FF41),
                    fontWeight = FontWeight.Bold,
                    fontFamily = LEDFontFamily
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
