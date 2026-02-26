package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.CyberpunkPink
import kotlinx.coroutines.launch

/**
 * 🎨 Icon Picker - 250,000+ Icons at Your Fingertips
 *
 * Beautiful, searchable icon picker integrated with Iconify API.
 * Features:
 * - Search 250K+ icons
 * - Browse by collection (Material, FontAwesome, Feather, etc.)
 * - Recent icons
 * - Favorites
 * - Preview with adjustable size
 * - SVG rendering via Coil
 *
 * Example:
 * ```
 * IconPicker(
 *     onIconSelected = { iconId ->
 *         component.icon = iconId
 *         customizationEngine.updateComponent(component)
 *     }
 * )
 * ```
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPicker(
    iconifyService: IconifyService,
    currentIcon: String? = null,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCollection by remember { mutableStateOf<String?>(null) }
    var selectedIcon by remember { mutableStateOf(currentIcon) }
    var searchResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var collections by remember { mutableStateOf<Map<String, IconCollection>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(IconPickerTab.SEARCH) }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // SVG image loader
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }

    // Load collections on start
    LaunchedEffect(Unit) {
        scope.launch {
            iconifyService.getCollections().onSuccess {
                collections = it
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0A0A0A)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            IconPickerHeader(
                onDismiss = onDismiss,
                currentIcon = currentIcon
            )

            // Tabs
            IconPickerTabs(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )

            // Search bar
            if (activeTab == IconPickerTab.SEARCH) {
                IconSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        keyboardController?.hide()
                        scope.launch {
                            isLoading = true
                            iconifyService.searchIcons(
                                query = searchQuery,
                                limit = 100,
                                prefixes = selectedCollection
                            ).onSuccess { result ->
                                searchResults = result.icons
                            }
                            isLoading = false
                        }
                    },
                    selectedCollection = selectedCollection,
                    collections = collections,
                    onCollectionSelected = { selectedCollection = it }
                )
            }

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    IconPickerTab.SEARCH -> {
                        IconSearchResults(
                            icons = searchResults,
                            isLoading = isLoading,
                            imageLoader = imageLoader,
                            iconifyService = iconifyService,
                            selectedIcon = selectedIcon,
                            onIconSelected = onIconSelected
                        )
                    }

                    IconPickerTab.COLLECTIONS -> {
                        IconCollectionsGrid(
                            collections = collections,
                            onCollectionSelected = { prefix ->
                                selectedCollection = prefix
                                activeTab = IconPickerTab.SEARCH
                            }
                        )
                    }

                    IconPickerTab.RECENT -> {
                        // Implement recent icons tracking
                        val recentIcons = remember { loadRecentIcons(context) }

                        if (recentIcons.isEmpty()) {
                            EmptyState(
                                icon = Icons.Default.History,
                                message = "No recent icons yet"
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 72.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(recentIcons) { iconId ->
                                    IconGridItem(
                                        iconId = iconId,
                                        imageLoader = imageLoader,
                                        iconifyService = iconifyService,
                                        selected = selectedIcon == iconId,
                                        onIconSelected = {
                                            selectedIcon = it
                                            saveRecentIcon(context, it)
                                            onIconSelected(it)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    IconPickerTab.FAVORITES -> {
                        // Implement favorites
                        val favoriteIcons = remember { loadFavoriteIcons(context) }
                        var favorites by remember { mutableStateOf(favoriteIcons) }

                        if (favorites.isEmpty()) {
                            EmptyState(
                                icon = Icons.Default.Favorite,
                                message = "No favorite icons yet"
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 72.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(favorites) { iconId ->
                                    IconGridItem(
                                        iconId = iconId,
                                        imageLoader = imageLoader,
                                        iconifyService = iconifyService,
                                        selected = selectedIcon == iconId,
                                        isFavorite = true,
                                        onIconSelected = {
                                            selectedIcon = it
                                            saveRecentIcon(context, it)
                                            onIconSelected(it)
                                        },
                                        onFavoriteToggle = {
                                            removeFavoriteIcon(context, iconId)
                                            favorites = favorites.filter { it != iconId }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class IconPickerTab {
    SEARCH,
    COLLECTIONS,
    RECENT,
    FAVORITES
}

/**
 * Header with title and close button
 */
@Composable
fun IconPickerHeader(
    onDismiss: () -> Unit,
    currentIcon: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Icon Picker",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CyberpunkPink
            )
            Text(
                text = "250,000+ icons • Powered by Iconify",
                fontSize = 12.sp,
                color = Color.Gray
            )
            if (currentIcon != null) {
                Text(
                    text = "Current: $currentIcon",
                    fontSize = 10.sp,
                    color = CyberpunkCyan
                )
            }
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

/**
 * Tab selector
 */
@Composable
fun IconPickerTabs(
    activeTab: IconPickerTab,
    onTabSelected: (IconPickerTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconPickerTabItem(
            icon = Icons.Default.Search,
            label = "Search",
            isActive = activeTab == IconPickerTab.SEARCH,
            onClick = { onTabSelected(IconPickerTab.SEARCH) }
        )

        IconPickerTabItem(
            icon = Icons.Default.GridView,
            label = "Collections",
            isActive = activeTab == IconPickerTab.COLLECTIONS,
            onClick = { onTabSelected(IconPickerTab.COLLECTIONS) }
        )

        IconPickerTabItem(
            icon = Icons.Default.History,
            label = "Recent",
            isActive = activeTab == IconPickerTab.RECENT,
            onClick = { onTabSelected(IconPickerTab.RECENT) }
        )

        IconPickerTabItem(
            icon = Icons.Default.Favorite,
            label = "Favorites",
            isActive = activeTab == IconPickerTab.FAVORITES,
            onClick = { onTabSelected(IconPickerTab.FAVORITES) }
        )
    }
}

@Composable
fun IconPickerTabItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) CyberpunkPink else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isActive) CyberpunkPink else Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Search bar with collection filter
 */
@Composable
fun IconSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    selectedCollection: String?,
    collections: Map<String, IconCollection>,
    onCollectionSelected: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search icons...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberpunkPink,
                unfocusedBorderColor = Color.Gray,
                cursorColor = CyberpunkPink
            )
        )

        // Collection filter chip
        if (selectedCollection != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                FilterChip(
                    selected = true,
                    onClick = { onCollectionSelected(null) },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = collections[selectedCollection]?.name ?: selectedCollection,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove filter",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberpunkPink,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
    }
}

/**
 * Search results grid
 */
@Composable
fun IconSearchResults(
    icons: List<String>,
    isLoading: Boolean,
    imageLoader: ImageLoader,
    iconifyService: IconifyService,
    selectedIcon: String?,
    onIconSelected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyberpunkPink)
                }
            }

            icons.isEmpty() -> {
                EmptyState(
                    icon = Icons.Default.SearchOff,
                    message = "No icons found. Try a different search term."
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(80.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(icons) { iconId ->
                        IconGridItem(
                            iconId = iconId,
                            imageLoader = imageLoader,
                            iconifyService = iconifyService,
                            selected = selectedIcon == iconId,
                            onIconSelected = {
                                onIconSelected(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual icon item in grid
 */
@Composable
fun IconGridItem(
    iconId: String,
    imageLoader: ImageLoader,
    iconifyService: IconifyService,
    selected: Boolean = false,
    isFavorite: Boolean = false,
    onIconSelected: (String) -> Unit,
    onFavoriteToggle: (() -> Unit)? = null
) {
    var isHovered by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onIconSelected(iconId)
            },
        color = if (isHovered) Color(0xFF2A2A2A) else Color(0xFF1A1A1A),
        shadowElevation = if (isHovered) 8.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // SVG Icon
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://api.iconify.design/$iconId.svg")
                    .crossfade(true)
                    .build(),
                contentDescription = iconId,
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Icon name
            Text(
                text = iconId.split(":").lastOrNull() ?: iconId,
                fontSize = 8.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Icon collections grid
 */
@Composable
fun IconCollectionsGrid(
    collections: Map<String, IconCollection>,
    onCollectionSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(collections.entries.toList()) { (prefix, collection) ->
            CollectionCard(
                collection = collection,
                onClick = { onCollectionSelected(prefix) }
            )
        }
    }
}

/**
 * Collection card
 */
@Composable
fun CollectionCard(
    collection: IconCollection,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = Color(0xFF1A1A1A),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = collection.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Column {
                Text(
                    text = "${collection.total} icons",
                    fontSize = 12.sp,
                    color = CyberpunkCyan
                )

                if (collection.author != null) {
                    Text(
                        text = "by ${collection.author.name}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Empty state
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = message,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Load recent icons from SharedPreferences
 */
private fun loadRecentIcons(context: Context): List<String> {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val recentIconsString = prefs.getString("recent_icons", "") ?: ""
    return if (recentIconsString.isNotEmpty()) {
        recentIconsString.split(",").take(20) // Keep last 20
    } else {
        emptyList()
    }
}

/**
 * Save recent icon to SharedPreferences
 */
private fun saveRecentIcon(context: Context, iconId: String) {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val currentRecent = loadRecentIcons(context).toMutableList()

    // Remove if already exists (to move to front)
    currentRecent.remove(iconId)

    // Add to front
    currentRecent.add(0, iconId)

    // Keep only last 20
    val recentToSave = currentRecent.take(20)

    prefs.edit().putString("recent_icons", recentToSave.joinToString(",")).apply()
}

/**
 * Load favorite icons from SharedPreferences
 */
private fun loadFavoriteIcons(context: Context): List<String> {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val favoriteIconsString = prefs.getString("favorite_icons", "") ?: ""
    return if (favoriteIconsString.isNotEmpty()) {
        favoriteIconsString.split(",")
    } else {
        emptyList()
    }
}

/**
 * Add icon to favorites
 */
private fun addFavoriteIcon(context: Context, iconId: String) {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val currentFavorites = loadFavoriteIcons(context).toMutableList()

    if (!currentFavorites.contains(iconId)) {
        currentFavorites.add(iconId)
        prefs.edit().putString("favorite_icons", currentFavorites.joinToString(",")).apply()
    }
}

/**
 * Remove icon from favorites
 */
private fun removeFavoriteIcon(context: Context, iconId: String) {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val currentFavorites = loadFavoriteIcons(context).toMutableList()

    currentFavorites.remove(iconId)
    prefs.edit().putString("favorite_icons", currentFavorites.joinToString(",")).apply()
}

