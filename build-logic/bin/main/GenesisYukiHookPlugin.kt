import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * ===================================================================
 * GENESIS YUKIHOOK CONVENTION PLUGIN
 * ===================================================================
 *
 * Convention plugin for modules requiring YukiHook and Xposed support.
 *
 * This plugin configures:
 * - YukiHook API and KSP annotation processor
 * - Xposed API (compile only)
 * - EzXHelper for simplified development
 *
 * Usage:
 * plugins {
 *     id("genesis.android.library")
 *     id("genesis.android.yukihook")
 * }
 */
class GenesisYukiHookPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            // Apply KSP if not already applied (YukiHook requires it)
            pluginManager.apply("com.google.devtools.ksp")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val yukihookVersion = libs.findVersion("yukihook").get().requiredVersion
            val xposedVersion = libs.findVersion("xposed").get().requiredVersion

            dependencies {
                add("compileOnly", "de.robv.android.xposed:api:$xposedVersion")
                add("implementation", "com.highcapable.yukihookapi:api:$yukihookVersion")
                add("ksp", "com.highcapable.yukihookapi:ksp-xposed:$yukihookVersion")
                add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
            }
        }
    }
}
