package dev.aurakai.auraframefx.ui.gates

import dev.aurakai.auraframefx.navigation.NavDestination
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * ROM Tools Submenu Screen
 * Provides access to all ROM-related functionality including live editing, flashing, and recovery tools
 */
/**
 * Renders the "ROM TOOLS" submenu screen UI for navigating ROM-related tools.
 *
 * The screen displays a header, a caution banner, a list of submenu cards (each navigating to its
 * configured route when tapped), and a back button that returns to the previous screen.
 *
 * @param navController NavController used to navigate to submenu destinations and to pop the back stack.
 */
@Composable
fun ROMToolsSubmenuScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E), // Dark blue-black
                        Color(0xFF16213E), // Darker blue
                        Color(0xFF0F3460)  // Medium blue
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "ROM TOOLS",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF4500), // Orange Red
                        Color(0xFFFF6347), // Tomato
                        Color(0xFFFF4500)  // Orange Red
                    )
                )
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Live ROM Editing & Flashing Suite",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFFFFA500), // Orange
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Warning Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF4500).copy(alpha = 0.1f),
                contentColor = Color(0xFFFF4500)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF4500))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️ CAUTION: These tools can brick your device. Backup your data first!",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Submenu Items
        val submenuItems = listOf(
            SubmenuItem(
                title = "Live ROM Editor",
                description = "Edit system files in real-time with live preview",
                icon = Icons.Default.Edit,
                route = NavDestination.LiveROMEditor.route,
                color = Color(0xFFFF4500)
            ),
            SubmenuItem(
                title = "ROM Flasher",
                description = "Flash custom ROMs, kernels, and recoveries",
                icon = Icons.Default.FlashOn,
                route = NavDestination.ROMFlasher.route,
                color = Color(0xFFFFD700)
            ),
            SubmenuItem(
                title = "Bootloader Manager",
                description = "Unlock/lock bootloader and manage boot states",
                icon = Icons.Default.Lock,
                route = NavDestination.BootloaderManager.route,
                color = Color(0xFF32CD32)
            ),
            SubmenuItem(
                title = "Recovery Tools",
                description = "TWRP integration and backup/restore operations",
                icon = Icons.Default.Refresh,
                route = NavDestination.RecoveryTools.route,
                color = Color(0xFF00CED1)
            )
        )

        submenuItems.forEach { item ->
            SubmenuCard(
                item = item,
                onClick = { navController.navigate(item.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Back Button
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFFF4500)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF4500))
        ) {
            Text(
                text = "← Back to Gates",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}