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
}