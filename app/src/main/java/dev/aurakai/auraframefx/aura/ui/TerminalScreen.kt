package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.ui.theme.AuraFrameFXTheme

/**
 * Genesis Protocol Terminal - Command Interface
 *
 * Provides direct command-line access to the Genesis Protocol.
 */
@Composable
fun TerminalScreen() {
    var input by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }
    val darkBg = Color(0xFF1A1A1A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Text(
                    text = "Genesis Protocol Terminal [Version 0.1.0]",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "(c) 2025 AuraKai Corporation. All rights reserved.",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
            items(history) { command ->
                Text(
                    text = "> $command",
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
                // In a real terminal, you'd process the command and show output here
                Text(
                    text = "Command executed: '$command'",
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textStyle = TextStyle(
                color = Color.White,
                fontFamily = FontFamily.Monospace
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (input.isNotBlank()) {
                        val command = input
                        history.add(command)
                        input = ""
                        
                        // Execute command in background
                        Thread {
                            try {
                                val process = Runtime.getRuntime().exec(command)
                                val reader = java.io.BufferedReader(java.io.InputStreamReader(process.inputStream))
                                val output = StringBuilder()
                                var line: String?
                                while (reader.readLine().also { line = it } != null) {
                                    output.append(line).append("\n")
                                }
                                val exitCode = process.waitFor()
                                if (exitCode != 0) {
                                    val errorReader = java.io.BufferedReader(java.io.InputStreamReader(process.errorStream))
                                    while (errorReader.readLine().also { line = it } != null) {
                                        output.append(line).append("\n")
                                    }
                                }
                                
                                val result = output.toString().trim()
                                if (result.isNotEmpty()) {
                                    history.add(result)
                                } else {
                                    history.add("Command executed (no output)")
                                }
                            } catch (e: Exception) {
                                history.add("Error: ${e.message}")
                            }
                        }.start()
                    }
                }
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row {
                    Text(
                        text = "> ",
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace
                    )
                    innerTextField()
                }
            }
        )
    }
}

@Preview
@Composable
private fun TerminalScreenPreview() {
    AuraFrameFXTheme {
        TerminalScreen()
    }
}
