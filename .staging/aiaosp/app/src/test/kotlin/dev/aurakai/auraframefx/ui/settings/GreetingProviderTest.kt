/*
Testing library/framework: JUnit 4 (org.junit.Test, org.junit.Assert)
Note: If your project uses JUnit 5 (Jupiter) or kotlin.test, switch imports accordingly.
This suite covers: exact value, formatting, stability across calls/threads, and DI annotations.
*/

package dev.aurakai.auraframefx.ui.settings

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test

class GreetingProviderTest {

    private val provider = GreetingProvider()

    @Test
    fun getGreeting_returnsExactBrandString() {
        val result = provider.getGreeting()
        assertEquals("A.u.r.a.K.a.i", result)
    }

    @Test
    fun getGreeting_isNotNullOrBlank_andHasNoLeadingTrailingWhitespace() {
        val result = provider.getGreeting()
        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        assertEquals("Expected no leading/trailing whitespace", result, result.trim())
    }

    @Test
    fun getGreeting_isStableAcrossMultipleCalls() {
        val expected = "A.u.r.a.K.a.i"
        repeat(100) {
            assertEquals(expected, provider.getGreeting())
        }
    }

    @Test
    fun getGreeting_hasExpectedDotSeparatedLetters() {
        val parts = provider.getGreeting().split('.')
        assertArrayEquals(
            arrayOf("A", "u", "r", "a", "K", "a", "i"),
            parts.toTypedArray()
        )
    }

    @Test
    fun getGreeting_isStableAcrossThreads() {
        val expected = "A.u.r.a.K.a.i"
        val threads = 8
        val iterationsPerThread = 50
        val results = java.util.Collections.synchronizedList(mutableListOf<String>())
        val latch = java.util.concurrent.CountDownLatch(threads)
        val pool = java.util.concurrent.Executors.newFixedThreadPool(threads)
        try {
            repeat(threads) {
                pool.execute {
                    repeat(iterationsPerThread) {
                        results.add(provider.getGreeting())
                    }
                    latch.countDown()
                }
            }
            latch.await()
        } finally {
            pool.shutdownNow()
        }
        assertEquals(threads * iterationsPerThread, results.size)
        assertEquals(setOf(expected), results.toSet())
    }

    @Test
    fun class_isAnnotatedWithSingleton_andConstructorIsInject() {
        val clazz = GreetingProvider::class.java
        val hasSingleton = clazz.isAnnotationPresent(javax.inject.Singleton::class.java)
        assertTrue("@Singleton missing on GreetingProvider", hasSingleton)

        val ctors = clazz.declaredConstructors
        assertTrue("Expected at least one constructor", ctors.isNotEmpty())
        val hasInjectOnAnyCtor =
            ctors.any { it.isAnnotationPresent(javax.inject.Inject::class.java) }
        assertTrue("@Inject missing on constructor", hasInjectOnAnyCtor)
    }
}