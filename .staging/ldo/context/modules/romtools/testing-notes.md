Testing stack assumptions for new tests:

- Instrumented UI tests: JUnit4 + AndroidX Compose UI Test (ui-test-junit4, ui-test-manifest).
- JVM unit tests: JUnit4.
  If your project uses different runners/libraries (e.g., Kotest, MockK, JUnit5, Hilt Android
  Testing),
  update imports and rules accordingly.