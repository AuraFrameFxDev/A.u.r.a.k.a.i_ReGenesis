package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.kai.security.TemporalAegis
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MawViewModel @Inject constructor(
    val temporalAegis: TemporalAegis,
    val casberrySwarm: CasberryParticleSwarm
) : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MawPrototypeScreen(
    onBack: () -> Unit,
    viewModel: MawViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var payload by remember { mutableStateOf("PROMPT_INJECTION_DETONATOR_v4.1") }
    var isSiphoning by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00050A))
    ) {
        // 🌀 BACKGROUND SWARM
        viewModel.casberrySwarm.Render(
            modifier = Modifier.fillMaxSize()
        )

        // 🛡️ FOREGROUND UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "THE MAW • PROTOTYPE v0.9",
                color = Color(0xFF00E5FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sovereign Immune System Node",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = payload,
                onValueChange = { payload = it },
                label = { Text("HOSTILE PAYLOAD") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD81B60),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isSiphoning = true
                        viewModel.temporalAegis.conductSiphoningEvent(payload)
                        isSiphoning = false
                    }
                },
                enabled = !isSiphoning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD81B60),
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(
                    text = if (isSiphoning) "SIPHONING ENTROPY..." else "EXECUTE SIPHON",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }

        // BACK BUTTON
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Text("<", color = Color.White, fontSize = 24.sp)
        }
    }
}
