# Agent Documentation Summary

This document provides an overview of the agent instruction system implemented in the AURAKAI
project.

## Overview

The AURAKAI project has implemented a comprehensive set of agent instructions and best practices to
ensure consistent, reliable, and secure development workflows. This system is designed to help both
human developers and automated agents (like CI/CD systems and AI assistants) work effectively on the
project.

## Documentation Structure

### Core Documents

1. **[AGENT_INSTRUCTIONS.md](AGENT_INSTRUCTIONS.md)** - Primary reference
    - Comprehensive guide covering 8 key areas of best practices
    - Detailed examples and patterns
    - Project-specific notes and quick reference
    - **Audience:** All agents and developers

2. **[.github/AGENT_WORKFLOW_GUIDE.md](.github/AGENT_WORKFLOW_GUIDE.md)** - GitHub Actions focus
    - Specific guidance for CI/CD workflows
    - Required setup patterns
    - Error handling for workflows
    - **Audience:** DevOps engineers, CI/CD maintainers

3. **[AGENT_TASK_CHECKLIST.md](AGENT_TASK_CHECKLIST.md)** - Validation tool
    - Comprehensive checklist for task execution
    - Pre-task and post-task validation
    - Module-specific requirements
    - **Audience:** Task executors, reviewers

### Supporting Documentation

- **[README.md](README.md)** - Includes reference to agent docs
- **Convention Plugins** - Reference AGENT_INSTRUCTIONS.md in comments
- **Test Suite** - Validates documentation presence and structure

## The 8 Key Best Practices

### 1. Always Validate File Paths

- Verify all file paths exist before use
- Use absolute paths in repository context
- Log clear errors for missing files

### 2. Confirm Plugin Application

- Apply plugins in correct order
- Use `pluginManager.withPlugin()` for safe configuration
- Never access extensions before plugins are applied

### 3. Enforce Toolchain Consistency

- Use Java 24 toolchain across all modules
- Enable auto-download for JDK
- Configure Foojay resolver for automatic provisioning

### 4. Order of Operations

- Code generation runs BEFORE compilation
- Use `dependsOn` not `finalizedBy`
- Add generated sources to source sets

### 5. Workflow Robustness

- Use explicit versions for all GitHub Actions
- Set environment variables before builds
- Follow correct setup order

### 6. Error Reporting

- Include failing step/file in error messages
- Show expected vs. actual state
- Provide actionable guidance
- Link to documentation

### 7. Security & Best Practices

- Never expose secrets in logs
- Use GitHub Secrets for sensitive data
- Validate all inputs
- Check dependencies for vulnerabilities

### 8. Continuous Improvement

- Log all task outcomes
- Identify recurring issues
- Propose preventive measures
- Update documentation with learnings

## Implementation in Build System

### Convention Plugins

Both `AndroidLibraryConventionPlugin` and `AndroidApplicationConventionPlugin` have been enhanced
with:

1. **Proper plugin ordering** - Android plugin → Kotlin plugin → others
2. **Safe configuration** - Using `pluginManager.withPlugin()` blocks
3. **Error handling** - Try-catch with detailed GradleException messages
4. **Toolchain setup** - Java 24 toolchain configuration
5. **Clean tasks** - Generated source cleanup before builds
6. **Documentation references** - Comments pointing to AGENT_INSTRUCTIONS.md

### Example Error Message

When a plugin configuration fails, the error message follows this structure:

```
ERROR: AndroidLibraryConventionPlugin configuration failed
Project: :module-name
Expected: Android library plugin applied before configuring android { } block
Actual: Plugin not found
Action: Ensure 'com.android.library' plugin is available
Documentation: See AGENT_INSTRUCTIONS.md section 2
```

## GitHub Actions Integration

The `.github/AGENT_WORKFLOW_GUIDE.md` ensures all workflows follow best practices:

- Java setup with version 24
- Android SDK with licenses
- Gradle setup with caching
- Proper step ordering
- Error artifact collection

## Testing

The `AgentInstructionsValidationTest` suite validates:

- All documentation files exist
- Required sections are present
- Convention plugins reference the documentation
- Error handling is implemented
- Toolchain configuration is correct

## Usage Guidelines

### For Developers

1. Read [AGENT_INSTRUCTIONS.md](AGENT_INSTRUCTIONS.md) before making changes
2. Use [AGENT_TASK_CHECKLIST.md](AGENT_TASK_CHECKLIST.md) when working on tasks
3. Follow patterns from convention plugins
4. Update documentation when discovering new patterns

### For Automated Agents

1. Parse and follow [AGENT_INSTRUCTIONS.md](AGENT_INSTRUCTIONS.md) guidelines
2. Validate actions against [AGENT_TASK_CHECKLIST.md](AGENT_TASK_CHECKLIST.md)
3. Generate errors using the documented format
4. Reference documentation in error messages

### For CI/CD Workflows

1. Follow [.github/AGENT_WORKFLOW_GUIDE.md](.github/AGENT_WORKFLOW_GUIDE.md) patterns
2. Use explicit action versions
3. Include error artifact collection
4. Set proper environment variables

## Continuous Improvement Process

When issues occur:

1. **Document** - Record the issue and resolution
2. **Analyze** - Determine root cause
3. **Prevent** - Propose systematic fix
4. **Update** - Enhance documentation
5. **Share** - Inform team of learnings

## Quick Reference

| Need                       | Document                          | Location                     |
|----------------------------|-----------------------------------|------------------------------|
| General best practices     | AGENT_INSTRUCTIONS.md             | Root                         |
| GitHub Actions patterns    | AGENT_WORKFLOW_GUIDE.md           | .github/                     |
| Task validation            | AGENT_TASK_CHECKLIST.md           | Root                         |
| Convention plugin examples | AndroidLibraryConventionPlugin.kt | build-logic/src/main/kotlin/ |
| Workflow examples          | ci-build.yml                      | .github/workflows/           |

## Benefits

This agent instruction system provides:

1. **Consistency** - Standardized patterns across all modules
2. **Reliability** - Fewer build failures and issues
3. **Security** - Protected secrets and validated inputs
4. **Maintainability** - Clear documentation and error messages
5. **Efficiency** - Quick resolution of common issues
6. **Quality** - Automated validation and testing

## Version History

- **v1.0** (2025-01-06) - Initial implementation
    - Created core documentation files
    - Enhanced convention plugins
    - Implemented validation tests

---

**Maintained by:** Genesis Protocol Quality Team  
**Last Updated:** 2025-01-06
