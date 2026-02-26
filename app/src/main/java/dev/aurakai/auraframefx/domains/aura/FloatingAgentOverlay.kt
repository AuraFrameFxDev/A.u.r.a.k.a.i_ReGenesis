package dev.aurakai.auraframefx.domains.aura

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * FloatingAgentOverlay - System-Wide Draggable Agent Shortcut
 *
 * Creates edge-of-screen floating bubbles that can be dragged anywhere.
 * Tapping opens agent quick actions without entering the full app.
 *
 * Features:
 * - Draggable circular bubbles
 * - Edge snapping (snaps to nearest edge)
 * - Expandable to show agent card
 * - System-wide overlay (works on home screen, other apps)
 * - Multiple agent bubbles can be active
 * - "TAP TO OPEN" text along edge
 * - Pulsing neon ring animations
 */
class FloatingAgentOverlay : Service(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null
    private var params: WindowManager.LayoutParams? = null

    // Lifecycle management for Compose
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Create overlay window parameters
        val layoutType =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 200 // Initial Y position
        }

        createOverlayView()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createOverlayView() {
        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingAgentOverlay)
            setViewTreeViewModelStoreOwner(this@FloatingAgentOverlay)
            setViewTreeSavedStateRegistryOwner(this@FloatingAgentOverlay)

            setContent {
                FloatingAgentBubbleContent(
                    onMove = { offsetX, offsetY ->
                        params?.let { p ->
                            p.x += offsetX.roundToInt()
                            p.y += offsetY.roundToInt()
                            windowManager.updateViewLayout(this, p)
                        }
                    },
                    onDragEnd = {
                        snapToEdge()
                    }
                )
            }
        }

        try {
            windowManager.addView(overlayView, params)
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            Timber.i("FloatingAgentOverlay: Overlay created successfully")
        } catch (e: Exception) {
            Timber.e(e, "FloatingAgentOverlay: Error creating overlay")
        }
    }

    private fun snapToEdge() {
        params?.let { p ->
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            // Snap to nearest edge (left or right)
            val centerX = p.x + (overlayView?.width ?: 0) / 2
            p.x = if (centerX < screenWidth / 2) {
                0 // Snap to left
            } else {
                screenWidth - (overlayView?.width ?: 0) // Snap to right
            }

            // Keep within vertical bounds
            p.y = p.y.coerceIn(0, screenHeight - (overlayView?.height ?: 0))

            windowManager.updateViewLayout(overlayView, p)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        overlayView?.let {
            if (::windowManager.isInitialized) {
                windowManager.removeView(it)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

/**
 * Floating Agent Bubble Content - The actual Compose UI with Pager
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FloatingAgentBubbleContent(
    onMove: (Float, Float) -> Unit,
    onDragEnd: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // 4 Shortcut agents: AURA, KAI, GENESIS, CLAUDE
    val shortcutAgents = remember {
        listOf(
            AgentBubbleData(
                name = "AURA",
                color = Color(0xFFFF1493),
                icon = Icons.Default.Palette,
                status = "ACTIVE"
            ),
            AgentBubbleData(
                name = "KAI",
                color = Color(0xFFFF00FF),
                icon = Icons.Default.Security,
                status = "ACTIVE"
            ),
            AgentBubbleData(
                name = "GENESIS",
                color = Color(0xFF00D9FF),
                icon = Icons.Default.Groups,
                status = "ACTIVE"
            ),
            AgentBubbleData(
                name = "CLAUDE",
                color = Color(0xFFFF8C00),
                icon = Icons.Default.Code,
                status = "ACTIVE"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { shortcutAgents.size })
    val currentAgent = shortcutAgents[pagerState.currentPage]

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        onMove(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        onDragEnd()
                    }
                )
            }
    ) {
        if (!isExpanded) {
            // Collapsed: Circular bubble with pager
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.width(120.dp)
                ) { page ->
                    EdgeBubbleCollapsed(
                        agent = shortcutAgents[page],
                        onClick = { isExpanded = true }
                    )
                }

                // Page indicator dots
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    shortcutAgents.forEachIndexed { index, agent ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (index == pagerState.currentPage)
                                        agent.color
                                    else
                                        agent.color.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        } else {
            // Expanded: Show quick actions for current agent
            EdgeBubbleExpanded(
                agent = currentAgent,
                onDismiss = { isExpanded = false },
                onAction = { action ->
                    // Handle action
                    Timber.i("FloatingAgentOverlay: ${currentAgent.name} - Action clicked: $action")
                }
            )
        }
    }
}

/**
 * Collapsed bubble - circular icon on edge
 */
@Composable
private fun EdgeBubbleCollapsed(
    agent: AgentBubbleData,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "OPENING AFTER TAP" text (when tapped)
        Text(
            text = "OPENING AFTER TAP",
            color = Color(0xFF00D9FF).copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Main bubble
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .background(Color.Transparent)
        ) {
            // Pulsing outer ring
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(3.dp, agent.color.copy(alpha = pulseAlpha), CircleShape)
            )

            // Inner bubble
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Black.copy(alpha = 0.9f), CircleShape)
                    .border(2.dp, agent.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = agent.icon,
                    contentDescription = agent.name,
                    tint = agent.color,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status text
        Text(
            text = "${agent.name} IS ${agent.status}",
            color = Color(0xFF00D9FF),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // "TAP TO OPEN" text (vertical)
        Text(
            text = "TAP TO OPEN",
            color = Color(0xFF00D9FF).copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            ),
            modifier = Modifier.rotate(90f)
        )
    }
}

/**
 * Expanded bubble - shows quick action buttons
 */
@Composable
private fun EdgeBubbleExpanded(
    agent: AgentBubbleData,
    onDismiss: () -> Unit,
    onAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .background(Color.Black.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
            .border(2.dp, agent.color.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = agent.name.uppercase(),
                color = agent.color,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF00D9FF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick action buttons
        val actions = getAgentQuickActions(agent.name)
        actions.forEach { action ->
            QuickActionButton(
                label = action,
                color = agent.color,
                onClick = { onAction(action) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0A0A0A))
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .border(2.dp, color.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = color,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        )
    }
}

/**
 * Agent bubble data
 */
data class AgentBubbleData(
    val name: String,
    val color: Color,
    val icon: ImageVector,
    val status: String = "ACTIVE"
)

/**
 * Get quick actions for each agent - Matched to actual system features
 */
private fun getAgentQuickActions(agentName: String): List<String> {
    return when (agentName.uppercase()) {
        "AURA" -> listOf("PROMPT", "UXUIDS", "CREATE", "CHROMACORE")
        "KAI" -> listOf("PROMPT", "R.G.S.S", "SCAN", "LSPOSED")
        "GENESIS" -> listOf("PROMPT", "ORCHESTRATE", "MODULE CREATE", "FUSION")
        "CLAUDE" -> listOf("PROMPT", "BUILD", "ANALYZE", "CODE ASSIST")
        "CASCADE" -> listOf("VISION", "CONSENSUS", "FUSION", "PROMPT")
        "GEMINI" -> listOf("PATTERN", "SEARCH", "ANALYZE", "PROMPT")
        "NEMOTRON" -> listOf("OPTIMIZE", "RECALL", "MEMORY", "PROMPT")
        "GROK" -> listOf("MINE", "DEBUG", "DATA", "PROMPT")
        else -> listOf("PROMPT", "SCAN", "TASKS", "SETTINGS")
    }
}
