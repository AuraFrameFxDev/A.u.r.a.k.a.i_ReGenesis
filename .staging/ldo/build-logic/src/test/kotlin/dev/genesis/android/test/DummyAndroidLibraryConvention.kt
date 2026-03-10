package dev.genesis.android.test

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Dummy replacement for "genesis.android.library" in tests.
 * Registers a fake 'android' LibraryExtension so the compose convention can configure it.
 *
 * NOTE: FakeLibraryExtension is disabled due to AGP API changes.
 * This plugin is currently a no-op stub for test compilation.
 */
class DummyAndroidLibraryConvention : Plugin<Project> {
    override fun apply(target: Project) {
        // No-op: FakeLibraryExtension disabled due to AGP 9.0 API changes
    }
}