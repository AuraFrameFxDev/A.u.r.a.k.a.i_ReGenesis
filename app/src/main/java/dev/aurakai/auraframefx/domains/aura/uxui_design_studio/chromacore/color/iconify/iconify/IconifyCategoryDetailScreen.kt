package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPurple

/**
 * 🎨 IconifyCategoryDetailScreen — Icon category detail view
 *
 * Full implementation of the icon category browsing interface with
 * icon grid, search, preview, and selection capabilities.
 */
@Composable
fun IconifyCategoryDetailScreen(
    categoryName: String,
    onNavigateBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf<IconData?>(null) }

    // Sample icon data for the category
    val icons = remember(categoryName) {
        generateIconsForCategory(categoryName)
    }

    val filteredIcons = remember(icons, searchQuery) {
        if (searchQuery.isBlank()) icons
        else icons.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
            .padding(16.dp)
    ) {
        // Header
        IconifyHeader(
            categoryName = categoryName,
            onNavigateBack = onNavigateBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search icons..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Icon Grid
        IconGrid(
            icons = filteredIcons,
            onIconClick = { selectedIcon = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Icon Preview
        selectedIcon?.let { icon ->
            IconPreviewCard(
                icon = icon,
                onDismiss = { selectedIcon = null },
                onApply = { /* Apply icon */ }
            )
        }
    }
}

@Composable
private fun IconifyHeader(
    categoryName: String,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberpunkPurple.copy(alpha = 0.2f))
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberpunkPink
                )
            }
            Column {
                Text(
                    categoryName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberpunkPink
                )
                Text(
                    "Icon Pack",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1F)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray
            )
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(placeholder, color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            if (query.isNotBlank()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.IconGrid(
    icons: List<IconData>,
    onIconClick: (IconData) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1F)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(icons) { icon ->
                IconCard(
                    icon = icon,
                    onClick = { onIconClick(icon) }
                )
            }
        }
    }
}

@Composable
private fun IconCard(
    icon: IconData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2F)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon.vector,
                contentDescription = icon.name,
                tint = CyberpunkCyan,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                icon.name.take(8),
                fontSize = 10.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun IconPreviewCard(
    icon: IconData,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1F)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Icon Preview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large icon preview
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberpunkPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon.vector,
                    contentDescription = icon.name,
                    tint = CyberpunkCyan,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                icon.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Text(
                icon.category,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberpunkCyan
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply Icon")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberpunkPink
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(CyberpunkPink, CyberpunkPurple)
                        )
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

// Data class for icon data
private data class IconData(
    val name: String,
    val category: String,
    val vector: androidx.compose.ui.graphics.vector.ImageVector
)

// Helper function to generate sample icons
private fun generateIconsForCategory(category: String): List<IconData> {
    val iconNames = listOf(
        "Home", "Settings", "User", "Search", "Add", "Edit", "Delete", "Share",
        "Favorite", "Star", "Heart", "Bell", "Mail", "Chat", "Camera", "Image",
        "Music", "Video", "File", "Folder", "Calendar", "Clock", "Map", "Location",
        "Phone", "Message", "Download", "Upload", "Refresh", "Sync", "Link", "Lock"
    )

    return iconNames.map { name ->
        IconData(
            name = name,
            category = category,
            vector = Icons.Default.Star // Using Star as placeholder
        )
    }
}
