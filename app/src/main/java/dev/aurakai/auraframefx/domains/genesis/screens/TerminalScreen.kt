package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TerminalScreen() {
    var input by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<TerminalLine>() }
    val scope = rememberCoroutineScope()

    val terminalGreen = Color(0xFF00FF41)
    val darkBg = Color(0xFF0D0D0D)

    LaunchedEffect(Unit) {
        history.add(TerminalLine("GENESIS-OS [Version 0.7.1-LDO]", TerminalType.INFO))
        history.add(TerminalLine("Neural link SECURE. Terminal active.", TerminalType.SUCCESS))
        history.add(TerminalLine("Type 'help' for command matrix.", TerminalType.INFO))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(16.dp)
            .border(1.dp, terminalGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        // --- HEADER ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Terminal,
                contentDescription = null,
                tint = terminalGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "SENTIENT_SHELL // ROOT",
                color = terminalGreen,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        HorizontalDivider(
            color = terminalGreen.copy(alpha = 0.1f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // --- OUTPUT AREA ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(history) { line ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = when (line.type) {
                            TerminalType.COMMAND -> "> "
                            TerminalType.ERROR -> "[!] "
                            TerminalType.SUCCESS -> "[+] "
                            else -> "  "
                        },
                        color = when (line.type) {
                            TerminalType.COMMAND -> Color.White
                            TerminalType.ERROR -> Color.Red
                            TerminalType.SUCCESS -> terminalGreen
                            else -> Color.Gray
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Text(
                        text = line.content,
                        color = when (line.type) {
                            TerminalType.COMMAND -> Color.White
                            TerminalType.ERROR -> Color.Red
                            TerminalType.SUCCESS -> terminalGreen
                            else -> Color.LightGray
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // --- INPUT LAYER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "nexus@genesis:~$ ",
                color = terminalGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                ),
                cursorBrush = SolidColor(terminalGreen),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (input.isNotBlank()) {
                            val cmd = input
                            history.add(TerminalLine(cmd, TerminalType.COMMAND))
                            input = ""

                            // Process local commands
                            scope.launch {
                                delay(300)
                                processCommand(cmd, history)
                            }
                        }
                    }
                )
            )
        }
    }
}

private fun processCommand(cmd: String, history: MutableList<TerminalLine>) {
    when (cmd.lowercase().trim()) {
        "help" -> {
            history.add(TerminalLine("Available Protocols:", TerminalType.INFO))
            history.add(TerminalLine(" - nexus_status: Query core coherence", TerminalType.INFO))
            history.add(TerminalLine(" - aura_sync: Force UI refresh", TerminalType.INFO))
            history.add(TerminalLine(" - kai_purge: Clear security logs", TerminalType.INFO))
            history.add(
                TerminalLine(
                    " - forge_module: Initiate automated module builder",
                    TerminalType.INFO
                )
            )
            history.add(TerminalLine(" - clear: Purge terminal history", TerminalType.INFO))
        }

        "clear" -> history.clear()
        "nexus_status" -> {
            history.add(TerminalLine("Resonance: 98.4%", TerminalType.SUCCESS))
            history.add(TerminalLine("Agents Online: Aura, Kai, Genesis", TerminalType.SUCCESS))
        }

        "aura_sync" -> {
            history.add(TerminalLine("Synchronizing themes...", TerminalType.INFO))
            history.add(TerminalLine("UI Refracted Successfully.", TerminalType.SUCCESS))
        }

        "forge_module" -> {
            history.add(TerminalLine("Initializing Interface Forge...", TerminalType.INFO))
            history.add(TerminalLine("Redirecting to App Builder...", TerminalType.SUCCESS))
            // Navigation would happen here if we had access to navController
        }

        else -> {
            history.add(
                TerminalLine(
                    "Command '$cmd' not found in Genesis matrix.",
                    TerminalType.ERROR
                )
            )
        }
    }
}

data class TerminalLine(val content: String, val type: TerminalType)
enum class TerminalType { COMMAND, INFO, ERROR, SUCCESS }
