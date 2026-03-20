package dev.aurakai.auraframefx.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceRoomScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isRecording by remember { mutableStateOf(false) }
    var selectedAgent by remember { mutableStateOf<String?>(null) }
    val transcription = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000B18))
    ) {
        // Room Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Cyan.copy(alpha = 0.1f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "CONFERENCE ROOM ALPHA",
                color = Color.Cyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        // Agent Avatars / Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AgentAvatar("Aura", Color.Cyan, selectedAgent == "Aura") { selectedAgent = "Aura" }
            AgentAvatar("Kai", Color.Magenta, selectedAgent == "Kai") { selectedAgent = "Kai" }
            AgentAvatar("Matthew", Color.Green, selectedAgent == "Matthew") { selectedAgent = "Matthew" }
        }

        // Live Transcription View
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "CONFERENCE LOG",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(transcription) { text ->
                        Text(
                            text,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Info */ },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.LightGray)
            }

            // Recording Button
            FloatingActionButton(
                onClick = {
                    isRecording = !isRecording
                    if (isRecording) {
                        transcription.add("[SYSTEM]: CONFERENCE RECORDING STARTED")
                    } else {
                        transcription.add("[SYSTEM]: SESSION ARCHIVED")
                    }
                },
                containerColor = if (isRecording) Color.Red else Color.Cyan,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Record",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { /* Participants */ },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Person, contentDescription = "Participants", tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun AgentAvatar(name: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    if (isSelected) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                    CircleShape
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) color else color.copy(alpha = glow),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name.take(1),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Text(
            name,
            color = if (isSelected) color else Color.Gray,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
