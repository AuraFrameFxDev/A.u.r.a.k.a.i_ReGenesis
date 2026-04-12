package dev.aurakai.auraframefx.domains.aura.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.HexagonGrid
import dev.aurakai.auraframefx.domains.aura.ui.components.SparkleButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XposedQuickAccessPanel(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LSPosed Quick Toggles", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Cyan
                )
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            HexagonGrid(color = Color.Cyan.copy(alpha = 0.05f))
            
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("NEURAL INJECTION MODULES", color = Color.Cyan, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Placeholder for actual toggles
                Text("No active hooks detected.", color = Color.Gray)
                
                Spacer(modifier = Modifier.height(48.dp))
                
                SparkleButton(
                    text = "Sync LSPosed",
                    onClick = { /* TODO */ },
                    color = Color.Cyan
                )
            }
        }
    }
}

