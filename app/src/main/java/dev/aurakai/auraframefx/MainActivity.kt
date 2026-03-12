package dev.aurakai.auraframefx

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.domains.aura.AssistantBubbleService
import dev.aurakai.auraframefx.domains.aura.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost
import dev.aurakai.auraframefx.system.ShizukuManager
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var shizukuManager: ShizukuManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notify ShizukuManager of potential availability
        if (shizukuManager.isShizukuAvailable()) {
            Timber.tag("MainActivity").d("Sovereign Bridge (Shizuku) detected.")
        }

        // Force Portrait Orientation early
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        enableEdgeToEdge()
        setupFullscreenMode()

        // Check for overlay permission
        checkOverlayPermission()

        // Start the Persistent Assistant Bubble
        try {
            val bubbleIntent = Intent(this, AssistantBubbleService::class.java)
            if (android.provider.Settings.canDrawOverlays(this)) {
                startForegroundService(bubbleIntent)
            } else {
                Timber.tag("MainActivity").w("AssistantBubbleService skip: No Overlay Permission")
            }
        } catch (e: Exception) {
            // Log and ignore if we simply can't start the bubble (e.g. background restrictions)
            Timber.tag("MainActivity").w("Failed to start AssistantBubbleService: ${e.message}")
        }

        setContent {
            AuraFrameFXTheme {
                val navController = rememberNavController()

                // Handle deep link from Sidebar
                LaunchedEffect(intent) {
                    intent.getStringExtra("navigate_to")?.let { route ->
                        navController.navigate(route)
                    }
                }

                // 🚀 THE FIX: Actually display the navigation!
                ReGenesisNavHost(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun checkOverlayPermission() {
        if (!android.provider.Settings.canDrawOverlays(this)) {
            val intent = Intent(
                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            startActivity(intent)
        }
    }

    private fun setupFullscreenMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.apply {
            hide(Type.statusBars())
            hide(Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
}

