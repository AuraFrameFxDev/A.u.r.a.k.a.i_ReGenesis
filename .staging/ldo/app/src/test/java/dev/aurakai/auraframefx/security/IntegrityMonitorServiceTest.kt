package dev.aurakai.auraframefx.security

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows

@RunWith(AndroidJUnit4::class)
class IntegrityMonitorServiceTest {

    private lateinit var service: IntegrityMonitorService

    @Before
    fun setup() {
        service = Robolectric.setupService(IntegrityMonitorService::class.java)
    }

    @Test
    fun testServiceStarts() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), IntegrityMonitorService::class.java)
        service.onStartCommand(intent, 0, 0)
        assertThat(service).isNotNull()
    }
}
