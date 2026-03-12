package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

/**
 * User Preferences Screen
 *
 * Comprehensive settings and configuration management:
 * - Account Settings
 * - App Behavior
 * - Security Settings
 * - UI Preferences
 * - Feature Flags
 * - Storage & Sync
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPreferencesScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Preference states
    var darkMode by remember { mutableStateOf(true) }
    var animationsEnabled by remember { mutableStateOf(true) }
    var autoLockEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var betaFeaturesEnabled by remember { mutableStateOf(false) }
    var cloudSyncEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Preferences",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.Cyan
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Settings Section
            item {
                PreferenceCategoryHeader("Account Settings")
            }

            item {
                PreferenceItem(
                    title = "Profile",
                    description = "Manage your account profile and information",
                    icon = Icons.Default.Person,
                    onClick = {
                        // TODO: Navigate to profile screen
                    }
                )
            }

            item {
                PreferenceItem(
                    title = "Authentication",
                    description = "Login settings and password management",
                    icon = Icons.Default.Lock,
                    onClick = {
                        // TODO: Navigate to login screen when implemented
                        navController.navigate(ReGenesisNavHost.UserPreferences.route)
                    }
                )
            }

            // App Behavior Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceCategoryHeader("App Behavior")
            }

            item {
                PreferenceItem(
                    title = "Default Gate",
                    description = "Choose which gate opens on startup",
                    icon = Icons.Default.Home,
                    onClick = {
                        // TODO: Show gate selection dialog
                    }
                )
            }

            item {
                PreferenceToggleItem(
                    title = "Enable Animations",
                    description = "Control UI animations and transitions",
                    icon = Icons.Default.Animation,
                    isChecked = animationsEnabled,
                    onCheckedChange = { animationsEnabled = it }
                )
            }

            // Security Settings Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceCategoryHeader("Security")
            }

            item {
                PreferenceToggleItem(
                    title = "Biometric Unlock",
                    description = "Use fingerprint or face unlock",
                    icon = Icons.Default.Fingerprint,
                    isChecked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )
            }

            item {
                PreferenceToggleItem(
                    title = "Auto-Lock",
                    description = "Lock app when switching away",
                    icon = Icons.Default.Lock,
                    isChecked = autoLockEnabled,
                    onCheckedChange = { autoLockEnabled = it }
                )
            }

            item {
                PreferenceItem(
                    title = "Auto-Lock Timeout",
                    description = "Set timeout before auto-lock: 30 seconds",
                    icon = Icons.Default.Timer,
                    onClick = {
                        // TODO: Show timeout picker
                    }
                )
            }

            // UI Preferences Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceCategoryHeader("UI Preferences")
            }

            item {
                PreferenceToggleItem(
                    title = "Dark Mode",
                    description = "Use dark theme throughout the app",
                    icon = Icons.Default.DarkMode,
                    isChecked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            item {
                PreferenceItem(
                    title = "Gate Layout",
                    description = "Customize gate carousel appearance",
                    icon = Icons.Default.ViewCarousel,
                    onClick = {
                        // TODO: Navigate to gate layout settings
                    }
                )
            }

            // Feature Flags Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceCategoryHeader("Feature Flags")
            }

            item {
                PreferenceToggleItem(
                    title = "Beta Features",
                    description = "Enable experimental features and early access",
                    icon = Icons.Default.Science,
                    isChecked = betaFeaturesEnabled,
                    onCheckedChange = { betaFeaturesEnabled = it }
                )
            }

            // Storage & Sync Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceCategoryHeader("Storage & Sync")
            }

            item {
                PreferenceToggleItem(
                    title = "Cloud Storage",
                    description = "Sync settings and data to Oracle Drive",
                    icon = Icons.Default.Cloud,
                    isChecked = cloudSyncEnabled,
                    onCheckedChange = { cloudSyncEnabled = it }
                )
            }

            item {
                PreferenceItem(
                    title = "Data Retention",
                    description = "Manage local and cloud data retention",
                    icon = Icons.Default.Storage,
                    onClick = {
                        // TODO: Navigate to data retention settings
                    }
                )
            }

            item {
                PreferenceItem(
                    title = "Clear Cache",
                    description = "Clear app cache and temporary files",
                    icon = Icons.Default.DeleteOutline,
                    onClick = {
                        // TODO: Show clear cache confirmation
                    },
                    isDangerous = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PreferenceCategoryHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(
            color = Color.Cyan,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        modifier = modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun PreferenceItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDangerous: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isDangerous) {
                Color.Red.copy(alpha = 0.1f)
            } else {
                Color(0xFF1A1A1A)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDangerous) Color.Red else Color.Cyan,
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun PreferenceToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Cyan,
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    )
                )
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Cyan,
                    checkedTrackColor = Color.Cyan.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}
