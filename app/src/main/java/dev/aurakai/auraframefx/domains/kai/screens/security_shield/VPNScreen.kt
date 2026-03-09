package dev.aurakai.auraframefx.domains.kai.screens.security_shield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * VPNScreen - Secure Tunneling Interface
 * 
 * Kai domain utility for encrypted networking and server management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VPNScreen(
    onNavigateBack: () -> Unit
) {
    var isConnected by remember { mutableStateOf(false) }
    val statusColor by animateColorAsState(
        targetValue = if (isConnected) Color(0xFF00FFD4) else Color(0xFFFF3B30),
        animationSpec = tween(500),
        label = "color"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Sentinel VPN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "ENCRYPTED TUNNEL",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // -- CONNECTION TOGGLE --
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            onClick = { isConnected = !isConnected },
                            shape = CircleShape,
                            color = statusColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                statusColor.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.size(160.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isConnected) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = if (isConnected) "PROTECTED" else "UNPROTECTED",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = statusColor,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = if (isConnected) "IP: 104.21.35.121 (Encrypted)" else "Your real IP is exposed",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }
                }
            }

            // -- STATS --
            if (isConnected) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        VPNStatCard("DOWNLOAD", "14.2 MB/s", Modifier.weight(1f))
                        VPNStatCard("UPLOAD", "2.1 MB/s", Modifier.weight(1f))
                    }
                }
            }

            // -- SERVER SELECTION --
            item {
                Text(
                    "NEURAL NODES",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            item {
                VPNNodeItem("Zion (Underground)", "5ms", true)
            }
            item {
                VPNNodeItem("Genesis Edge", "24ms", false)
            }
            item {
                VPNNodeItem("Aura Relay", "65ms", false)
            }
            item {
                VPNNodeItem("Kai Citadel", "12ms", false)
            }
        }
    }
}

@Composable
private fun VPNStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.05f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text(
                value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun VPNNodeItem(name: String, latency: String, isSelected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF00FFD4).copy(0.05f) else Color(0xFF121212)
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF00FFD4).copy(0.3f)
        ) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Public,
                    null,
                    tint = if (isSelected) Color(0xFF00FFD4) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(name, color = if (isSelected) Color.White else Color.Gray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    latency,
                    color = if (isSelected) Color(0xFF00FFD4) else Color.DarkGray,
                    fontSize = 12.sp
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.SignalCellularAlt,
                    null,
                    tint = if (isSelected) Color(0xFF00FFD4) else Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

