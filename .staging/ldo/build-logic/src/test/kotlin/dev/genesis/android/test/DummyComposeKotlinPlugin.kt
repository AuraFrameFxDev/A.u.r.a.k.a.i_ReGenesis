package dev.genesis.android.test

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Dummy stand-in for "org.jetbrains.kotlin.plugin.compose" for unit tests.
 */
class DummyComposeKotlinPlugin : Plugin<Project> {
    /**
     * No-op implementation of Plugin.apply used in tests as a stand-in for
     * "org.jetbrains.kotlin.plugin.compose".
     *
     * Intentionally does not modify the provided Project.
     */
    override fun apply(target: Project) { /* no-op */
    }
}