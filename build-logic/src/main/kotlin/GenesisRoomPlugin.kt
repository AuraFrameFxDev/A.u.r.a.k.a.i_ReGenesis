import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * ===================================================================
 * GENESIS ROOM CONVENTION PLUGIN
 * ===================================================================
 *
 * Convention plugin for modules requiring Room Database support.
 *
 * This plugin configures:
 * - Room runtime and KTX dependencies
 * - Room KSP annotation processor
 * - Consistent Room version across all database modules
 *
 * Usage:
 * plugins {
 *     id("genesis.android.library")
 *     id("genesis.android.room")
 * }
 */
class GenesisRoomPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            // Apply KSP if not already applied (Room requires it)
            pluginManager.apply("com.google.devtools.ksp")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val roomVersion = libs.findVersion("room").get().requiredVersion

            dependencies {
                add("implementation", "androidx.room:room-runtime:$roomVersion")
                add("implementation", "androidx.room:room-ktx:$roomVersion")
                add("ksp", "androidx.room:room-compiler:$roomVersion")
            }
        }
    }
}
