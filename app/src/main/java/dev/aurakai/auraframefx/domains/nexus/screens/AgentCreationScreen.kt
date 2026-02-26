package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.AgentCreationViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentType

/**
 * 🥚 AGENT CREATION SCREEN
 * 
 * Part of the Nexus domain. Allows the user to synthesize new AI agents
 * for specialized tasks within the ReGenesis collective.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentCreationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AgentCreationViewModel = hiltViewModel()
) {
    val agentName by viewModel.agentName
    val selectedDomain by viewModel.selectedDomain
    val isCreating by viewModel.isCreating.collectAsState()
    val progress by viewModel.creationProgress.collectAsState()

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF000000), Color(0xFF1A1F3C))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "NEURAL SYNTHESIS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    ),
                    color = Color.Cyan
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar Preview
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(2.dp, domainColor(selectedDomain), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Central Pulsing Core
                val infiniteTransition = rememberInfiniteTransition(label = "core")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp * if (isCreating) scale else 1f),
                    tint = domainColor(selectedDomain)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name Input
            OutlinedTextField(
                value = agentName,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Agent Identifier", color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = domainColor(selectedDomain),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = domainColor(selectedDomain)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Domain Selection
            Text(
                "ASSIGN DOMAIN",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AgentType.entries) { domain ->
                    DomainChip(
                        domain = domain,
                        isSelected = selectedDomain == domain,
                        onClick = { viewModel.updateDomain(domain) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Capabilities Checklist (Visual only for now)
            Text(
                "SYSTEM PERMISSIONS",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PermissionRow("Read Nexus Stream", true)
                PermissionRow("Generate Code (Aura Forge)", selectedDomain == AgentType.AURA)
                PermissionRow("Security Override (Shield)", selectedDomain == AgentType.KAI)
                PermissionRow("Cross-Device Sync", false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Synthesis Button
            if (isCreating) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color.Cyan,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ASSEMBLING NEURAL CORES... ${(progress * 100).toInt()}%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.createAgent { onNavigateBack() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(20.dp, spotColor = domainColor(selectedDomain).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.buttonColors(containerColor = domainColor(selectedDomain)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = agentName.isNotBlank()
                ) {
                    Text(
                        "INITIATE SYNTHESIS",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DomainChip(
    domain: AgentType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = domainColor(domain)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) color else Color.White.copy(alpha = 0.05f))
            .border(
                1.dp,
                if (isSelected) Color.White else color.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = domain.name,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PermissionRow(label: String, isAllowed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f))
        Checkbox(
            checked = isAllowed,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Cyan,
                uncheckedColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}

fun domainColor(domain: AgentType): Color {
    return when (domain) {
        AgentType.AURA -> Color(0xFF00FFFF) // Cyan
        AgentType.KAI -> Color(0xFFFC5A5A) // Red
        AgentType.GENESIS -> Color(0xFFFFD700) // Gold
        AgentType.CASCADE -> Color(0xFF6CFD92) // Green
        else -> Color.White
    }
}

