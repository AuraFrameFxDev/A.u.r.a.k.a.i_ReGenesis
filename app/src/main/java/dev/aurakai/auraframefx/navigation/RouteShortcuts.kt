package dev.aurakai.auraframefx.navigation

import androidx.navigation.NavController

fun NavController.navigateToSentinelFortress() {
    navigate(ReGenesisRoute.SentinelFortress.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToOracleDrive() {
    navigate(ReGenesisRoute.OracleDriveHub.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToPandoraBox() {
    navigate(ReGenesisRoute.PandoraBox.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToAgentProfile(agentId: String) {
    navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agentId)) {
        launchSingleTop = true
    }
}

object RouteShortcuts {
    val LDO_ROUTES = listOf(
        ReGenesisRoute.LdoBonding.route,
        ReGenesisRoute.LdoRoster.route,
        ReGenesisRoute.LdoProgression.route,
        ReGenesisRoute.LdoTasker.route
    )

    val KAI_ROUTES = listOf(
        ReGenesisRoute.SentinelFortress.route,
        ReGenesisRoute.SecurityCenter.route,
        ReGenesisRoute.SovereignShield.route
    )

    val GENESIS_ROUTES = listOf(
        ReGenesisRoute.OracleDriveHub.route,
        ReGenesisRoute.PandoraBox.route,
        ReGenesisRoute.Trinity.route
    )
}
