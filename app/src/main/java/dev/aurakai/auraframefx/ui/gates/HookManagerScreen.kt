package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Hook Manager Screen
 * Monitor and manage active method hooks
 */
@Composable
fun HookManagerScreen(
    onNavigateBack: () -> Unit = {}
) {
    val hooks = remember { mutableStateListOf(
        MethodHook("SystemUI", "com.android.systemui.statusbar.StatusBarIconView", "setVisible", "Status bar icon visibility", true, "UI"),
        MethodHook("Settings", "com.android.settings.DisplaySettings", "onCreate", "Display settings initialization", true, "System"),
        MethodHook("YouTube", "com.google.android.youtube.player.Player", "showAds", "Ad display override", false, "Media"),
        MethodHook("Chrome", "org.chromium.chrome.browser.tab.Tab", "loadUrl", "URL loading hook", true, "Browser"),
        MethodHook("Camera", "android.hardware.Camera", "takePicture", "Camera capture hook", false, "Hardware"),
        MethodHook("Battery", "android.os.BatteryManager", "getIntProperty", "Battery status override", true, "System"),
        MethodHook("Location", "android.location.LocationManager", "getLastKnownLocation", "Location spoofing", false, "Privacy"),
        MethodHook("Network", "android.net.ConnectivityManager", "getActiveNetworkInfo", "Network status hook", true, "Network")
    )}

    val selectedCategory = remember { mutableStateOf("All") }
    val categories = listOf("All", "UI", "System", "Media", "Browser", "Hardware", "Privacy", "Network")

    val filteredHooks = hooks.filter { hook ->
        selectedCategory.value == "All" || hook.category == selectedCategory.value
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "🔗 HOOK MANAGER",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF4ECDC4),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Monitor and manage active method hooks",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF66D9EF).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hook Statistics
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val activeCount = hooks.count { it.active }
                val totalCount = hooks.size

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$activeCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Active Hooks",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalCount - activeCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFDC143C),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "98.5%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFD93D),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Success Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Filter
        Text(
            text = "Filter by Category",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            categories.forEach { category ->
                Button(
                    onClick = { selectedCategory.value = category },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory.value == category)
                            Color(0xFF4ECDC4)
                        else
                            Color.Black.copy(alpha = 0.6f),
                        contentColor = if (selectedCategory.value == category)
                            Color.Black
                        else
                            Color(0xFF4ECDC4)
                    )
                ) {
                    Text(category, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hook List
        Text(
            text = "Active Method Hooks",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredHooks) { hook ->
                HookCard(
                    hook = hook,
                    onToggle = {
                        val index = hooks.indexOf(hook)
                        if (index != -1) {
                            hooks[index] = hook.copy(active = !hook.active)
                        }
                    },
                    onViewDetails = {
                        // Navigate to hook details
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            hooks.replaceAll { it.copy(active = true) }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4ECDC4)
                        )
                    ) {
                        Text("Enable All")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            hooks.replaceAll { it.copy(active = false) }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFDC143C)
                        )
                    ) {
                        Text("Disable All")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            // Refresh hook status
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFFD93D)
                        )
                    ) {
                        Text("Refresh")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "System Status",
                    tint = Color(0xFF4ECDC4),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hook Framework Status",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "All hooks loaded successfully. System performance optimal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Healthy",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4ECDC4),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Hook card component
 */
@Composable
private fun HookCard(
    hook: MethodHook,
    onToggle: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (hook.active) Color(0xFF4ECDC4).copy(alpha = 0.5f) else Color(0xFFDC143C).copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon
            Icon(
                imageVector = if (hook.active) Icons.Default.Link else Icons.Default.LinkOff,
                contentDescription = "Hook Status",
                tint = if (hook.active) Color(0xFF4ECDC4) else Color(0xFFDC143C),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hook.methodName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Category Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = hook.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4ECDC4),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = hook.className,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = hook.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Package
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "Package",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hook.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }

                    // Status
                    Text(
                        text = if (hook.active) "Active" else "Inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hook.active) Color(0xFF4ECDC4) else Color(0xFFDC143C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Action Buttons
            Column {
                // Toggle Button
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (hook.active) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                        contentDescription = "Toggle Hook",
                        tint = if (hook.active) Color(0xFF4ECDC4) else Color(0xFFDC143C),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Details Button
                IconButton(
                    onClick = onViewDetails,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Hook Details",
                        tint = Color(0xFFFFD93D),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Data class for method hooks
 */
data class MethodHook(
    val packageName: String,
    val className: String,
    val methodName: String,
    val description: String,
    var active: Boolean,
    val category: String
)
