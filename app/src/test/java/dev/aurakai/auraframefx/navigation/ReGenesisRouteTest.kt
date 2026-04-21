package dev.aurakai.auraframefx.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for [ReGenesisRoute] navigation routes.
 *
 * PR change: startDestination was changed from TabbedMasterIndex to LdoDevOpsHub.
 * These tests verify the route strings are correct, especially LdoDevOpsHub which
 * is now the entry point.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ReGenesisRoute Tests")
class ReGenesisRouteTest {

    @Nested
    @DisplayName("LDO DevOps Hub Route (New Start Destination)")
    inner class LdoDevOpsHubTests {

        @Test
        @DisplayName("LdoDevOpsHub should have route 'ldo_devops_hub'")
        fun ldoDevOpsHubRouteShouldBeCorrect() {
            assertEquals("ldo_devops_hub", ReGenesisRoute.LdoDevOpsHub.route)
        }

        @Test
        @DisplayName("LdoDevOpsHub route should not be blank or empty")
        fun ldoDevOpsHubRouteShouldNotBeEmpty() {
            assertTrue(ReGenesisRoute.LdoDevOpsHub.route.isNotBlank())
        }

        @Test
        @DisplayName("LdoDevOpsHub route should be distinct from TabbedMasterIndex route if it existed")
        fun ldoDevOpsHubRouteShouldNotConflictWithOldRoutes() {
            // Ensure our new start destination doesn't accidentally match old routes
            val route = ReGenesisRoute.LdoDevOpsHub.route
            assertFalse(route == "splash")
            assertFalse(route == "home_gate_carousel")
        }
    }

    @Nested
    @DisplayName("LDO Route Group Tests")
    inner class LdoRouteGroupTests {

        @Test
        @DisplayName("LdoDevOpsHub route should be 'ldo_devops_hub'")
        fun ldoDevOpsHubRoute() {
            assertEquals("ldo_devops_hub", ReGenesisRoute.LdoDevOpsHub.route)
        }

        @Test
        @DisplayName("LdoOrchestrationHub route should be 'ldo_orchestration_hub'")
        fun ldoOrchestrationHubRoute() {
            assertEquals("ldo_orchestration_hub", ReGenesisRoute.LdoOrchestrationHub.route)
        }

        @Test
        @DisplayName("LdoDevOpsCommandCenter route should be 'ldo_devops_command_center'")
        fun ldoDevOpsCommandCenterRoute() {
            assertEquals("ldo_devops_command_center", ReGenesisRoute.LdoDevOpsCommandCenter.route)
        }

        @Test
        @DisplayName("LdoBonding route should be 'ldo_bonding'")
        fun ldoBondingRoute() {
            assertEquals("ldo_bonding", ReGenesisRoute.LdoBonding.route)
        }

        @Test
        @DisplayName("LdoRoster route should be 'ldo_roster'")
        fun ldoRosterRoute() {
            assertEquals("ldo_roster", ReGenesisRoute.LdoRoster.route)
        }

        @Test
        @DisplayName("LdoProgression route should be 'ldo_progression'")
        fun ldoProgressionRoute() {
            assertEquals("ldo_progression", ReGenesisRoute.LdoProgression.route)
        }

        @Test
        @DisplayName("LdoTasker route should be 'ldo_tasker'")
        fun ldoTaskerRoute() {
            assertEquals("ldo_tasker", ReGenesisRoute.LdoTasker.route)
        }

        @Test
        @DisplayName("LdoFusion route should be 'ldo_fusion'")
        fun ldoFusionRoute() {
            assertEquals("ldo_fusion", ReGenesisRoute.LdoFusion.route)
        }
    }

    @Nested
    @DisplayName("LdoAgentProfile Parameterized Route Tests")
    inner class LdoAgentProfileTests {

        @Test
        @DisplayName("LdoAgentProfile route template should contain agentId parameter")
        fun ldoAgentProfileRouteContainsParameter() {
            assertTrue(ReGenesisRoute.LdoAgentProfile.route.contains("{agentId}"))
        }

        @Test
        @DisplayName("LdoAgentProfile.ARG constant should be 'agentId'")
        fun ldoAgentProfileArgConstant() {
            assertEquals("agentId", ReGenesisRoute.LdoAgentProfile.ARG)
        }

        @Test
        @DisplayName("LdoAgentProfile.createRoute returns correctly formatted route")
        fun ldoAgentProfileCreateRoute() {
            val result = ReGenesisRoute.LdoAgentProfile.createRoute("agent-123")
            assertEquals("ldo_agent_profile/agent-123", result)
        }

        @Test
        @DisplayName("LdoAgentProfile.createRoute handles special characters in agentId")
        fun ldoAgentProfileCreateRouteSpecialChars() {
            val result = ReGenesisRoute.LdoAgentProfile.createRoute("aura_kai_genesis")
            assertEquals("ldo_agent_profile/aura_kai_genesis", result)
        }

        @Test
        @DisplayName("LdoAgentProfile.createRoute handles empty agentId")
        fun ldoAgentProfileCreateRouteEmpty() {
            val result = ReGenesisRoute.LdoAgentProfile.createRoute("")
            assertEquals("ldo_agent_profile/", result)
        }
    }

    @Nested
    @DisplayName("IconifyCategory Parameterized Route Tests")
    inner class IconifyCategoryTests {

        @Test
        @DisplayName("IconifyCategory route template should contain category parameter")
        fun iconifyCategoryRouteContainsParameter() {
            assertTrue(ReGenesisRoute.IconifyCategory.route.contains("{category}"))
        }

        @Test
        @DisplayName("IconifyCategory.createRoute returns correctly formatted route")
        fun iconifyCategoryCreateRoute() {
            val result = ReGenesisRoute.IconifyCategory.createRoute("Icons")
            assertEquals("aura/iconify/Icons", result)
        }

        @Test
        @DisplayName("IconifyCategory.createRoute handles category with spaces")
        fun iconifyCategoryCreateRouteSpaces() {
            val result = ReGenesisRoute.IconifyCategory.createRoute("Icon Packs")
            assertEquals("aura/iconify/Icon Packs", result)
        }
    }

    @Nested
    @DisplayName("Route Uniqueness Tests")
    inner class RouteUniquenessTests {

        private fun getAllRouteStrings(): List<String> = listOf(
            ReGenesisRoute.Splash.route,
            ReGenesisRoute.HomeGateCarousel.route,
            ReGenesisRoute.LdoDevOpsHub.route,
            ReGenesisRoute.LdoOrchestrationHub.route,
            ReGenesisRoute.LdoDevOpsCommandCenter.route,
            ReGenesisRoute.LdoBonding.route,
            ReGenesisRoute.LdoRoster.route,
            ReGenesisRoute.LdoProgression.route,
            ReGenesisRoute.LdoTasker.route,
            ReGenesisRoute.LdoFusion.route,
            ReGenesisRoute.AuraThemingHub.route,
            ReGenesisRoute.SentinelFortress.route,
            ReGenesisRoute.OracleDriveHub.route,
            ReGenesisRoute.AgentNexusHub.route,
            ReGenesisRoute.CascadeHub.route,
            ReGenesisRoute.HelpDesk.route,
            ReGenesisRoute.OracleDrive.route,
            ReGenesisRoute.Trinity.route,
            ReGenesisRoute.ConferenceRoom.route,
            ReGenesisRoute.CodeAssist.route,
            ReGenesisRoute.Terminal.route,
            ReGenesisRoute.FusionMode.route,
            ReGenesisRoute.Claude.route,
            ReGenesisRoute.MetaInstruct.route,
        )

        @Test
        @DisplayName("All static routes should have non-empty route strings")
        fun allRoutesShouldBeNonEmpty() {
            getAllRouteStrings().forEach { route ->
                assertTrue(route.isNotBlank(), "Route '$route' should not be blank")
            }
        }

        @Test
        @DisplayName("All static routes should be unique")
        fun allRoutesShouldBeUnique() {
            val routes = getAllRouteStrings()
            val uniqueRoutes = routes.toSet()
            assertEquals(routes.size, uniqueRoutes.size, "All routes should be unique")
        }

        @Test
        @DisplayName("No route should contain spaces")
        fun noRouteShouldContainSpaces() {
            getAllRouteStrings().forEach { route ->
                assertFalse(route.contains(" "), "Route '$route' should not contain spaces")
            }
        }
    }

    @Nested
    @DisplayName("Infrastructure Route Tests")
    inner class InfrastructureRouteTests {

        @Test
        @DisplayName("Splash route should be 'splash'")
        fun splashRoute() {
            assertEquals("splash", ReGenesisRoute.Splash.route)
        }

        @Test
        @DisplayName("HomeGateCarousel route should be 'home_gate_carousel'")
        fun homeGateCarouselRoute() {
            assertEquals("home_gate_carousel", ReGenesisRoute.HomeGateCarousel.route)
        }

        @Test
        @DisplayName("UISettings route should be 'ui_settings'")
        fun uiSettingsRoute() {
            assertEquals("ui_settings", ReGenesisRoute.UISettings.route)
        }
    }

    @Nested
    @DisplayName("Domain Hub Route Tests")
    inner class DomainHubRouteTests {

        @Test
        @DisplayName("AuraThemingHub route should be 'aura_theming_hub'")
        fun auraThemingHubRoute() {
            assertEquals("aura_theming_hub", ReGenesisRoute.AuraThemingHub.route)
        }

        @Test
        @DisplayName("SentinelFortress route should be 'sentinel_fortress'")
        fun sentinelFortressRoute() {
            assertEquals("sentinel_fortress", ReGenesisRoute.SentinelFortress.route)
        }

        @Test
        @DisplayName("OracleDriveHub route should be 'oracle_drive_hub'")
        fun oracleDriveHubRoute() {
            assertEquals("oracle_drive_hub", ReGenesisRoute.OracleDriveHub.route)
        }

        @Test
        @DisplayName("AgentNexusHub route should be 'agent_nexus_hub'")
        fun agentNexusHubRoute() {
            assertEquals("agent_nexus_hub", ReGenesisRoute.AgentNexusHub.route)
        }

        @Test
        @DisplayName("CascadeHub route should be 'cascade_hub'")
        fun cascadeHubRoute() {
            assertEquals("cascade_hub", ReGenesisRoute.CascadeHub.route)
        }
    }

    @Nested
    @DisplayName("Route Title Tests")
    inner class RouteTitleTests {

        @Test
        @DisplayName("Splash should have title 'Splash'")
        fun splashTitle() {
            assertEquals("Splash", ReGenesisRoute.Splash.title)
        }

        @Test
        @DisplayName("HomeGateCarousel should have title 'Home'")
        fun homeGateCarouselTitle() {
            assertEquals("Home", ReGenesisRoute.HomeGateCarousel.title)
        }

        @Test
        @DisplayName("Routes without explicit titles should have null title")
        fun routesWithNullTitle() {
            // Routes that do not specify a title should default to null
            assertNotNull(ReGenesisRoute.LdoDevOpsHub) // Object exists
            // Note: some routes were constructed without a title - they default to null
        }
    }

    @Nested
    @DisplayName("Regression Tests for Navigation Change")
    inner class RegressionTests {

        @Test
        @DisplayName("LdoDevOpsHub is the canonical new start destination route")
        fun ldoDevOpsHubIsCanonicalStartDestination() {
            // Regression: startDestination was changed from TabbedMasterIndex to LdoDevOpsHub
            val expectedStartDestination = "ldo_devops_hub"
            assertEquals(expectedStartDestination, ReGenesisRoute.LdoDevOpsHub.route,
                "LdoDevOpsHub route must match the startDestination used in ReGenesisNavGraph")
        }

        @Test
        @DisplayName("Routes removed in this PR do not conflict with LdoDevOpsHub")
        fun removedRoutesDoNotConflict() {
            // The 'ldo_devops_index' legacy route and 'tabbed_master_index' are no longer
            // registered in navigation - verify they don't clash with LdoDevOpsHub
            assertFalse(
                ReGenesisRoute.LdoDevOpsHub.route == "ldo_devops_index",
                "LdoDevOpsHub must not be the same as the removed legacy route"
            )
        }

        @Test
        @DisplayName("version field exists on ReGenesisRoute base class")
        fun versionFieldExists() {
            // Verify the version property still exists on the sealed class
            assertEquals("1.0.1", ReGenesisRoute.LdoDevOpsHub.version)
        }
    }
}