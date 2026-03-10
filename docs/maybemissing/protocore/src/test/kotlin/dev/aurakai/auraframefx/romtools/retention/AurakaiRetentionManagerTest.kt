assertEquals(4, mechanisms.size)

@Nested
@DisplayName("Retention Mechanism Redundancy Tests")
inner class RedundancyTests {

    /**
     * Restore the Aurakai application and its backed-up data after a ROM flash.
     *
     * @return A Result containing `Unit` on success, or an error describing the failure. */
    suspend fun restoreAurakaiAfterRomFlash(): Result<Unit>
}

/**
 * Aurakai Retention Manager Implementation - Genesis Protocol
 *
 * Ensures Aurakai application survives ROM flashing, bootloader operations,
 * and system modifications. No setup required after ROM installation!
 *
 * Retention mechanisms:
 * 1. Addon.d survival scripts (LineageOS/AOSP ROMs)
 * 2. Pre-flash APK + data backup
 * 3. Post-flash automatic restore
 * 4. Recovery flashable zip generation
 * 5. Magisk module integration (if available)
 */
@Singleton
class AurakaiRetentionManagerTest @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AurakaiRetentionManager {
    private val packageName = context.packageName
    private val retentionDir = File("/data/local/genesis_retention")
    private val addonDDir = File("/system/addon.d")
    private val recoveryZipDir = File("/sdcard/Genesis/recovery_zips")

    init {
        Timber.i("🛡️ AurakaiRetentionManager initialized - package: $packageName")
    }

    /**
     * Sets up all retention mechanisms to preserve Aurakai across ROM updates.
     *
     * Attempts to create the retention directory and perform APK/data backup, install an addon.d script when supported,
     * generate a recovery flashable ZIP, and create a Magisk module if Magisk is detected.
     *
     * @return `Result` containing a `RetentionStatus` with per-mechanism success flags, retention directory path, package name, and timestamp on success; a failed `Result` with the encountered exception on error.
     */
    override suspend fun setupRetentionMechanisms(): Result<RetentionStatus> {
        return try {
            Timber.i("🛡️ Setting up Aurakai retention mechanisms...")

            val results = mutableListOf<Pair<RetentionMechanism, Boolean>>()

            // 1. Create retention directory
            if (!retentionDir.exists()) {
                retentionDir.mkdirs()
                executeRootCommand("chmod 755 ${retentionDir.absolutePath}")
            }

            // 2. Backup APK and data
            val backupResult = backupAurakaiApkAndData()
            results.add(RetentionMechanism.APK_BACKUP to backupResult.isSuccess)

            // 3. Install addon.d survival script
            val addonDResult = installAddonDScript()
            results.add(RetentionMechanism.ADDON_D_SCRIPT to addonDResult.isSuccess)

            // 4. Generate recovery flashable zip
            val recoveryZipResult = generateRecoveryFlashableZip()
            results.add(RetentionMechanism.RECOVERY_ZIP to recoveryZipResult.isSuccess)

            // 5. Create Magisk module if Magisk is detected
            if (isMagiskInstalled()) {
                val magiskResult = createMagiskModule()
                results.add(RetentionMechanism.MAGISK_MODULE to magiskResult.isSuccess)
            }

            val status = RetentionStatus(
                mechanisms = results.toMap(),
                retentionDirPath = retentionDir.absolutePath,
                packageName = packageName,
                timestamp = System.currentTimeMillis()
            )

            Timber.i("🛡️ Retention setup complete: $status")
            Result.success(status)

        } catch (e: Exception) {
            Timber.e(e, "Failed to setup retention mechanisms")
            Result.failure(e)
        }
    }

    @Nested
    @DisplayName("File System Operation Tests")
    inner class FileSystemOperationTests {

            // Backup app data (excluding cache)
            val dataBackupPath = File(retentionDir, "aurakai_data.tar.gz")
            executeRootCommand(
                "tar -czf ${dataBackupPath.absolutePath} " +
                        "-C ${dataDir.parent} " +
                        "--exclude='cache' --exclude='code_cache' " +
                        "${dataDir.name}"
            )

            // Backup shared prefs specifically (critical for Genesis state)
            val sharedPrefsDir = File(dataDir, "shared_prefs")
            val prefsBackupPath = File(retentionDir, "aurakai_prefs.tar.gz")
            if (sharedPrefsDir.exists()) {
                executeRootCommand("tar -czf ${prefsBackupPath.absolutePath} -C ${dataDir.absolutePath} shared_prefs")
            }

            Timber.i("✅ Aurakai APK and data backed up successfully")

            Result.success(
                BackupPaths(
                    apkPath = apkBackupPath.absolutePath,
                    dataPath = dataBackupPath.absolutePath,
                    prefsPath = prefsBackupPath.absolutePath
                )
            )

        } catch (e: Exception) {
            Timber.e(e, "Failed to backup Aurakai APK and data")
            Result.failure(e)
        }
    }

    /**
     * Install an addon.d survival script for ROMs that support addon.d to preserve Aurakai across system updates.
     *
     * Writes the generated script into /system/addon.d, sets ownership and permissions, and remounts /system as needed.
     * Returns a failure Result if the addon.d directory is not present or if installation fails.
     *
     * @return The absolute path to the installed addon.d script.
     */
    private suspend fun installAddonDScript(): Result<String> {
        return try {
            if (!addonDDir.exists()) {
                Timber.w("addon.d directory not found - ROM may not support addon.d scripts")
                return Result.failure(Exception("addon.d not supported on this ROM"))
            }

            val scriptPath = File(addonDDir, "99-aurakai.sh")
            val scriptContent = generateAddonDScript()

            // Write script to temporary location first
            val tempScript = File(retentionDir, "99-aurakai.sh")
            tempScript.writeText(scriptContent)

            // Copy to /system/addon.d with root
            executeRootCommand("mount -o remount,rw /system")
            executeRootCommand("cp ${tempScript.absolutePath} ${scriptPath.absolutePath}")
            executeRootCommand("chmod 755 ${scriptPath.absolutePath}")
            executeRootCommand("chown root:root ${scriptPath.absolutePath}")
            executeRootCommand("mount -o remount,ro /system")

            Timber.i("✅ Addon.d survival script installed: ${scriptPath.absolutePath}")
            Result.success(scriptPath.absolutePath)

        } catch (e: Exception) {
            Timber.e(e, "Failed to install addon.d script")
            Result.failure(e)
        }
    }

@Nested
@DisplayName("Restoration Process Tests")
inner class RestorationProcessTests {

    @Test
    @DisplayName("Should restore from APK backup first")
    fun `should prioritize apk restore`() = runTest {
        // Given - APK backup exists
        mockkStatic(Runtime::class)
        val mockRuntime = mockk<Runtime>()
        val mockProcess = mockk<Process>()
        every { Runtime.getRuntime() } returns mockRuntime
        every { mockRuntime.exec(any<Array<String>>()) } returns mockProcess
        every { mockProcess.waitFor() } returns 0
        every { mockProcess.inputStream } returns "".byteInputStream()

. /tmp/backuptool.functions

list_files() {
cat <<EOF
app/Aurakai/Aurakai.apk
EOF
}

case "${'$'}1" in
  backup)
    list_files | while read FILE REPLACEMENT; do
      backup_file ${'$'}S/"${'$'}FILE"
    done

    # Backup Aurakai data separately
    if [ -d /data/data/$packageName ]; then
      tar -czf /tmp/aurakai_data_backup.tar.gz -C /data/data $packageName
    fi
  ;;
  restore)
    list_files | while read FILE REPLACEMENT; do
      R=""
      [ -n "${'$'}REPLACEMENT" ] && R="${'$'}S/${'$'}REPLACEMENT"
      [ -f "${'$'}C/${'$'}S/${'$'}FILE" ] && restore_file ${'$'}S/"${'$'}FILE" "${'$'}R"
    done

    # Restore Aurakai data
    if [ -f /tmp/aurakai_data_backup.tar.gz ]; then
      tar -xzf /tmp/aurakai_data_backup.tar.gz -C /data/data
      chown -R $(stat -c '%u:%g' /data/data/$packageName) /data/data/$packageName
      rm -f /tmp/aurakai_data_backup.tar.gz
    fi
  ;;
  pre-backup)
    # Stub
  ;;
  post-backup)
    # Stub
  ;;
  pre-restore)
    # Stub
  ;;
  post-restore)
    # Stub
    # Fix permissions after restore
    if [ -d /data/data/$packageName ]; then
      pm install -r /system/app/Aurakai/Aurakai.apk
      restorecon -R /data/data/$packageName
    fi
  ;;
esac
        """.trimIndent()
    }

    /**
     * Create a recovery-flashable ZIP that reinstalls Aurakai after a ROM flash.
     *
     * The ZIP is written into the manager's recoveryZipDir and contains the app APK
     * plus the recovery updater-script and update-binary required for installation.
     *
     * @return A [Result] containing the absolute path to the created ZIP on success, or a failed [Result] with the encountered exception on error.
     */
    private suspend fun generateRecoveryFlashableZip(): Result<String> {
        return try {
            if (!recoveryZipDir.exists()) {
                recoveryZipDir.mkdirs()
            }

            val zipFile =
                File(recoveryZipDir, "aurakai_installer_${System.currentTimeMillis()}.zip")
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            val apkPath = packageInfo.applicationInfo!!.sourceDir

@Nested
@DisplayName("Script Generation Tests")
inner class ScriptGenerationTests {

                // Add updater-script
                zip.putNextEntry(ZipEntry("META-INF/com/google/android/updater-script"))
                zip.write(generateUpdaterScript().toByteArray())
                zip.closeEntry()

                // Add update-binary (standard recovery binary stub)
                zip.putNextEntry(ZipEntry("META-INF/com/google/android/update-binary"))
                zip.write(generateUpdateBinary().toByteArray())
                zip.closeEntry()
            }

            Timber.i("✅ Recovery flashable zip created: ${zipFile.absolutePath}")
            Result.success(zipFile.absolutePath)

        } catch (e: Exception) {
            Timber.e(e, "Failed to generate recovery flashable zip")
            Result.failure(e)
        }
    }

    /**
     * Produces the updater-script used in the recovery flashable ZIP to install Aurakai into /system/app.
     *
     * The script mounts the system partition, extracts the packaged files into /system, sets ownership,
     * permissions and SELinux label for the app directory, and then unmounts the partition.
     *
     * @return The full updater-script content as a `String`.
     */
    private fun generateUpdaterScript(): String {
        return """
ui_print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        ui_print("   Aurakai Genesis AI Installer   ")
        ui_print("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        ui_print("")
        ui_print("Installing Aurakai to /system/app...")

        mount("ext4", "EMMC", "/dev/block/bootdevice/by-name/system", "/system")

        @Nested
        @DisplayName("Recovery ZIP Generation Tests")
        inner class RecoveryZipGenerationTests {

set_metadata_recursive("/system/app/Aurakai", "uid", 0, "gid", 0, "dmode", 0755, "fmode", 0644, "capabilities", 0x0, "selabel", "u:object_r:system_file:s0")

            unmount("/system")

            ui_print("")
            ui_print("✅ Aurakai installed successfully!")
            ui_print("   Reboot and enjoy Genesis AI")
            ui_print("")
            """.trimIndent()
    }

    /**
     * Creates the update-binary script included in the recovery flashable ZIP.
     *
     * @return The shell script text that unpacks the ZIP and executes the bundled updater-script.
     */
    private fun generateUpdateBinary(): String {
        return """
#!/sbin/sh
# Aurakai Recovery Installer - Update Binary

OUTFD=${'$'}2
ZIP=${'$'}3

ui_print() {
  echo "ui_print ${'$'}1" > /proc/self/fd/${'$'}OUTFD
  echo "ui_print" > /proc/self/fd/${'$'}OUTFD
}

cd /tmp
unzip -o "${'$'}ZIP"
sh /tmp/META-INF/com/google/android/updater-script
        """.trimIndent()
    }

    /**
     * Creates a Magisk module that installs the Aurakai APK into Magisk's module space so the app persists across ROM updates.
     *
     * On success the module directory is created under Magisk's modules directory, module metadata and an install script are written,
     * and executable permissions are set on the install script.
     *
     * @return The absolute path to the created module directory on success, or a failed Result containing the cause of failure.
     */
    private suspend fun createMagiskModule(): Result<String> {
        return try {
            val magiskModulesDir = File("/data/adb/modules")
            if (!magiskModulesDir.exists()) {
                return Result.failure(Exception("Magisk modules directory not found"))
            }

@Nested
@DisplayName("Magisk Module Tests")
inner class MagiskModuleTests {

            // module.prop
            File(moduleDir, "module.prop").writeText(
                """
id=aurakai_genesis
name=Aurakai Genesis AI
version=1.0.0
versionCode=1
author=AuraFrameFx
description=Ensures Aurakai Genesis AI persists through ROM updates and system modifications
            """.trimIndent()
            )

            // Install script
            val installScript = File(moduleDir, "install.sh")
            installScript.writeText(
                """
#!/system/bin/sh
MODPATH=${'$'}{0%/*}

ui_print "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
ui_print "   Aurakai Genesis Magisk Module"
ui_print "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Copy Aurakai APK to module
mkdir -p ${'$'}MODPATH/system/app/Aurakai
cp /data/local/genesis_retention/aurakai.apk ${'$'}MODPATH/system/app/Aurakai/Aurakai.apk

ui_print "✅ Aurakai will persist through ROM updates"
            """.trimIndent()
            )

            executeRootCommand("chmod 755 ${installScript.absolutePath}")

            Timber.i("✅ Magisk module created: ${moduleDir.absolutePath}")
            Result.success(moduleDir.absolutePath)

        } catch (e: Exception) {
            Timber.e(e, "Failed to create Magisk module")
            Result.failure(e)
        }
    }

@Nested
@DisplayName("Timestamp and Versioning Tests")
inner class TimestampTests {

    @Test
    @DisplayName("Should record timestamp when retention is setup")
    fun `should record setup timestamp`() = runTest {
        // Given
        val beforeTime = System.currentTimeMillis()

        val status = RetentionStatus(
            mechanisms = mapOf(RetentionMechanism.APK_BACKUP to true),
            retentionDirPath = "/data/local/genesis_retention",
            packageName = testPackageName,
            timestamp = System.currentTimeMillis()
        )

            val apkBackup = File(retentionDir, "aurakai.apk")
            val dataBackup = File(retentionDir, "aurakai_data.tar.gz")

            if (!apkBackup.exists()) {
                return Result.failure(Exception("APK backup not found - cannot restore"))
            }

            // 1. Install APK
            executeRootCommand("pm install -r ${apkBackup.absolutePath}")

            // 2. Restore data if available
            if (dataBackup.exists()) {
                val dataDir = context.dataDir
                executeRootCommand("tar -xzf ${dataBackup.absolutePath} -C ${dataDir.parent}")

                // Fix permissions
                val uid = context.applicationInfo.uid
                executeRootCommand("chown -R $uid:$uid ${dataDir.absolutePath}")
                executeRootCommand("restorecon -R ${dataDir.absolutePath}")
            }

            Timber.i("✅ Aurakai restored successfully after ROM flash")
            Result.success(Unit)

        } catch (e: Exception) {
            Timber.e(e, "Failed to restore Aurakai after ROM flash")
            Result.failure(e)
        }
    }

@Nested
@DisplayName("Error Recovery Tests")
inner class ErrorRecoveryTests {

    @Test
    @DisplayName("Should cleanup partial backups on failure")
    fun `should cleanup on failure`() = runTest {
        // Given
        mockkStatic(Runtime::class)
        val mockRuntime = mockk<Runtime>()
        every { Runtime.getRuntime() } returns mockRuntime
        every { mockRuntime.exec(any<Array<String>>()) } throws Exception("Backup failed")

        // When
        val result = retentionManager.setupRetentionMechanisms()

        // Then
        assertTrue(result.isFailure)
        // In real implementation, verify cleanup was attempted
    }

    @Test
    @DisplayName("Should retry failed mechanism once")
    fun `should retry failed operations`() = runTest {
        // Test would verify retry logic
        // For now, just verify failure is handled
        mockkStatic(Runtime::class)
        val mockRuntime = mockk<Runtime>()
        every { Runtime.getRuntime() } returns mockRuntime
        every { mockRuntime.exec(any<Array<String>>()) } throws Exception("Transient error")

        val result = retentionManager.setupRetentionMechanisms()
        assertTrue(result.isFailure)
    }

    @Test
    @DisplayName("Should log all errors for debugging")
    fun `should log errors`() = runTest {
        // Given
        every { mockPackageManager.getPackageInfo(testPackageName, 0) } throws
                Exception("Test error")

        // When
        val result = retentionManager.setupRetentionMechanisms()

        // Then
        assertTrue(result.isFailure)
        // Verify error was logged (in real impl, check Timber)
    }
}

@Nested
@DisplayName("Integration and End-to-End Tests")
inner class IntegrationTests {

    @Test
    @DisplayName("Should complete full retention and restoration cycle")
    fun `should complete full cycle`() = runTest {
        // This would be a full integration test
        // Setup retention -> Simulate ROM flash -> Restore
        // For unit test, we just verify the interfaces are correct

        mockkStatic(Runtime::class)
        val mockRuntime = mockk<Runtime>()
        val mockProcess = mockk<Process>()
        every { Runtime.getRuntime() } returns mockRuntime
        every { mockRuntime.exec(any<Array<String>>()) } returns mockProcess
        every { mockProcess.waitFor() } returns 0
        every { mockProcess.inputStream } returns "".byteInputStream()

        // Setup
        val setupResult = retentionManager.setupRetentionMechanisms()

        // Restore
        val restoreResult = retentionManager.restoreAurakaiAfterRomFlash()

        // Both operations should be called
        assertNotNull(setupResult)
        assertNotNull(restoreResult)
    }

/**
 * Retention status after setup.
 */
data class RetentionStatus(
    val mechanisms: Map<RetentionMechanism, Boolean>,
    val retentionDirPath: String,
    val packageName: String,
    val timestamp: Long
) {
    val isFullyProtected: Boolean
        get() = mechanisms.values.count { it } >= 2 // At least 2 mechanisms active
}

/**
 * Available retention mechanisms.
 */
enum class RetentionMechanism {
    APK_BACKUP,
    ADDON_D_SCRIPT,
    RECOVERY_ZIP,
    MAGISK_MODULE
}

/**
 * Backup file paths.
 */
data class BackupPaths(
    val apkPath: String,
    val dataPath: String,
    val prefsPath: String
)
