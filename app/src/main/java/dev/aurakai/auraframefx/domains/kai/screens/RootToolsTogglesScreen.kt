package dev.aurakai.auraframefx.domains.kai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import dev.aurakai.auraframefx.domains.aura.ui.screens.BrutalistCard
import dev.aurakai.auraframefx.domains.aura.ui.screens.SettingsSectionHeader
import dev.aurakai.auraframefx.domains.aura.ui.screens.SettingsToggleCard
import dev.aurakai.auraframefx.domains.kai.RootShellService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootToolsTogglesScreen(
    onNavigateBack: () -> Unit,
    rootShellService: RootShellService // Or use a ViewModel
) {
    val shellStatus by rootShellService.shellStatus.collectAsState()
    val scope = rememberCoroutineScope()
    var terminalOutput by remember { mutableStateOf("Ready for system operations...") }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0A0000), Color(0xFF1A0505), Color(0xFF000000))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ROOT AUTHORITY", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Red
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(bgGradient).padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    SettingsSectionHeader("SYSTEM PERMISSIONS")
                }

                item {
                    AuthorityStatusCard(status = shellStatus)
                }

                item {
                    SettingsSectionHeader("QUICK ACTIONS")
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton(
                            text = "REBOOT",
                            icon = Icons.Default.PowerSettingsNew,
                            color = Color.Red,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    val result = rootShellService.executeCommand("reboot")
                                    terminalOutput = if (result.isSuccess) "Rebooting..." else "Error: ${result.error}"
                                }
                            }
                        )
                        ActionButton(
                            text = "UI RESTART",
                            icon = Icons.Default.Refresh,
                            color = Color.Yellow,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    val result = rootShellService.executeCommand("pkill -l KILL com.android.systemui")
                                    terminalOutput = if (result.isSuccess) "SystemUI restarted" else "Error: ${result.error}"
                                }
                            }
                        )
                    }
                }

                item {
                    SettingsSectionHeader("TERMINAL OUTPUT")
                }

                item {
                    BrutalistCard(accentColor = Color.Gray) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.Black).padding(8.dp)) {
                            Text(
                                text = terminalOutput,
                                color = Color.Green,
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthorityStatusCard(status: RootShellService.ShellStatus) {
    val (text, color) = when (status) {
        RootShellService.ShellStatus.RootAccess -> "ROOT ACCESS: ACTIVE" to Color.Red
        RootShellService.ShellStatus.ShizukuAccess -> "SHIZUKU BRIDGE: ACTIVE" to Color.Green
        else -> "LIMITED USER MODE" to Color.Yellow
    }

    BrutalistCard(accentColor = color) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f), contentColor = color),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier.height(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null)
            Text(text, fontSize = 10.sp, fontWeight = FontWeight.Black)
        }
    }
}
