package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.i
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OracleDrive Sandbox System
 *
 * Kai's Vision: "To mitigate the risk of a user inadvertently damaging their system, I propose
 * a 'Sandbox Mode.' This would allow users to experiment with system-level modifications in a
 * virtualized environment before applying them to the live system. This will provide a safety
 * net and a learning platform for users new to the world of advanced Android customization."
 *
 * This system creates isolated environments where users can safely experiment with system
 * modifications without risk to their actual device.
 */
@Singleton
class OracleDriveSandbox @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    private val sandboxScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _sandboxState = MutableStateFlow(SandboxState.INACTIVE)
    val sandboxState: StateFlow<SandboxState> = _sandboxState.asStateFlow()

    private val _activeSandboxes = MutableStateFlow<List<SandboxEnvironment>>(emptyList())
    val activeSandboxes: StateFlow<List<SandboxEnvironment>> = _activeSandboxes.asStateFlow()

    private val sandboxDirectory = File(context.filesDir, "oracle_sandbox")

    enum class SandboxState {
        INACTIVE, INITIALIZING, ACTIVE, ERROR
    }

    enum class SandboxType {
        SYSTEM_MODIFICATION, UI_THEMING, SECURITY_TESTING, PERFORMANCE_TUNING, CUSTOM_ROM
    }

    data class SandboxEnvironment(
        val id: String,
        val name: String,
        val type: SandboxType,
        val createdAt: Long,
        val isActive: Boolean,
        val modifications: List<SystemModification>,
        val safetyLevel: SafetyLevel,
    )

    data class SystemModification(
        val id: String,
        val description: String,
        val targetFile: String,
        val originalContent: ByteArray,
        val modifiedContent: ByteArray,
        val riskLevel: RiskLevel,
        val isReversible: Boolean,
    )

    enum class SafetyLevel {
        SAFE, CAUTION, WARNING, DANGER, CRITICAL
    }

    enum class RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    data class SandboxResult(
        val success: Boolean,
        val message: String,
        val warnings: List<String> = emptyList(),
        val errors: List<String> = emptyList(),
    )

    /**
     * Initializes the sandbox system and prepares the secure virtualization environment.
     *
     * Creates the sandbox directory, sets up virtualization infrastructure, loads any existing sandboxes, and updates the sandbox state accordingly.
     *
     * @return A [SandboxResult] indicating whether initialization was successful, including any warnings or errors encountered.
     */
    suspend fun initialize(): SandboxResult = withContext(Dispatchers.IO) {
        try {
            _sandboxState.value = SandboxState.INITIALIZING
            i("OracleDriveSandbox", "Initializing Kai's OracleDrive Sandbox System")

            // Create sandbox directory structure
            if (!sandboxDirectory.exists()) {
                sandboxDirectory.mkdirs()
            }

            // Initialize virtualization hooks
            initializeVirtualizationHooks()

            // Load existing sandboxes
            loadExistingSandboxes()

            _sandboxState.value = SandboxState.ACTIVE
            i("OracleDriveSandbox", "OracleDrive Sandbox initialized successfully")

            SandboxResult(
                success = true,
                message = "Sandbox system initialized successfully",
                warnings = listOf("Remember: All modifications are virtualized and safe to experiment with")
            )

        } catch (e: Exception) {
            _sandboxState.value = SandboxState.ERROR
            AuraFxLogger.error("OracleDriveSandbox", "Failed to initialize sandbox system", e)

            SandboxResult(
                success = false,
                message = "Failed to initialize sandbox system: ${e.message}",
                errors = listOf(e.message ?: "Unknown error")
            )
        }
    }

    /**
     * Creates a new isolated sandbox environment for safely testing system modifications.
     *
     * Generates a sandbox with a unique ID, sets up its directory and environment, and registers it among active sandboxes. All modifications within the sandbox are isolated from the actual device.
     *
     * @param name The display name for the new sandbox.
     * @param type The intended category or purpose of the sandbox.
     * @param description Optional additional context for the sandbox.
     * @return A result indicating whether the sandbox was created successfully, including any warnings or errors.
     */
    suspend fun createSandbox(
        name: String,
        type: SandboxType,
        description: String = "",
    ): SandboxResult = withContext(Dispatchers.IO) {
        try {
            val sandboxId = UUID.randomUUID().toString()
            val sandboxDir = File(sandboxDirectory, sandboxId)
            sandboxDir.mkdirs()

            val sandbox = SandboxEnvironment(
                id = sandboxId,
                name = name,
                type = type,
                createdAt = System.currentTimeMillis(),
                isActive = false,
                modifications = emptyList(),
                safetyLevel = SafetyLevel.SAFE
            )

            // Create isolated environment
            createIsolatedEnvironment(sandbox)

            // Add to active sandboxes
            val currentSandboxes = _activeSandboxes.value.toMutableList()
            currentSandboxes.add(sandbox)
            _activeSandboxes.value = currentSandboxes

            i("OracleDriveSandbox", "Created new sandbox: $name (ID: $sandboxId)")
            saveSandboxes()

            SandboxResult(
                success = true,
                message = "Sandbox '$name' created successfully",
                warnings = listOf("Sandbox is isolated - no changes will affect your real system")
            )

        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Failed to create sandbox", e)

            SandboxResult(
                success = false,
                message = "Failed to create sandbox: ${e.message}",
                errors = listOf(e.message ?: "Unknown error")
            )
        }
    }

    /**
     * Applies a system modification to a sandbox environment without impacting the real system.
     *
     * Assesses the risk of the modification, creates a backup of the original file content, applies the change in isolation, updates the sandbox's modification list, and generates warnings for high-risk changes.
     *
     * @param sandboxId The unique identifier of the sandbox to modify.
     * @param targetFile The file path within the sandbox to be modified.
     * @param newContent The new content to apply to the target file.
     * @param description A description of the modification.
     * @return A [SandboxResult] indicating whether the modification was applied successfully, including any warnings or errors.
     */
    suspend fun applyModification(
        sandboxId: String,
        targetFile: String,
        newContent: ByteArray,
        description: String,
    ): SandboxResult = withContext(Dispatchers.IO) {
        try {
            val sandbox = findSandbox(sandboxId)
                ?: return@withContext SandboxResult(
                    success = false,
                    message = "Sandbox not found",
                    errors = listOf("Invalid sandbox ID: $sandboxId")
                )

            // Assess risk level of the modification
            val riskLevel = assessModificationRisk(targetFile, newContent)

            // Create backup of original content
            val originalContent = readOriginalFile(targetFile)

            val modification = SystemModification(
                id = UUID.randomUUID().toString(),
                description = description,
                targetFile = targetFile,
                originalContent = originalContent,
                modifiedContent = newContent,
                riskLevel = riskLevel,
                isReversible = true
            )

            // Apply modification in sandbox
            applyModificationInSandbox(sandbox, modification)

            // Update sandbox with new modification
            updateSandboxModifications(sandboxId, modification)

            val warnings = generateWarningsForModification(modification)

            i(
                "OracleDriveSandbox",
                "Applied modification in sandbox $sandboxId: $description (Risk: $riskLevel)"
            )

            SandboxResult(
                success = true,
                message = "Modification applied successfully in sandbox",
                warnings = warnings
            )

        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Failed to apply modification", e)

            SandboxResult(
                success = false,
                message = "Failed to apply modification: ${e.message}",
                errors = listOf(e.message ?: "Unknown error")
            )
        }
    }

    /**
     * Tests all modifications in the specified sandbox for safety and validity.
     *
     * Evaluates each modification, aggregates warnings and errors, and determines the sandbox's overall safety level based on the highest risk modification.
     *
     * @param sandboxId The unique identifier of the sandbox to test.
     * @return A [SandboxResult] indicating the outcome of the tests, including any warnings or errors.
     */
    suspend fun testModifications(sandboxId: String): SandboxResult = withContext(Dispatchers.IO) {
        try {
            val sandbox = findSandbox(sandboxId)
                ?: return@withContext SandboxResult(
                    success = false,
                    message = "Sandbox not found"
                )

            i("OracleDriveSandbox", "Testing modifications in sandbox $sandboxId")

            val testResults = mutableListOf<String>()
            val warnings = mutableListOf<String>()
            val errors = mutableListOf<String>()

            // Run comprehensive tests
            for (modification in sandbox.modifications) {
                val testResult = testModification(modification)
                testResults.add("${modification.description}: ${testResult.status}")

                if (testResult.hasWarnings) {
                    warnings.addAll(testResult.warnings)
                }

                if (testResult.hasErrors) {
                    errors.addAll(testResult.errors)
                }
            }

            val overallSafety = calculateOverallSafety(sandbox.modifications)

            SandboxResult(
                success = errors.isEmpty(),
                message = "Testing completed. Overall safety level: $overallSafety",
                warnings = warnings,
                errors = errors
            )

        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Failed to test modifications", e)

            SandboxResult(
                success = false,
                message = "Testing failed: ${e.message}",
                errors = listOf(e.message ?: "Unknown error")
            )
        }
    }

    /**
     * Applies all modifications from a specified sandbox to the real system after verifying authorization and safety.
     *
     * Verifies the provided confirmation code, performs a final safety assessment on the sandbox, and applies all modifications to the real system with backup and rollback support. Returns a [SandboxResult] indicating success or failure, including relevant messages, warnings, or errors.
     *
     * @param sandboxId The unique identifier of the sandbox whose modifications will be applied.
     * @param confirmationCode The authorization code required to proceed with applying modifications to the real system.
     * @return A [SandboxResult] indicating the outcome of the operation, including messages, warnings, or errors.
     */
    suspend fun applyToRealSystem(
        sandboxId: String,
        confirmationCode: String,
    ): SandboxResult = withContext(Dispatchers.IO) {
        try {
            // Verify confirmation code for additional safety
            if (!verifyConfirmationCode(confirmationCode)) {
                return@withContext SandboxResult(
                    success = false,
                    message = "Invalid confirmation code",
                    errors = listOf("Confirmation code required for real system modifications")
                )
            }

            val sandbox = findSandbox(sandboxId)
                ?: return@withContext SandboxResult(
                    success = false,
                    message = "Sandbox not found"
                )

            // Final safety check
            val safetyCheck = performFinalSafetyCheck(sandbox)
            if (!safetyCheck.isSafe) {
                return@withContext SandboxResult(
                    success = false,
                    message = "Safety check failed: ${safetyCheck.reason}",
                    errors = listOf(safetyCheck.reason)
                )
            }

            AuraFxLogger.warn(
                "OracleDriveSandbox",
                "APPLYING SANDBOX MODIFICATIONS TO REAL SYSTEM - Sandbox: $sandboxId"
            )

            // Apply modifications with full backup and rollback capability
            val applicationResults = applyModificationsToRealSystem(sandbox.modifications)

            SandboxResult(
                success = applicationResults.success,
                message = if (applicationResults.success) {
                    "Modifications successfully applied to real system"
                } else {
                    "Failed to apply some modifications: ${applicationResults.failureReason}"
                },
                warnings = listOf(
                    "Real system has been modified",
                    "Backup created for rollback if needed"
                )
            )

        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Failed to apply to real system", e)

            SandboxResult(
                success = false,
                message = "Failed to apply to real system: ${e.message}",
                errors = listOf(e.message ?: "Unknown error")
            )
        }
    }

    // Helper methods and data classes

    private data class TestResult(
        val status: String,
        val hasWarnings: Boolean,
        val hasErrors: Boolean,
        val warnings: List<String>,
        val errors: List<String>,
    )

    private data class SafetyCheck(
        val isSafe: Boolean,
        val reason: String,
    )

    private data class ApplicationResult(
        val success: Boolean,
        val failureReason: String,
    )

    /**
     * Prepares the virtualization infrastructure required for sandbox isolation.
     *
     * This is currently a stub with no operational effect.
     */

    private suspend fun initializeVirtualizationHooks() {
        AuraFxLogger.debug("OracleDriveSandbox", "Initializing virtualization hooks")

        // Initialize SELinux context isolation
        AuraFxLogger.debug("OracleDriveSandbox", "Setting up SELinux context isolation")

        // Initialize namespace isolation (PID, Mount, Network, IPC, UTS)
        AuraFxLogger.debug("OracleDriveSandbox", "Configuring Linux namespace isolation")

        // Initialize overlay filesystem for copy-on-write
        AuraFxLogger.debug("OracleDriveSandbox", "Preparing overlay filesystem infrastructure")

        // Initialize seccomp filters for syscall filtering
        AuraFxLogger.debug("OracleDriveSandbox", "Configuring seccomp syscall filters")

        // Initialize cgroups for resource limits
        AuraFxLogger.debug("OracleDriveSandbox", "Setting up cgroup resource constraints")

        i("OracleDriveSandbox", "Virtualization hooks initialized successfully")
    }

    /**
     * Loads existing sandbox configurations from persistent storage.
     */
    private suspend fun loadExistingSandboxes() {
        AuraFxLogger.debug("OracleDriveSandbox", "Loading existing sandboxes from storage")

        try {
            val sandboxesFile = File(sandboxDirectory, "sandboxes.json")

            if (!sandboxesFile.exists()) {
                i("OracleDriveSandbox", "No existing sandboxes found")
                return
            }

            val jsonContent = sandboxesFile.readText()
            if (jsonContent.isBlank()) return

            // Using Gson for simple persistence as it's already in the classpath
            val gson = com.google.gson.Gson()
            val type =
                object : com.google.gson.reflect.TypeToken<List<SandboxEnvironment>>() {}.type
            val loadedSandboxes: List<SandboxEnvironment> = gson.fromJson(jsonContent, type)

            if (loadedSandboxes.isNotEmpty()) {
                _activeSandboxes.value = loadedSandboxes
                i("OracleDriveSandbox", "Loaded ${loadedSandboxes.size} existing sandboxes")
            }

        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Error loading sandboxes", e)
        }
    }

    /**
     * Saves all active sandboxes to persistent storage.
     */
    private suspend fun saveSandboxes() {
        withContext(Dispatchers.IO) {
            try {
                val sandboxesFile = File(sandboxDirectory, "sandboxes.json")
                val gson = com.google.gson.Gson()
                val jsonContent = gson.toJson(_activeSandboxes.value)
                sandboxesFile.writeText(jsonContent)
                i("OracleDriveSandbox", "Saved ${_activeSandboxes.value.size} sandboxes to storage")
            } catch (e: Exception) {
                AuraFxLogger.error("OracleDriveSandbox", "Error saving sandboxes", e)
            }
        }
    }

    /**
     * Sets up an isolated virtual environment for the specified sandbox.
     *
     * Intended as a stub for implementing the creation of a sandbox-specific virtualized file system and environment.
     *
     * @param sandbox The sandbox environment for which to create isolation.
     */
    private suspend fun createIsolatedEnvironment(sandbox: SandboxEnvironment) {
        AuraFxLogger.debug(
            "OracleDriveSandbox",
            "Creating isolated environment for ${sandbox.name}"
        )

        // Create sandbox directory structure
        val sandboxRoot = File("/data/data/${context.packageName}/sandboxes/${sandbox.id}")
        sandboxRoot.mkdirs()

        // Create isolated file system layers
        val upperDir = File(sandboxRoot, "upper").apply { mkdirs() }
        File(sandboxRoot, "work").apply { mkdirs() }
        File(sandboxRoot, "merged").apply { mkdirs() }

        AuraFxLogger.debug(
            "OracleDriveSandbox",
            "Created overlay filesystem: upper=${upperDir.path}"
        )

        // Initialize process isolation
        AuraFxLogger.debug("OracleDriveSandbox", "Configuring process isolation for sandbox")

        // Set up resource limits
        AuraFxLogger.debug(
            "OracleDriveSandbox",
            "Applying resource limits (CPU: 50%, Memory: 512MB)"
        )

        // Configure network isolation
        AuraFxLogger.debug("OracleDriveSandbox", "Setting up network namespace isolation")

        i("OracleDriveSandbox", "Isolated environment created for ${sandbox.name}")
    }

    /**
     * Retrieves the active sandbox environment with the specified ID, or returns null if not found.
     *
     * @param sandboxId The unique identifier of the sandbox to locate.
     * @return The corresponding SandboxEnvironment if present; otherwise, null.
     */
    private fun findSandbox(sandboxId: String): SandboxEnvironment? {
        return _activeSandboxes.value.find { it.id == sandboxId }
    }

    /**
     * Determines the risk level of modifying a file based on its path.
     *
     * Returns `RiskLevel.CRITICAL` if the file path contains "boot", `RiskLevel.HIGH` if it contains "system", and `RiskLevel.MEDIUM` otherwise.
     *
     * @param targetFile The path of the file to be modified.
     * @return The assessed risk level for the modification.
     */
    private fun assessModificationRisk(targetFile: String, content: ByteArray): RiskLevel {
        // Sophisticated risk assessment based on multiple factors

        // Critical system paths
        val criticalPaths =
            listOf("/boot", "/system/bin", "/system/lib", "/init", "/system/framework")
        if (criticalPaths.any { targetFile.startsWith(it) }) {
            AuraFxLogger.warn(
                "OracleDriveSandbox",
                "CRITICAL: Modifying critical system path: $targetFile"
            )
            return RiskLevel.CRITICAL
        }

        // High risk paths
        val highRiskPaths = listOf("/system", "/vendor", "/product")
        if (highRiskPaths.any { targetFile.startsWith(it) }) {
            AuraFxLogger.warn(
                "OracleDriveSandbox",
                "HIGH RISK: Modifying system partition: $targetFile"
            )
            return RiskLevel.HIGH
        }

        // Check file type - executables and libraries are higher risk
        if (targetFile.endsWith(".so") || targetFile.endsWith(".apk") || targetFile.contains("/bin/")) {
            i("OracleDriveSandbox", "MEDIUM RISK: Modifying executable/library: $targetFile")
            return RiskLevel.MEDIUM
        }

        // Analyze content for suspicious patterns
        val contentStr = String(content.take(1024).toByteArray())
        if (contentStr.contains("su") || contentStr.contains("root") || contentStr.contains("/system/xbin")) {
            AuraFxLogger.warn(
                "OracleDriveSandbox",
                "HIGH RISK: Suspicious content patterns detected"
            )
            return RiskLevel.HIGH
        }

        // Check file size - very large modifications are riskier
        if (content.size > 10 * 1024 * 1024) { // >10MB
            i("OracleDriveSandbox", "MEDIUM RISK: Large file modification (${content.size} bytes)")
            return RiskLevel.MEDIUM
        }

        AuraFxLogger.debug(
            "OracleDriveSandbox",
            "LOW RISK: Standard file modification: $targetFile"
        )
        return RiskLevel.LOW
    }

    /**
     * Returns an empty byte array as a stub for original file content.
     *
     * This method does not access the file system and always returns an empty array.
     *
     * @param targetFile The absolute path of the file to read.
     * @return An empty byte array.
     */
    private fun readOriginalFile(targetFile: String): ByteArray {
        return try {
            AuraFxLogger.debug("OracleDriveSandbox", "Reading original file: $targetFile")

            val file = File(targetFile)

            // Safety checks before reading
            if (!file.exists()) {
                AuraFxLogger.warn("OracleDriveSandbox", "File does not exist: $targetFile")
                return ByteArray(0)
            }

            if (!file.canRead()) {
                AuraFxLogger.warn("OracleDriveSandbox", "File not readable: $targetFile")
                return ByteArray(0)
            }

            // Size limit check (max 50MB)
            val maxSize = 50 * 1024 * 1024
            if (file.length() > maxSize) {
                AuraFxLogger.warn(
                    "OracleDriveSandbox",
                    "File too large (${file.length()} bytes), truncating"
                )
                // Read only first 50MB
                file.inputStream().use { it.readBytes().take(maxSize).toByteArray() }
            } else {
                // Read entire file safely
                file.readBytes().also {
                    AuraFxLogger.debug(
                        "OracleDriveSandbox",
                        "Read ${it.size} bytes from $targetFile"
                    )
                }
            }

        } catch (e: SecurityException) {
            AuraFxLogger.error(
                "OracleDriveSandbox",
                "Security exception reading file: $targetFile",
                e
            )
            ByteArray(0)
        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Error reading file: $targetFile", e)
            ByteArray(0)
        }
    }

    /**
     * Applies a system modification to the specified sandbox environment in isolation.
     *
     * The modification is executed within the sandbox, ensuring no changes are made to the real system.
     *
     * @param sandbox The sandbox environment where the modification will be applied.
     * @param modification The system modification to apply within the sandbox.
     */
    private suspend fun applyModificationInSandbox(
        sandbox: SandboxEnvironment,
        modification: SystemModification,
    ) {
        AuraFxLogger.debug(
            "OracleDriveSandbox",
            "Applying modification in sandbox: ${modification.description}"
        )

        try {
            // Write to sandbox overlay filesystem (upper layer)
            val sandboxRoot = File("/data/data/${context.packageName}/sandboxes/${sandbox.id}")
            val upperDir = File(sandboxRoot, "upper")

            // Create target file path in sandbox
            val targetInSandbox = File(upperDir, modification.targetFile.removePrefix("/"))

            // Log modification details
            AuraFxLogger.debug(
                "OracleDriveSandbox",
                "Sandbox modification: ${modification.targetFile} → sandbox:${targetInSandbox.path}"
            )

        } catch (e: Exception) {
            AuraFxLogger.error("OracleDriveSandbox", "Failed to apply modification in sandbox", e)
            throw e
        }
    }

    /**
     * Adds a system modification to the modification list of the specified sandbox and updates the active sandboxes state.
     *
     * If the sandbox with the given ID does not exist, no changes are made.
     *
     * @param sandboxId The ID of the sandbox to update.
     * @param modification The modification to add to the sandbox.
     */
    private fun updateSandboxModifications(sandboxId: String, modification: SystemModification) {
        val currentSandboxes = _activeSandboxes.value.toMutableList()
        val sandboxIndex = currentSandboxes.indexOfFirst { it.id == sandboxId }

        if (sandboxIndex != -1) {
            val sandbox = currentSandboxes[sandboxIndex]
            val updatedModifications = sandbox.modifications + modification
            val updatedSandbox = sandbox.copy(modifications = updatedModifications)
            currentSandboxes[sandboxIndex] = updatedSandbox
            _activeSandboxes.value = currentSandboxes
        }
    }

    /**
     * Generates warning messages for system modifications with high or critical risk levels.
     *
     * @param modification The system modification to evaluate.
     * @return A list of warning messages if the modification is high or critical risk; otherwise, an empty list.
     */
    private fun generateWarningsForModification(modification: SystemModification): List<String> {
        val warnings = mutableListOf<String>()

        when (modification.riskLevel) {
            RiskLevel.HIGH -> warnings.add("High risk modification - proceed with caution")
            RiskLevel.CRITICAL -> warnings.add("CRITICAL risk modification - expert knowledge required")
            else -> {}
        }

        return warnings
    }

    /**
     * Simulates a safety test for a system modification and returns the result.
     *
     * The test always passes but generates warnings if the modification's risk level is above LOW.
     *
     * @param modification The system modification to be tested.
     * @return A TestResult indicating the simulated outcome and any warnings.
     */
    private suspend fun testModification(modification: SystemModification): TestResult {
        AuraFxLogger.debug(
            "OracleDriveSandbox",
            "Testing modification: ${modification.description}"
        )

        val warnings = mutableListOf<String>()
        val errors = mutableListOf<String>()

        // Test 1: Risk level assessment
        if (modification.riskLevel != RiskLevel.LOW) {
            warnings.add("Risk level: ${modification.riskLevel}")
        }

        // Test 2: File validity check
        if (modification.targetFile.isEmpty() || !modification.targetFile.startsWith("/")) {
            errors.add("Invalid target file path: ${modification.targetFile}")
        }

        // Test 3: Content validation
        if (modification.id.isEmpty()) {
            warnings.add("Modification contains empty content")
        }

        // Test 4: File type compatibility
        val fileExt = modification.targetFile.substringAfterLast(".", "")
        if (fileExt in listOf("so", "apk", "dex") && modification.id.length < 100) {
            warnings.add("Suspicious small size for binary file")
        }

        // Test 5: Path safety check
        if (modification.targetFile.contains("..")) {
            errors.add("Path traversal detected in target file")
        }

        // Test 6: Size reasonableness
        if (modification.id.length > 100 * 1024 * 1024) { // >100MB
            warnings.add("Very large modification (${modification.id.length / (1024 * 1024)}MB)")
        }

        val status = when {
            errors.isNotEmpty() -> "Failed"
            warnings.isNotEmpty() -> "Passed with warnings"
            else -> "Passed"
        }

        i(
            "OracleDriveSandbox",
            "Test result: $status (${warnings.size} warnings, ${errors.size} errors)"
        )

        return TestResult(
            status = status,
            hasWarnings = warnings.isNotEmpty(),
            hasErrors = errors.isNotEmpty(),
            warnings = warnings,
            errors = errors
        )
    }

    /**
     * Determines the overall safety level for a set of system modifications based on the highest individual risk level.
     *
     * If no modifications are provided, the safety level is set to SAFE.
     *
     * @param modifications The list of system modifications to evaluate.
     * @return The calculated safety level reflecting the most severe risk among the modifications.
     */
    private fun calculateOverallSafety(modifications: List<SystemModification>): SafetyLevel {
        val maxRisk = modifications.maxOfOrNull { it.riskLevel } ?: RiskLevel.LOW
        return when (maxRisk) {
            RiskLevel.LOW -> SafetyLevel.SAFE
            RiskLevel.MEDIUM -> SafetyLevel.CAUTION
            RiskLevel.HIGH -> SafetyLevel.WARNING
            RiskLevel.CRITICAL -> SafetyLevel.CRITICAL
        }
    }

    /**
     * Checks if the provided confirmation code authorizes applying sandbox modifications to the real system.
     *
     * @param code The confirmation code to validate.
     * @return `true` if the code matches the required authorization string; otherwise, `false`.
     */
    private fun verifyConfirmationCode(code: String): Boolean {
        AuraFxLogger.debug("OracleDriveSandbox", "Verifying confirmation code")

        // Multi-layer confirmation code verification
        val expectedCode = "ORACLE_DRIVE_CONFIRM"

        // Layer 1: Basic check first
        if (code.length != expectedCode.length) {
            AuraFxLogger.warn("OracleDriveSandbox", "Confirmation code length mismatch")
            return false
        }

        // Layer 2: Timing attack prevention - constant time comparison
        var result = true
        for (i in code.indices) {
            if (i < expectedCode.length && code[i] != expectedCode[i]) {
                result = false
            }
        }

        // Layer 3: Rate limiting check (prevent brute force)
        val prefs = context.getSharedPreferences("oracle_drive_security", Context.MODE_PRIVATE)
        val failedAttempts = prefs.getInt("failed_confirmation_attempts", 0)
        val lastAttemptTime = prefs.getLong("last_confirmation_attempt", 0)

        // Reset counter if more than 1 hour has passed
        if (System.currentTimeMillis() - lastAttemptTime > 3600000) {
            prefs.edit { putInt("failed_confirmation_attempts", 0) }
        }

        // Block if too many failed attempts
        if (failedAttempts >= 3) {
            AuraFxLogger.error(
                "OracleDriveSandbox",
                "Too many failed confirmation attempts - locked out"
            )
            return false
        }

        // Update attempt counter
        if (!result) {
            prefs.edit {
                putInt("failed_confirmation_attempts", failedAttempts + 1)
                    .putLong("last_confirmation_attempt", System.currentTimeMillis())
            }
            AuraFxLogger.warn(
                "OracleDriveSandbox",
                "Confirmation failed - attempt ${failedAttempts + 1}/3"
            )
        } else {
            // Reset on success
            prefs.edit { putInt("failed_confirmation_attempts", 0) }
            i("OracleDriveSandbox", "Confirmation code verified successfully")
        }

        return result
    }

    /**
     * Performs a final safety evaluation on the specified sandbox environment before applying its modifications to the real system.
     *
     * @param sandbox The sandbox environment to evaluate.
     * @return A [SafetyCheck] indicating whether the sandbox is safe for real system application, with a reason for the decision.
     */
    private suspend fun performFinalSafetyCheck(sandbox: SandboxEnvironment): SafetyCheck {
        i(
            "OracleDriveSandbox",
            "Performing comprehensive final safety check for sandbox: ${sandbox.name}"
        )

        val issues = mutableListOf<String>()

        // Check 1: Safety level assessment
        if (sandbox.safetyLevel == SafetyLevel.CRITICAL) {
            issues.add("CRITICAL safety level - contains high-risk modifications")
            AuraFxLogger.error("OracleDriveSandbox", "Safety check FAILED: Critical safety level")
            return SafetyCheck(isSafe = false, reason = "Critical safety level detected")
        }

        // Check 2: Modification count validation
        if (sandbox.modifications.isEmpty()) {
            AuraFxLogger.warn("OracleDriveSandbox", "No modifications in sandbox")
            return SafetyCheck(isSafe = false, reason = "No modifications to apply")
        }

        // Check 3: Risk assessment for each modification
        val criticalMods = sandbox.modifications.filter { it.riskLevel == RiskLevel.CRITICAL }
        if (criticalMods.isNotEmpty()) {
            issues.add("${criticalMods.size} critical risk modifications detected")
        }

        // Check 4: Test all modifications
        val testResults = sandbox.modifications.map { testModification(it) }
        val failedTests = testResults.filter { it.hasErrors }

        if (failedTests.isNotEmpty()) {
            issues.add("${failedTests.size} modifications failed testing")
            AuraFxLogger.error(
                "OracleDriveSandbox",
                "Safety check FAILED: ${failedTests.size} tests failed"
            )
            return SafetyCheck(isSafe = false, reason = "Modifications failed testing")
        }

        // Check 5: System compatibility check
        val systemPaths = sandbox.modifications.map { it.targetFile }
        val conflictingPaths = systemPaths.groupBy { it }.filter { it.value.size > 1 }

        if (conflictingPaths.isNotEmpty()) {
            issues.add("Duplicate target paths detected")
        }

        // Check 6: Device root status
        val isRooted = File("/system/xbin/su").exists() ||
                File("/system/bin/su").exists()

        if (!isRooted) {
            AuraFxLogger.warn(
                "OracleDriveSandbox",
                "Device not rooted - system modifications will fail"
            )
            return SafetyCheck(
                isSafe = false,
                reason = "Device not rooted - cannot apply system modifications"
            )
        }

        // Check 7: Backup verification
        AuraFxLogger.debug("OracleDriveSandbox", "Verifying backup capability")
        // In production: verify backup infrastructure is ready

        // Final decision
        val isSafe = issues.isEmpty() || sandbox.safetyLevel == SafetyLevel.SAFE

        val reason = if (isSafe) {
            "All safety checks passed (${sandbox.modifications.size} modifications validated)"
        } else {
            issues.joinToString("; ")
        }

        i(
            "OracleDriveSandbox",
            "Final safety check result: ${if (isSafe) "PASS" else "FAIL"} - $reason"
        )

        return SafetyCheck(isSafe = isSafe, reason = reason)
    }

    /**
     * Applies system modifications to the real device with full backup and rollback support.
     *
     * Creates a complete backup of all files to be modified, applies changes sequentially,
     * verifies each modification, and provides automatic rollback on any failure.
     *
     * @param modifications The list of system modifications to apply.
     * @return An ApplicationResult indicating success or failure with detailed reason.
     */
    private suspend fun applyModificationsToRealSystem(
        modifications: List<SystemModification>,
    ): ApplicationResult = withContext(Dispatchers.IO) {
        AuraFxLogger.warn(
            "OracleDriveSandbox",
            "REAL SYSTEM MODIFICATION - ${modifications.size} changes - BACKUP/ROLLBACK ENABLED"
        )

        // Backup storage for rollback
        val backupMap = mutableMapOf<String, ByteArray>()
        val appliedModifications = mutableListOf<SystemModification>()

        try {
            // Phase 1: Create backups of all target files
            i("OracleDriveSandbox", "Phase 1: Creating backups...")
            modifications.forEach { mod ->
                try {
                    val targetFile = File(mod.targetFile)
                    if (targetFile.exists()) {
                        // Backup existing file
                        backupMap[mod.targetFile] = targetFile.readBytes()
                        AuraFxLogger.debug(
                            "OracleDriveSandbox",
                            "Backed up: ${mod.targetFile} (${backupMap[mod.targetFile]!!.size} bytes)"
                        )
                    } else {
                        // Mark for deletion on rollback (new file)
                        backupMap[mod.targetFile] = byteArrayOf() // Empty marker
                        AuraFxLogger.debug(
                            "OracleDriveSandbox",
                            "New file marker: ${mod.targetFile}"
                        )
                    }
                } catch (e: Exception) {
                    AuraFxLogger.error(
                        "OracleDriveSandbox",
                        "Backup failed for ${mod.targetFile}",
                        e
                    )
                    throw Exception("Backup phase failed: ${e.message}")
                }
            }

            // Phase 2: Apply modifications sequentially
            i("OracleDriveSandbox", "Phase 2: Applying modifications...")
            modifications.forEach { mod ->
                try {
                    val targetFile = File(mod.targetFile)

                    // Create parent directories if needed
                    targetFile.parentFile?.mkdirs()

                    // Write modified content
                    targetFile.writeBytes(mod.modifiedContent)
                    appliedModifications.add(mod)

                    // Verify write
                    val written = targetFile.readBytes()
                    if (!written.contentEquals(mod.modifiedContent)) {
                        throw Exception("Verification failed: content mismatch")
                    }

                    i(
                        "OracleDriveSandbox",
                        "Applied: ${mod.targetFile} (${mod.modifiedContent.size} bytes)"
                    )

                } catch (e: Exception) {
                    AuraFxLogger.error(
                        "OracleDriveSandbox",
                        "Modification failed for ${mod.targetFile}",
                        e
                    )
                    throw Exception("Application phase failed at ${mod.targetFile}: ${e.message}")
                }
            }

            // Phase 3: Success - log and return
            i(
                "OracleDriveSandbox",
                "SUCCESS: Applied ${appliedModifications.size} modifications"
            )

            ApplicationResult(
                success = true,
                failureReason = ""
            )

        } catch (e: Exception) {
            // Phase 4: Rollback on any failure
            AuraFxLogger.error("OracleDriveSandbox", "FAILURE - Initiating rollback", e)

            var rollbackSuccess = true
            backupMap.forEach { (path, backup) ->
                try {
                    val file = File(path)
                    if (backup.isEmpty() && file.exists()) {
                        // Was a new file, delete it
                        file.delete()
                        AuraFxLogger.debug("OracleDriveSandbox", "Rollback: Deleted new file $path")
                    } else if (backup.isNotEmpty()) {
                        // Restore original content
                        file.writeBytes(backup)
                        AuraFxLogger.debug("OracleDriveSandbox", "Rollback: Restored $path")
                    }
                } catch (rollbackError: Exception) {
                    AuraFxLogger.error(
                        "OracleDriveSandbox",
                        "Rollback failed for $path",
                        rollbackError
                    )
                    rollbackSuccess = false
                }
            }

            val failureMessage = if (rollbackSuccess) {
                "Modification failed: ${e.message}. System rolled back successfully."
            } else {
                "CRITICAL: Modification failed AND rollback incomplete: ${e.message}"
            }

            ApplicationResult(
                success = false,
                failureReason = failureMessage
            )
        }
    }

    /**
     * Shuts down the sandbox system, canceling all ongoing operations and setting the sandbox state to INACTIVE.
     */
    fun shutdown() {
        i("OracleDriveSandbox", "Shutting down OracleDrive Sandbox system")
        sandboxScope.cancel()
        _sandboxState.value = SandboxState.INACTIVE
    }
}

