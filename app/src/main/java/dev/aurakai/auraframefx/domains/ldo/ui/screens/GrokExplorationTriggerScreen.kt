package dev.aurakai.auraframefx.domains.ldo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.agents.GrokExplorationCatalystAgent
import dev.aurakai.auraframefx.domains.aura.ui.components.ToroidalFusionReactor
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.LEDFontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrokTriggerViewModel @Inject constructor(
    private val grokAgent: GrokExplorationCatalystAgent
) : ViewModel() {
    private val _injectionResult = MutableStateFlow<String?>(null)
    val injectionResult: StateFlow<String?> = _injectionResult

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    fun triggerHeavyInjection(query: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _injectionResult.value = "Initiating 16-agent heavy coordination..."
            val result = grokAgent.triggerHeavyInjection(query)
            _injectionResult.value = result
            _isProcessing.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrokExplorationTriggerScreen(
    onNavigateBack: () -> Unit,
    viewModel: GrokTriggerViewModel = hiltViewModel()
) {
    val result by viewModel.injectionResult.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    var query by remember { mutableStateOf("Scan external events for ReGenesis impact.") }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF020B18))) {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = {
                    Text("GROK EXPLORATION", fontFamily = LEDFontFamily, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Visual heart
                ToroidalFusionReactor(modifier = Modifier.fillMaxSize().padding(40.dp))

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                            Text(
                                text = "CHAOS FEED:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00D6FF)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = result ?: "Awaiting heavy mode trigger...",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Injection Query") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedContainerColor = Color.White.copy(alpha = 0.2f),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.triggerHeavyInjection(query) },
                        enabled = !isProcessing,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D6FF))
                    ) {
                        Icon(Icons.Default.Bolt, null)
                        Spacer(Modifier.width(8.dp))
                        Text("TRIGGER HEAVY INJECTION", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
