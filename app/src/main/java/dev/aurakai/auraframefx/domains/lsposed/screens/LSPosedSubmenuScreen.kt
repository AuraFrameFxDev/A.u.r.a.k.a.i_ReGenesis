package dev.aurakai.auraframefx.domains.lsposed.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.cascade.utils.LSPosedDetector
import dev.aurakai.auraframefx.navigation.gates.components.SubmenuItem
import dev.aurakai.auraframefx.navigation.gates.components.SubmenuCard

@Composable
fun LSPosedSubmenuScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val detectionResult = remember { LSPosedDetector.getDetectionSummary(context) }

    if (!detectionResult.isInstalled) {
        LSPosedNotInstalledScreen(onNavigateBack = { navController.popBackStack() })
        return
    }

    val menuItems = listOf(
        SubmenuItem(
            title = "Sovereign Module Manager",
            description = "Unified control for Magisk, LSPosed, and KernelSU modules",
            icon = Icons.Default.Extension,
            color = Color(0xFF00FF85),
            route = "module_manager"
        ),
        SubmenuItem(
            title = "Hook Manager",
            description = "Monitor and manage active method hooks",
            icon = Icons.AutoMirrored.Filled.CallSplit,
            color = Color(0xFF4ECDC4),
            route = "hook_manager"
        ),
        SubmenuItem(
            title = "Logs Viewer",
            description = "View system logs and module activity",
            icon = Icons.AutoMirrored.Filled.ListAlt,
            color = Color(0xFFFFD93D),
            route = "logs_viewer"
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0033), Color.Black, Color(0xFF330066))
                )
            )
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                "🔧 LSPOSED GATE",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Xposed framework management and module control",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFFF8C69).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Quick Actions", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            XposedQuickActionsPanel()

            Spacer(modifier = Modifier.height(24.dp))
            Text("Advanced Options", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(menuItems) { item ->
                    SubmenuCard(item = item, onClick = { navController.navigate(item.route) })
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f))
                    ) {
                        Text("← Back to Gates", color = Color(0xFFFF6B35))
                    }
                }
            }
        }
    }
}

@Composable
private fun LSPosedNotInstalledScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, null, tint = Color(0xFFFF6B35), modifier = Modifier.size(120.dp))
        Spacer(modifier = Modifier.height(32.dp))
        Text("LSPosed Not Detected", color = Color(0xFFFF6B35), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("LSPosed framework is required for these features.", color = Color.White.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35))) {
            Text("Back to Gates")
        }
    }
}
