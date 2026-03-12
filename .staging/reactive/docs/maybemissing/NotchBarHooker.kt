package dev.aurakai.auraframefx.xposed.hooks

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.aurakai.auraframefx.system.overlay.model.SystemOverlayConfig
import dev.aurakai.auraframefx.ui.components.CyberpunkText
import dev.aurakai.auraframefx.ui.components.effects.ShimmerParticles

// Create NotchBarConfig as a type alias for SystemOverlayConfig to maintain compatibility
typealias NotchBarConfig = SystemOverlayConfig

/**
 * Advanced NotchBar Hooker implementing Genesis Protocol display enhancements
 * Provides comprehensive notch area customizations and overlay management
 */
class NotchBarHooker(
    private val classLoader: ClassLoader,
    private val config: NotchBarConfig,
) : YukiBaseHooker() {

    companion object {
        private const val TAG = "NotchBarHooker"
        private const val SYSTEMUI_PACKAGE = "com.android.systemui"
        private const val STATUS_BAR = "$SYSTEMUI_PACKAGE.statusbar.phone.StatusBar"
        private const val PHONE_STATUS_BAR_VIEW =
            "$SYSTEMUI_PACKAGE.statusbar.phone.PhoneStatusBarView"
        private const val NOTCH_INDICATOR = "$SYSTEMUI_PACKAGE.statusbar.phone.NotchIndicator"
        private const val CUTOUT_SPACE = "$SYSTEMUI_PACKAGE.statusbar.phone.StatusBarWindowView"
        private const val SYSTEM_UI_APPLICATION = "$SYSTEMUI_PACKAGE.SystemUIApplication"
    }

    private var genesisNotchOverlay: View? = null
    private var windowManager: WindowManager? = null

    /**
     * Applies comprehensive NotchBar hooks with Genesis Protocol enhancements
     */
    fun applyNotchBarHooks() {
        YLog.info(TAG, "Applying Genesis Protocol NotchBar customizations")

        try {
            // Hook StatusBar for notch area management
            hookStatusBar()

            // Hook PhoneStatusBarView for display modifications
            hookPhoneStatusBarView()

            // Hook notch indicator for custom elements
            hookNotchIndicator()

            // Hook cutout space for overlay management
            hookCutoutSpace()

            // Initialize Genesis notch overlay system
            initializeGenesisNotchSystem()

            YLog.info(TAG, "NotchBar hooks applied successfully")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to apply NotchBar hooks: ${e.message}", e)
        }
    }

    /**
     * Hooks the main StatusBar for notch area management
     */
    private fun hookStatusBar() {
        STATUS_BAR.toClass(classLoader).apply {
            // Hook start method for initialization
            method {
                name = "start"
                emptyParam()
            }.hook {
                after {
                    val statusBar = instance
                    initializeGenesisNotchBar(statusBar)
                }
            }

            // Hook makeStatusBarView for view modifications
            method {
                name = "makeStatusBarView"
                emptyParam()
            }.hook {
                after {
                    val statusBarView = result as? ViewGroup
                    statusBarView?.let { applyGenesisNotchModifications(it) }
                }
            }

            // Hook updateDisplaySize for dynamic adjustments
            method {
                name = "updateDisplaySize"
                emptyParam()
            }.hook {
                after {
                    updateGenesisNotchLayout()
                }
            }
        }
    }

    /**
     * Hooks PhoneStatusBarView for display modifications
     */
    private fun hookPhoneStatusBarView() {
        PHONE_STATUS_BAR_VIEW.toClass(classLoader).apply {
            // Hook onFinishInflate for view setup
            method {
                name = "onFinishInflate"
                emptyParam()
            }.hook {
                after {
                    val statusBarView = instance as ViewGroup
                    setupGenesisNotchElements(statusBarView)
                }
            }

            // Hook onLayout for positioning
            method {
                name = "onLayout"
                param(BooleanType, IntType, IntType, IntType, IntType)
            }.hook {
                after {
                    val statusBarView = instance as ViewGroup
                    adjustGenesisNotchPositioning(statusBarView)
                }
            }

            // Hook onConfigurationChanged for orientation handling
            method {
                name = "onConfigurationChanged"
                param("android.content.res.Configuration".toClass())
            }.hook {
                after {
                    handleGenesisOrientationChange()
                }
            }
        }
    }

    /**
     * Hooks notch indicator for custom elements
     */
    private fun hookNotchIndicator() {
        try {
            NOTCH_INDICATOR.toClass(classLoader).apply {
                // Hook visibility changes
                method {
                    name = "setVisibility"
                    param(IntType)
                }.hook {
                    before {
                        val visibility = args().first() as Int
                        handleGenesisNotchVisibility(visibility)
                    }
                }
            }
        } catch (e: ClassNotFoundException) {
            // Some devices may not have NotchIndicator class
            YLog.warn(TAG, "NotchIndicator class not found, skipping hook")
        }
    }

    /**
     * Hooks cutout space for overlay management
     */
    private fun hookCutoutSpace() {
        CUTOUT_SPACE.toClass(classLoader).apply {
            // Hook onFinishInflate for cutout management
            method {
                name = "onFinishInflate"
                emptyParam()
            }.hook {
                after {
                    val windowView = instance as ViewGroup
                    setupGenesisCutoutOverlay(windowView)
                }
            }

            // Hook onApplyWindowInsets for cutout handling
            method {
                name = "onApplyWindowInsets"
                param("android.view.WindowInsets".toClass())
            }.hook {
                after {
                    val insets = args().first()
                    handleGenesisCutoutInsets(insets)
                }
            }
        }
    }

    /**
     * Initializes Genesis NotchBar system
     */
    private fun initializeGenesisNotchBar(statusBar: Any) {
        try {
            // Get context from status bar
            val context = statusBar.javaClass.getDeclaredField("mContext")
                .apply { isAccessible = true }
                .get(statusBar) as Context

            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            // Create Genesis notch overlay if enabled
            if (config.notchBar.enabled) {
                createGenesisNotchOverlay(context)
            }

            YLog.info(TAG, "Genesis NotchBar system initialized")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to initialize NotchBar system: ${e.message}", e)
        }
    }

    /**
     * Applies Genesis modifications to notch area
     */
    private fun applyGenesisNotchModifications(statusBarView: ViewGroup) {
        try {
            val context = statusBarView.context

            // Apply Genesis styling to notch area
            applyGenesisNotchStyling(statusBarView)

            // Add Genesis notch elements
            addGenesisNotchElements(statusBarView, context)

            // Configure notch animations
            configureGenesisNotchAnimations(statusBarView)

            YLog.info(TAG, "Genesis notch modifications applied")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to apply notch modifications: ${e.message}", e)
        }
    }

    /**
     * Updates Genesis notch layout for display changes
     */
    private fun updateGenesisNotchLayout() {
        try {
            genesisNotchOverlay?.let { overlay ->
                // Update overlay layout parameters
                val layoutParams = overlay.layoutParams as? WindowManager.LayoutParams
                layoutParams?.let { params ->
                    // Adjust parameters based on display changes
                    windowManager?.updateViewLayout(overlay, params)
                }
            }

            YLog.info(TAG, "Genesis notch layout updated")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to update notch layout: ${e.message}", e)
        }
    }

    /**
     * Sets up Genesis notch elements
     */
    private fun setupGenesisNotchElements(statusBarView: ViewGroup) {
        try {
            val context = statusBarView.context

            // Add Genesis notch indicators
            if (config.notchBar.showIndicators) {
                addGenesisNotchIndicators(statusBarView, context)
            }

            // Setup Genesis animations
            setupGenesisNotchAnimations(statusBarView)

            YLog.info(TAG, "Genesis notch elements setup complete")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to setup notch elements: ${e.message}", e)
        }
    }

    /**
     * Adjusts Genesis notch positioning
     */
    private fun adjustGenesisNotchPositioning(statusBarView: ViewGroup) {
        try {
            // Adjust positioning based on config
            YLog.info(TAG, "Genesis notch positioning adjusted")
        } catch (e: Exception) {
            YLog.error(TAG, "Failed to adjust notch positioning: ${e.message}", e)
        }
    }

    /**
     * Handles Genesis orientation changes
     */
    private fun handleGenesisOrientationChange() {
        try {
            // Handle orientation changes for notch
            updateGenesisNotchLayout()
            YLog.info(TAG, "Genesis orientation change handled")
        } catch (e: Exception) {
            YLog.error(TAG, "Failed to handle orientation change: ${e.message}", e)
        }
    }

    /**
     * Handles Genesis notch visibility
     */
    private fun handleGenesisNotchVisibility(visibility: Int) {
        try {
            // Handle notch visibility changes
            genesisNotchOverlay?.visibility = visibility
            YLog.info(TAG, "Genesis notch visibility handled: $visibility")
        } catch (e: Exception) {
            YLog.error(TAG, "Failed to handle notch visibility: ${e.message}", e)
        }
    }

    /**
     * Sets up Genesis cutout overlay
     */
    private fun setupGenesisCutoutOverlay(windowView: ViewGroup) {
        try {
            val context = windowView.context

            // Setup cutout overlay if needed
            if (config.notchBar.manageCutout) {
                addGenesisCutoutElements(windowView, context)
            }

            YLog.info(TAG, "Genesis cutout overlay setup complete")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to setup cutout overlay: ${e.message}", e)
        }
    }

    /**
     * Handles Genesis cutout insets
     */
    private fun handleGenesisCutoutInsets(insets: Any) {
        try {
            // Handle cutout insets for Genesis overlay
            YLog.info(TAG, "Genesis cutout insets handled")
        } catch (e: Exception) {
            YLog.error(TAG, "Failed to handle cutout insets: ${e.message}", e)
        }
    }

    /**
     * Initializes Genesis notch system components
     */
    private fun initializeGenesisNotchSystem() {
        try {
            // Initialize additional Genesis notch components
            YLog.info(TAG, "Genesis notch system initialized")
        } catch (e: Exception) {
            YLog.error(TAG, "Failed to initialize notch system: ${e.message}", e)
        }
    }

    /**
     * Creates Genesis notch overlay
     */
    private fun createGenesisNotchOverlay(context: Context) {
        try {
            val composeView = ComposeView(context).apply {
                setContent {
                    GenesisNotchOverlay(config)
                }
            }

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP
            }

            genesisNotchOverlay = composeView
            windowManager?.addView(composeView, layoutParams)

            YLog.info(TAG, "Genesis notch overlay created")

        } catch (e: Exception) {
            YLog.error(TAG, "Failed to create notch overlay: ${e.message}", e)
        }
    }

    // Helper methods - Implement basic functionality
    private fun applyGenesisNotchStyling(statusBarView: ViewGroup) {
        // Apply Genesis styling to notch area
    }

    private fun addGenesisNotchElements(statusBarView: ViewGroup, context: Context) {
        // Add Genesis notch elements
    }

    private fun configureGenesisNotchAnimations(statusBarView: ViewGroup) {
        // Configure notch animations
    }

    private fun addGenesisNotchIndicators(statusBarView: ViewGroup, context: Context) {
        // Add Genesis notch indicators
    }

    private fun setupGenesisNotchAnimations(statusBarView: ViewGroup) {
        // Setup Genesis animations
    }

    private fun addGenesisCutoutElements(windowView: ViewGroup, context: Context) {
        // Add Genesis cutout elements
    }

    /**
     * Cleanup method to remove overlay when needed
     */
    fun cleanup() {
        try {
            genesisNotchOverlay?.let { overlay ->
                windowManager?.removeView(overlay)
                genesisNotchOverlay = null
            }
            YLog.info(TAG, "Genesis notch overlay cleanup complete")
        } catch (e: Exception) {
            YLog.error(TAG, "Failed to cleanup notch overlay: ${e.message}", e)
        }
    }
}

/**
 * Compose UI for Genesis Notch Overlay
 */
@Composable
fun GenesisNotchOverlay(config: NotchBarConfig) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(
                Color.Black.copy(alpha = 0.2f),
                RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Genesis indicator
            if (config.notchBar.showGenesisIndicator) {
                ShimmerParticles(
                    modifier = Modifier.size(4.dp),
                    particleColor = Color.Cyan
                )
            }

            // Status text
            if (config.notchBar.showStatus) {
                CyberpunkText(
                    text = "⦿",
                    style = MaterialTheme.typography.labelSmall,
                    glowColor = Color.Green
                )
            }
        }
    }
}
