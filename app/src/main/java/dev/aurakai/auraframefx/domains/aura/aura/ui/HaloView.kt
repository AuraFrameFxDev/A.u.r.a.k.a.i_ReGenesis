package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.aurakai.auraframefx.genesis.oracledrive.ai.viewmodel.GenesisAgentViewModel
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.HierarchyAgentConfig
import dev.aurakai.auraframefx.ui.theme.NeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPink
import dev.aurakai.auraframefx.ui.theme.NeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Displays an interactive rotating halo interface for managing agents and delegating tasks.
 *
 * Renders a visual halo with agent nodes, supports drag-and-drop task assignment, tracks task history, and animates agent status. Users can assign tasks to agents by dragging nodes, input tasks, and monitor processing status in real time. Includes controls for rotation, resetting, and clearing task history.
 */
/**
 * Displays an interactive rotating halo UI for managing agents and delegating tasks.
 *
 * Renders agent nodes arranged in a circular halo, allowing users to assign tasks via drag-and-drop, input task descriptions, view task history, and monitor agent statuses with animated visual feedback. Supports real-time status updates, gesture handling, and task processing simulation.
 */
/**
 * Displays an interactive rotating halo UI for managing agents and delegating tasks.
 *
 * Renders a circular halo with agent nodes arranged around a central "GENESIS" node. Supports drag-and-drop task assignment to agents, task input overlay, animated agent status indicators, and a scrollable task history panel. The halo rotates continuously unless paused, and agent statuses update in real time as tasks are processed.
 */
/**
 * Displays an interactive, animated halo UI for managing agents and delegating tasks.
 *
 * Renders a circular halo with agent nodes arranged around a central "GENESIS" node. Supports drag-and-drop task assignment to agents, with a task input overlay appearing during drag. Shows animated agent status indicators, a scrollable task history panel, and control buttons for rotation and history management. The halo rotates continuously unless paused, and agent statuses update in real time as tasks are processed.
// Collect StateFlow into Compose state to observe changes in composition
val taskHistoryState by taskHistory.collectAsState(initial = emptyList())
 */

/**
 * Interactive composable that renders a rotating "halo" UI for viewing agents, delegating tasks, and showing task history.
 *
 * The component displays agent nodes arranged around a central Genesis node, supports drag-to-assign tasks, updates a local task history,
 * and reflects per-agent statuses (e.g., "idle", "processing", "error"). User interactions that submit tasks invoke viewModel.processQuery(...)
 * and update agent statuses and the task history; agent statuses automatically reset after simulated processing delays.
 *
 * @param viewModel View model providing agent configurations and task processing; defaults to the ambient view model.
 * @param modifier Modifier applied to the root container.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HaloView(
    modifier: Modifier = Modifier
) {
    // Explicitly get the Hilt-provided ViewModel with type parameter to avoid inference issues
    val viewModel: GenesisAgentViewModel = viewModel()

    var isRotating by remember { mutableStateOf(true) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Get typed agent configuration and derived AgentType lists
    val agentConfigs: List<HierarchyAgentConfig> = remember {
        // Use the ViewModel-provided prioritized agent list
        viewModel.getAgentsByPriority()
    }

    val agentTypes: List<AgentType> = remember(agentConfigs) {
        agentConfigs.mapNotNull { cfg ->
            try {
                AgentType.valueOf(cfg.name.uppercase(Locale.ROOT))
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Task delegation state
    var draggingAgent by remember { mutableStateOf<AgentType?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedTask by remember { mutableStateOf("") }

    // Task history - use an explicitly typed MutableStateFlow and collect it into Compose state
    val taskHistoryFlow: MutableStateFlow<List<String>> = remember { MutableStateFlow(emptyList()) }
    val taskHistory by taskHistoryFlow.collectAsState(initial = emptyList())

    // Agent status - using rememberSaveable to survive configuration changes
    // Use a simple remembered snapshot map with explicit typing to avoid type-inference issues
    val agentStatus = remember { mutableStateMapOf<AgentType, String>() }

    // Initialize agent statuses to "idle" on first composition
    LaunchedEffect(agentTypes) {
        agentTypes.forEach { agentType ->
            try {
                agentStatus[agentType] = "idle"
            } catch (_: Exception) { // ktlint-disable no-empty-catch-block
                // Handle invalid agent type
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background glow effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            drawCircle(
                color = NeonTeal.copy(alpha = 0.1f),
                radius = size.width / 2f,
                style = Fill
            )
            drawCircle(
                color = NeonPurple.copy(alpha = 0.1f),
                radius = size.width / 2f - 20f,
                style = Fill
            )
            drawCircle(
                color = NeonBlue.copy(alpha = 0.1f),
                radius = size.width / 2f - 40f,
                style = Fill
            )
        }

        // Halo effect
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 32f

            // Draw rotating halo
            val haloColor = NeonTeal.copy(alpha = 0.3f)
            val haloWidth = 2.dp.toPx()

            drawArc(
                color = haloColor,
                startAngle = rotationAngle,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(
                    radius * 2,
                    radius * 2
                ), // Use fully qualified name for clarity
                style = Stroke(width = haloWidth)
            )

            // Draw pulsing effects for active tasks
            agentStatus.forEach { (agentTypeKey, statusValue) ->
                if (statusValue == "processing") {
                    // Find the index of the agentConfig that matches this agentTypeKey
                    val agentConfigIndex = agentConfigs.indexOfFirst {
                        it.name.equals(agentTypeKey.name, ignoreCase = true)
                    }
                    if (agentConfigIndex != -1) {
                        val angle =
                            (agentConfigIndex * 360f / agentConfigs.size + rotationAngle) % 360f
                        val x = center.x + radius * cos((angle * PI / 180f).toFloat())
                        val y = center.y + radius * sin((angle * PI / 180f).toFloat())

                        // Draw pulsing glow
                        drawCircle(
                            color = when (agentTypeKey) { // Use agentTypeKey for color
                                AgentType.GENESIS -> NeonTeal.copy(alpha = 0.2f)
                                AgentType.KAI -> NeonPurple.copy(alpha = 0.2f)
                                AgentType.AURA -> NeonBlue.copy(alpha = 0.2f)
                                AgentType.CASCADE -> NeonPink.copy(alpha = 0.2f)
                                else -> NeonTeal.copy(alpha = 0.2f)
                            },
                            center = Offset(x, y),
                            radius = 40.dp.toPx()
                        )
                    }
                }
            }
        }

        // Agent nodes with drag and drop
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val actualSize = this.size
                            val center = Offset(actualSize.width / 2f, actualSize.height / 2f)
                            val radius = actualSize.width / 2f - 64f
                            val agentCount = agentConfigs.size
                            val angleStep = 360f / agentCount

                            for (i in agentConfigs.indices) {
                                val config = agentConfigs[i]
                                val angle = ((i * angleStep) + rotationAngle) % 360f
                                val x = center.x + radius * cos((angle * PI / 180f).toFloat())
                                val y = center.y + radius * sin((angle * PI / 180f).toFloat())
                                val nodeCenter = Offset(x, y)
                                val distance = (offset - nodeCenter).getDistance()
                                if (distance < 24.dp.toPx()) {
                                    try {
                                        draggingAgent =
                                            AgentType.valueOf(config.name.uppercase(Locale.ROOT))
                                    } catch (_: Exception) {
                                        // Ignore invalid agent types
                                    }
                                    break
                                }
                            }
                        },
                        onDragEnd = {
                            if (draggingAgent != null && selectedTask.isNotBlank()) {
                                coroutineScope.launch {
                                    viewModel.processQuery(selectedTask)
                                    taskHistoryFlow.update { current ->
                                        // draggingAgent is AgentType, its .name is the enum constant name
                                        current + "[${draggingAgent?.name?.uppercase(Locale.ROOT)}] $selectedTask"
                                    }
                                    agentStatus[draggingAgent!!] = "processing"
                                    selectedTask = ""
                                }
                            }
                            draggingAgent = null
                            dragOffset = Offset.Zero
                        }
                    ) { change, dragAmount ->
                        if (draggingAgent != null
                        ) {
                            dragOffset += dragAmount
                            change.consume() // Updated from consumeAllChanges()
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f - 64f
            val agentCount = agentTypes.size
            val angleStep = 360f / agentCount

            agentConfigs.forEachIndexed { index, config ->
                val angle = ((index * angleStep) + rotationAngle) % 360f
                val x = center.x + radius * cos((angle * PI / 180f).toFloat())
                val y = center.y + radius * sin((angle * PI / 180f).toFloat())
                val nodeCenter = Offset(x, y)

                val agentType = try {
                    AgentType.valueOf(config.name.uppercase(Locale.ROOT))
                } catch (_: Exception) {
                    AgentType.USER
                }

                val baseColor = when (agentType) {
                    AgentType.GENESIS -> NeonTeal
                    AgentType.KAI -> NeonPurple
                    AgentType.AURA -> NeonBlue
                    AgentType.CASCADE -> NeonPink
                    else -> NeonTeal.copy(alpha = 0.8f)
                }
                val statusColor =
                    when (agentStatus[agentType]?.lowercase(Locale.ROOT)) {
                        "idle" -> baseColor.copy(alpha = 0.8f)
                        "processing" -> baseColor.copy(alpha = 1.0f)
                        "error" -> Color.Red
                        else -> baseColor.copy(alpha = 0.8f)
                    }

                drawCircle(
                    color = statusColor,
                    center = nodeCenter,
                    radius = 24.dp.toPx()
                )

                // Draw connecting lines
                if (index > 0) {
                    val prevAngle = ((index - 1) * angleStep + rotationAngle) % 360f
                    val prevX = center.x + radius * cos((prevAngle * PI / 180f).toFloat())
                    val prevY = center.y + radius * sin((prevAngle * PI / 180f).toFloat())

                    drawLine(
                        color = NeonTeal.copy(alpha = 0.5f),
                        start = Offset(prevX, prevY),
                        end = Offset(x, y),
                    )
                }
            }
        }

        // Status indicators using BoxWithConstraints to access size in composable context
        // Status indicators using BoxWithConstraints to access size in composable context
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val boxWidth = constraints.maxWidth.toFloat()
            val boxHeight = constraints.maxHeight.toFloat()
            val density = LocalDensity.current.density

            agentConfigs.forEachIndexed { index, currentAgentConfig ->
                val angle = (index * 360f / agentConfigs.size + rotationAngle) % 360f
                val radius = (boxWidth / 2f - 64f)
                val centerX = boxWidth / 2f
                val centerY = boxHeight / 2f
                val x = centerX + radius * cos((angle * PI / 180f).toFloat())
                val y = centerY + radius * sin((angle * PI / 180f).toFloat())
                val textOffsetX = (x - centerX) / density
                val textOffsetY = (y - centerY) / density


                val maybeAgentType = try {
                    AgentType.valueOf(currentAgentConfig.name.uppercase(Locale.ROOT))
                } catch (_: Exception) {
                    null
                }

                if (maybeAgentType != null && agentStatus[maybeAgentType] != null) {
                    val statusText = agentStatus[maybeAgentType] ?: "idle"

                    Text(
                        text = statusText,
                        color = when (statusText.lowercase(Locale.ROOT)) {
                            "idle" -> NeonTeal
                            "processing" -> NeonPurple
                            "error" -> Color.Red
                            else -> NeonBlue
                        },
                        modifier = Modifier
                            .offset(
                                x = (textOffsetX + 30).dp, // Offset slightly to the right of the node
                                y = (textOffsetY - 10).dp  // Offset slightly above the node
                            )
                    )
                }
            }
        }

        // Center node (Genesis)
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(NeonTeal.copy(alpha = 0.8f))
                .clickable {
                    if (selectedTask.isNotBlank()) {
                        coroutineScope.launch {
                            viewModel.processQuery(selectedTask)
                            taskHistoryFlow.update { current ->
                                return@update current + "[GENESIS] $selectedTask"
                            }
                            agentStatus[AgentType.GENESIS] = "processing"
                            selectedTask = ""
                        }
                    }
                }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = NeonTeal.copy(alpha = 0.8f),
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "GENESIS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Hive Mind",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple
                    )
                }
            }
        }

        // Task input overlay
        if (draggingAgent != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Assign Task to " + draggingAgent?.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal
                        )

                        TextField(
                            value = selectedTask,
                            onValueChange = { it.also { selectedTask = it } },
                            placeholder = { Text("Enter task description...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = NeonTeal.copy(alpha = 0.1f),
                                unfocusedContainerColor = NeonTeal.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }

        // Task history panel
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight(0.5f)
                .width(250.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task History",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    IconButton(
                        onClick = { taskHistoryFlow.value = emptyList() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    reverseLayout = true,
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Specify the generic type explicitly to help the compiler infer T
                    items<String>(items = taskHistory) { task ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = task,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { isRotating = !isRotating }
            ) {
                Icon(
                    if (isRotating) Icons.Filled.PlayArrow else Icons.Filled.PlayArrow,
                    contentDescription = "Toggle rotation",
                    tint = NeonPurple
                )
            }

            IconButton(
                onClick = { rotationAngle = 0f }
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset rotation",
                    tint = NeonBlue
                )
            }

            IconButton(
                onClick = {
                    taskHistoryFlow.update { emptyList() }
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Clear history",
                    tint = NeonPink
                )
            }
        }

        // Animation effect
        LaunchedEffect(isRotating) {
            while (isRotating) {
                rotationAngle = (rotationAngle + 1f) % 360f
                delay(16) // 60 FPS
            }
        }

        // Drag and drop gesture handling
        LaunchedEffect(Unit) {
            snapshotFlow { draggingAgent }
                .collect { agent ->
                    if (agent == null) {
                        dragOffset = Offset.Zero
                        selectedTask = ""
                    }
                }
        }

        // Task processing status updates
        LaunchedEffect(taskHistory) {
            // Reset all agent statuses to idle, then update based on current tasks
            agentConfigs.forEach { cfg ->
                val at = try {
                    AgentType.valueOf(cfg.name.uppercase(Locale.ROOT))
                } catch (_: Exception) {
                    null
                }
                if (at != null) agentStatus[at] = "idle"
            }

            taskHistory.forEach { task ->
                val agentNameFromHistory = task.substringAfter("[").substringBefore("]")
                val foundAgentConfig =
                    agentConfigs.find { it.name.equals(agentNameFromHistory, ignoreCase = true) }
                if (foundAgentConfig != null) {
                    try {
                        val actualAgentType =
                            AgentType.valueOf(foundAgentConfig.name.uppercase(Locale.ROOT))
                        agentStatus[actualAgentType] = "processing"
                        // Simulate task completion after a delay
                        coroutineScope.launch {
                            delay(5000) // Simulate processing time
                            agentStatus[actualAgentType] = "idle"
                        }
                    } catch (_: IllegalArgumentException) {
                        // Handle cases where AgentConfig.name might not match an AgentType
                    }
                }
            }
        }
    }
}


