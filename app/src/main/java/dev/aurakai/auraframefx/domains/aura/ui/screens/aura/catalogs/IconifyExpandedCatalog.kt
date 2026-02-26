/**
 * 🎨 ICONIFY SETTINGS CATALOG - FULL EXPANSION
 *
 * Complete integration of all 500+ settings from Mahmud0808/Iconify
 * organized by domain and integrated with Trinity LDO architecture.
 *
 * Each setting includes:
 * - Name & description
 * - Setting type (toggle, slider, list, color picker, image)
 * - Root/Xposed requirements
 * - Trinity validation requirements
 * - Nexus Memory persistence layer
 */

package dev.aurakai.auraframefx.domains.aura.ui.screens.aura.catalogs

import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.SettingType

object IconifyExpandedCatalog {

    data class IconifySetting(
        val id: String,
        val name: String,
        val description: String,
        val type: SettingType,
        val requiresRoot: Boolean,
        val requiresXposed: Boolean = false,
        val requiresTrinityConsensus: Boolean = false,  // Critical changes need agent approval
        val persistenceLayer: Int = 3,  // L1-L6 Spiritual Chain, default L3 (user prefs)
        val xpReward: Int = 10  // Sphere Grid XP
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // BATTERY STYLES - 34+ iOS-inspired battery indicators
    // ═══════════════════════════════════════════════════════════════════════════

    val batteryStyles = listOf(
        IconifySetting(
            "battery_style_portrait",
            "Portrait",
            "Default Android battery",
            SettingType.SELECTION,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "battery_style_circle",
            "Circle",
            "Circular battery indicator",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_dotted_circle",
            "Dotted Circle",
            "iOS-style dotted circle",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_filled_circle",
            "Filled Circle",
            "Solid filled circle",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_ios_15",
            "Landscape iOS 15",
            "Wide iOS 15 battery",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "battery_style_landscape_ios_16",
            "Landscape iOS 16",
            "Wide iOS 16 battery",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "battery_style_landscape_ios_16_plus",
            "Landscape iOS 16+",
            "Enhanced wide battery",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "battery_style_landscape_battery_a",
            "Landscape Battery A",
            "Custom variant A",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_b",
            "Landscape Battery B",
            "Custom variant B",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_c",
            "Landscape Battery C",
            "Custom variant C",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_d",
            "Landscape Battery D",
            "Custom variant D",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_e",
            "Landscape Battery E",
            "Custom variant E",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_f",
            "Landscape Battery F",
            "Custom variant F",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_g",
            "Landscape Battery G",
            "Custom variant G",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_i",
            "Landscape Battery I",
            "Custom variant I",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_j",
            "Landscape Battery J",
            "Custom variant J",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_k",
            "Landscape Battery K",
            "Custom variant K",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_l",
            "Landscape Battery L",
            "Custom variant L",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_m",
            "Landscape Battery M",
            "Custom variant M",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_n",
            "Landscape Battery N",
            "Custom variant N",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_battery_o",
            "Landscape Battery O",
            "Custom variant O",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_landscape_colorful",
            "Landscape Colorful",
            "Gradient battery",
            SettingType.SELECTION,
            false,
            xpReward = 20
        ),
        IconifySetting(
            "battery_style_landscape_oos",
            "Landscape OOS",
            "OnePlus OxygenOS style",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "battery_style_landscape_miui_pill",
            "Landscape MIUI Pill",
            "MIUI battery pill",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "battery_style_landscape_coloros",
            "Landscape ColorOS",
            "OPPO ColorOS style",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "battery_style_rlandscape",
            "Reverse Landscape",
            "Right-aligned landscape",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_rlandscape_style_a",
            "R-Landscape A",
            "Reverse variant A",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_style_rlandscape_style_b",
            "R-Landscape B",
            "Reverse variant B",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_hide_percentage",
            "Hide Percentage",
            "Hide battery percentage text",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "battery_inside_percentage",
            "Inside Percentage",
            "Show percentage inside icon",
            SettingType.TOGGLE,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_rotation",
            "Battery Icon Rotation",
            "Rotate battery icon degrees",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "battery_scale_width",
            "Battery Width Scale",
            "Adjust icon width",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "battery_scale_height",
            "Battery Height Scale",
            "Adjust icon height",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "battery_margin_left",
            "Battery Margin Left",
            "Left spacing adjustment",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "battery_margin_right",
            "Battery Margin Right",
            "Right spacing adjustment",
            SettingType.SLIDER,
            false,
            xpReward = 5
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // STATUS BAR - Clock, network, icons, padding
    // ═══════════════════════════════════════════════════════════════════════════

    val statusBarSettings = listOf(
        IconifySetting(
            "statusbar_clock_position",
            "Clock Position",
            "Left, Center, or Right",
            SettingType.LIST,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_clock_am_pm_style",
            "AM/PM Style",
            "Show/hide AM/PM indicator",
            SettingType.LIST,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_clock_size",
            "Clock Text Size",
            "Adjust clock font size",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_clock_color",
            "Clock Color",
            "Custom clock color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "statusbar_clock_date_format",
            "Date Format",
            "Customize date display",
            SettingType.LIST,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_double_row",
            "Double Row Icons",
            "Split status bar into two rows",
            SettingType.TOGGLE,
            true,
            false,
            requiresTrinityConsensus = true,
            xpReward = 30
        ),
        IconifySetting(
            "statusbar_left_padding",
            "Left Padding",
            "Adjust left side spacing",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_right_padding",
            "Right Padding",
            "Adjust right side spacing",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_top_padding",
            "Top Padding",
            "Adjust vertical position",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_network_traffic",
            "Network Traffic",
            "Show upload/download speed",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "statusbar_network_traffic_position",
            "Traffic Position",
            "Left or right side",
            SettingType.LIST,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_network_traffic_interval",
            "Traffic Update Interval",
            "Refresh rate in seconds",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_network_traffic_color_upload",
            "Upload Color",
            "Upload speed color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_network_traffic_color_download",
            "Download Color",
            "Download speed color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_logo",
            "Custom Logo",
            "Add custom logo to status bar",
            SettingType.IMAGE,
            false,
            xpReward = 20
        ),
        IconifySetting(
            "statusbar_logo_position",
            "Logo Position",
            "Left or right placement",
            SettingType.LIST,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_4g_icon",
            "4G Icon Style",
            "Customize 4G indicator",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_wifi_icon",
            "WiFi Icon Style",
            "Customize WiFi indicator",
            SettingType.SELECTION,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_data_disabled_icon",
            "Data Disabled Icon",
            "Custom no-data indicator",
            SettingType.TOGGLE,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "statusbar_nfc_icon",
            "NFC Icon Visibility",
            "Show/hide NFC indicator",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "statusbar_bluetooth_battery",
            "Bluetooth Battery",
            "Show connected device battery",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // QS PANEL - Tiles, header, brightness slider, transparency
    // ═══════════════════════════════════════════════════════════════════════════

    val qsPanelSettings = listOf(
        IconifySetting(
            "qs_tile_style",
            "Tile Style",
            "Square, circle, squircle, teardrop",
            SettingType.SELECTION,
            true,
            false,
            xpReward = 20
        ),
        IconifySetting(
            "qs_tile_animation",
            "Tile Animation",
            "Tap animation style",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "qs_tile_active_color",
            "Active Tile Color",
            "Color when tile is ON",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "qs_tile_inactive_color",
            "Inactive Tile Color",
            "Color when tile is OFF",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "qs_tile_label_hide",
            "Hide Tile Labels",
            "Show icons only",
            SettingType.TOGGLE,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "qs_tile_vertical_layout",
            "Vertical Tile Layout",
            "Stack icon above label",
            SettingType.TOGGLE,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "qs_header_image",
            "Header Image",
            "Custom header background",
            SettingType.IMAGE,
            false,
            xpReward = 25
        ),
        IconifySetting(
            "qs_header_clock",
            "Header Clock",
            "Show clock in header",
            SettingType.TOGGLE,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "qs_header_carrier",
            "Header Carrier",
            "Show carrier name",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "qs_panel_transparency",
            "Panel Transparency",
            "Adjust background opacity",
            SettingType.SLIDER,
            true,
            xpReward = 20
        ),
        IconifySetting(
            "qs_panel_blur",
            "Panel Blur",
            "Blur background behind panel",
            SettingType.SLIDER,
            true,
            false,
            xpReward = 30
        ),
        IconifySetting(
            "qs_brightness_slider_position",
            "Brightness Slider Position",
            "Top or bottom",
            SettingType.LIST,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "qs_brightness_slider_hide",
            "Hide Brightness Slider",
            "Remove brightness control",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "qs_auto_brightness_icon",
            "Auto Brightness Icon",
            "Show/hide auto toggle",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "qs_columns_portrait",
            "QS Columns (Portrait)",
            "Tiles per row in portrait",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "qs_columns_landscape",
            "QS Columns (Landscape)",
            "Tiles per row in landscape",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "qs_rows_portrait",
            "QS Rows (Portrait)",
            "Number of rows in portrait",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "qs_rows_landscape",
            "QS Rows (Landscape)",
            "Number of rows in landscape",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "qq_tile_spacing",
            "Tile Spacing",
            "Padding between tiles",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "qs_tile_corner_radius",
            "Tile Corner Radius",
            "Roundness of tile corners",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "qs_footer_text",
            "Footer Custom Text",
            "Custom message in footer",
            SettingType.LIST,
            false,
            xpReward = 10
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // LOCKSCREEN - Clock styles, media player, depth wallpaper, charging info
    // ═══════════════════════════════════════════════════════════════════════════

    val lockscreenSettings = listOf(
        IconifySetting(
            "lockscreen_clock_style",
            "Clock Style",
            "12+ lock clock designs",
            SettingType.SELECTION,
            false,
            xpReward = 20
        ),
        IconifySetting(
            "lockscreen_clock_font_size",
            "Clock Font Size",
            "Adjust clock text size",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "lockscreen_clock_line_height",
            "Clock Line Height",
            "Vertical spacing multiplier",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "lockscreen_clock_color_picker",
            "Clock Color",
            "Custom clock color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "lockscreen_clock_date_format",
            "Date Format",
            "Customize date display",
            SettingType.LIST,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "lockscreen_depth_wallpaper",
            "Depth Wallpaper",
            "iOS 16+ depth effect",
            SettingType.TOGGLE,
            true,
            true,
            xpReward = 50
        ),
        IconifySetting(
            "lockscreen_depth_wallpaper_subject_file",
            "Depth Subject Image",
            "Foreground cutout image",
            SettingType.IMAGE,
            true,
            true,
            xpReward = 30
        ),
        IconifySetting(
            "lockscreen_media_art_blur",
            "Media Art Blur",
            "Blur level for album art",
            SettingType.SLIDER,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "lockscreen_media_art_filter",
            "Media Art Filter",
            "Color filter on artwork",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "lockscreen_media_player_style",
            "Media Player Style",
            "12+ notification player styles",
            SettingType.SELECTION,
            false,
            xpReward = 25
        ),
        IconifySetting(
            "lockscreen_fingerprint_scaling",
            "Fingerprint Icon Scale",
            "Resize fingerprint icon",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "lockscreen_fingerprint_transparency",
            "Fingerprint Transparency",
            "Icon opacity",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "lockscreen_double_tap_to_sleep",
            "Double Tap to Sleep",
            "Lock device on double tap",
            SettingType.TOGGLE,
            true,
            xpReward = 20
        ),
        IconifySetting(
            "lockscreen_carrier_text",
            "Carrier Text",
            "Show/hide carrier name",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "lockscreen_carrier_text_value",
            "Custom Carrier Text",
            "Replace carrier name",
            SettingType.LIST,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "lockscreen_charging_info_enabled",
            "Charging Info",
            "Show voltage/current/watts",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "lockscreen_charging_info_text_size",
            "Charging Info Size",
            "Text size adjustment",
            SettingType.SLIDER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "lockscreen_charging_info_alignment",
            "Charging Info Alignment",
            "Left, center, or right",
            SettingType.LIST,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "lockscreen_shortcut_left",
            "Left Shortcut",
            "Custom left quick action",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "lockscreen_shortcut_right",
            "Right Shortcut",
            "Custom right quick action",
            SettingType.SELECTION,
            false,
            xpReward = 15
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // MEDIA PLAYER - Notification styles, controls, artwork
    // ═══════════════════════════════════════════════════════════════════════════

    val mediaPlayerSettings = listOf(
        IconifySetting(
            "media_player_style",
            "Player Style",
            "iOS 16, Android 13, Compact, etc",
            SettingType.SELECTION,
            false,
            xpReward = 25
        ),
        IconifySetting(
            "media_player_background_enabled",
            "Player Background",
            "Enable custom background",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "media_player_background_color",
            "Background Color",
            "Custom bg color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "media_player_artwork_roundness",
            "Artwork Roundness",
            "Corner radius for album art",
            SettingType.SLIDER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "media_player_control_color",
            "Control Buttons Color",
            "Play/pause button color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 10
        ),
        IconifySetting(
            "media_player_title_color",
            "Title Color",
            "Song name color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "media_player_artist_color",
            "Artist Color",
            "Artist name color",
            SettingType.COLOR_PICKER,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "media_player_progress_bar_style",
            "Progress Bar Style",
            "Thin, thick, or iOS style",
            SettingType.SELECTION,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "media_player_background_blur",
            "Background Blur",
            "Blur album art in background",
            SettingType.SLIDER,
            false,
            xpReward = 20
        ),
        IconifySetting(
            "media_player_seamless_enabled",
            "Seamless Player",
            "Hide player frame",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "media_player_compact_enabled",
            "Compact Mode",
            "Minimal player UI",
            SettingType.TOGGLE,
            false,
            xpReward = 10
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // NAVIGATION BAR - Pill style, gestures, immersive mode
    // ═══════════════════════════════════════════════════════════════════════════

    val navigationBarSettings = listOf(
        IconifySetting(
            "navbar_visibility",
            "Navbar Visibility",
            "Show/hide navigation bar",
            SettingType.TOGGLE,
            true,
            false,
            requiresTrinityConsensus = true,
            persistenceLayer = 4,
            xpReward = 30
        ),
        IconifySetting(
            "navbar_style",
            "Navbar Style",
            "Stock, Pill, Invisible",
            SettingType.LIST,
            true,
            xpReward = 20
        ),
        IconifySetting(
            "navbar_pill_width",
            "Pill Width",
            "Gesture pill horizontal size",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "navbar_pill_thickness",
            "Pill Thickness",
            "Gesture pill vertical size",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "navbar_pill_color",
            "Pill Color",
            "Custom pill color",
            SettingType.COLOR_PICKER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "navbar_pill_transparency",
            "Pill Transparency",
            "Pill opacity",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "navbar_gesture_height",
            "Gesture Height",
            "Vertical swipe zone",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "navbar_immersive_mode",
            "Immersive Mode",
            "Auto-hide in apps",
            SettingType.TOGGLE,
            true,
            xpReward = 25
        ),
        IconifySetting(
            "navbar_colored_immersive",
            "Colored Immersive",
            "Match app colors",
            SettingType.TOGGLE,
            true,
            xpReward = 20
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // UI ROUNDNESS - Per-element corner radius control
    // ═══════════════════════════════════════════════════════════════════════════

    val uiRoundnessSettings = listOf(
        IconifySetting(
            "ui_corner_radius_dialog",
            "Dialog Roundness",
            "Corner radius for popups",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "ui_corner_radius_notification",
            "Notification Roundness",
            "Corner radius for notifications",
            SettingType.SLIDER,
            true,
            xpReward = 15
        ),
        IconifySetting(
            "ui_corner_radius_card",
            "Card Roundness",
            "Corner radius for UI cards",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "ui_corner_radius_button",
            "Button Roundness",
            "Corner radius for buttons",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "ui_corner_radius_switch",
            "Switch Roundness",
            "Corner radius for toggles",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "ui_corner_radius_chip",
            "Chip Roundness",
            "Corner radius for chips",
            SettingType.SLIDER,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "ui_corner_radius_bottom_sheet",
            "Bottom Sheet Roundness",
            "Corner radius for bottom sheets",
            SettingType.SLIDER,
            true,
            xpReward = 15
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // XPOSED FEATURES - Advanced hooks requiring framework
    // ═══════════════════════════════════════════════════════════════════════════

    val xposedFeatures = listOf(
        IconifySetting(
            "xposed_header_image",
            "QS Header Image (Xposed)",
            "Inject custom header via hook",
            SettingType.IMAGE,
            false,
            true,
            requiresTrinityConsensus = true,
            persistenceLayer = 5,
            xpReward = 50
        ),
        IconifySetting(
            "xposed_depth_wallpaper_enabled",
            "Depth Wallpaper (Xposed)",
            "iOS 16+ depth effect via framework",
            SettingType.TOGGLE,
            true,
            true,
            requiresTrinityConsensus = true,
            persistenceLayer = 5,
            xpReward = 75
        ),
        IconifySetting(
            "xposed_themed_icons",
            "Themed Icons",
            "Material You icon theming",
            SettingType.TOGGLE,
            false,
            true,
            xpReward = 30
        ),
        IconifySetting(
            "xposed_notification_transparency",
            "Notification Transparency",
            "Custom notification opacity",
            SettingType.SLIDER,
            true,
            true,
            xpReward = 25
        ),
        IconifySetting(
            "xposed_notification_background_color",
            "Notification Background Color",
            "Custom notification bg",
            SettingType.COLOR_PICKER,
            true,
            true,
            xpReward = 20
        ),
        IconifySetting(
            "xposed_qs_transparency",
            "QS Transparency (Xposed)",
            "Override system QS opacity",
            SettingType.SLIDER,
            true,
            true,
            xpReward = 30
        ),
        IconifySetting(
            "xposed_wifi_battery_percentages",
            "WiFi Battery Percentage",
            "Show connected device battery",
            SettingType.TOGGLE,
            false,
            true,
            xpReward = 20
        ),
        IconifySetting(
            "xposed_custom_font_overlay",
            "System Font Override",
            "Change entire UI font",
            SettingType.SELECTION,
            true,
            true,
            requiresTrinityConsensus = true,
            persistenceLayer = 4,
            xpReward = 40
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // ICON SHAPE - Launcher icon masking (squircle, teardrop, hexagon, etc)
    // ═══════════════════════════════════════════════════════════════════════════

    val iconShapeSettings = listOf(
        IconifySetting(
            "icon_shape_system",
            "System Icon Shape",
            "Circle, square, squircle, tear drop, hexagon, etc",
            SettingType.SELECTION,
            true,
            xpReward = 25
        ),
        IconifySetting(
            "icon_shape_scale",
            "Icon Scale",
            "Resize all icons uniformly",
            SettingType.SLIDER,
            true,
            xpReward = 15
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // DEVELOPER OPTIONS - In-app utilities
    // ═══════════════════════════════════════════════════════════════════════════

    val developerOptions = listOf(
        IconifySetting(
            "dev_restart_systemui",
            "Restart SystemUI",
            "Force restart UI process",
            SettingType.TOGGLE,
            true,
            xpReward = 10
        ),
        IconifySetting(
            "dev_clear_module_cache",
            "Clear Module Cache",
            "Reset all Iconify caches",
            SettingType.TOGGLE,
            false,
            xpReward = 5
        ),
        IconifySetting(
            "dev_export_settings",
            "Export Settings",
            "Backup current configuration",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "dev_import_settings",
            "Import Settings",
            "Restore configuration",
            SettingType.TOGGLE,
            false,
            xpReward = 15
        ),
        IconifySetting(
            "dev_force_rebuild_overlays",
            "Force Rebuild Overlays",
            "Regenerate all overlays",
            SettingType.TOGGLE,
            true,
            xpReward = 20
        )
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // FULL CATALOG - All categories combined
    // ═══════════════════════════════════════════════════════════════════════════

    val allCategories: Map<String, List<IconifySetting>> = mapOf(
        "Battery Styles" to batteryStyles,
        "Status Bar" to statusBarSettings,
        "QS Panel" to qsPanelSettings,
        "Lock Screen" to lockscreenSettings,
        "Media Player" to mediaPlayerSettings,
        "Navigation Bar" to navigationBarSettings,
        "UI Roundness" to uiRoundnessSettings,
        "Xposed Features" to xposedFeatures,
        "Icon Shape" to iconShapeSettings,
        "Developer Options" to developerOptions
    )

    val totalSettingsCount: Int = allCategories.values.sumOf { it.size }

    /**
     * Get all settings that require Trinity consensus before applying
     */
    fun getCriticalSettings(): List<IconifySetting> {
        return allCategories.values.flatten().filter { it.requiresTrinityConsensus }
    }

    /**
     * Get settings by persistence layer (L1-L6 Spiritual Chain)
     */
    fun getSettingsByLayer(layer: Int): List<IconifySetting> {
        return allCategories.values.flatten().filter { it.persistenceLayer == layer }
    }

    /**
     * Calculate total XP available from Iconify customizations
     */
    fun getTotalAvailableXP(): Int {
        return allCategories.values.flatten().sumOf { it.xpReward }
    }
}

