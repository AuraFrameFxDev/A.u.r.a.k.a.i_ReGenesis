# Gradle Build Configuration Tests

This document describes the comprehensive test suite created for validating the recent Gradle build configuration changes.

## Test Files Created

### 1. AppBuildGradleChangesTest.kt
Comprehensive test suite for `app/build.gradle.kts` changes.

#### Test Coverage Areas:

##### Xposed and Hooking Dependencies (6 tests)
- Validates Xposed API is correctly declared as `compileOnly`
- Ensures YukiHookAPI is properly configured with implementation and KSP
- Verifies KavaRef libraries (core and extension) are included
- Checks local Xposed API JAR files use correct path format
- Validates hooking dependencies are positioned before Compose dependencies

##### Room Database KSP Configuration (5 tests)
- Confirms Room compiler uses KSP annotation processing
- Ensures Room runtime/KTX are not redundantly declared
- Validates Room compiler is not used as implementation
- Checks incorrect KSP usage (navigation compose, yukihookapi.api) is removed

##### Firebase KTX Migration (4 tests)
- Verifies Firebase BOM platform is declared
- Ensures all Firebase dependencies use KTX variants
- Confirms non-KTX Firebase dependencies are removed
- Validates no Firebase dependency duplication

##### Moshi Removal (4 tests)
- Confirms Moshi and Moshi Kotlin dependencies are removed
- Validates Retrofit Moshi converter is removed
- Ensures Moshi KSP code generation is removed
- Verifies Kotlinx Serialization is used for Retrofit instead

##### Core Library Desugaring (2 tests)
- Validates correct `coreLibraryDesugaring` configuration
- Ensures desugar doesn't use incorrect `implementation` configuration

##### Dependency Organization and Deduplication (5 tests)
- Validates lifecycle dependencies are not duplicated
- Ensures libsu dependencies appear exactly once
- Confirms Hilt Work is declared only once
- Validates Navigation Compose is declared only once
- Checks proper organizational comments exist

##### Edge Cases and Error Conditions (4 tests)
- Ensures no hardcoded version strings in dependencies
- Validates KSP is only used for annotation processors
- Confirms all project module references are valid
- Checks dependencies block structure is valid

##### Regression Prevention (4 tests)
- Verifies Compose BOM is still present
- Ensures essential Compose dependencies exist
- Confirms Hilt dependencies are configured
- Validates networking dependencies are present

##### Total: 34 comprehensive tests

### 2. VersionCatalogChangesTest.kt
Comprehensive test suite for `gradle/libs.versions.toml` changes.

#### Test Coverage Areas:

##### Version Declarations (5 tests)
- Confirms `moshiCodegen` version is removed
- Validates `androidxNavigationComposeGradlePlugin` version is added (2.9.5)
- Ensures all essential versions are present with correct values
- Verifies Firebase BOM and Room versions are declared

##### Library Declarations (8 tests)
- Confirms `moshi-kotlin-codegen` library entry is removed
- Validates `yuApiClient` library is added with correct configuration
- Ensures Firebase KTX libraries are declared
- Verifies Firebase BOM library is declared
- Confirms Room libraries are properly declared
- Validates YukiHookAPI libraries are declared
- Ensures KavaRef libraries are declared
- Checks Xposed API library is declared

##### Version References (2 tests)
- Validates libraries reference correct version variables
- Ensures Firebase KTX libraries reference correct versions

##### Bundle Definitions (2 tests)
- Confirms Firebase bundle includes KTX variants
- Validates Room bundle includes required libraries

##### Consistency and Integrity (3 tests)
- Ensures all version references have declarations
- Identifies potentially orphaned version declarations (informational)
- Validates TOML file is well-formed

##### Regression Prevention (2 tests)
- Confirms essential dependency versions remain
- Validates backward compatibility of common library aliases

##### Edge Cases and Special Scenarios (4 tests)
- Validates semantic versioning format
- Ensures no duplicate library aliases
- Confirms library groups are valid Maven coordinates
- Validates module coordinates are complete

##### Total: 26 comprehensive tests

### 3. CoreUiBuildGradleTest.kt
Test suite for `core/ui/build.gradle.kts` structural validation.

#### Test Coverage Areas (5 tests):
- Dependencies block formatting validation
- Android library plugin application check
- Essential UI dependencies presence verification
- Blank line formatting validation
- Kotlin script syntax validation (balanced braces/parentheses)

## Test Philosophy

### Bias for Action
These tests follow a "bias for action" approach, comprehensively validating:
1. **What was added** - New dependencies, configurations, and features
2. **What was removed** - Obsolete dependencies and incorrect configurations
3. **What was changed** - Migration from non-KTX to KTX variants
4. **What should never change** - Critical dependencies and configurations

### Test Categories

#### Happy Path Tests
- Validate correct dependency declarations
- Verify proper version references
- Confirm expected configurations exist

#### Edge Case Tests
- Check for duplicate dependencies
- Validate no hardcoded versions
- Ensure correct KSP usage patterns
- Verify proper file formats

#### Failure Condition Tests
- Detect removed obsolete dependencies
- Identify incorrect configurations
- Catch potential regressions

#### Regression Prevention Tests
- Ensure critical dependencies remain
- Validate backward compatibility
- Confirm essential configurations persist

## Running the Tests

These tests use JUnit 5 (Jupiter) and can be run via:

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests AppBuildGradleChangesTest
./gradlew test --tests VersionCatalogChangesTest
./gradlew test --tests CoreUiBuildGradleTest

# Run with detailed output
./gradlew test --info
```

## Test Maintenance

When making future changes to build configuration:

1. **Update these tests** to reflect intentional changes
2. **Add new tests** for new features or dependencies
3. **Keep regression tests** to prevent reintroduction of issues
4. **Document changes** in this README

## Key Changes Validated

### app/build.gradle.kts
- ✅ Added Xposed/YukiHook/KavaRef hooking framework
- ✅ Reorganized dependencies with proper comments
- ✅ Fixed Room KSP configuration
- ✅ Migrated to Firebase KTX variants with BOM
- ✅ Removed Moshi in favor of Kotlinx Serialization
- ✅ Fixed desugar configuration to use `coreLibraryDesugaring`
- ✅ Eliminated duplicate dependencies

### gradle/libs.versions.toml
- ✅ Removed `moshiCodegen` version
- ✅ Added `androidxNavigationComposeGradlePlugin` version
- ✅ Replaced `moshi-kotlin-codegen` with `yuApiClient` library
- ✅ Maintained all essential library declarations
- ✅ Preserved version reference integrity

### core/ui/build.gradle.kts
- ✅ Cleaned up formatting (removed extra blank line)
- ✅ Maintained all essential dependencies
- ✅ Preserved proper structure

## Total Test Count: 65 tests

This comprehensive test suite ensures the build configuration changes are:
- Correctly implemented
- Free of regressions
- Properly structured
- Ready for production use