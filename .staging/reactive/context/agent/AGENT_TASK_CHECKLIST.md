# Agent Task Success Checklist

Use this checklist when performing tasks on the AURAKAI project to ensure adherence to best
practices.

## Pre-Task Validation

- [ ] I have read the [AGENT_INSTRUCTIONS.md](AGENT_INSTRUCTIONS.md) document
- [ ] I understand the task requirements and scope
- [ ] I have identified all files and modules that will be affected
- [ ] I have a rollback plan if changes cause issues

## File Path Validation

- [ ] All referenced file paths have been verified to exist
- [ ] Absolute paths use the correct repository root
- [ ] Relative paths in build scripts use `$rootDir`, `$projectDir`, or `layout.buildDirectory`
- [ ] Generated file paths account for the build directory structure
- [ ] No hardcoded paths that may break in different environments

## Plugin Application Checklist

- [ ] Android plugin (`com.android.application` or `com.android.library`) is applied first
- [ ] Kotlin Android plugin (`org.jetbrains.kotlin.android`) is applied second
- [ ] Other plugins (Hilt, KSP, etc.) are applied after core plugins
- [ ] Extension blocks (e.g., `android { }`) are only accessed after plugin application
- [ ] `pluginManager.withPlugin()` is used for conditional plugin configuration
- [ ] Plugin order is documented if non-standard

## Toolchain Verification

- [ ] Java toolchain version matches project requirement (Java 24)
- [ ] `jvmToolchain()` is configured in Kotlin plugin setup
- [ ] `org.gradle.java.installations.auto-download=true` is set in `gradle.properties`
- [ ] Foojay resolver is enabled in `settings.gradle.kts`
- [ ] Build environment has access to required JDK or can download it
- [ ] Toolchain configuration is consistent across all modules

## Code Generation Task Order

- [ ] Code generation tasks are registered before build tasks
- [ ] `preBuild` or compilation tasks depend on code generation (`dependsOn`)
- [ ] Code generation tasks do NOT use `finalizedBy`
- [ ] Generated source directories are added to `sourceSets`
- [ ] Clean tasks remove generated sources before regeneration
- [ ] Generated sources are excluded from version control (`.gitignore`)

## Workflow Configuration (GitHub Actions)

- [ ] `actions/setup-java@v5` is used with explicit version
- [ ] `android-actions/setup-android@v3` is included for Android builds
- [ ] `gradle/actions/setup-gradle@v5` is used for Gradle setup
- [ ] `chmod +x ./gradlew` is executed before using gradlew
- [ ] Environment variables are properly set before build steps
- [ ] Workflow steps are in correct order (checkout → setup → build → test)
- [ ] Artifacts are uploaded on success or failure as appropriate

## Error Reporting Standards

- [ ] Error messages identify the failing step or file
- [ ] Error messages show expected vs. actual state
- [ ] Error messages include actionable guidance for resolution
- [ ] Links to documentation are provided where applicable
- [ ] Error logs do not expose sensitive information
- [ ] Stack traces are available for debugging but not in production logs

## Security & Best Practices

- [ ] No secrets or credentials are hardcoded in code
- [ ] Sensitive values use environment variables or GitHub Secrets
- [ ] All user inputs are validated and sanitized
- [ ] Dependencies are checked for known vulnerabilities
- [ ] File permissions are set appropriately
- [ ] Access controls are properly configured
- [ ] Encryption is used for sensitive data

## Testing Requirements

- [ ] Changes are tested locally before committing
- [ ] Affected modules are built individually to verify
- [ ] Clean build is performed to check for stale artifacts
- [ ] Existing tests pass after changes
- [ ] New tests are added for new functionality
- [ ] CI/CD workflows pass all checks
- [ ] Edge cases are considered and tested

## Documentation Updates

- [ ] Code changes are accompanied by comment updates
- [ ] README or docs are updated if user-facing changes
- [ ] Build configuration changes are documented
- [ ] Breaking changes are clearly noted
- [ ] Examples are provided for new features
- [ ] Changelog is updated (if applicable)

## Convention Plugin Compliance

For changes to build-logic convention plugins:

- [ ] Plugin structure follows established patterns
- [ ] Plugin application order is correct
- [ ] Extensions are configured after plugins are applied
- [ ] Toolchain configuration is included
- [ ] Plugin is registered in `build-logic/build.gradle.kts`
- [ ] Plugin is tested with sample module
- [ ] Plugin documentation is updated

## Build Configuration Changes

For changes to `build.gradle.kts` files:

- [ ] Plugin IDs are correct and versions specified where needed
- [ ] Dependencies are organized and duplicates removed
- [ ] Exclusions are properly configured
- [ ] Task dependencies are correctly wired
- [ ] Source sets include generated sources if applicable
- [ ] Build types and flavors are properly configured
- [ ] Lint and other quality checks are enabled

## Module Creation or Modification

For new modules or significant module changes:

- [ ] Module is included in `settings.gradle.kts`
- [ ] Module has correct convention plugin applied
- [ ] Module has proper `build.gradle.kts` configuration
- [ ] Module namespace is unique and follows naming convention
- [ ] Module dependencies are minimal and necessary
- [ ] Module is buildable independently
- [ ] Module is buildable as part of full project

## Post-Task Validation

- [ ] All affected modules build successfully
- [ ] All tests pass (or expected failures are documented)
- [ ] No new linting errors introduced
- [ ] Git status shows only intended changes
- [ ] Commit messages are clear and descriptive
- [ ] Changes are pushed to correct branch
- [ ] PR description explains changes and rationale
- [ ] Related issues are linked or referenced

## Continuous Improvement

- [ ] Recurring issues have been identified
- [ ] Root causes have been analyzed
- [ ] Preventive measures have been proposed or implemented
- [ ] Documentation has been updated with learnings
- [ ] Team has been notified of important patterns or issues
- [ ] This checklist has been updated if new patterns discovered

## Rollback Plan

In case of issues:

- [ ] I know how to revert my changes
- [ ] I have identified dependent systems that may be affected
- [ ] I can communicate rollback status to stakeholders
- [ ] I have documented what went wrong for future reference

---

## Quick Reference

| Area                    | Key Document            | Location                     |
|-------------------------|-------------------------|------------------------------|
| General Guidelines      | AGENT_INSTRUCTIONS.md   | Root                         |
| Workflow Best Practices | AGENT_WORKFLOW_GUIDE.md | .github/                     |
| Build System            | BUILD.md                | Root                         |
| Convention Plugins      | Plugin files            | build-logic/src/main/kotlin/ |
| CI/CD Workflows         | Workflow files          | .github/workflows/           |

---

**Last Updated:** 2025-01-06
**Version:** 1.0
**Maintained by:** Genesis Protocol Quality Team
