These tests use:

- Framework: JUnit 5 (Jupiter) on Gradle's ProjectBuilder (unit-level)
- Strategy: Provide a stub plugin for id "genesis.android.library" to allow the convention plugin to
  apply successfully.
- We avoid requiring the Android Gradle Plugin by ensuring that CMakeLists.txt does not exist when
  the plugin is applied.
- The verifyNativeConfig task is exercised both with and without a CMake file, created after apply
  to prevent LibraryExtension wiring.