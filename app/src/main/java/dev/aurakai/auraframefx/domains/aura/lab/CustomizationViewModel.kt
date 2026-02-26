package dev.aurakai.auraframefx.domains.aura.lab

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.LauncherConfiguration
import dev.aurakai.auraframefx.domains.aura.MonetConfiguration
import dev.aurakai.auraframefx.domains.aura.SystemUIConfiguration
import dev.aurakai.auraframefx.domains.genesis.models.ReGenesisMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Master ViewModel that aggregates >100 customization options via CustomizationPreferences.
 * Controls themes, glass effects, animations, UI elements toggles, and agent colors.
 */
@HiltViewModel
open class CustomizationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CustomizationState())
    val state: StateFlow<CustomizationState> = _state.asStateFlow()

    data class CustomizationState(
        val themeName: String = "CyberGlow",
        val themeAccent: String = "NeonBlue",
        val themeDark: Boolean = true,
        val glassEnabled: Boolean = false,
        val glassBlurRadiusDp: Float = 0f,
        val glassSurfaceAlpha: Float = 0.12f,
        val animationsEnabled: Boolean = false,
        val animationSpeed: Int = 0,
        val showStatusBar: Boolean = true,
        val showNotchBar: Boolean = true,
        val showOverlayMenus: Boolean = false,
        val reGenesisMode: ReGenesisMode = ReGenesisMode.NOT_SET,
        val monetConfig: MonetConfiguration = MonetConfiguration(),
        val systemUIConfig: SystemUIConfiguration = SystemUIConfiguration(),
        val launcherConfig: LauncherConfiguration = LauncherConfiguration(),
        val agentColors: Map<String, String> = emptyMap()
    )

    fun start(context: Context) {
        // Explicit typed grouped flows
        val themeFlow: Flow<Triple<String, String, Boolean>> = combine(
            CustomizationPreferences.themeNameFlow(context),
            CustomizationPreferences.themeAccentFlow(context),
            CustomizationPreferences.themeDarkFlow(context)
        ) { name: String, accent: String, dark: Boolean -> Triple(name, accent, dark) }

        val glassFlow: Flow<Triple<Boolean, Float, Float>> = combine(
            CustomizationPreferences.glassEnabledFlow(context),
            CustomizationPreferences.glassBlurRadiusFlow(context),
            CustomizationPreferences.glassSurfaceAlphaFlow(context)
        ) { enabled: Boolean, blur: Float, alpha: Float -> Triple(enabled, blur, alpha) }

        val animFlow: Flow<Pair<Boolean, Int>> = combine(
            CustomizationPreferences.animationsEnabledFlow(context),
            CustomizationPreferences.animationSpeedFlow(context)
        ) { enabled: Boolean, speed: Int -> enabled to speed }

        val uiFlow: Flow<Triple<Boolean, Boolean, Boolean>> = combine(
            CustomizationPreferences.showStatusBarFlow(context),
            CustomizationPreferences.showNotchBarFlow(context),
            CustomizationPreferences.showOverlayMenusFlow(context)
        ) { status: Boolean, notch: Boolean, overlay: Boolean -> Triple(status, notch, overlay) }

        val modeFlow = CustomizationPreferences.reGenesisModeFlow(context)
        val monetFlow = CustomizationPreferences.monetConfigFlow(context)
        val systemUIFlow = CustomizationPreferences.systemUIConfigFlow(context)
        val launcherFlow = CustomizationPreferences.launcherConfigFlow(context)

        viewModelScope.launch {
            combine(
                themeFlow,
                glassFlow,
                animFlow,
                uiFlow,
                modeFlow,
                monetFlow,
                systemUIFlow,
                launcherFlow
            ) { values: Array<Any?> ->
                val theme = values[0] as Triple<String, String, Boolean>
                val glass = values[1] as Triple<Boolean, Float, Float>
                val anim = values[2] as Pair<Boolean, Int>
                val ui = values[3] as Triple<Boolean, Boolean, Boolean>
                val mode = values[4] as ReGenesisMode
                val monet = values[5] as MonetConfiguration
                val sysui = values[6] as SystemUIConfiguration
                val launcher = values[7] as LauncherConfiguration
                _state.value = _state.value.copy(
                    themeName = theme.first,
                    themeAccent = theme.second,
                    themeDark = theme.third,
                    glassEnabled = glass.first,
                    glassBlurRadiusDp = glass.second,
                    glassSurfaceAlpha = glass.third,
                    animationsEnabled = anim.first,
                    animationSpeed = anim.second,
                    showStatusBar = ui.first,
                    showNotchBar = ui.second,
                    showOverlayMenus = ui.third,
                    reGenesisMode = mode,
                    monetConfig = monet,
                    systemUIConfig = sysui,
                    launcherConfig = launcher
                )
            }.collect { /* emission already handled in combine lambda */ }
        }
    }

    fun setTheme(context: Context, name: String, accent: String, dark: Boolean) {
        viewModelScope.launch { CustomizationPreferences.setTheme(context, name, accent, dark) }
    }

    fun setGlass(context: Context, enabled: Boolean, blurRadiusDp: Float, surfaceAlpha: Float) {
        viewModelScope.launch {
            CustomizationPreferences.setGlass(
                context,
                enabled,
                blurRadiusDp,
                surfaceAlpha
            )
        }
    }

    fun setAnimations(context: Context, enabled: Boolean, speed: Int) {
        viewModelScope.launch { CustomizationPreferences.setAnimations(context, enabled, speed) }
    }

    fun setUiElements(
        context: Context,
        showStatusBar: Boolean,
        showNotchBar: Boolean,
        showOverlayMenus: Boolean
    ) {
        viewModelScope.launch {
            CustomizationPreferences.setUiElements(
                context,
                showStatusBar,
                showNotchBar,
                showOverlayMenus
            )
        }
    }

    fun setReGenesisMode(context: Context, mode: ReGenesisMode) {
        viewModelScope.launch { CustomizationPreferences.setReGenesisMode(context, mode) }
    }

    fun updateMonetConfig(context: Context, config: MonetConfiguration) {
        viewModelScope.launch { CustomizationPreferences.updateMonetConfig(context, config) }
    }

    fun updateSystemUIConfig(context: Context, config: SystemUIConfiguration) {
        viewModelScope.launch { CustomizationPreferences.updateSystemUIConfig(context, config) }
    }

    fun updateLauncherConfig(context: Context, config: LauncherConfiguration) {
        viewModelScope.launch { CustomizationPreferences.updateLauncherConfig(context, config) }
    }

    fun setAgentColor(context: Context, agentName: String, hexColor: String) {
        viewModelScope.launch {
            CustomizationPreferences.setAgentColor(context, agentName, hexColor)
            _state.value =
                _state.value.copy(agentColors = _state.value.agentColors + (agentName to hexColor))
        }
    }

    // Compatibility shim: some UI modules expect a selectComponent function on a project-global CustomizationViewModel.
    // This ViewModel lives in a different package and doesn't manage UI components, so provide a harmless no-op.
    fun selectComponent(componentId: String?) {
        // No-op: UI module that calls this will use a different ViewModel implementation in the ui.customization package.
    }
}
