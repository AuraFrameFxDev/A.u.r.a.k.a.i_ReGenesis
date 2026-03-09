package dev.aurakai.auraframefx.aura.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.databinding.ActivityQuickSettingsConfigBinding
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsConfigManager
import dev.aurakai.auraframefx.ui.QuickSettingsBackground
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsConfig as SystemQuickSettingsConfig
import dev.aurakai.auraframefx.system.quicksettings.QuickSettingsTileConfig as SystemQuickSettingsTileConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for configuring Quick Settings tiles.
 */
@AndroidEntryPoint
class QuickSettingsConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuickSettingsConfigBinding
    lateinit var configManager: QuickSettingsConfigManager
    private lateinit var adapter: TileConfigAdapter
    var currentConfig: SystemQuickSettingsConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickSettingsConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the config manager
        configManager = QuickSettingsConfigManager.getInstance(applicationContext)

        setupToolbar()
        setupRecyclerView()
        loadConfig()
        setupButtons()
    }

    private fun setupToolbar() {
    }

    private fun setupRecyclerView() {
        adapter = TileConfigAdapter(
            onItemClick = { tile -> showTileConfigDialog(tile) }
        )

        binding.tilesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@QuickSettingsConfigActivity)
            this.adapter = this@QuickSettingsConfigActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun loadConfig() {
        CoroutineScope(Dispatchers.Main).launch {
            val config = withContext(Dispatchers.IO) {
                configManager.loadConfig()
            }
            config.also { currentConfig = it }
            adapter.submitList(newTiles = config.tiles)
            updatePreview()
        }
    }

    private fun setupButtons() {
        binding.resetButton.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun showTileConfigDialog(tile: SystemQuickSettingsTileConfig) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tile_config, null)

        // Initialize views
        val tileName = dialogView.findViewById<TextView>(R.id.tileName)
        val enabledSwitch = dialogView.findViewById<SwitchMaterial>(R.id.enabledSwitch)
        val enableClicksSwitch = dialogView.findViewById<SwitchMaterial>(R.id.enableClicksSwitch)
        val rippleEffectSwitch = dialogView.findViewById<SwitchMaterial>(R.id.rippleEffectSwitch)
        val previewCard = dialogView.findViewById<MaterialCardView>(R.id.previewCard)

        // Set current values
        tileName.text = tile.id
        enabledSwitch.isChecked = tile.enabled
        enableClicksSwitch.isChecked = tile.enableClicks
        rippleEffectSwitch.isChecked = tile.rippleEffect

        // Apply preview styling
        applyTilePreviewStyle(previewCard, tile)

        // Show dialog
        MaterialAlertDialogBuilder(this)
            .setTitle("Configure ${tile.id}")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Update the tile config
                val updatedTile = tile.copy(
                    enabled = enabledSwitch.isChecked,
                    enableClicks = enableClicksSwitch.isChecked,
                    rippleEffect = rippleEffectSwitch.isChecked
                )

                // Update the config
                currentConfig?.let { config ->
                    val updatedTiles = config.tiles.toMutableList()
                    val index = updatedTiles.indexOfFirst { it.id == tile.id }
                    if (index != -1) {
                        updatedTiles[index] = updatedTile
                        currentConfig = config.copy(tiles = updatedTiles)
                        adapter.notifyItemChanged(index)
                        updatePreview()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyTilePreviewStyle(card: MaterialCardView, tile: SystemQuickSettingsTileConfig) {
        // Apply card background
        tile.background?.let { background ->
            when (background) {
                is QuickSettingsBackground.Solid -> {
                    card.setCardBackgroundColor(background.color.toInt())
                    card.alpha = background.alpha
                }
                // Handle other background types as needed
            }
        } ?: run {
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_surface
                )
            )
        }

        // Apply corner radius
        tile.background?.cornerRadius?.let { radius ->
            card.radius = resources.displayMetrics.density * radius
        } ?: run {
            card.radius = resources.displayMetrics.density * 8 // Default radius
        }

        // Apply elevation
        tile.background?.elevation?.let { elevation ->
            card.elevation = resources.displayMetrics.density * elevation
        } ?: run {
            card.elevation = resources.displayMetrics.density * 2 // Default elevation
        }
    }

    private fun showResetConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Reset to Defaults")
            .setMessage("Are you sure you want to reset all Quick Settings to their default values?")
            .setPositiveButton("Reset") { _, _ ->
                resetToDefault()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetToDefault() {
        CoroutineScope(Dispatchers.Main).launch {
            val success = withContext(Dispatchers.IO) {
                configManager.resetToDefault()
            }

            if (success) {
                loadConfig()
            }
        }
    }

    private fun updatePreview() {
        // Update the preview based on the current config
        // This is a simplified example - in a real app, you'd update a preview view
        // to show how the Quick Settings will look with the current configuration
    }

    class TileConfigAdapter(
        private val onItemClick: (SystemQuickSettingsTileConfig) -> Unit,
    ) : Adapter<TileConfigAdapter.TileViewHolder>() {

        private var tiles: List<SystemQuickSettingsTileConfig> = emptyList()

        class TileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tileName: TextView = view.findViewById(R.id.tileName)
            val enabledSwitch: SwitchMaterial = view.findViewById(R.id.enabledSwitch)
            val cardView: MaterialCardView = view.findViewById(R.id.tileCard)
        }

        fun submitList(newTiles: List<SystemQuickSettingsTileConfig>) {
            tiles = newTiles
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tile_config, parent, false)
            return TileViewHolder(view)
        }

        override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
            val tile = tiles[position]

            holder.tileName.text = tile.id
            holder.enabledSwitch.isChecked = tile.enabled

            // Apply visual styling based on the tile config
            applyTileStyle(holder.cardView, tile)

            // Set up click listener
            holder.itemView.setOnClickListener {
                onItemClick(tile)
            }

            // Set up switch change listener
            holder.enabledSwitch.setOnCheckedChangeListener { _, isChecked ->
                val updatedTiles = tiles.toMutableList()
                updatedTiles[position] = tile.copy(enabled = isChecked)
                submitList(updatedTiles)
            }
        }

        private fun applyTileStyle(
            cardView: MaterialCardView,
            tile: SystemQuickSettingsTileConfig
        ) {
            // Apply background color based on tile state
            val backgroundColor = if (tile.enabled) {
                ContextCompat.getColor(cardView.context, R.color.primary)
            } else {
                ContextCompat.getColor(cardView.context, R.color.light_surface_variant)
            }

            cardView.setCardBackgroundColor(backgroundColor)

            // Apply corner radius
            tile.background?.cornerRadius?.let { radius ->
                cardView.radius = cardView.resources.displayMetrics.density * radius
            } ?: run {
                cardView.radius = cardView.resources.displayMetrics.density * 8 // Default radius
            }

            // Apply elevation
            tile.background?.elevation?.let { elevation ->
                cardView.elevation = cardView.resources.displayMetrics.density * elevation
            } ?: run {
                cardView.elevation =
                    cardView.resources.displayMetrics.density * 2 // Default elevation
            }
        }

        override fun getItemCount(): Int = tiles.size
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, QuickSettingsConfigActivity::class.java)
        }
    }
}

private fun saveConfig(quickSettingsConfigActivity: QuickSettingsConfigActivity) {
    quickSettingsConfigActivity.currentConfig?.let { config ->
        CoroutineScope(Dispatchers.Main).launch {
            val success = withContext(Dispatchers.IO) {
                return@withContext quickSettingsConfigActivity.configManager.saveConfig(config)
            }

            if (success) {
                quickSettingsConfigActivity.setResult(Activity.RESULT_OK)
                quickSettingsConfigActivity.finish()
            }
        }
    }
}

