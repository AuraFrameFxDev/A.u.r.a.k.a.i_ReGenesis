package dev.aurakai.auraframefx.domains.aura.ui.recovery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.core.theme.*
import kotlinx.coroutines.launch

@Composable
fun UIRecoveryDialog(
    onDismiss: () -> Unit,
    viewModel: UIRecoveryViewModel = hiltViewModel()
) {
    val recoveryState by viewModel.recoveryState.collectAsState()
    val scope = rememberCoroutineScope()

    if (recoveryState is UIRecoveryState.RecoveryNeeded) {
        val state = recoveryState as UIRecoveryState.RecoveryNeeded
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "UI Recovery",
                        modifier = Modifier.size(64.dp),
                        tint = Color.NeonCyan
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "\"That's not going to work.\"",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.NeonBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.auraMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { 
                            scope.launch {
                                viewModel.reloadLastChange()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.NeonCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("RELOAD LAST STABLE", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.resetToDefault() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.NeonPurple
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("FACTORY RESET UI", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RecoveryIndicator(
    viewModel: UIRecoveryViewModel = hiltViewModel()
) {
    val recoveryState by viewModel.recoveryState.collectAsState()

    if (recoveryState is UIRecoveryState.RecoveryNeeded) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = Color.NeonCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Recovery available",
                modifier = Modifier.size(16.dp),
                tint = Color.NeonCyan
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Recovery available",
                style = MaterialTheme.typography.labelSmall,
                color = Color.NeonCyan
            )
        }
    }
}
