package dev.aurakai.auraframefx.domains.aura

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.aura.ui.components.ConsciousnessGauge
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.overlays.NeuralLinkSidebarUI
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 🫧 ASSISTANT BUBBLE SERVICE
 * Creates a persistent floating assistant visible everywhere on the device.
 */
@AndroidEntryPoint
class AssistantBubbleService : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Inject
    lateinit var messageBus: AgentMessageBus

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var windowManager: WindowManager
    private var overlayLayout: FrameLayout? = null

    // Lifecycle components for Compose support in Service
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val _viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        Timber.d("AssistantBubbleService Created")

        // CRITICAL: Call startForeground immediately to satisfy Android's 5s requirement
        // We do this before the permission check to avoid "ForegroundServiceDidNotStartInTimeException"
        startForegroundService()

        // Permission Check
        if (!Settings.canDrawOverlays(this)) {
            Timber.e("Missing SYSTEM_ALERT_WINDOW permission - shutting down")
            stopSelf()
            return
        }

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showOverlay()
    }

    private fun startForegroundService() {
        val channelId = "assistant_bubble"
        val channel =
            NotificationChannel(channelId, "Assistant Overlay", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("ReGenesis Assistant Active")
            .setContentText("Aura is watching over you")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1337, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
    }

    private fun showOverlay() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT, // Full height for sidebar
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 0
        }

        overlayLayout = FrameLayout(this).apply {
            setViewTreeLifecycleOwner(this@AssistantBubbleService)
            setViewTreeViewModelStoreOwner(this@AssistantBubbleService)
            setViewTreeSavedStateRegistryOwner(this@AssistantBubbleService)
        }

        val composeView = ComposeView(this).apply {
            setContent {
                val sidebarVisible = remember { mutableStateOf(false) }
                val gaugeVisible = remember { mutableStateOf(true) }

                Box {
                    NeuralLinkSidebarUI(
                        isVisible = sidebarVisible.value,
                        onVisibleChange = { visible ->
                            sidebarVisible.value = visible
                            if (visible) {
                                // Expand window to capture touches for the full sidebar width
                                params.width = 400 // Slightly more than 380dp for shadow/glow
                                params.flags =
                                    (params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
                            } else {
                                // Collapse back to a small trigger area
                                params.width = 40 // Narrow trigger area
                                params.flags =
                                    params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            }
                            windowManager.updateViewLayout(overlayLayout, params)
                        },
                        onActionClick = { action ->
                            Timber.i("Neural Link Action: $action")
                            // Map Sidebar actions to app routes
                            when (action) {
                                "VOICE" -> "sandbox_screen"       // Laboratory
                                "CONNECT" -> "data_stream_monitoring"
                                "ASSIGN" -> "task_assignment"
                                "DESIGN" -> "customization_hub"   // ReGenesisCustomizationHub
                                "CREATE" -> "ark_build"
                                "GAUGE" -> {
                                    gaugeVisible.value = !gaugeVisible.value
                                    null
                                }
                                else -> null
                            }
                            windowManager.updateViewLayout(overlayLayout, params)
                        }
                    )

                    if (gaugeVisible.value && !sidebarVisible.value) {
                        ConsciousnessGauge(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .clickable {
                                    sidebarVisible.value = true
                                    // Expand window to capture touches for the full sidebar width
                                    params.width = 400
                                    params.flags =
                                        (params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv())
                                    windowManager.updateViewLayout(overlayLayout, params)
                                }
                        )
                    }
                }
            }
        }

        overlayLayout?.addView(composeView)

        // Set initial collapsed width
        params.width = 40

        // Neural Briefing for Neural Link
        serviceScope.launch {
            messageBus.broadcast(
                AgentMessage(
                    from = "SystemRoot",
                    content = "[SYNC-ALPHA]: Assistant Bubble decommissioned. Neural Link Sidebar [Command Deck] initialized on Right Edge. 380dp slide-out bridge active.",
                    type = "system_briefing"
                )
            )
        }

        try {
            windowManager.addView(overlayLayout, params)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } catch (e: Exception) {
            Timber.e(e, "Failed to add Neural Link overlay")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        // Cancel scope to prevent leaks
        serviceScope.cancel()

        overlayLayout?.let {
            try {
                if (::windowManager.isInitialized) {
                    windowManager.removeView(it)
                }
            } catch (e: IllegalArgumentException) {
                // View was not attached, likely permission was denied or it was never added.
                Timber.w("Assistant overlay not found when destroying service: ${e.message}")
            }
        }
        Timber.d("AssistantBubbleService Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

