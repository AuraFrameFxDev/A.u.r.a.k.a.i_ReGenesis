package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor

object GateAssetConfig {
    enum class GateStyle { STYLE_A, STYLE_B }
    
    object StyleMode {
        var auraStyle = GateStyle.STYLE_A
        var kaiStyle = GateStyle.STYLE_A
        var genesisStyle = GateStyle.STYLE_A
        var nexusStyle = GateStyle.STYLE_A
    }
    
    fun toggleAuraStyle() {
        StyleMode.auraStyle = if (StyleMode.auraStyle == GateStyle.STYLE_A) GateStyle.STYLE_B else GateStyle.STYLE_A
    }
    
    fun toggleKaiStyle() {
        StyleMode.kaiStyle = if (StyleMode.kaiStyle == GateStyle.STYLE_A) GateStyle.STYLE_B else GateStyle.STYLE_A
    }
    
    fun toggleGenesisStyle() {
        StyleMode.genesisStyle = if (StyleMode.genesisStyle == GateStyle.STYLE_A) GateStyle.STYLE_B else GateStyle.STYLE_A
    }
    
    fun toggleNexusStyle() {
        StyleMode.nexusStyle = if (StyleMode.nexusStyle == GateStyle.STYLE_A) GateStyle.STYLE_B else GateStyle.STYLE_A
    }

    // Sub-gate asset mappings
    enum class AuraSubGates(val styleA: String, val styleB: String, val fallback: String? = null) {
        AURA_LAB("aura_lab_a", "aura_lab_b"),
        CHROMA_CORE("chroma_core_a", "chroma_core_b"),
        THEME_ENGINE("theme_engine_a", "theme_engine_b"),
        UXUI_ENGINE("uxui_engine_a", "uxui_engine_b"),
        ICONIFY("iconify_a", "iconify_b"),
        PIXEL_LAUNCHER("pixel_launcher_a", "pixel_launcher_b"),
        COLLAB_CANVAS("collab_canvas_a", "collab_canvas_b")
    }

    enum class KaiSubGates(val styleA: String, val styleB: String, val fallback: String? = null) {
        ROM_FLASHER("rom_flasher_a", "rom_flasher_b"),
        BOOTLOADER("bootloader_a", "bootloader_b"),
        MODULE_MANAGER("module_manager_a", "module_manager_b"),
        ROOT_TOOLS("root_tools_a", "root_tools_b"),
        SECURITY("security_a", "security_b"),
        VPN("vpn_a", "vpn_b"),
        LSPOSED("lsposed_a", "lsposed_b"),
        RECOVERY("recovery_a", "recovery_b")
    }

    enum class GenesisSubGates(val styleA: String, val styleB: String, val fallback: String? = null) {
        CODE_ASSIST("code_assist_a", "code_assist_b"),
        NEURAL_ARCHIVE("neural_archive_a", "neural_archive_b"),
        AGENT_BRIDGE("agent_bridge_a", "agent_bridge_b"),
        CLOUD_STORAGE("cloud_storage_a", "cloud_storage_b"),
        TERMINAL("terminal_a", "terminal_b"),
        CONFERENCE_ROOM("conference_room_a", "conference_room_b")
    }

    enum class NexusSubGates(val styleA: String, val styleB: String, val fallback: String? = null) {
        CONSTELLATION("constellation_a", "constellation_b"),
        MONITORING("monitoring_a", "monitoring_b"),
        SPHERE_GRID("sphere_grid_a", "sphere_grid_b"),
        FUSION_MODE("fusion_mode_a", "fusion_mode_b"),
        TASK_ASSIGNMENT("task_assignment_a", "task_assignment_b"),
        META_INSTRUCT("meta_instruct_a", "meta_instruct_b")
    }
}
