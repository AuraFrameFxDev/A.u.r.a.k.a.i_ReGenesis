package dev.aurakai.auraframefx.domains.aura

import dev.aurakai.auraframefx.core.models.OverlayAnimation
import dev.aurakai.auraframefx.core.models.OverlayTransition
import dev.aurakai.auraframefx.domains.aura.models.OverlayElement
import dev.aurakai.auraframefx.domains.aura.models.OverlayShape
import dev.aurakai.auraframefx.core.models.OverlayTheme
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootOverlayManager @Inject constructor() : SystemOverlayManager {

    override fun applyTheme(theme: OverlayTheme) {
        Timber.d("Applying system-wide theme: ${theme.name}")
    }

    override fun applyElement(element: OverlayElement) {
        Timber.d("Customizing system element: ${element.id}")
    }

    override fun applyAnimation(animation: OverlayAnimation) {
        Timber.d("Applying system animation: ${animation.id}")
    }

    override fun applyTransition(transition: OverlayTransition) {
        Timber.d("Applying system transition: ${transition.id}")
    }

    override fun applyShape(shape: OverlayShape) {
        Timber.d("Applying system shape: ${shape.id}")
        val command = "cmd overlay fabricate --target android --name aura_shape --res dimen/config_buttonCornerRadius --type 0x05 --value ${shape.cornerRadius.toInt()} && cmd overlay enable com.android.shell:aura_shape"
        runShellCommand(command)
    }

    override fun applyConfig(config: SystemOverlayConfig) {
        Timber.d("Applying system config")
    }

    override fun removeElement(elementId: String) {
        Timber.d("Removing system overlay: $elementId")
    }

    override fun clearAll() {
        Timber.w("Clearing ALL ReGenesis system overlays")
        runShellCommand("cmd overlay fabricate --clear")
    }

    override fun applyAccent(hex: String): Result<String> {
        Timber.i("🎨 Fabricating system accent: $hex")
        val cleanHex = hex.replace("#", "").let {
            if (it.length == 6) "FF$it" else it
        }
        val command = "cmd overlay fabricate --target android --name aura_accent --res color/accent_device_default --type 0x1c --value 0x$cleanHex && cmd overlay enable com.android.shell:aura_accent"
        return runShellCommand(command)
    }

    override fun applyBackgroundSaturation(percent: Int): Result<String> {
        Timber.i("🌈 Setting background saturation to $percent%")
        return runShellCommand("settings put system system_background_saturation $percent")
    }

    private fun runShellCommand(command: String): Result<String> {
        return try {
            Timber.v("Executing: $command")
            val process = ProcessBuilder("su", "-c", command).start()
            val output = StringBuilder()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Result.success(output.toString())
            } else {
                val errorOutput = process.errorStream.bufferedReader().readText()
                Result.failure(Exception("Shell Error ($exitCode): $errorOutput"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
