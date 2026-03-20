package dev.aurakai.auraframefx.domains.nexus.billing

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import dev.aurakai.auraframefx.config.FeatureToggles
import dev.aurakai.auraframefx.domains.cascade.utils.debug

/**
 * App-level billing wrapper
 *
 * Wraps entire app to enforce subscription rules:`
 * - Shows paywall when trial expires
 * - Manages feature access throughout app
 */
@Composable
fun BillingWrapper(
    viewModel: SubscriptionViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val subscriptionState by viewModel.subscriptionState.collectAsState()

    // Refresh subscription status on app start
    LaunchedEffect(Unit) {
        viewModel.refreshStatus()
    }

    // Show app content
    content()

    // Overlay paywall when trial expires
    if (subscriptionState is SubscriptionState.Free && FeatureToggles.isPaywallEnabled) {
        PaywallDialog(viewModel = viewModel)
    }
}
