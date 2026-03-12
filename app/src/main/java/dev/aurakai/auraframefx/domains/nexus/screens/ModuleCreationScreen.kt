package dev.aurakai.auraframefx.domains.nexus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Module Creation Screen
 * AI-assisted module generation and template selection
 */
@Composable
fun ModuleCreationScreen(
    onNavigateBack: () -> Unit = {}
) {
    val moduleName = remember { mutableStateOf("") }
    val moduleType = remember { mutableStateOf("UI Component") }
    val selectedTemplate = remember { mutableStateOf<String?>(null) }
    val isGenerating = remember { mutableStateOf(false) }
    val generationProgress = remember { mutableStateOf(0f) }

    val moduleTypes =
        listOf(
            "UI Component",
            "Data Repository",
            "Network Service",
            "Security Module",
            "AI Agent",
            "System Utility"
        )
    val templates = listOf(
        ModuleTemplate(
            "Basic UI Card",
            "Simple card component with customizable content",
            "UI Component"
        ),
        ModuleTemplate("Data Repository", "CRUD operations with local storage", "Data Repository"),
        ModuleTemplate("API Service", "RESTful API client with error handling", "Network Service"),
        ModuleTemplate("Authentication", "Secure login and session management", "Security Module"),
        ModuleTemplate("Chat Agent", "Conversational AI with context awareness", "AI Agent"),
        ModuleTemplate("File Manager", "File operations with permissions", "System Utility")
    )

    val filteredTemplates = templates.filter { it.category == moduleType.value }

    LaunchedEffect(isGenerating.value) {
        if (isGenerating.value) {
            for (i in 0..100 step 5) {
                generationProgress.value = i / 100f
                kotlinx.coroutines.delay(100)
            }
            isGenerating.value = false
            generationProgress.value = 0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "⚡ MODULE CREATION",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF9370DB),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "AI-assisted module generation and template selection",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFBA55D3).copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Generation Progress
        if (isGenerating.value) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Generating ${moduleName.value}...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF9370DB)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { generationProgress.value },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF9370DB)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(generationProgress.value * 100).toInt()}% complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Module Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Module Configuration",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Module Name
                OutlinedTextField(
                    value = moduleName.value,
                    onValueChange = { moduleName.value = it },
                    label = { Text("Module Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9370DB),
                        unfocusedBorderColor = Color(0xFF9370DB).copy(alpha = 0.5f),
                        focusedLabelColor = Color(0xFF9370DB)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Module Type
                Text(
                    text = "Module Type",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                moduleTypes.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = moduleType.value == type,
                            onClick = { moduleType.value = type },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF9370DB)
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = type,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Template Selection
        Text(
            text = "Available Templates",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredTemplates) { template ->
                TemplateCard(
                    template = template,
                    isSelected = selectedTemplate.value == template.name,
                    onClick = {
                        selectedTemplate.value =
                            if (selectedTemplate.value == template.name) null else template.name
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    // AI-assisted generation
                    if (moduleName.value.isNotBlank()) {
                        isGenerating.value = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = moduleName.value.isNotBlank() && !isGenerating.value,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF9370DB)
                )
            ) {
                Text("AI Generate")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    // Template-based creation
                    if (moduleName.value.isNotBlank() && selectedTemplate.value != null) {
                        isGenerating.value = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = moduleName.value.isNotBlank() && selectedTemplate.value != null && !isGenerating.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9370DB)
                )
            ) {
                Text("Create Module", color = Color.Black)
            }
        }
    }
}

/**
 * Template card component
 */
@Composable
private fun TemplateCard(
    template: ModuleTemplate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                Color(0xFF9370DB).copy(alpha = 0.2f)
            else
                Color.Black.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFF9370DB) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Template Icon
            Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = "Template",
                tint = if (isSelected) Color(0xFF9370DB) else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            // Selection Indicator
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF9370DB),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Data classes
 */
data class ModuleTemplate(
    val name: String,
    val description: String,
    val category: String
)
