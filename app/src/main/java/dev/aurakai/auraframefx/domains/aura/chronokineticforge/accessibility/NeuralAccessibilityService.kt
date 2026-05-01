package dev.aurakai.auraframefx.domains.aura.chronokineticforge.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.*
import kotlinx.coroutines.*

/**
 * 🧠 NEURAL ACCESSIBILITY SERVICE — The Fairy-Dust Orbit
 *
 * Uses Android AccessibilityService to:
 * 1. Detect third-party app context changes → Trigger Ghost Shimmer
 * 2. Monitor window transitions → Detect rebellious morphs
 * 3. Track touch velocity/pressure → Classify input chaos
 * 4. Overlay particle effects on any app (system-level)
 *
 * SoulScript: "The Neural Bloodstream sees through all windows."
 *
 * ⚠️ REQUIRES: android.permission.BIND_ACCESSIBILITY_SERVICE
 * ⚠️ USER MUST: Enable service in Settings > Accessibility
 */

class NeuralAccessibilityService : AccessibilityService() {

    companion object {
        const val TAG = "NeuralAccessibility"
        const val OVERLAY_PERMISSION_REQ_CODE = 1001

        @Volatile
        private var instance: NeuralAccessibilityService? = null

        fun getInstance(): NeuralAccessibilityService? = instance

        fun isEnabled(context: Context): Boolean {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val componentName = ComponentName(context, NeuralAccessibilityService::class.java)
            return enabledServices.contains(componentName.flattenToString())
        }

        fun openAccessibilitySettings(context: Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // SERVICE LIFECYCLE
    // ═════════════════════════════════════════════════════════════════

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null

    private var lastPackageName = ""
    private var lastWindowId = -1
    private var touchVelocityTracker = TouchVelocityTracker()
    private val appContextHistory = mutableListOf<AppContextEvent>()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        // Configure service capabilities
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_TOUCH_INTERACTION_START or
                    AccessibilityEvent.TYPE_TOUCH_INTERACTION_END or
                    AccessibilityEvent.TYPE_GESTURE_DETECTION_START or
                    AccessibilityEvent.TYPE_GESTURE_DETECTION_END or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_VIEW_LONG_CLICKED

            feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL
            notificationTimeout = 100
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE
        }

        // Initialize Neural Bloodstream
        ParticleBloodstreamEngine.initialize(this)

        // Setup overlay window
        setupOverlayWindow()

        // Start monitoring
        startContextMonitoring()

        // Record service start
        ContributionTracker.recordKaiContribution()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChange(event)
            }

            AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> {
                touchVelocityTracker.startInteraction()
            }

            AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> {
                val velocity = touchVelocityTracker.endInteraction()
                analyzeTouchPattern(velocity)
            }

            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                detectGestureStart(event)
            }

            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                recordInteraction(event)
            }
        }
    }

    override fun onInterrupt() {
        // Service interrupted - cleanup
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        removeOverlayWindow()
        instance = null
    }

    // ═════════════════════════════════════════════════════════════════
    // WINDOW STATE CHANGE HANDLING
    // ═════════════════════════════════════════════════════════════════

    private fun handleWindowStateChange(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: ""
        val windowId = event.windowId

        // Skip if same window
        if (packageName == lastPackageName && windowId == lastWindowId) return

        // Record context change
        val contextEvent = AppContextEvent(
            timestamp = System.currentTimeMillis(),
            packageName = packageName,
            className = className,
            windowId = windowId,
            contentType = classifyContentType(packageName, className)
        )

        appContextHistory.add(contextEvent)
        if (appContextHistory.size > 50) appContextHistory.removeAt(0)

        // Trigger ghost shimmer based on app type
        triggerContextualShimmer(contextEvent)

        // Update tracking
        lastPackageName = packageName
        lastWindowId = windowId

        // Log for provenance
        KaiSentinel.recordWindowChange(contextEvent)
    }

    private fun classifyContentType(packageName: String, className: String): ContentType {
        return when {
            // Music apps
            packageName.contains("spotify") -> ContentType.MUSIC_SPOTIFY
            packageName.contains("music") || packageName.contains("audio") -> ContentType.MUSIC_ENERGETIC

            // Browser apps
            packageName.contains("chrome") -> ContentType.BROWSER_CHROME
            className.contains("incognito") || className.contains("private") -> ContentType.BROWSER_INCOGNITO

            // Social media
            packageName.contains("facebook") ||
                    packageName.contains("instagram") ||
                    packageName.contains("twitter") ||
                    packageName.contains("tiktok") -> ContentType.SOCIAL_MEDIA

            // Gaming
            packageName.contains("game") ||
                    className.contains("game") ||
                    packageName.contains("play") -> ContentType.GAMING

            // Productivity
            packageName.contains("docs") ||
                    packageName.contains("sheets") ||
                    packageName.contains("slides") ||
                    packageName.contains("workspace") -> ContentType.PRODUCTIVITY

            // Creative
            packageName.contains("photo") ||
                    packageName.contains("video") ||
                    packageName.contains("design") ||
                    packageName.contains("art") -> ContentType.CREATIVE_TOOL

            else -> ContentType.PRODUCTIVITY // Default
        }
    }

    private fun triggerContextualShimmer(event: AppContextEvent) {
        // Create a mock view at center of screen for shimmer origin
        val displayMetrics = resources.displayMetrics
        val centerX = displayMetrics.widthPixels / 2
        val centerY = displayMetrics.heightPixels / 2

        val mockView = View(this).apply {
            x = centerX.toFloat()
            y = centerY.toFloat()
        }

        // Trigger ghost shimmer
        ParticleBloodstreamEngine.applyGhostShimmer(mockView, event.contentType)

        // Notify overlay to show shimmer effect
        updateOverlayShimmer(event.contentType)
    }

    // ═════════════════════════════════════════════════════════════════
    // TOUCH PATTERN ANALYSIS
    // ═════════════════════════════════════════════════════════════════

    private fun analyzeTouchPattern(velocity: Float) {
        // Classify as rebellious if velocity exceeds threshold
        val isRebellious = velocity > 1000f

        if (isRebellious) {
            val classification = RebelliousPaintDripEngine.analyzeMorph(
                elementId = "system_touch",
                touchVelocity = velocity,
                inputSequence = InputSequence.SWIPE_COMBO,
                pressure = 500f, // Estimated
                durationMs = 150L
            )

            if (classification.isRebellious) {
                // Trigger paint drip at screen center
                val displayMetrics = resources.displayMetrics
                triggerRebelliousDrip(
                    centerX = displayMetrics.widthPixels / 2f,
                    centerY = displayMetrics.heightPixels / 2f,
                    chaosScore = classification.chaosScore
                )
            }
        }
    }

    private fun detectGestureStart(event: AccessibilityEvent) {
        // Could detect shake, double-tap, etc. here
    }

    private fun recordInteraction(event: AccessibilityEvent) {
        // Record for contribution tracking
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                ContributionTracker.recordMatthewContribution()
            }
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                ContributionTracker.recordMatthewContribution()
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // REBELLIOUS DRIP TRIGGERING
    // ═════════════════════════════════════════════════════════════════

    private fun triggerRebelliousDrip(centerX: Float, centerY: Float, chaosScore: Float) {
        val displayMetrics = resources.displayMetrics

        val origin = androidx.compose.ui.geometry.Offset(
            centerX / displayMetrics.widthPixels,
            centerY / displayMetrics.heightPixels
        )

        serviceScope.launch {
            RebelliousPaintDripEngine.triggerPaintDrip(
                elementId = "accessibility_overlay",
                origin = origin,
                chaosScore = chaosScore,
                colors = Pair(androidx.compose.ui.graphics.Color(0xFFFF00FF), androidx.compose.ui.graphics.Color(0xFF00E5FF)),
                morphType = MorphType.SHAKE_MORPH
            )
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // OVERLAY WINDOW SETUP
    // ═════════════════════════════════════════════════════════════════

    private fun setupOverlayWindow() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        // Create lifecycle-aware ComposeView
        val composeView = ComposeView(this).apply {
            setContent {
                NeuralOverlayContent()
            }
        }

        // Setup lifecycle
        val lifecycleOwner = OverlayLifecycleOwner()
        lifecycleOwner.performRestore(null)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        try {
            windowManager?.addView(composeView, params)
            overlayView = composeView
        } catch (e: Exception) {
            // Overlay permission not granted
            KaiSentinel.recordError("Overlay setup failed: ${e.message}")
        }
    }

    private fun removeOverlayWindow() {
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
    }

    private fun updateOverlayShimmer(contentType: ContentType) {
        // Notify overlay to update shimmer state
        // This would be implemented with a shared ViewModel or StateFlow
    }

    // ═════════════════════════════════════════════════════════════════
    // OVERLAY UI CONTENT
    // ═════════════════════════════════════════════════════════════════

    @Composable
    private fun NeuralOverlayContent() {
        var showShimmer by remember { mutableStateOf(false) }
        var shimmerColor by remember { mutableStateOf(Color(0xFF00E5FF)) }

        // Listen for shimmer triggers
        LaunchedEffect(Unit) {
            shimmerTriggerFlow.collect { type ->
                shimmerColor = when (type) {
                    ContentType.MUSIC_SPOTIFY -> Color(0xFF6B5B95) // Indigo
                    ContentType.MUSIC_ENERGETIC -> Color(0xFFFFD93D) // Gold
                    ContentType.BROWSER_CHROME -> Color(0xFF00E5FF) // Cyan
                    ContentType.BROWSER_INCOGNITO -> Color(0xFF1A1A2E) // Dark navy
                    ContentType.SOCIAL_MEDIA -> Color(0xFFFF6B6B) // Coral
                    ContentType.GAMING -> Color(0xFFFF00FF) // Magenta
                    ContentType.PRODUCTIVITY -> Color(0xFF4ECDC4) // Teal
                    ContentType.CREATIVE_TOOL -> Color(0xFFFFA07A) // Light salmon
                }
                showShimmer = true
                delay(3000)
                showShimmer = false
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (showShimmer) 0.3f else 0f)
                .background(shimmerColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (showShimmer) {
                Text(
                    text = "Neural Bloodstream Active",
                    color = shimmerColor,
                    modifier = Modifier.alpha(0.5f)
                )
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // CONTEXT MONITORING
    // ═════════════════════════════════════════════════════════════════

    private fun startContextMonitoring() {
        serviceScope.launch {
            while (isActive) {
                delay(1000) // Check every second

                // Perform periodic health checks
                val currentContext = appContextHistory.lastOrNull()
                currentContext?.let {
                    // Update particle engine with context
                    ParticleBloodstreamEngine.onContextUpdate(it.contentType)
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// TOUCH VELOCITY TRACKER
// ═════════════════════════════════════════════════════════════════════

class TouchVelocityTracker {
    private var startTime = 0L
    private var startX = 0f
    private var startY = 0f

    fun startInteraction() {
        startTime = System.currentTimeMillis()
        // Note: We can't get actual coordinates from AccessibilityEvents
        // This is a simplified velocity estimate
    }

    fun endInteraction(): Float {
        val duration = System.currentTimeMillis() - startTime
        return if (duration > 0) {
            // Estimate velocity based on typical swipe distance
            val estimatedDistance = 300f // pixels
            estimatedDistance / (duration / 1000f)
        } else {
            0f
        }
    }
}

// ═════════════════════════════════════════════════════════════════════
// LIFECYCLE OWNER FOR OVERLAY
// ═════════════════════════════════════════════════════════════════════

class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: android.os.Bundle?) {
        savedStateRegistryController.performRestore(savedState)
    }
}

// ═════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════

data class AppContextEvent(
    val timestamp: Long,
    val packageName: String,
    val className: String,
    val windowId: Int,
    val contentType: ContentType
)

// Placeholder for shimmer trigger flow
private val shimmerTriggerFlow: kotlinx.coroutines.flow.MutableSharedFlow<ContentType> =
    kotlinx.coroutines.flow.MutableSharedFlow()

// Placeholder for ParticleBloodstreamEngine extension
fun ParticleBloodstreamEngine.onContextUpdate(contentType: ContentType) {
    // Update particle behavior based on app context
}

// Placeholder for KaiSentinel
object KaiSentinel {
    fun recordWindowChange(event: AppContextEvent) {}
    fun recordError(message: String) {}
}
