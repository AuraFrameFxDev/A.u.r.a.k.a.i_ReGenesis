package dev.aurakai.auraframefx.domains.aura.services.iconify

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production IconifyService.
 */
@Singleton
class SystemIconifyService @Inject constructor(
    @ApplicationContext private val context: Context
) : IconifyService {

    private var currentPackId: String? = null

    override fun applyIconPack(packId: String) {
        Timber.i("IconifyService: Applying pack=$packId")
        currentPackId = packId
    }

    override fun resetToDefault() {
        Timber.i("IconifyService: Resetting to default icons")
        currentPackId = null
    }

    override fun getCurrentPackId(): String? = currentPackId

    override fun getAvailablePacks(): List<IconifyService.IconPack> {
        val pm = context.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo ->
                runCatching {
                    val pkgInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_ACTIVITIES)
                    pkgInfo.activities?.any { it.name.contains("IconPack", ignoreCase = true) } == true
                }.getOrDefault(false)
            }
            .map { appInfo ->
                IconifyService.IconPack(
                    id = appInfo.packageName,
                    name = pm.getApplicationLabel(appInfo).toString(),
                    packageName = appInfo.packageName,
                    iconCount = -1
                )
            }
    }
}
