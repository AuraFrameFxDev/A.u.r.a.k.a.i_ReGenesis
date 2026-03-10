Testing library/framework: JUnit 4 (`org.junit.Test`, `org.junit.Assert.*`).

These tests target `FileUtils` with the following assumed static methods:

- `readText(File)`: `String`.
- `writeText(File, String, boolean)`: `void`.
- `copyFile(File, File, boolean)`: `void`.
- `moveFile(File, File, boolean)`: `void`.
- `deleteQuietly(File)`: `boolean`.
- `ensureParentDirs(File)`: `void`.
- `resolvePath(Path, Path)`: `Path`.
- `isSubPath(Path, Path)`: `boolean`.
- `listFilesRecursively(Path, String)`: `List<Path>` (the second argument may be `null` to indicate
  no filter).

If your `FileUtils` uses different names or resides in a package (e.g.,
`com.example.util.FileUtils`), either:

- Import your `FileUtils` class and call its methods directly.
- Keep the reflection bridges but update `Class.forName("com.example.util.FileUtils")` to the fully
  qualified class name.

Focus on `diff` coverage: prioritize writing tests for functions changed in the PR diff by adjusting
or adding cases that hit new branches/edge-cases revealed by the changes.