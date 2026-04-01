package dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PandoraConsentDialog(
    tierName: String,
    riskText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var unlockText by remember { mutableStateOf("") }
    val isTextCorrect = unlockText.uppercase() == "UNLOCK"
    
    var holdProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0A0A18))
                .border(2.dp, Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF4444))), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "CRITICAL AUTHORIZATION REQUIRED",
                    color = Color(0xFFFF4444),
                    fontFamily = LEDFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "UNLOCK TIER: ${tierName.uppercase()}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        riskText,
                        color = Color(0xFFFF8A80),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "TYPE 'UNLOCK' TO CONFIRM",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
                
                OutlinedTextField(
                    value = unlockText,
                    onValueChange = { unlockText = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = LEDFontFamily
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Hold to Confirm Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isTextCorrect) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.1f))
                        .pointerInput(isTextCorrect) {
                            if (!isTextCorrect) return@pointerInput
                            detectTapGestures(
                                onPress = {
                                    val job = scope.launch {
                                        val startTime = System.currentTimeMillis()
                                        while (System.currentTimeMillis() - startTime < 3000) {
                                            holdProgress = (System.currentTimeMillis() - startTime) / 3000f
                                            delay(16)
                                        }
                                        holdProgress = 1f
                                        onConfirm()
                                    }
                                    tryAwaitRelease()
                                    job.cancel()
                                    holdProgress = 0f
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Progress Fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(holdProgress)
                            .fillMaxHeight()
                            .background(Color(0xFFFFD700).copy(alpha = 0.3f))
                            .align(Alignment.CenterStart)
                    )
                    
                    Text(
                        if (!isTextCorrect) "ENTER 'UNLOCK' FIRST" else if (holdProgress > 0f) "HOLDING..." else "HOLD 3s TO OPEN BOX",
                        color = if (isTextCorrect) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("ABORT", color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}

