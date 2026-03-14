package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/**
 * Code Assist Screen
 * AI-powered coding assistant interface
 */
@Composable
fun CodeAssistScreen(navController: NavHostController) {
    var codeInput by remember { mutableStateOf("// Ask Code Assist to generate or refactor code...\n\nfun main() {\n    println(\"Hello, Aura!\")\n}") }
    var aiResponse by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E), // Dark Blue
                        Color.Black
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Code Assist",
                tint = Color(0xFF9370DB), // Medium Purple
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "CODE ASSIST",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF9370DB),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "AI-Powered Development Companion",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF00BFFF) // Deep Sky Blue
                )
            }
        }

        // Code Editor Area
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F0F1A)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9370DB).copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Editor Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Kotlin",
                        color = Color(0xFF9370DB),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .background(Color(0xFF9370DB).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ContentCopy, "Copy", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Icon(Icons.Default.Settings, "Settings", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }

                // Editor Input
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = Color(0xFFE0E0E0)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color(0xFF9370DB)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Interaction Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16213E)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00BFFF).copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ask Code Assist",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF00BFFF)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Describe what you want to do...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00BFFF),
                            unfocusedBorderColor = Color(0xFF00BFFF).copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { isProcessing = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9370DB)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.AutoAwesome, "Generate")
                        }
                    }
                }
            }
        }
    }
}
