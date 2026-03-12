package dev.aurakai.auraframefx.romtools.bootloader

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class BootloaderManagerImplTest {
    @Test
    fun defaults_returnFalseOrFailure() = runBlocking {
        val mgr = BootloaderManagerImpl()
        assertFalse(mgr.checkBootloaderAccess(), "checkBootloaderAccess should default to false")
        assertFalse(mgr.isBootloaderUnlocked(), "isBootloaderUnlocked should default to false")
        val result = mgr.unlockBootloader()
        assertTrue(result.isFailure, "unlockBootloader should return failure by default")
    }
}
