package dev.aurakai.auraframefx.data.customization

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for ReGenesis Customization Settings data classes and enums.
 *
 * Covers:
 * - [IconifyCategory] enum
 * - [IconPackSetting] sealed class instances
 * - [BrightnessBarStyle] data class and companion factory instances
 * - [QSTileStyle] data class, [QSTileStyle.TileShape] enum, and companion instances
 * - [NotificationStyle] data class and companion instances
 * - [IconifyXposedSetting] sealed class instances
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("ReGenesisCustomizationSettings Tests")
class ReGenesisCustomizationSettingsTest {

    // =========================================================================
    // IconifyCategory Enum
    // =========================================================================

    @Nested
    @DisplayName("IconifyCategory Enum")
    inner class IconifyCategoryTests {

        @Test
        @DisplayName("should have 10 categories")
        fun `should have 10 categories`() {
            assertEquals(10, IconifyCategory.entries.size)
        }

        @Test
        @DisplayName("HOME should have correct displayName and description")
        fun `HOME should have correct displayName and description`() {
            assertEquals("Home", IconifyCategory.HOME.displayName)
            assertEquals("Quick access and overview", IconifyCategory.HOME.description)
        }

        @Test
        @DisplayName("ICON_PACKS should have correct displayName")
        fun `ICON_PACKS should have correct displayName`() {
            assertEquals("Icon Packs", IconifyCategory.ICON_PACKS.displayName)
        }

        @Test
        @DisplayName("BRIGHTNESS_BARS should have correct displayName")
        fun `BRIGHTNESS_BARS should have correct displayName`() {
            assertEquals("Brightness Bars", IconifyCategory.BRIGHTNESS_BARS.displayName)
        }

        @Test
        @DisplayName("QS_PANEL_TILES should have correct displayName")
        fun `QS_PANEL_TILES should have correct displayName`() {
            assertEquals("QS Panel Tiles", IconifyCategory.QS_PANEL_TILES.displayName)
        }

        @Test
        @DisplayName("NOTIFICATIONS should have correct displayName")
        fun `NOTIFICATIONS should have correct displayName`() {
            assertEquals("Notifications", IconifyCategory.NOTIFICATIONS.displayName)
        }

        @Test
        @DisplayName("XPOSED should have correct displayName")
        fun `XPOSED should have correct displayName`() {
            assertEquals("Xposed Menu", IconifyCategory.XPOSED.displayName)
        }

        @Test
        @DisplayName("all categories should have non-empty displayName and description")
        fun `all categories should have non-empty displayName and description`() {
            IconifyCategory.entries.forEach { category ->
                assertTrue(
                    category.displayName.isNotEmpty(),
                    "Category ${category.name} should have non-empty displayName"
                )
                assertTrue(
                    category.description.isNotEmpty(),
                    "Category ${category.name} should have non-empty description"
                )
            }
        }

        @Test
        @DisplayName("should resolve enum entries by name")
        fun `should resolve enum entries by name`() {
            assertEquals(IconifyCategory.HOME, IconifyCategory.valueOf("HOME"))
            assertEquals(IconifyCategory.XPOSED, IconifyCategory.valueOf("XPOSED"))
            assertEquals(IconifyCategory.SETTINGS, IconifyCategory.valueOf("SETTINGS"))
        }
    }

    // =========================================================================
    // IconPackSetting Sealed Class
    // =========================================================================

    @Nested
    @DisplayName("IconPackSetting Sealed Class")
    inner class IconPackSettingTests {

        @Test
        @DisplayName("AuroraIconPack should have correct id")
        fun `AuroraIconPack should have correct id`() {
            assertEquals("aurora_icons", IconPackSetting.AuroraIconPack.id)
        }

        @Test
        @DisplayName("AuroraIconPack should have correct name")
        fun `AuroraIconPack should have correct name`() {
            assertEquals("Aurora", IconPackSetting.AuroraIconPack.name)
        }

        @Test
        @DisplayName("AuroraIconPack should be in 'Icon Packs' category")
        fun `AuroraIconPack should be in Icon Packs category`() {
            assertEquals("Icon Packs", IconPackSetting.AuroraIconPack.category)
        }

        @Test
        @DisplayName("GradientIconPack should have correct id")
        fun `GradientIconPack should have correct id`() {
            assertEquals("gradient_icons", IconPackSetting.GradientIconPack.id)
        }

        @Test
        @DisplayName("LornIconPack should have correct name")
        fun `LornIconPack should have correct name`() {
            assertEquals("Lorn", IconPackSetting.LornIconPack.name)
        }

        @Test
        @DisplayName("PlumpyIconPack should have correct description")
        fun `PlumpyIconPack should have correct description`() {
            assertEquals("Bold filled icons", IconPackSetting.PlumpyIconPack.description)
        }

        @Test
        @DisplayName("BatteryStyleCircle should have correct id")
        fun `BatteryStyleCircle should have correct id`() {
            assertEquals("battery_circle", IconPackSetting.BatteryStyleCircle.id)
        }

        @Test
        @DisplayName("BatteryStyleCircle should be in 'Battery Styles' category")
        fun `BatteryStyleCircle should be in Battery Styles category`() {
            assertEquals("Battery Styles", IconPackSetting.BatteryStyleCircle.category)
        }

        @Test
        @DisplayName("BatteryStyleIOS15 should reference iOS 15")
        fun `BatteryStyleIOS15 should reference iOS 15`() {
            assertEquals("battery_ios15", IconPackSetting.BatteryStyleIOS15.id)
            assertTrue(IconPackSetting.BatteryStyleIOS15.name.contains("iOS 15"))
        }

        @Test
        @DisplayName("BatteryStyleIOS16 should reference iOS 16")
        fun `BatteryStyleIOS16 should reference iOS 16`() {
            assertEquals("battery_ios16", IconPackSetting.BatteryStyleIOS16.id)
            assertTrue(IconPackSetting.BatteryStyleIOS16.name.contains("iOS 16"))
        }

        @Test
        @DisplayName("VolumePanelRounded should be in 'Volume Icons' category")
        fun `VolumePanelRounded should be in Volume Icons category`() {
            assertEquals("Volume Icons", IconPackSetting.VolumePanelRounded.category)
        }

        @Test
        @DisplayName("WifiIconPill should have 'Pill WiFi' name")
        fun `WifiIconPill should have Pill WiFi name`() {
            assertEquals("Pill WiFi", IconPackSetting.WifiIconPill.name)
        }

        @Test
        @DisplayName("all IconPackSetting objects should have non-empty id, name, description")
        fun `all IconPackSetting objects should have non-empty fields`() {
            val allSettings = listOf(
                IconPackSetting.AuroraIconPack,
                IconPackSetting.GradientIconPack,
                IconPackSetting.LornIconPack,
                IconPackSetting.PlumpyIconPack,
                IconPackSetting.AcherusIconPack,
                IconPackSetting.CircularIconPack,
                IconPackSetting.VectorIconPack,
                IconPackSetting.BatteryStyleCircle,
                IconPackSetting.BatteryStyleDottedCircle,
                IconPackSetting.BatteryStyleIOS15,
                IconPackSetting.BatteryStyleIOS16,
                IconPackSetting.BatteryStyleFilledCircle,
                IconPackSetting.BatteryStyleRectangle,
                IconPackSetting.BatteryStylePill,
                IconPackSetting.VolumePanelRounded,
                IconPackSetting.VolumePanelSharp,
                IconPackSetting.WifiIconPill,
                IconPackSetting.SignalIconBars
            )
            allSettings.forEach { setting ->
                assertTrue(setting.id.isNotEmpty(), "id should not be empty for ${setting.name}")
                assertTrue(setting.name.isNotEmpty(), "name should not be empty for ${setting.id}")
                assertTrue(setting.description.isNotEmpty(), "description should not be empty for ${setting.id}")
                assertTrue(setting.category.isNotEmpty(), "category should not be empty for ${setting.id}")
            }
        }
    }

    // =========================================================================
    // BrightnessBarStyle Data Class
    // =========================================================================

    @Nested
    @DisplayName("BrightnessBarStyle")
    inner class BrightnessBarStyleTests {

        @Test
        @DisplayName("STOCK should have correct id and name")
        fun `STOCK should have correct id and name`() {
            assertEquals("stock", BrightnessBarStyle.STOCK.id)
            assertEquals("Stock", BrightnessBarStyle.STOCK.name)
        }

        @Test
        @DisplayName("STOCK should have default flags false")
        fun `STOCK should have default flags false`() {
            assertFalse(BrightnessBarStyle.STOCK.hasGradient)
            assertFalse(BrightnessBarStyle.STOCK.isOutlined)
            assertFalse(BrightnessBarStyle.STOCK.isTranslucent)
        }

        @Test
        @DisplayName("GRADIENT should have hasGradient true")
        fun `GRADIENT should have hasGradient true`() {
            assertTrue(BrightnessBarStyle.GRADIENT.hasGradient)
        }

        @Test
        @DisplayName("OUTLINED should have isOutlined true")
        fun `OUTLINED should have isOutlined true`() {
            assertTrue(BrightnessBarStyle.OUTLINED.isOutlined)
        }

        @Test
        @DisplayName("TRANSLUCENT_OUTLINED should have both flags true")
        fun `TRANSLUCENT_OUTLINED should have both flags true`() {
            assertTrue(BrightnessBarStyle.TRANSLUCENT_OUTLINED.isOutlined)
            assertTrue(BrightnessBarStyle.TRANSLUCENT_OUTLINED.isTranslucent)
        }

        @Test
        @DisplayName("SEMI_TRANSPARENT should have isTranslucent true but not isOutlined")
        fun `SEMI_TRANSPARENT should have isTranslucent true but not isOutlined`() {
            assertTrue(BrightnessBarStyle.SEMI_TRANSPARENT.isTranslucent)
            assertFalse(BrightnessBarStyle.SEMI_TRANSPARENT.isOutlined)
        }

        @Test
        @DisplayName("ALL list should contain 8 styles")
        fun `ALL list should contain 8 styles`() {
            assertEquals(8, BrightnessBarStyle.ALL.size)
        }

        @Test
        @DisplayName("ALL list should contain STOCK")
        fun `ALL list should contain STOCK`() {
            assertTrue(BrightnessBarStyle.ALL.contains(BrightnessBarStyle.STOCK))
        }

        @Test
        @DisplayName("ALL list should contain FLUID")
        fun `ALL list should contain FLUID`() {
            assertTrue(BrightnessBarStyle.ALL.contains(BrightnessBarStyle.FLUID))
        }

        @Test
        @DisplayName("ALL entries should have unique ids")
        fun `ALL entries should have unique ids`() {
            val ids = BrightnessBarStyle.ALL.map { it.id }
            assertEquals(ids.size, ids.distinct().size)
        }

        @Test
        @DisplayName("data class should support copy")
        fun `data class should support copy`() {
            val original = BrightnessBarStyle.STOCK
            val modified = original.copy(hasGradient = true)
            assertTrue(modified.hasGradient)
            assertEquals(original.id, modified.id)
        }

        @Test
        @DisplayName("data class should support equality")
        fun `data class should support equality`() {
            val style1 = BrightnessBarStyle("custom", "Custom", "A custom style")
            val style2 = BrightnessBarStyle("custom", "Custom", "A custom style")
            assertEquals(style1, style2)
        }
    }

    // =========================================================================
    // QSTileStyle Data Class and TileShape Enum
    // =========================================================================

    @Nested
    @DisplayName("QSTileStyle")
    inner class QSTileStyleTests {

        @Test
        @DisplayName("TileShape should have 6 values")
        fun `TileShape should have 6 values`() {
            assertEquals(6, QSTileStyle.TileShape.entries.size)
        }

        @Test
        @DisplayName("STOCK style should use ROUNDED_SQUARE shape by default")
        fun `STOCK style should use ROUNDED_SQUARE shape by default`() {
            assertEquals(QSTileStyle.TileShape.ROUNDED_SQUARE, QSTileStyle.STOCK.shape)
        }

        @Test
        @DisplayName("CIRCULAR style should use CIRCLE shape")
        fun `CIRCULAR style should use CIRCLE shape`() {
            assertEquals(QSTileStyle.TileShape.CIRCLE, QSTileStyle.CIRCULAR.shape)
        }

        @Test
        @DisplayName("SQUIRCLE style should use SQUIRCLE shape")
        fun `SQUIRCLE style should use SQUIRCLE shape`() {
            assertEquals(QSTileStyle.TileShape.SQUIRCLE, QSTileStyle.SQUIRCLE.shape)
        }

        @Test
        @DisplayName("OUTLINE style should have hasOutline true")
        fun `OUTLINE style should have hasOutline true`() {
            assertTrue(QSTileStyle.OUTLINE.hasOutline)
        }

        @Test
        @DisplayName("TRANSLUCENT style should have isTranslucent true")
        fun `TRANSLUCENT style should have isTranslucent true`() {
            assertTrue(QSTileStyle.TRANSLUCENT.isTranslucent)
        }

        @Test
        @DisplayName("TRANSLUCENT_OUTLINE should have both flags true")
        fun `TRANSLUCENT_OUTLINE should have both flags true`() {
            assertTrue(QSTileStyle.TRANSLUCENT_OUTLINE.isTranslucent)
            assertTrue(QSTileStyle.TRANSLUCENT_OUTLINE.hasOutline)
        }

        @Test
        @DisplayName("ALL list should contain 7 styles")
        fun `ALL list should contain 7 styles`() {
            assertEquals(7, QSTileStyle.ALL.size)
        }

        @Test
        @DisplayName("ALL entries should have unique ids")
        fun `ALL entries should have unique ids`() {
            val ids = QSTileStyle.ALL.map { it.id }
            assertEquals(ids.size, ids.distinct().size)
        }

        @Test
        @DisplayName("data class should support equality")
        fun `data class should support equality`() {
            val style1 = QSTileStyle("test", "Test", "A test style")
            val style2 = QSTileStyle("test", "Test", "A test style")
            assertEquals(style1, style2)
        }
    }

    // =========================================================================
    // NotificationStyle Data Class
    // =========================================================================

    @Nested
    @DisplayName("NotificationStyle")
    inner class NotificationStyleTests {

        @Test
        @DisplayName("STOCK should have 'stock' id")
        fun `STOCK should have stock id`() {
            assertEquals("stock", NotificationStyle.STOCK.id)
        }

        @Test
        @DisplayName("IOS_STYLE should have 'ios' id")
        fun `IOS_STYLE should have ios id`() {
            assertEquals("ios", NotificationStyle.IOS_STYLE.id)
        }

        @Test
        @DisplayName("ROUNDED should have 'rounded' id")
        fun `ROUNDED should have rounded id`() {
            assertEquals("rounded", NotificationStyle.ROUNDED.id)
        }

        @Test
        @DisplayName("ALL list should contain 5 notification styles")
        fun `ALL list should contain 5 notification styles`() {
            assertEquals(5, NotificationStyle.ALL.size)
        }

        @Test
        @DisplayName("ALL should contain TRANSPARENT style")
        fun `ALL should contain TRANSPARENT style`() {
            assertTrue(NotificationStyle.ALL.any { it.id == "transparent" })
        }

        @Test
        @DisplayName("ALL entries should have unique ids")
        fun `ALL entries should have unique ids`() {
            val ids = NotificationStyle.ALL.map { it.id }
            assertEquals(ids.size, ids.distinct().size)
        }

        @Test
        @DisplayName("all notification styles should have non-empty fields")
        fun `all notification styles should have non-empty fields`() {
            NotificationStyle.ALL.forEach { style ->
                assertTrue(style.id.isNotEmpty(), "id should not be empty for ${style.name}")
                assertTrue(style.name.isNotEmpty(), "name should not be empty for ${style.id}")
                assertTrue(style.description.isNotEmpty(), "description should not be empty for ${style.id}")
            }
        }

        @Test
        @DisplayName("data class should support copy")
        fun `data class should support copy`() {
            val original = NotificationStyle.STOCK
            val copied = original.copy(name = "Custom Stock")
            assertEquals("Custom Stock", copied.name)
            assertEquals(original.id, copied.id)
        }
    }

    // =========================================================================
    // IconifyXposedSetting Sealed Class
    // =========================================================================

    @Nested
    @DisplayName("IconifyXposedSetting Sealed Class")
    inner class IconifyXposedSettingTests {

        @Test
        @DisplayName("HeaderClockStyle1 should have correct id")
        fun `HeaderClockStyle1 should have correct id`() {
            assertEquals("header_clock_1", IconifyXposedSetting.HeaderClockStyle1.id)
        }

        @Test
        @DisplayName("HeaderClockStyle2 should have correct id")
        fun `HeaderClockStyle2 should have correct id`() {
            assertEquals("header_clock_2", IconifyXposedSetting.HeaderClockStyle2.id)
        }

        @Test
        @DisplayName("HeaderClockStyle3 should have correct id")
        fun `HeaderClockStyle3 should have correct id`() {
            assertEquals("header_clock_3", IconifyXposedSetting.HeaderClockStyle3.id)
        }

        @Test
        @DisplayName("LockscreenClock1 should have correct id")
        fun `LockscreenClock1 should have correct id`() {
            assertEquals("lockscreen_clock_1", IconifyXposedSetting.LockscreenClock1.id)
        }

        @Test
        @DisplayName("HeaderClockStyle1 should not require reboot by default")
        fun `HeaderClockStyle1 should not require reboot by default`() {
            assertFalse(IconifyXposedSetting.HeaderClockStyle1.requiresReboot)
        }

        @Test
        @DisplayName("all xposed settings should have non-empty id, name, description")
        fun `all xposed settings should have non-empty fields`() {
            val allSettings = listOf(
                IconifyXposedSetting.HeaderClockStyle1,
                IconifyXposedSetting.HeaderClockStyle2,
                IconifyXposedSetting.HeaderClockStyle3,
                IconifyXposedSetting.LockscreenClock1
            )
            allSettings.forEach { setting ->
                assertTrue(setting.id.isNotEmpty(), "id should not be empty for ${setting.name}")
                assertTrue(setting.name.isNotEmpty(), "name should not be empty")
                assertTrue(setting.description.isNotEmpty(), "description should not be empty for ${setting.id}")
            }
        }
    }
}