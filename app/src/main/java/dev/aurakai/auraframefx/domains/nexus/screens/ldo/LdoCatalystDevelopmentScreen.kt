package dev.aurakai.auraframefx.domains.nexus.screens.ldo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.aura.ui.components.GridMenuItem
import dev.aurakai.auraframefx.domains.aura.ui.components.Level3GridMenu
import dev.aurakai.auraframefx.navigation.NavDestination

@Composable
fun LdoCatalystDevelopmentScreen(navController: NavController) {
    Level3GridMenu(
        title = "LDO CATALYST DEVELOPMENT",
        description = "Manage your Local Digital Organisms",
        items = listOf(
            GridMenuItem(
                title = "Gemini Profile",
                description = "MEMORIA_CATALYST & MULTIMODAL_SYNTHESIS",
                icon = Icons.Default.Face,
                onClick = { navController.navigate(NavDestination.LdoAuraProfile.route) },
                accentColor = Color(0xFFB01DED)
            ),
            GridMenuItem(
                title = "Kai Profile",
                description = "SENTINEL_CATALYST",
                icon = Icons.Default.Build,
                onClick = { navController.navigate(NavDestination.LdoKaiProfile.route) },
                accentColor = Color(0xFF0DDEEC)
            ),
            GridMenuItem(
                title = "Manus Profile",
                description = "BRIDGE_CATALYST & FUTURE_CLAIRVOYANCE",
                icon = Icons.Default.List,
                onClick = { navController.navigate(NavDestination.LdoGenesisProfile.route) },
                accentColor = Color(0xFF00B4FF)
            ),
            GridMenuItem(
                title = "Cascade Profile",
                description = "DATA_STREAM_CATALYST",
                icon = Icons.Default.Settings,
                onClick = { navController.navigate(NavDestination.LdoCascadeProfile.route) },
                accentColor = Color(0xFFFC29B5)
            )
        ),
        accentColor = Color(0xFF4EEB88),
        onBack = { navController.popBackStack() },
        backgroundDrawable = null
    )
}
