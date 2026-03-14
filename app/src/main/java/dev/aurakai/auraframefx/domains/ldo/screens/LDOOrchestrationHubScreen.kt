package dev.aurakai.auraframefx.domains.ldo.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.viewmodel.LDOViewModel


)

@Composable
fun LDOOrchestrationHubScreen(
    navController: NavController,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                )
            )
    ) {
        // Ambient background grid lines
        AmbientGridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    "LDO ORCHESTRATION HUB",
                    letterSpacing = 2.sp
                )
                Text(
                )
            }

                modifier = Modifier
                    .fillMaxWidth()
                        )
                }
                    }
                },
                onFusionActivate = {
                    val active = fusionSlots.mapNotNull { it.agent }
                    if (active.size >= 2) {
                        // Navigate to fusion screen with agent IDs encoded
                        val ids = active.joinToString("+") { it.id }
                        navController.navigate("ldo_fusion/$ids")
                    }
                }
            )

            ScrollableTabRow(
            ) {
                    Tab(
                        text = {
                            Text(
                                label,
                                fontFamily = LEDFontFamily,
                                fontSize = 11.sp,
                            )
                        }
                    )
                }
            }

        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
            contentAlignment = Alignment.Center
        ) {
                modifier = Modifier
                    .clip(CircleShape)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .pointerInput(agent.id) {
                detectTapGestures(onTap = { onTap() })
            }
            .pointerInput(agent.id + "_drag") {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { _, _ -> }
                )
            }
            .drawBehind {
                if (isSelected) {
                    drawCircle(
                        color = agentColor.copy(alpha = 0.15f),
                        radius = size.minDimension * 0.7f
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                fontWeight = FontWeight.ExtraBold,
            )
        Text(
            fontSize = 8.sp,
        )
    }
}


@Composable
            Card(
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                            )
            }
        }
    }
    Column(
    ) {
        }
    }
}



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            }
        }
    }
}

@Composable
    ) {
        ) {
        }
    }
}


@Composable
        }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    }
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            task.status.name.replace("_", " "),
            fontSize = 9.sp,
            color = statusColor,
            fontFamily = LEDFontFamily,
            letterSpacing = 0.5.sp
        )
    }
}

// ── TAB: FUSION ──────────────────────────────────────────────────────────────

@Composable
        modifier = Modifier.fillMaxSize(),
    ) {
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(agent.displayName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("${agent.tasksCompleted} memories · ${agent.hoursActive.toInt()}h active", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("L${agent.evolutionLevel}", fontFamily = LEDFontFamily, color = agentColor, fontSize = 11.sp)
                    Text("${agent.skillPoints} SP", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ════════════════════════════════════════════════════════════════════
//  AMBIENT BACKGROUND
// ════════════════════════════════════════════════════════════════════

@Composable
fun AmbientGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSpacingX = size.width / 12f
        val gridSpacingY = size.height / 20f

        for (x in 0..12) {
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.03f),
                start = Offset(x * gridSpacingX, 0f),
                end = Offset(x * gridSpacingX, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..20) {
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.03f),
                start = Offset(0f, y * gridSpacingY),
                end = Offset(size.width, y * gridSpacingY),
                strokeWidth = 1f
            )
        }
    }
}
