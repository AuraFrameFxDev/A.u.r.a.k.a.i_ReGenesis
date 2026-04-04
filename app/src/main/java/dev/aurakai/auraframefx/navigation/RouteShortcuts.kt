package dev.aurakai.auraframefx.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * 🚀 REGENESIS ROUTE SHORTCUTS
 *
 * Extension functions for NavController to simplify navigation across the project.
 * Centralizes NavOptions for consistent transition behavior.
 */

fun NavController.navigateToSentinelFortress() {
    navigate(ReGenesisRoute.SentinelFortress.route) {
        setupStandardOptions(this)
    }
}

fun NavController.navigateToOracleDrive() {
    navigate(ReGenesisRoute.OracleDrive.route) {
        setupStandardOptions(this)
    }
}

fun NavController.navigateToPandoraBox() {
    navigate(ReGenesisRoute.PandoraBox.route) {
        setupStandardOptions(this)
    }
}

fun NavController.navigateToConferenceRoom() {
    navigate(ReGenesisRoute.ConferenceRoom.route) {
        setupStandardOptions(this)
    }
}

fun NavController.navigateToAgentProfile(agentId: String) {
    navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agentId)) {
        setupStandardOptions(this)
    }
}

/**
 * Common NavOptions setup for standard hub transitions.
 */
private fun setupStandardOptions(builder: NavOptionsBuilder) {
    builder.apply {
        popUpTo(ReGenesisRoute.HomeGateCarousel.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Grouped route constants for organizational access.
 */
object RouteShortcuts {
    val LDO_ROUTES = listOf(
        ReGenesisRoute.LdoOrchestrationHub.route,
        ReGenesisRoute.LdoDevOpsHub.route,
        ReGenesisRoute.LdoCatalystDevelopment.route,
        ReGenesisRoute.LdoTasker.route,
        ReGenesisRoute.LdoFusion.route,
        ReGenesisRoute.LdoBonding.route,
        ReGenesisRoute.LdoWorldTree.route,
        ReGenesisRoute.LdoRoster.route
    )

    val KAI_ROUTES = listOf(
        ReGenesisRoute.SentinelFortress.route,
        ReGenesisRoute.RomToolsHub.route,
        ReGenesisRoute.SovereignShield.route,
        ReGenesisRoute.Bootloader.route,
        ReGenesisRoute.RootTools.route
    )

    val GENESIS_ROUTES = listOf(
        ReGenesisRoute.OracleDriveHub.route,
        ReGenesisRoute.AgentNexusHub.route,
        ReGenesisRoute.ConferenceRoom.route,
        ReGenesisRoute.Trinity.route,
        ReGenesisRoute.PandoraBox.route
    )

    val AURA_ROUTES = listOf(
        ReGenesisRoute.AuraThemingHub.route,
        ReGenesisRoute.AuraLab.route,
        ReGenesisRoute.ChromaCore.route,
        ReGenesisRoute.CollabCanvas.route
    )
}
