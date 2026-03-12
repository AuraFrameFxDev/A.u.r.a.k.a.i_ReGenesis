package dev.aurakai.auraframefx.securecomm.protocol

import dev.aurakai.auraframefx.securecomm.crypto.CryptoManager
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents a secure communication channel between two parties.
 * Handles session key establishment and secure message exchange.
 */
@Singleton
class SecureChannel @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    private var sessionKey: SecretKey? = null
    private var localPrivateKey: PrivateKey? = null
    private var remotePublicKey: PublicKey? = null
    private var handshakeComplete = false

    /**
     * Initializes the secure channel with the local key pair.
     * @return The local public key to be sent to the remote party.
     */
    fun initialize(): ByteArray {
        val keyPair = cryptoManager.getOrCreateKeyPair()
        localPrivateKey = keyPair.private
        return keyPair.public.encoded
    }

    /**
     * Completes the handshake with the remote party's public key.
     * @param remotePublicKeyBytes The remote party's public key in encoded form.
     * @return True if the handshake was successful, false otherwise.
     */
    fun completeHandshake(remotePublicKeyBytes: ByteArray): Boolean {
        return try {
            val keyFactory = java.security.KeyFactory.getInstance("EC")
            val keySpec = java.security.spec.X509EncodedKeySpec(remotePublicKeyBytes)
            remotePublicKey = keyFactory.generatePublic(keySpec)

            val sharedSecret = cryptoManager.performKeyAgreement(
                localPrivateKey!!,
                remotePublicKey!!
            )

            sessionKey = cryptoManager.deriveSessionKey(sharedSecret)
            handshakeComplete = true
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Encrypts and signs a message for secure transmission.
     * @param message The message to encrypt.
     * @return An encrypted and signed message packet.
     * @throws IllegalStateException if the handshake is not complete.
     */
    fun encryptMessage(message: ByteArray): ByteArray {
        check(handshakeComplete) { "Handshake not complete" }

        // Encrypt the message
        val (ciphertext, iv) = cryptoManager.encrypt(message, sessionKey!!)

        // Create a message packet
        val packet = MessagePacket(
            ciphertext = ciphertext,
            iv = iv,
            signature = cryptoManager.sign(message)
        )

        // Serialize the packet
        return packet.toByteArray()
    }

    /**
     * Decrypts and verifies a received message.
     * @param encryptedData The encrypted message packet.
     * @return The decrypted message if verification succeeds, null otherwise.
     */
    fun decryptMessage(encryptedData: ByteArray): ByteArray? {
        check(handshakeComplete) { "Handshake not complete" }

        return try {
            // Deserialize the packet
            val packet = MessagePacket.fromByteArray(encryptedData)

            // Decrypt the message
            val decrypted = cryptoManager.decrypt(
                packet.ciphertext,
                sessionKey!!,
                packet.iv
            )

            // Verify the signature
            if (cryptoManager.verify(decrypted, packet.signature)) {
                decrypted
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks if the secure channel is ready for communication.
     */
    fun isReady(): Boolean = handshakeComplete && sessionKey != null

    /**
     * Resets the secure channel, clearing all session data.
     */
    fun reset() {
        sessionKey = null
        remotePublicKey = null
        handshakeComplete = false
    }
}

/**
 * Represents a secure message packet containing encrypted data and metadata.
 */
data class MessagePacket(
    val ciphertext: ByteArray,
    val iv: ByteArray,
    val signature: ByteArray
) {
    companion object {
        /**
         * Deserializes a MessagePacket from a byte array.
         */
        fun fromByteArray(data: ByteArray): MessagePacket {
            val input = java.io.ByteArrayInputStream(data)
            val dis = java.io.DataInputStream(input)

            // Read lengths
            val ciphertextLength = dis.readInt()
            val ivLength = dis.readInt()
            val signatureLength = dis.readInt()

            // Read data
            val ciphertext = ByteArray(ciphertextLength)
            dis.readFully(ciphertext)

            val iv = ByteArray(ivLength)
            dis.readFully(iv)

            val signature = ByteArray(signatureLength)
            dis.readFully(signature)

            return MessagePacket(ciphertext, iv, signature)
        }
    }

    /**
     * Serializes the MessagePacket to a byte array.
     */
    fun toByteArray(): ByteArray {
        val output = java.io.ByteArrayOutputStream()
        val dos = java.io.DataOutputStream(output)

        // Write lengths
        dos.writeInt(ciphertext.size)
        dos.writeInt(iv.size)
        dos.writeInt(signature.size)

        // Write data
        dos.write(ciphertext)
        dos.write(iv)
        dos.write(signature)

        return output.toByteArray()
    }

    // Override equals and hashCode for proper data class behavior
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessagePacket

        if (!ciphertext.contentEquals(other.ciphertext)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!signature.contentEquals(other.signature)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ciphertext.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}
