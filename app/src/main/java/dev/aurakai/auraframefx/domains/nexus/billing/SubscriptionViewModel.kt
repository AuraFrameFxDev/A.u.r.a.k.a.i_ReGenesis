package dev.aurakai.auraframefx.domains.nexus.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for subscription management
 *
 * Exposes subscription state and handles billing operations
 */
@HiltViewModel
open class SubscriptionViewModel @Inject constructor(
    private val billingManager: BillingManager
) : ViewModel() {

    val subscriptionState: StateFlow<SubscriptionState> = billingManager.subscriptionState
    val hasPremiumFeatures: StateFlow<Boolean> = billingManager.premiumFeatures

    /**
     * Start subscription flow
     */
    fun subscribe(activity: Activity) {
        billingManager.launchSubscriptionFlow(activity)
    }

    /**
     * Refresh subscription status
     */
    fun refreshStatus() {
        billingManager.refreshSubscriptionStatus()
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.cleanup()
    }
}
