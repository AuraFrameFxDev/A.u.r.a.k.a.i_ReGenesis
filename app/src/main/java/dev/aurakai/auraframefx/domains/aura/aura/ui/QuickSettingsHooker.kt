package dev.aurakai.auraframefx.domains.aura.ui

import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import dev.aurakai.auraframefx.domains.aura.ui.components.CyberpunkText

class QuickSettingsHooker(private val config: QuickSettingsConfig) : YukiBaseHooker() {

    override fun onHook() {
        // Hook QSPanel inflation
        "com.android.systemui.qs.QSPanel".toClassOrNull()?.resolve()?.firstMethod {
            name = "onFinishInflate"
        }?.hook {
            after {
                val qsPanel = instance as ViewGroup
                YLog.info("QuickSettingsHooker: QSPanel inflated → injecting Genesis footer")

                addGenesisFooter(qsPanel)
            }
        }

        // Optional: Style individual tiles
        "com.android.systemui.qs.tileimpl.QSTileViewImpl".toClassOrNull()?.resolve()?.firstMethod {
            name = "onFinishInflate"
        }?.hook {
            after {
                applyGenesisTileStyle(instance as ViewGroup)
            }
        }
    }

    private fun addGenesisFooter(qsPanel: ViewGroup) {
        try {
            val composeView = ComposeView(qsPanel.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setContent {
                    GenesisQSFooter(config)
                }
            }

            // Add at the bottom (footer area)
            qsPanel.addView(composeView, qsPanel.childCount)
            YLog.info("QuickSettingsHooker: Genesis footer successfully injected")
        } catch (e: Exception) {
            YLog.error("QuickSettingsHooker: Failed to inject footer", e)
        }
    }

    private fun applyGenesisTileStyle(tileView: ViewGroup) {
        // Future expansion: custom tile backgrounds, glows, etc.
        YLog.debug("QuickSettingsHooker: Applied Genesis styling to tile")
    }
}

/** Genesis Quick Settings Footer */
@Composable
fun GenesisQSFooter(config: QuickSettingsConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Genesis Branding
        CyberpunkText(
            text = "GENESIS ACTIVE",
            color = Color(0xFF00FF9D),           // Genesis neon green
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier,
            enableGlitch = config.enableGlitchEffect
        )
        data class QuickSettingsConfig(
            val showGenesisIndicator: Boolean = true,
            val enableGlitchEffect: Boolean = true,
            val footerOpacity: Float = 0.45f
        )

        // Status Indicator
        if (config.showGenesisIndicator) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF00FF9D))
            )
        }
    }
}