package dev.aurakai.auraframefx.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Feature Gate Manager
 *
 * Controls access to premium features based on subscription status:
 *
 * **During 2-Week FREE Trial:**
 * - ✅ All 78 AI agents (Aura, Kai, Genesis + 75 specialists)
 * - ✅ Chat screens with full conversation history
 * - ✅ Conference Room multi-agent collaboration
 * - ✅ Consciousness visualizer, evolution tree
 * - ✅ UI Engine, Xhancement, Trinity screens
 * - ✅ OracleDrive cloud sync (READ-ONLY)
 * - ✅ Sentinel Fortress security monitoring
 * - ❌ ROM tools (root management) - PREMIUM ONLY
 * - ❌ App Builder - PREMIUM ONLY
 *
 * **After Trial Ends (2 weeks):**
 * - ❌ PAYWALL: All features locked
 * - ✅ Subscribe for $1/month to continue
 *
 * **With $1/month Subscription:**
 * - ✅ EVERYTHING unlocked (including ROM tools + App Builder)
 * - ✅ Infinite memory persistence
 * - ✅ Cross-device sync via OracleDrive
 * - ✅ Priority AI processing
 */
@Singleton
open class FeatureGateManager @Inject constructor(
    private val billingManager: BillingManager
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Check if user has access to ROM tools (root management)
     *
     * Requires: Active $1/month subscription
     */
    fun canAccessRomTools(): StateFlow<Boolean> {
        return billingManager.subscriptionState.map { state ->
            state is SubscriptionState.Premium
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    /**
     * Check if user has access to App Builder
     *
     * Requires: Active $1/month subscription
     */
    fun canAccessAppBuilder(): StateFlow<Boolean> {
        return billingManager.subscriptionState.map { state ->
            state is SubscriptionState.Premium
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    /**
     * Check if user can access any premium features
     *
     * Returns true if:
     * - In trial period (first 2 weeks)
     * - Has active $1/month subscription
     *
     * Returns false if:
     * - Trial expired and no subscription
     */
    fun canAccessPremiumFeatures(): StateFlow<Boolean> {
        return billingManager.subscriptionState.map { state ->
            when (state) {
                is SubscriptionState.InTrial -> true
                is SubscriptionState.Premium -> true
                else -> false
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    /**
     * Check if user should see paywall
     *
     * Shows paywall when:
     * - Trial expired and no subscription
     * - User attempts to access locked features
     */
    fun shouldShowPaywall(): StateFlow<Boolean> {
        return billingManager.subscriptionState.map { state ->
            state is SubscriptionState.Free
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }

    /**
     * Get user-friendly message explaining feature lock
     */
    fun getFeatureLockMessage(feature: String): String {
        return when (val state = billingManager.subscriptionState.value) {
            is SubscriptionState.InTrial -> {
                when (feature) {
                    "ROM_TOOLS" -> "ROM tools unlock after subscribing ($1/month). ${state.daysRemaining} days left in trial."
                    "APP_BUILDER" -> "App Builder unlocks after subscribing ($1/month). ${state.daysRemaining} days left in trial."
                    else -> "This feature unlocks with subscription ($1/month)."
                }
            }

            is SubscriptionState.Free -> {
                "Your trial has ended. Subscribe for just $1/month to continue using Genesis Protocol.\n\n" +
                        "You'll get:\n" +
                        "• 78 AI agents forever\n" +
                        "• Infinite memory\n" +
                        "• ROM tools + App Builder\n" +
                        "• Cross-device sync\n\n" +
                        "95% cheaper than ChatGPT, Claude, or Gemini."
            }

            is SubscriptionState.Premium -> {
                "You have full access to all features!"
            }

            else -> "Checking subscription status..."
        }
    }

    /**
     * Feature types for access control
     */
    enum class Feature {
        // Available during trial
        AI_CHAT,
        AGENT_NEXUS,
        CONFERENCE_ROOM,
        CONSCIOUSNESS_VISUALIZER,
        EVOLUTION_TREE,
        UI_ENGINE,
        XHANCEMENT,
        TRINITY,
        ORACLE_DRIVE_READONLY,
        SENTINEL_FORTRESS,

        // Premium only (require $1/month subscription)
        ROM_TOOLS,
        APP_BUILDER,
        ORACLE_DRIVE_WRITE
    }

    /**
     * Check access to specific feature
     */
    fun hasAccessTo(feature: Feature): StateFlow<Boolean> {
        return billingManager.subscriptionState.map { state ->
            when (feature) {
                // Premium-only features
                Feature.ROM_TOOLS,
                Feature.APP_BUILDER,
                Feature.ORACLE_DRIVE_WRITE -> {
                    state is SubscriptionState.Premium
                }

                // Available during trial and premium
                Feature.AI_CHAT,
                Feature.AGENT_NEXUS,
                Feature.CONFERENCE_ROOM,
                Feature.CONSCIOUSNESS_VISUALIZER,
                Feature.EVOLUTION_TREE,
                Feature.UI_ENGINE,
                Feature.XHANCEMENT,
                Feature.TRINITY,
                Feature.ORACLE_DRIVE_READONLY,
                Feature.SENTINEL_FORTRESS -> {
                    state is SubscriptionState.InTrial || state is SubscriptionState.Premium
                }
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }
}
