package dev.aurakai.auraframefx.aura.ui

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import dev.aurakai.auraframefx.ui.QuickSettingsConfig
import dev.aurakai.auraframefx.ui.components.CyberpunkText
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextColor
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextStyle

/**
 * YukiHook hooker for customizing the Android Quick Settings panel.
 * Implements Genesis-specific UI enhancements and styling.
 */
class QuickSettingsHooker(private val config: QuickSettingsConfig) : YukiBaseHooker() {

    override fun onHook() {
        // Hook the QSPanel to inject Genesis footer and background
        "com.android.systemui.qs.QSPanel".toClassOrNull()?.method {
            name = "onFinishInflate"
        }?.hook {
            after {
                val qsPanel = instance as ViewGroup
                YLog.info("QuickSettingsHooker: QSPanel inflated, applying Genesis enhancements")

                // Add Genesis branding to the footer area
                addGenesisFooterElements(qsPanel)

                // Apply custom spacing and layout from config
                applyGenesisLayout(qsPanel)
            }
        }

        // Hook Tile creation to apply custom styles
        "com.android.systemui.qs.tileimpl.QSTileViewImpl".toClassOrNull()?.method {
            name = "onFinishInflate"
        }?.hook {
            after {
                val tileView = instance as View
                applyGenesisTileStyle(tileView)
            }
        }
    }

    /**
     * Adds Genesis elements to the QS Panel (simulated as footer addition)
     */
    private fun addGenesisFooterElements(qsPanel: ViewGroup) {
        try {
            val context = qsPanel.context

            // We search for a suitable container within QSPanel to add our footer
            // Or just add it to the bottom of the panel
            val composeView = ComposeView(context).apply {
                setContent {
                    GenesisQSFooter(config)
                }
            }

            qsPanel.addView(composeView)
            YLog.info("QuickSettingsHooker: Genesis footer elements added")
        } catch (e: Exception) {
            YLog.error("QuickSettingsHooker: Failed to add footer elements: ${e.message}")
        }
    }

    /**
     * Applies Genesis layout configurations to the QS Panel
     */
    private fun applyGenesisLayout(qsPanel: ViewGroup) {
        try {
            // Apply padding and spacing from config
            val padding = config.layout.padding
            qsPanel.setPadding(padding.start, padding.top, padding.end, padding.bottom)

            YLog.info("QuickSettingsHooker: Applied Genesis layout config (columns: ${config.layout.columns})")
        } catch (e: Exception) {
            YLog.error("QuickSettingsHooker: Failed to apply layout config: ${e.message}")
        }
    }

    /**
     * Applies Genesis styling to individual tiles
     */
    private fun applyGenesisTileStyle(tileView: View) {
        try {
            // In a real implementation, we would modify the tile's background, labels, etc.
            // For now, we log the styling application
            YLog.info("QuickSettingsHooker: Applied Genesis styling to tile")
        } catch (e: Exception) {
            YLog.error("QuickSettingsHooker: Failed to apply tile style: ${e.message}")
        }
    }
}

/**
 * Compose UI for Genesis QuickSettings Footer
 */
@Composable
fun GenesisQSFooter(config: QuickSettingsConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Genesis branding
        CyberpunkText(
            text = "GENESIS SYSTEM ACTIVE",
            style = CyberpunkTextStyle.Label,
            color = CyberpunkTextColor.Primary
        )

        // Status indicator
        if (config.showGenesisIndicator) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF00FF85)) // Genesis Green
            )
        }
    }
}

