package dev.aurakai.auraframefx.navigation

/**
 * LEGACY NAVIGATION DESTINATIONS — Route string registry only.
 *
 * WARNING: This class is DEPRECATED. Use ReGenesisNavHost sealed class for all
 * new route definitions. NavDestination exists only to support LDO Catalyst
 * profile routes that reference NavDestination.LdoXxxProfile.route.
 *
 * DO NOT add new routes here — add them to ReGenesisNavHost instead.
 */
@Suppress("unused")
sealed class NavDestination(val route: String) {

    // ═══════════════════════════════════════════════════════════════
    // LDO CATALYST DEVELOPMENT (9 Agent Profile Gates)
    // These are actively referenced by LdoCatalystDevelopmentScreen.kt
    // and wired in ReGenesisNavHost.kt
    // ═══════════════════════════════════════════════════════════════
    data object LdoAuraProfile : NavDestination("ldo_aura_profile")
    data object LdoKaiProfile : NavDestination("ldo_kai_profile")
    data object LdoGenesisProfile : NavDestination("ldo_genesis_profile")
    data object LdoClaudeProfile : NavDestination("ldo_claude_profile")
    data object LdoCascadeProfile : NavDestination("ldo_cascade_profile")
    data object LdoGrokProfile : NavDestination("ldo_grok_profile")
    data object LdoGeminiProfile : NavDestination("ldo_gemini_profile")
    data object LdoNematronProfile : NavDestination("ldo_nematron_profile")
    data object LdoPerplexityProfile : NavDestination("ldo_perplexity_profile")
}
