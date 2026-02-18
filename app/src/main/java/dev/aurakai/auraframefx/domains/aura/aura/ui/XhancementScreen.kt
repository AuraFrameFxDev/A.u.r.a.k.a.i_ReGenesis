package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonBlue
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonPurple
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.XhancementViewModel

/**
 * Xhancement Screen - Xposed Hook Toggle Control Panel
 *
 * Real-time toggle controls for UniversalComponentHooks with Kai security monitoring.
 * Instant enable/disable for system modifications with HARD VETO protection.
 */
/**
 * Displays the Xhancement placeholder screen with a centered icon, title, subtitle ("Coming Soon"),
 * and a short descriptive line about quick toggles for Xposed hooks.
 *
 * This composable also clears any transient error or success messages exposed by the screen's
 * ViewModel after three seconds when such a message is present.
 */
/**
 * Displays the Xhancement feature placeholder UI centered on the screen.
 *
 * Observes feature state and transient messages from the associated ViewModel and clears any displayed
 * error or success message after three seconds. The UI includes an icon, title, subtitle, and a brief
 * descriptive line about quick toggles for Xposed hooks.
 *
 * @param onNavigateBack Callback invoked to navigate back; defaults to no-op.
 */
/**
 * Displays the Xhancement placeholder screen and manages transient status messages.
 *
 * The UI centers an icon, title ("Xhancement"), subtitle ("Coming Soon"), and a short
 * descriptive line. While visible it observes feature state (hook modules and Kai security)
 * and transient messages (error and success); when a transient message appears it is cleared
 * automatically after three seconds.
 *
 * @param onNavigateBack Callback invoked to navigate back from this screen. Defaults to a no-op.
 */
@Composable
fun XhancementScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: XhancementViewModel = hiltViewModel()
) {
    val hookModules by viewModel.hookModules.collectAsState()
    val kaiSecurityEnabled by viewModel.kaiSecurityEnabled.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Show snackbar for messages
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessages()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Xhancement Control Panel",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NeonBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Instant hook toggles - LSPosed framework required",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Kai Security Toggle Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (kaiSecurityEnabled) {
                        Color(0xFF1A0A2E)
                    } else {
                        Color(0xFF2E0A0A)
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Kai Security",
                            tint = if (kaiSecurityEnabled) NeonPurple else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kai Security (HARD VETO)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (kaiSecurityEnabled) NeonPurple else Color.Gray
                            )
                            Text(
                                text = if (kaiSecurityEnabled) {
                                    "Active - Modules validated before activation"
                                } else {
                                    "Disabled - Modules unprotected"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Switch(
                        checked = kaiSecurityEnabled,
                        onCheckedChange = { viewModel.toggleKaiSecurity(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonPurple,
                            checkedTrackColor = NeonPurple.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Hook Modules List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hookModules) { module ->
                    HookModuleCard(
                        module = module,
                        onToggle = { enabled ->
                            viewModel.toggleModule(module.id, enabled)
                        }
                    )
                }
            }

            // Apply Changes Button
            Button(
                onClick = { viewModel.applyChanges() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonBlue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Apply Changes (Requires LSPosed Restart)",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Error/Success Snackbar
        if (errorMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = Color(0xFF2E0A0A)
            ) {
                Text(errorMessage!!, color = Color.White)
            }
        }

        if (successMessage != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                containerColor = Color(0xFF0A2E0A)
            ) {
                Text(successMessage!!, color = Color.White)
            }
        }
    }
}

@Composable
private fun HookModuleCard(
    module: XhancementViewModel.HookModule,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                module.isKaiBlocked -> Color(0xFF2E0A0A)
                module.isEnabled -> Color(0xFF0A1A2E)
                else -> Color(0xFF1A1A1A)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (module.isKaiBlocked) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Blocked",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else if (module.isEnabled) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = NeonBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = module.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            module.isKaiBlocked -> Color.Red
                            module.isEnabled -> NeonBlue
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (module.isKaiBlocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ Kai VETO: Security risk detected",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${module.hookCount} hooks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = module.isEnabled,
                onCheckedChange = onToggle,
                enabled = !module.isKaiBlocked,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonBlue,
                    checkedTrackColor = NeonBlue.copy(alpha = 0.5f),
                    disabledCheckedThumbColor = Color.Red,
                    disabledCheckedTrackColor = Color.Red.copy(alpha = 0.3f)
                )
            )
        }
    }
}

