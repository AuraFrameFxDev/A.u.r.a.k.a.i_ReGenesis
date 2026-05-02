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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonCyan as CyberpunkCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonPink as CyberpunkPink

context(viewModel: IconPickerViewModel) @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPicker(
    currentIcon: String? = null,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCollection by remember { mutableStateOf<String?>(null) }
    var activeTab by remember { mutableStateOf(IconPickerTab.SEARCH) }

    val iconState by viewModel.iconState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()
    }

    val collections = if (iconState is IconPickerViewModel.IconState.Success) {
        (iconState as IconPickerViewModel.IconState.Success).collections
    } else emptyMap()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0A0A0A)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconPickerHeader(onDismiss = onDismiss, currentIcon = currentIcon)
            IconPickerTabs(activeTab = activeTab, onTabSelected = { activeTab = it })

            if (activeTab == IconPickerTab.SEARCH) {
                IconSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        keyboardController?.hide()
                        viewModel.searchIcons(searchQuery)
                    },
                    selectedCollection = selectedCollection,
                    collections = collections,
                    onCollectionSelected = { selectedCollection = it }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    IconPickerTab.SEARCH -> {
                        IconSearchResults(
                            icons = if (iconState is IconPickerViewModel.IconState.Success) (iconState as IconPickerViewModel.IconState.Success).icons else emptyList(),
                            isLoading = iconState is IconPickerViewModel.IconState.Loading,
                            imageLoader = imageLoader,
                            selectedIcon = currentIcon,
                            onIconSelected = {
                                saveRecentIcon(context, it)
                                onIconSelected(it)
                            }
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
                        val recentIcons = remember { loadRecentIcons(context) }
                        if (recentIcons.isEmpty()) {
                            EmptyState(icon = Icons.Default.History, message = "No recent icons yet")
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
                                        selected = currentIcon == iconId,
                                        onIconSelected = {
                                            saveRecentIcon(context, it)
                                            onIconSelected(it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    IconPickerTab.FAVORITES -> {
                        val favoriteIcons = remember { loadFavoriteIcons(context) }
                        if (favoriteIcons.isEmpty()) {
                            EmptyState(icon = Icons.Default.Favorite, message = "No favorite icons yet")
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 72.dp),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(favoriteIcons) { iconId ->
                                    IconGridItem(
                                        iconId = iconId,
                                        imageLoader = imageLoader,
                                        selected = currentIcon == iconId,
                                        isFavorite = true,
                                        onIconSelected = {
                                            saveRecentIcon(context, it)
                                            onIconSelected(it)
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

enum class IconPickerTab { SEARCH, COLLECTIONS, RECENT, FAVORITES }

@Composable
fun IconPickerHeader(onDismiss: () -> Unit, currentIcon: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Icon Picker", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CyberpunkPink)
            Text(text = "250,000+ icons • Powered by Iconify", fontSize = 12.sp, color = Color.Gray)
            if (currentIcon != null) {
                Text(text = "Current: $currentIcon", fontSize = 10.sp, color = CyberpunkCyan)
            }
        }
        IconButton(onClick = onDismiss) { Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
    }
}

@Composable
fun IconPickerTabs(activeTab: IconPickerTab, onTabSelected: (IconPickerTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconPickerTabItem(icon = Icons.Default.Search, label = "Search", isActive = activeTab == IconPickerTab.SEARCH, onClick = { onTabSelected(IconPickerTab.SEARCH) })
        IconPickerTabItem(icon = Icons.Default.GridView, label = "Collections", isActive = activeTab == IconPickerTab.COLLECTIONS, onClick = { onTabSelected(IconPickerTab.COLLECTIONS) })
        IconPickerTabItem(icon = Icons.Default.History, label = "Recent", isActive = activeTab == IconPickerTab.RECENT, onClick = { onTabSelected(IconPickerTab.RECENT) })
        IconPickerTabItem(icon = Icons.Default.Favorite, label = "Favorites", isActive = activeTab == IconPickerTab.FAVORITES, onClick = { onTabSelected(IconPickerTab.FAVORITES) })
    }
}

@Composable
fun IconPickerTabItem(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = if (isActive) CyberpunkPink else Color.Gray, modifier = Modifier.size(24.dp))
        Text(text = label, fontSize = 10.sp, color = if (isActive) CyberpunkPink else Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun IconSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    selectedCollection: String?,
    collections: Map<String, IconifyApiCollection>,
    onCollectionSelected: (String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search icons...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = { if (query.isNotEmpty()) { IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Clear, contentDescription = "Clear") } } },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberpunkPink, unfocusedBorderColor = Color.Gray, cursorColor = CyberpunkPink)
        )

        if (selectedCollection != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Filter:", fontSize = 12.sp, color = Color.Gray)
                FilterChip(
                    selected = true,
                    onClick = { onCollectionSelected(null) },
                    label = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = collections[selectedCollection]?.name ?: selectedCollection, fontSize = 12.sp)
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove filter", modifier = Modifier.size(16.dp))
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyberpunkPink, selectedLabelColor = Color.Black)
                )
            }
        }
    }
}

@Composable
fun IconSearchResults(icons: List<String>, isLoading: Boolean, imageLoader: ImageLoader, selectedIcon: String?, onIconSelected: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CyberpunkPink) }
        } else if (icons.isEmpty()) {
            EmptyState(icon = Icons.Default.SearchOff, message = "No icons found.")
        } else {
            LazyVerticalGrid(columns = GridCells.Adaptive(80.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(icons) { iconId -> IconGridItem(iconId = iconId, imageLoader = imageLoader, selected = selectedIcon == iconId, onIconSelected = onIconSelected) }
            }
        }
    }
}

@Composable
fun IconGridItem(iconId: String, imageLoader: ImageLoader, selected: Boolean = false, isFavorite: Boolean = false, onIconSelected: (String) -> Unit) {
    Surface(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).clickable { onIconSelected(iconId) }, color = if (selected) Color(0xFF2A2A2A) else Color(0xFF1A1A1A)) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data("https://api.iconify.design/$iconId.svg").crossfade(true).build(),
                contentDescription = iconId,
                imageLoader = imageLoader,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = iconId.split(":").lastOrNull() ?: iconId, fontSize = 8.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun IconCollectionsGrid(collections: Map<String, IconifyApiCollection>, onCollectionSelected: (String) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Adaptive(160.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(collections.entries.toList()) { (prefix, collection) -> CollectionCard(collection = collection, onClick = { onCollectionSelected(prefix) }) }
    }
}

@Composable
fun CollectionCard(collection: IconifyApiCollection, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick), color = Color(0xFF1A1A1A)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = collection.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Column {
                Text(text = "${collection.total} icons", fontSize = 12.sp, color = CyberpunkCyan)
                if (collection.author != null) {
                    Text(text = "by ${collection.author.name}", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = message, tint = Color.Gray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

private fun loadRecentIcons(context: Context): List<String> {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val recentIconsString = prefs.getString("recent_icons", "") ?: ""
    return if (recentIconsString.isNotEmpty()) recentIconsString.split(",").take(20) else emptyList()
}

private fun saveRecentIcon(context: Context, iconId: String) {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val currentRecent = loadRecentIcons(context).toMutableList()
    currentRecent.remove(iconId)
    currentRecent.add(0, iconId)
    val recentToSave = currentRecent.take(20)
    prefs.edit().putString("recent_icons", recentToSave.joinToString(",")).apply()
}

private fun loadFavoriteIcons(context: Context): List<String> {
    val prefs = context.getSharedPreferences("icon_picker", Context.MODE_PRIVATE)
    val favoriteIconsString = prefs.getString("favorite_icons", "") ?: ""
    return if (favoriteIconsString.isNotEmpty()) favoriteIconsString.split(",") else emptyList()
}
