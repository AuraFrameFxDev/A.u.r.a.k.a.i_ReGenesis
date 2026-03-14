import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete

class GenesisAndroidLibraryStubPlugin : Plugin<Project> {
    /**

     * Applies the stub plugin to the given Gradle project.
     *
     * Registers a no-op "cleanGeneratedSources" Delete task in the project's task container so
     * the convention plugin (used in tests) can locate and enhance that task later. The task is
     * created without default delete targets; the convention plugin is responsible for configuring
     * what gets deleted. The plugin deliberately does not add Android library extensions to keep
     * test setups lightweight.

     */
    override fun apply(target: Project) {
        with(target) {
            // Provide a dummy cleanGeneratedSources task so the convention plugin can enhance it.
            // Use Delete task type to allow safe execution in tests.
            tasks.register("cleanGeneratedSources", Delete::class.java) {
                group = "verification"
                // No default delete targets here; the convention plugin will add its own deletions.
            }
            // NOTE: We intentionally do NOT add Android Library extensions to keep the test lightweight.
            // The convention plugin only configures LibraryExtension when CMakeLists exists at apply-time,
            // and our tests control that timing to avoid requiring AGP.
        }
    }
}