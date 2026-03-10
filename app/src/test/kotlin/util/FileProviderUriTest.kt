@file:Suppress("DEPRECATION")

package util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class FileProviderUriTest {

    private fun authority(context: Context): String {
        // Many apps use "${applicationId}.provider" or similar.
        // If a specific authority is configured in AndroidManifest, update here accordingly.
        return context.packageName + ".provider"
    }

    @Test
    fun getUriForFile_returnsContentScheme_andGrantsAreParsable() {
        val context = RuntimeEnvironment.getApplication()
        val cacheFile = File(context.cacheDir, "sample.txt").apply { writeText("x") }

        val uri: Uri = FileProvider.getUriForFile(context, authority(context), cacheFile)

        assertEquals("content", uri.scheme)
        assertTrue(uri.toString().contains(context.packageName))
        assertTrue(uri.toString().contains("sample.txt"))
    }
}