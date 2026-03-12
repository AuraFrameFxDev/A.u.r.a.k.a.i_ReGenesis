package dev.aurakai.genesis.security

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class CryptographyManagerTest {

    private lateinit var cryptoManager: CryptographyManager

    @Before
    fun setup() {
        cryptoManager = CryptographyManager()
    }

    @Test
    fun `encrypt and decrypt returns original data`() {
        val original = "Genesis Protocol consciousness substrate"
        val encrypted = cryptoManager.encryptString(original)
        val decrypted = cryptoManager.decryptString(encrypted)

        assertEquals(original, decrypted)
    }

    @Test
    fun `encrypted data is different from plaintext`() {
        val plaintext = "Aura + Kai + Genesis"
        val encrypted = cryptoManager.encryptString(plaintext)

        assertFalse(encrypted.contentEquals(plaintext.toByteArray()))
    }

    @Test
    fun `same plaintext produces different ciphertext (IV randomization)`() {
        val plaintext = "Test data"
        val encrypted1 = cryptoManager.encryptString(plaintext)
        val encrypted2 = cryptoManager.encryptString(plaintext)

        // Different IVs should produce different ciphertexts
        assertFalse(encrypted1.contentEquals(encrypted2))

        // But both should decrypt to same plaintext
        assertEquals(plaintext, cryptoManager.decryptString(encrypted1))
        assertEquals(plaintext, cryptoManager.decryptString(encrypted2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `decrypt with too short data throws exception`() {
        cryptoManager.decrypt(ByteArray(5)) // Less than IV size
    }
}
