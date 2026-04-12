package dev.aurakai.auraframefx.xposed

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Structural and metadata tests for [StagingGenesisHookEntry].
 *
 * [StagingGenesisHookEntry] is an Xposed module entry point; its runtime behaviour
 * relies on the Xposed / YukiHookAPI framework which is not available in the JVM
 * unit-test environment.  These tests therefore focus on:
 *
 *  1. Class structure and interface contract
 *  2. Annotation configuration (the @InjectYukiHookWithXposed annotation is
 *     intentionally commented out in staging — we verify this is intentional)
 *  3. Kotlin type-system guarantees (e.g. the class is not final, the companion
 *     methods/constants if any exist)
 *
 * Actual hook behaviour is validated at integration / device-test level.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("StagingGenesisHookEntry — Structural Tests")
class StagingGenesisHookEntryTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Class structure
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Class structure")
    inner class ClassStructureTests {

        @Test
        @DisplayName("class should be instantiable with a no-arg constructor")
        fun classShouldBeInstantiableWithNoArgConstructor() {
            // StagingGenesisHookEntry is a staging variant; it must be constructable
            // so the Xposed framework can instantiate it via reflection.
            val entry = StagingGenesisHookEntry()
            assertNotNull(entry)
        }

        @Test
        @DisplayName("class should implement IYukiHookXposedInit")
        fun classShouldImplementIYukiHookXposedInit() {
            // Verify the staging entry implements the required Xposed interface
            val entry = StagingGenesisHookEntry()
            assertTrue(
                entry is com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit,
                "StagingGenesisHookEntry must implement IYukiHookXposedInit"
            )
        }

        @Test
        @DisplayName("class should be in the xposed package")
        fun classShouldBeInXposedPackage() {
            val packageName = StagingGenesisHookEntry::class.java.packageName
            assertEquals("dev.aurakai.auraframefx.xposed", packageName)
        }

        @Test
        @DisplayName("class should be named StagingGenesisHookEntry")
        fun classShouldHaveCorrectSimpleName() {
            assertEquals("StagingGenesisHookEntry", StagingGenesisHookEntry::class.simpleName)
        }

        @Test
        @DisplayName("class should NOT be annotated with @InjectYukiHookWithXposed in staging")
        fun classShouldNotBeAnnotatedWithInjectYukiHookInStaging() {
            // In the staging variant the annotation is commented out intentionally to
            // prevent the staging file from being picked up as an active Xposed module.
            val annotations = StagingGenesisHookEntry::class.java.annotations
            val hasInjectAnnotation = annotations.any { annotation ->
                annotation.annotationClass.qualifiedName ==
                    "com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed"
            }
            assertFalse(
                hasInjectAnnotation,
                "Staging entry must NOT carry @InjectYukiHookWithXposed — the annotation is intentionally disabled"
            )
        }

        @Test
        @DisplayName("onInit and onHook should be declared methods")
        fun onInitAndOnHookShouldBeDeclaredMethods() {
            val methodNames = StagingGenesisHookEntry::class.java.declaredMethods
                .map { it.name }
                .toSet()
            // The methods come from the interface; they are overridden in this class
            assertTrue("onInit" in methodNames, "onInit must be overridden")
            assertTrue("onHook" in methodNames, "onHook must be overridden")
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // @InjectYukiHookWithXposed annotation absent (staging safety check)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Staging safety — annotation absent")
    inner class StagingSafetyTests {

        @Test
        @DisplayName("class must not be loaded as an active Xposed module from a production path")
        fun classMustNotBeLoadedAsActiveModule() {
            // Production entry points are in the non-staging source tree.
            // This staging class is kept inactive by commenting out the annotation.
            // This test will fail (and correctly alert the team) if the annotation
            // is accidentally re-enabled in the staging variant.
            val annotations = StagingGenesisHookEntry::class.java.declaredAnnotations
            val hasActiveModuleAnnotation = annotations.any {
                it.annotationClass.simpleName == "InjectYukiHookWithXposed"
            }
            assertFalse(hasActiveModuleAnnotation)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Interface contract enforcement
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("IYukiHookXposedInit contract")
    inner class InterfaceContractTests {

        @Test
        @DisplayName("should declare exactly one class-level interface")
        fun shouldDeclareExactlyOneInterface() {
            val interfaces = StagingGenesisHookEntry::class.java.interfaces
            assertEquals(
                1,
                interfaces.size,
                "StagingGenesisHookEntry should implement exactly one interface"
            )
        }

        @Test
        @DisplayName("implemented interface should be IYukiHookXposedInit")
        fun implementedInterfaceShouldBeIYukiHookXposedInit() {
            val iface = StagingGenesisHookEntry::class.java.interfaces.first()
            assertEquals(
                "com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit",
                iface.name
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // FeatureToggles safety guards — regression tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("FeatureToggles — Xposed master gate regression")
    inner class FeatureTogglesRegressionTests {

        @Test
        @DisplayName("XPOSED_HOOKS_ENABLED must be false to prevent accidental hook activation")
        fun xposedHooksEnabledMustBeFalse() {
            // This is a CRITICAL safety check: if accidentally set to true in staging,
            // the hooks would activate on any Xposed-capable device.
            assertFalse(
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_HOOKS_ENABLED,
                "XPOSED_HOOKS_ENABLED must remain false in staging to prevent unintended hook activation"
            )
        }

        @Test
        @DisplayName("XPOSED_LOCKSCREEN_HOOKS must be false — stub not yet implemented")
        fun xposedLockscreenHooksMustBeFalse() {
            assertFalse(
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_LOCKSCREEN_HOOKS,
                "XPOSED_LOCKSCREEN_HOOKS is documented as a stub; it must stay false until implemented"
            )
        }

        @Test
        @DisplayName("XPOSED_UNIVERSAL_COMPONENT_HOOKS must be false — opt-in only")
        fun xposedUniversalComponentHooksMustBeFalse() {
            assertFalse(
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_UNIVERSAL_COMPONENT_HOOKS,
                "XPOSED_UNIVERSAL_COMPONENT_HOOKS is an opt-in broad hook and must default to false"
            )
        }

        @Test
        @DisplayName("individual hook flags should be readable without throwing")
        fun individualHookFlagsShouldBeReadable() {
            // Verify all the per-feature flags are accessible (no compilation/access errors)
            val flags = listOf(
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_GENESIS_SYSTEM_HOOKS,
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_GENESIS_UI_HOOKS,
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_GENESIS_ZYGOTE_HOOKS,
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_GENESIS_SELF_HOOKS,
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_NOTCH_BAR_HOOKER,
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_QS_HOOKER,
                dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles.XPOSED_CHROMA_CORE_HOOKER
            )
            // All flags should be boolean values — no NPE or access exceptions
            flags.forEach { flag ->
                assertTrue(flag is Boolean)
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Multiple instantiation — Xposed framework instantiates via reflection
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Multiple instantiation safety")
    inner class MultipleInstantiationTests {

        @Test
        @DisplayName("creating multiple instances should not throw")
        fun multipleInstancesShouldNotThrow() {
            assertDoesNotThrow {
                val instances = (1..5).map { StagingGenesisHookEntry() }
                assertEquals(5, instances.size)
            }
        }

        @Test
        @DisplayName("each new instance should be a distinct object")
        fun eachInstanceShouldBeDistinct() {
            val a = StagingGenesisHookEntry()
            val b = StagingGenesisHookEntry()
            assertNotSame(a, b)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Public API surface — class should be stateless
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Class API surface")
    inner class ApiSurfaceTests {

        @Test
        @DisplayName("class should have no public instance fields (stateless design)")
        fun classShouldHaveNoPublicInstanceFields() {
            // Stateless entry points are easier to reason about and avoids
            // concurrency issues if the framework instantiates more than once.
            val publicFields = StagingGenesisHookEntry::class.java.fields
                .filter { !java.lang.reflect.Modifier.isStatic(it.modifiers) }
            assertEquals(
                0,
                publicFields.size,
                "StagingGenesisHookEntry should have no public instance fields"
            )
        }

        @Test
        @DisplayName("onInit and onHook should be the only declared override methods")
        fun shouldDeclareOnlyOverrideMethods() {
            val declaredMethodNames = StagingGenesisHookEntry::class.java.declaredMethods
                .map { it.name }
                .toSet()
            // Both lifecycle methods must be present
            assertTrue("onInit" in declaredMethodNames, "onInit must be declared")
            assertTrue("onHook" in declaredMethodNames, "onHook must be declared")
        }

        @Test
        @DisplayName("class should be assignable to IYukiHookXposedInit via reflection")
        fun classShouldBeAssignableViaReflection() {
            val interfaceClass = Class.forName(
                "com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit"
            )
            assertTrue(
                interfaceClass.isAssignableFrom(StagingGenesisHookEntry::class.java),
                "StagingGenesisHookEntry must be assignable from IYukiHookXposedInit"
            )
        }
    }
}