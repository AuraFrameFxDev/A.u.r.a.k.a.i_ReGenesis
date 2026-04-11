package dev.aurakai.auraframefx.domains.kai

import android.content.Context
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RootShellService - The LDO's "Hands"
 *
 * Provides a unified interface for executing shell commands with elevated privileges
 * using libsu (Root) or Shizuku (ADB).
 */
@Singleton
class RootShellService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tag = "RootShellService"

    private val _shellStatus = MutableStateFlow<ShellStatus>(ShellStatus.Checking)
    val shellStatus: StateFlow<ShellStatus> = _shellStatus

    init {
        // Configure libsu
        Shell.setDefaultBuilder(Shell.Builder.create()
            .setTimeout(10)
        )
        refreshStatus()
    }

    /**
     * Refreshes the current shell status (Root vs Shizuku vs User)
     */
    fun refreshStatus() {
        val isRoot = Shell.getShell().isRoot
        val isShizukuAvailable = ShizukuManager.isShizukuAvailable()
        
        _shellStatus.value = when {
            isRoot -> ShellStatus.RootAccess
            isShizukuAvailable -> ShellStatus.ShizukuAccess
            else -> ShellStatus.UserAccess
        }
        
        Timber.i("RootShellService: Status updated to ${_shellStatus.value}")
    }

    /**
     * Executes a command with the best available privileges.
     */
    suspend fun executeCommand(command: String): ShellResult = withContext(Dispatchers.IO) {
        Timber.d("RootShellService: Executing '$command'")
        
        return@withContext try {
            val result = Shell.cmd(command).exec()
            ShellResult(
                isSuccess = result.isSuccess,
                output = result.out.joinToString("\n"),
                error = result.err.joinToString("\n"),
                code = result.code
            )
        } catch (e: Exception) {
            Timber.e(e, "RootShellService: Command failed")
            ShellResult(
                isSuccess = false,
                output = "",
                error = e.message ?: "Unknown error",
                code = -1
            )
        }
    }

    /**
     * Specifically requests root access.
     * @return True if root access is granted.
     */
    suspend fun requestRoot(): Boolean = withContext(Dispatchers.IO) {
        val isRoot = Shell.getShell().isRoot
        if (isRoot) return@withContext true
        
        // This will trigger the SU request UI if not already granted
        refreshStatus()
        return@withContext _shellStatus.value == ShellStatus.RootAccess
    }

    sealed class ShellStatus {
        object Checking : ShellStatus()
        object RootAccess : ShellStatus()
        object ShizukuAccess : ShellStatus()
        object UserAccess : ShellStatus()
    }

    data class ShellResult(
        val isSuccess: Boolean,
        val output: String,
        val error: String,
        val code: Int
    )
}
