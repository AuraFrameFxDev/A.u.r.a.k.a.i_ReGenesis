package dev.aurakai.auraframefx.ui.trinity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrinityScreen(
    viewModel: TrinityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show error messages in a snackbar
    LaunchedEffect(uiState) {
        if (uiState is TrinityUiState.Error) {
            val errorMessage = (uiState as TrinityUiState.Error).message
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    actionLabel = "Retry"
                )
                viewModel.refresh()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Trinity System") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is TrinityUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TrinityUiState.Error -> {
                // Error state is handled by the snackbar
                EmptyContent("An error occurred") { viewModel.refresh() }
            }

            is TrinityUiState.Processing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing agent request...")
                    }
                }
            }

            is TrinityUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User Info Section
                    item {
                        UserInfoSection(state.user)
                    }

                    // Agent Status Section
                    item {
                        Text(
                            text = "Agent Status",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.agentStatus.isNotEmpty()) {
                        items(state.agentStatus.entries.toList()) { (agentType, status) ->
                            AgentStatusCard(agentType, status)
                        }
                    }

                    // Themes Section
                    item {
                        Text(
                            text = "Available Themes",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.availableThemes.isNotEmpty()) {
                        items(state.availableThemes) { theme ->
                            ThemeItem(
                                theme = theme,
                                onThemeSelected = { viewModel.applyTheme(theme.id) }
                            )
                        }
                    }

                    // Last Agent Response
                    state.lastAgentResponse?.let { response ->
                        item {
                            LastAgentResponse(
                                agentType = state.lastAgentType ?: "Unknown",
                                response = response
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserInfoSection(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = user?.username ?: "Guest User",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (user?.email != null) {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (user?.role != null) {
                Text(
                    text = "Role: ${user.role}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun AgentStatusCard(agentType: String, status: AgentResponse) {
    val statusColor = when (status.status.lowercase()) {
        "online" -> Color.Green
        "offline" -> Color.Red
        "busy" -> Color.Yellow
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = agentType.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, shape = MaterialTheme.shapes.small)
                )

                Text(
                    text = status.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }

            if (status.message != null) {
                Text(
                    text = status.message,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(theme: Theme, onThemeSelected: () -> Unit) {
    Card(
        onClick = onThemeSelected,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (theme.description != null) {
                    Text(
                        text = theme.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (theme.isActive == true) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Theme",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun LastAgentResponse(agentType: String, response: AgentResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$agentType Response",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = response.message ?: "No message",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            if (response.timestamp != null) {
                Text(
                    text = response.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
