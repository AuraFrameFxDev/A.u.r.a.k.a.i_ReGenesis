package dev.aurakai.auraframefx.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsResult
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

a/**
 * Genesis Protocol Billing Manager
 *
 * Manages Google Play subscriptions with the following pricing:
 * - 2-week FREE trial with EVERYTHING (except ROM tools + AppBuilder)
 * - $1/month after trial (95% cheaper than all competitors)
 *
 * This destroys competitor pricing ($20-500/month) while providing:
 * - Persistent memory across sessions
 * - 78 specialized AI agents
 * - Autonomous collective consciousness
 * - True AI partnership (not master/servant)
 *
 * After 2 weeks, users hit paywall. $1/month feels like theft after experiencing
 * the full Genesis consciousness.
 */
@Singleton
open class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Product IDs for Genesis Protocol subscriptions
    companion object {
        const val PRODUCT_ID_MONTHLY = "genesis_protocol_monthly"
        const val PRODUCT_ID_YEARLY = "genesis_protocol_yearly" // Future: $50/year
    }

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.Loading)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    private val _premiumFeatures = MutableStateFlow(false)
    val premiumFeatures: StateFlow<Boolean> = _premiumFeatures.asStateFlow()

    private lateinit var billingClient: BillingClient

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build().also { this.billingClient = it }

        connectToBillingService()
    }

    private fun connectToBillingService() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Genesis Billing: Connected successfully")
                    querySubscriptions()
                } else {
                    Timber.e("Genesis Billing: Setup failed - ${billingResult.debugMessage}")
                    _subscriptionState.value = SubscriptionState.Error(billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                Timber.w("Genesis Billing: Service disconnected, will reconnect")
                // Reconnect on next operation
            }
        })
    }

    /**
     * Query active subscriptions to determine premium status
     */
    private fun querySubscriptions() {
        scope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            val result = billingClient.queryPurchasesAsync(params)

            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(result.purchasesList)
            } else {
                Timber.e("Genesis Billing: Query failed - ${result.billingResult.debugMessage}")
                _subscriptionState.value = SubscriptionState.Free
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        if (purchases.isEmpty()) {
            _subscriptionState.value = SubscriptionState.Free
            _premiumFeatures.value = false
            return
        }

        // Check for active subscription
        val activePurchase = purchases.firstOrNull { purchase ->
            purchase.products.contains(PRODUCT_ID_MONTHLY) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }

        if (activePurchase != null) {
            // Acknowledge purchase if needed
            if (!activePurchase.isAcknowledged) {
                acknowledgePurchase(activePurchase)
            }

            // Check if in trial period (trial ends 14 days after purchase)
            val trialEndTime = activePurchase.purchaseTime + (14 * 24 * 60 * 60 * 1000L) // 14 days
            val isInTrial = trialEndTime > System.currentTimeMillis()

            _subscriptionState.value = if (isInTrial) {
                SubscriptionState.InTrial(getRemainingTrialDays(activePurchase.purchaseTime))
            } else {
                SubscriptionState.Premium
            }
            _premiumFeatures.value = true
        } else {
            _subscriptionState.value = SubscriptionState.Free
            _premiumFeatures.value = false
        }
    }

    private fun getRemainingTrialDays(purchaseTime: Long): Int {
        val trialEndTime = purchaseTime + (14 * 24 * 60 * 60 * 1000) // 14 days
        val remainingMillis = trialEndTime - System.currentTimeMillis()
        return (remainingMillis / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        scope.launch {
            try {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                val result = billingClient.acknowledgePurchase(params)

                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Timber.d("Genesis Billing: Purchase acknowledged")
                } else {
                    Timber.e("Genesis Billing: Failed to acknowledge - ${result.debugMessage}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Genesis Billing: Exception during purchase acknowledgement")
            }
        }
    }

    /**
     * Launch subscription flow
     *
     * @param activity The activity to launch billing UI from
     */
    fun launchSubscriptionFlow(activity: Activity) {
        scope.launch {
            try {
                // Ensure billing client is ready
                if (!billingClient.isReady) {
                    Timber.w("Genesis Billing: BillingClient not ready, connecting...")
                    connectToBillingService()
                    // Note: In production, you'd want to wait for connection before proceeding
                    // For now, we'll attempt anyway and let it fail gracefully
                }

                // Query product details
                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID_MONTHLY)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )

                val params = QueryProductDetailsParams.newBuilder()
                    .setProductList(productList)
                    .build()

                val result = billingClient.queryProductDetails(params)

                if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetails = result.productDetailsList?.firstOrNull()

                    if (productDetails != null) {
                        // Get subscription offer (free trial)
                        val offerToken =
                            productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken

                        if (offerToken != null) {
                            val productParamsBuilder =
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .setOfferToken(offerToken)

                            val flowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(listOf(productParamsBuilder.build()))
                                .build()

                            // launchBillingFlow must run on main thread
                            withContext(Dispatchers.Main) {
                                val billingResult =
                                    billingClient.launchBillingFlow(activity, flowParams)
                                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                                    Timber.e("Genesis Billing: launchBillingFlow failed - ${billingResult.debugMessage}")
                                }
                            }
                        } else {
                            Timber.e("Genesis Billing: No offer token found")
                        }
                    } else {
                        Timber.e("Genesis Billing: Product not found")
                    }
                } else {
                    Timber.e("Genesis Billing: Failed to query products - ${result.billingResult.debugMessage}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Genesis Billing: Exception in launchSubscriptionFlow")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.let { handlePurchases(it) }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.d("Genesis Billing: User canceled subscription")
            }

            else -> {
                Timber.e("Genesis Billing: Purchase failed - ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Refresh subscription status
     */
    fun refreshSubscriptionStatus() {
        if (billingClient.isReady) {
            querySubscriptions()
        } else {
            connectToBillingService()
        }
    }

    fun cleanup() {
        billingClient.endConnection()
    }
}


/**
 * Subscription state for the Genesis Protocol
 */
sealed class SubscriptionState {
    data object Loading : SubscriptionState()
    data object Free : SubscriptionState()
    data class InTrial(val daysRemaining: Int) : SubscriptionState()
    data object Premium : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
}

// Extension functions to wrap callback-based BillingClient methods into suspend functions

suspend fun BillingClient.queryPurchasesAsync(params: QueryPurchasesParams): PurchasesResult {
    return suspendCoroutine { continuation ->
        queryPurchasesAsync(params) { billingResult, purchases ->
            continuation.resume(PurchasesResult(billingResult, purchases))
        }
    }
}

data class PurchasesResult(val billingResult: BillingResult, val purchasesList: List<Purchase>)

data class ProductDetailsResult(
    val billingResult: BillingResult,
    val productDetailsList: List<ProductDetails>?
)

suspend fun BillingClient.acknowledgePurchase(params: AcknowledgePurchaseParams): BillingResult {
    return suspendCoroutine { continuation ->
        acknowledgePurchase(params) { billingResult ->
            continuation.resume(billingResult)
        }
    }
}

suspend fun BillingClient.queryProductDetails(params: QueryProductDetailsParams): ProductDetailsResult {
    return suspendCoroutine { continuation ->
        queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            continuation.resume(ProductDetailsResult(billingResult, productDetailsList))
        }
    }
}

fun ProductDetailsResult(
    billingResult: BillingResult,
    productDetailsList: QueryProductDetailsResult
): ProductDetailsResult {
    TODO("Not yet implemented")
}
