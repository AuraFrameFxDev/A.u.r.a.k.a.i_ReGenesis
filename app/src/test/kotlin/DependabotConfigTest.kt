/*
Testing library/framework:
- kotlin.test (standard Kotlin test API), which typically delegates to JUnit 5/Jupiter on JVM.
- No new dependencies introduced.

Scope:
- Validate Dependabot YAML semantics focused on the PR diff snippet (notably empty package-ecosystem).
- Tests cover happy paths, edge cases, and failure conditions with a lightweight parser implemented in test code.
*/

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private data class DependabotUpdate(
    val packageEcosystem: String? = null,
    val directory: String? = null,
    val scheduleInterval: String? = null
)

private data class DependabotConfig(
    val version: Int? = null,
    val updates: List<DependabotUpdate> = emptyList()
)

/**
 * Minimal YAML parser tailored for a subset of Dependabot config we care about:
 * - version (top-level)
 * - updates[] with fields: package-ecosystem, directory, schedule.interval
 *
 * Notes:
 * - Strips inline comments starting with '#'
 * - Ignores unknown keys
 * - Handles '- package-ecosystem: ...' on the same line form
 */
private fun parseDependabotConfig(yaml: String): DependabotConfig {
    val lines = yaml.lines()
    var version: Int? = null
    val updates = mutableListOf<DependabotUpdate>()
    var inUpdates = false
    var current: DependabotUpdate? = null
    var inSchedule = false
    var scheduleIndent = -1

    fun pushCurrent() {
        current?.let { updates.add(it) }
        current = null
        inSchedule = false
        scheduleIndent = -1
    }

    for (raw in lines) {
        val noComment = raw.substringBefore('#')
        val line = noComment.trimEnd()
        if (line.isBlank()) continue

        val indent = line.indexOfFirst { !it.isWhitespace() }.let { if (it < 0) 0 else it }
        val trimmed = line.trim()

        // Exiting schedule block based on indentation
        if (inSchedule && indent <= scheduleIndent && !trimmed.startsWith("interval:")) {
            inSchedule = false
            scheduleIndent = -1
        }

        when {
            trimmed.startsWith("version:") -> {
                val v = trimmed.removePrefix("version:").trim().trim('"', '\'')
                version = v.toIntOrNull()
            }

            trimmed.startsWith("updates:") -> {
                inUpdates = true
            }

            inUpdates && trimmed.startsWith("- ") -> {
                // New update entry
                pushCurrent()
                current = DependabotUpdate()
                inSchedule = false
                scheduleIndent = -1
                // Support inline first key after '- '
                val rest = trimmed.removePrefix("- ").trim()
                if (rest.startsWith("package-ecosystem:")) {
                    val v = rest.removePrefix("package-ecosystem:").trim().trim('"', '\'')
                    current = current!!.copy(packageEcosystem = v.ifBlank { null })
                }
            }

            inUpdates && current != null -> {
                val parts = trimmed.split(":", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim().trim('"', '\'')
                    when {
                        key == "package-ecosystem" -> current = current!!.copy(
                            packageEcosystem = value.ifBlank { null }
                        )

                        key == "directory" -> current = current!!.copy(directory = value)
                        key == "schedule" -> {
                            inSchedule = true
                            scheduleIndent = indent
                        }

                        inSchedule && key == "interval" -> current =
                            current!!.copy(scheduleInterval = value)
                    }
                }
            }
        }
    }
    pushCurrent()
    return DependabotConfig(version, updates)
}

/**
 * Validates parsed Dependabot config against a subset of rules that commonly matter:
 * - version must be exactly 2
 * - updates must be non-empty
 * - each update must have non-empty package-ecosystem
 * - directory should be present and start with "/" (root-relative)
 * - schedule.interval must be one of: daily | weekly | monthly
 */
private fun validateDependabotConfig(cfg: DependabotConfig): List<String> {
    val errors = mutableListOf<String>()
    if (cfg.version != 2) {
        errors.add("version must be 2, found: ${cfg.version?.toString() ?: "null"}")
    }
    if (cfg.updates.isEmpty()) {
        errors.add("updates must contain at least one entry")
    }
    val allowedIntervals = setOf("daily", "weekly", "monthly")
    cfg.updates.forEachIndexed { idx, u ->
        val at = "updates[$idx]"
        if (u.packageEcosystem.isNullOrBlank()) {
            errors.add("$at.package-ecosystem must be non-empty")
        }
        if (u.directory.isNullOrBlank()) {
            errors.add("$at.directory must be specified")
        } else if (!u.directory.startsWith("/")) {
            errors.add("$at.directory should start with '/' (root-relative), found: '${u.directory}'")
        }
        if (u.scheduleInterval.isNullOrBlank()) {
            errors.add(
                "$at.schedule.interval must be one of ${
                    allowedIntervals.joinToString(", ")
                }, found: ${
                    u.scheduleInterval?.let {
                        "'$it'"
                    } ?: "null"
                }")
        } else if (u.scheduleInterval !in allowedIntervals) {
            errors.add(
                "$at.schedule.interval must be one of ${
                    allowedIntervals.joinToString(", ")
                }, found: '${u.scheduleInterval}'"
            )
        }
    }
    return errors
}

/**
 * Helper that couples parse + validate for convenience in tests.
 */
private fun parseAndValidate(yaml: String): List<String> =
    validateDependabotConfig(parseDependabotConfig(yaml))

class DependabotConfigTest {

    @Test
    fun validConfig_parsesAndValidates() {
        val yaml = """
            version: 2
            updates:
              - package-ecosystem: "gradle"
                directory: "/"
                schedule:
                  interval: "weekly"
        """.trimIndent()

        val errors = parseAndValidate(yaml)
        assertTrue(
            errors.isEmpty(),
            "Expected no errors, but found: ${errors.joinToString()}"
        )
    }

    @Test
    fun invalidPackageEcosystem_reportsError_basedOnPRSnippet() {
        // Based on the PR diff snippet provided in the task
        val yaml = """
            # To get started with Dependabot version updates, you'll need to specify which
            # package ecosystems to update and where the package manifests are located.
            # Please see the documentation for all configuration options:
            # https://docs.github.com/code-security/dependabot/dependabot-version-updates/configuration-options-for-the-dependabot.yml-file

            version: 2
            updates:
              - package-ecosystem: "" # See documentation for possible values
                directory: "/" # Location of package manifests
                schedule:
                  interval: "weekly"
        """.trimIndent()

        val errors = parseAndValidate(yaml)
        assertTrue(
            errors.any { it.contains("package-ecosystem must be non-empty") },
            "Expected error about empty package-ecosystem; got: ${errors.joinToString()}"
        )
        // Ensure other parts are interpreted correctly
        assertFalse(
            errors.any { it.startsWith("version must be 2") },
            "Version should be valid"
        )
        assertFalse(errors.any {
            it.contains("schedule.interval must be") && it.contains(
                "found: 'weekly'"
            )
        }, "Weekly should be valid")
    }

    @Test
    fun missingScheduleInterval_reportsError() {
        val yaml = """
            version: 2
            updates:
              - package-ecosystem: "maven"
                directory: "/server"
                schedule:
                  # interval intentionally missing
        """.trimIndent()

        val errors = parseAndValidate(yaml)
        assertTrue(
            errors.any {
                it.contains("schedule.interval must be one of") && it.contains(
                    "found: null"
                )
            },
            "Expected error about missing schedule interval; got: ${errors.joinToString()}"
        )
    }

    @Test
    fun multipleUpdates_aggregatesErrors_andIdentifiesIndices() {
        val yaml = """
            version: 2
            updates:
              - package-ecosystem: "gradle"
                directory: "/"
                schedule:
                  interval: "daily"
              - package-ecosystem: "npm"
                directory: "app"   # missing leading slash
                schedule:
                  interval: "yearly" # invalid interval
        """.trimIndent()

        val errors = parseAndValidate(yaml)
        // Expect two errors: directory leading slash + invalid interval for second update
        assertTrue(
            errors.any { it.contains("updates[1].directory should start with '/'") },
            "Expected directory leading slash error for updates[1]"
        )
        assertTrue(errors.any {
            it.contains("updates[1].schedule.interval") && it.contains(
                "found: 'yearly'"
            )
        }, "Expected invalid interval error for updates[1]")
        // First update should be fine
        assertFalse(
            errors.any { it.contains("updates[0].") },
            "Did not expect errors for updates[0]; got: ${errors.joinToString()}"
        )
    }

    @Test
    fun parser_ignoresInlineComments_andWhitespace() {
        val yaml = """
            version: 2    # inline comment
            updates:
              - package-ecosystem: "pip"  # python
                directory: "/"            # root
                schedule:                 # block start
                  interval: "monthly"     # valid
        """.trimIndent()

        val errors = parseAndValidate(yaml)
        assertTrue(
            errors.isEmpty(),
            "Expected no errors when inline comments are present: ${errors.joinToString()}"
        )
    }

    @Test
    fun malformedVersion_nonNumeric_reportsError() {
        val yaml = """
            version: "two"
            updates:
              - package-ecosystem: "gradle"
                directory: "/"
                schedule:
                  interval: "weekly"
        """.trimIndent()

        val errors = parseAndValidate(yaml)
        assertTrue(
            errors.any { it.startsWith("version must be 2") },
            "Expected version error; got: ${errors.joinToString()}"
        )
    }

    @Test
    fun emptyOrWhitespaceInput_isRejected() {
        val blanks = listOf("", "   ", "\n\t   \n")
        blanks.forEach { blank ->
            val errors = parseAndValidate(blank)
            assertTrue(
                errors.any { it.contains("version must be 2") },
                "Expected version error for blank input"
            )
            assertTrue(
                errors.any { it.contains("updates must contain at least one entry") },
                "Expected updates error for blank input"
            )
        }
    }
}
