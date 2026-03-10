// Plugin classpath configuration script
// This isolates plugin resolution from individual module build scripts

// NOTE: This script must not add repositories when repositoriesMode = FAIL_ON_PROJECT_REPOS
// Repositories are declared centrally in settings.gradle.kts

buildscript {
    dependencies {
        // No local repositories declared here; resolution uses settings-defined repositories
    }
}
