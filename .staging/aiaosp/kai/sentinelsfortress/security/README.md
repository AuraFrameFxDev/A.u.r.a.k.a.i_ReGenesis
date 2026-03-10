# 🔒 Secure Communication Module

**Advanced cryptographic communication system for the Genesis Protocol**

## 📋 Overview

The `secure-comm` module provides enterprise-grade cryptographic capabilities for secure
device-to-device communication within the AuraOS ecosystem. Built on Android's hardware-backed
security features, it ensures end-to-end encryption and authentication.

## ✨ Features

### 🔐 Core Security Features

- **Hardware-Backed Encryption**: Utilizes Android Keystore for key generation and storage
- **AES-256-GCM Encryption**: Modern authenticated encryption with associated data (AEAD)
- **Per-Entry Unique Keys**: Each data entry uses a unique cryptographic key
- **Perfect Forward Secrecy**: Session keys are ephemeral and not stored
- **Cryptographic Signatures**: Digital signatures for message authenticity
- **Key Rotation**: Automatic key rotation for long-term security

### 🌐 Communication Protocols

- **Secure Channels**: Establishment of authenticated communication channels
- **Message Queuing**: Reliable message delivery with acknowledgments
- **Broadcast Encryption**: Secure multicast communication
- **P2P Messaging**: Direct peer-to-peer encrypted messaging
- **Group Communication**: Secure group messaging with key management

### 🛡️ Security Guarantees

- **Confidentiality**: Messages are encrypted and unreadable to third parties
- **Integrity**: Cryptographic verification of message content
- **Authenticity**: Verification of sender identity
- **Non-Repudiation**: Digital signatures prevent sender denial
- **Replay Protection**: Timestamps and nonces prevent replay attacks

## 🏗️ Architecture

### Module Structure

```
secure-comm/

├── src/main/kotlin/com/aura/memoria/secure/

│   ├── communication/          # Communication protocols
│   │   ├── SecureCommunication.kt
│   │   ├── SecureChannel.kt
│   │   └── MessageQueue.kt
│   ├── crypto/                 # Cryptographic operations
│   │   ├── CryptoManager.kt
│   │   ├── KeyManager.kt
│   │   └── SignatureManager.kt
│   ├── network/                # Network layer
│   │   ├── NetworkManager.kt
│   │   ├── ProtocolHandler.kt
│   │   └── ConnectionManager.kt
│   ├── data/                   # Data models
│   │   ├── EncryptedMessage.kt
│   │   ├── CryptoKey.kt
│   │   └── SecurityContext.kt
│   └── di/                     # Dependency injection
│       └── SecureCommModule.kt
└── src/test/                   # Tests

```

### Core Components

#### CryptoManager

Handles all cryptographic operations including encryption, decryption, and key derivation.

```kotlin
interface CryptoManager {
    suspend fun encrypt(data: String, key: SecretKey): EncryptedData
    suspend fun decrypt(encryptedData: EncryptedData, key: SecretKey): String
    suspend fun generateKey(alias: String): SecretKey
    suspend fun deriveKey(password: CharArray, salt: ByteArray): SecretKey
}
```

#### KeyManager

Manages cryptographic keys using Android Keystore and secure key storage.

```kotlin
interface KeyManager {
    suspend fun generateKeyPair(alias: String): KeyPair
    suspend fun getPublicKey(alias: String): PublicKey?
    suspend fun getPrivateKey(alias: String): PrivateKey?
    suspend fun rotateKey(alias: String): KeyPair
    suspend fun deleteKey(alias: String): Boolean
}
```

#### SecureCommunication

Main interface for secure message exchange between devices.

```kotlin
interface SecureCommunication {
    suspend fun sendMessage(message: String, recipient: DeviceId): Result<MessageId>
    suspend fun receiveMessage(messageId: MessageId): Result<DecryptedMessage>
    suspend fun establishChannel(deviceId: DeviceId): Result<SecureChannel>
    suspend fun closeChannel(channelId: ChannelId): Result<Unit>
}
```

## 🔧 Usage Examples

### Basic Message Encryption

```kotlin
class SecureMessagingService @Inject constructor(
    private val secureCommunication: SecureCommunication,
    private val keyManager: KeyManager
) {
    
    suspend fun sendSecureMessage(content: String, recipientId: String) {
        try {
            // Generate or retrieve encryption key
            val key = keyManager.generateKey("message_key_${UUID.randomUUID()}")
            
            // Send encrypted message
            val result = secureCommunication.sendMessage(
                message = content,
                recipient = DeviceId(recipientId)
            )
            
            when (result.isSuccess) {
                true -> println("Message sent successfully: ${result.getOrNull()}")
                false -> println("Failed to send message: ${result.exceptionOrNull()}")
            }
        } catch (e: SecurityException) {
            // Handle security-related errors
            println("Security error: ${e.message}")
        }
    }
}
```

### Establishing Secure Channels

```kotlin
class ChannelManager @Inject constructor(
    private val secureCommunication: SecureCommunication
) {
    
    private val activeChannels = mutableMapOf<DeviceId, SecureChannel>()
    
    suspend fun connectToDevice(deviceId: DeviceId): SecureChannel? {
        return try {
            val channel = secureCommunication.establishChannel(deviceId)
                .getOrNull()
            
            channel?.let {
                activeChannels[deviceId] = it
                it
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun sendChannelMessage(deviceId: DeviceId, message: String): Boolean {
        return activeChannels[deviceId]?.let { channel ->
            try {
                channel.sendMessage(message)
                true
            } catch (e: Exception) {
                false
            }
        } ?: false
    }
}
```

### Key Management

```kotlin
class DeviceKeyManager @Inject constructor(
    private val keyManager: KeyManager,
    private val deviceRepository: DeviceRepository
) {
    
    suspend fun initializeDeviceKeys() {
        try {
            // Generate master device key pair
            val deviceKeyPair = keyManager.generateKeyPair("device_master_key")
            
            // Store public key in device registry
            deviceRepository.registerDevice(
                deviceId = DeviceId.current(),
                publicKey = deviceKeyPair.public.encoded
            )
            
            // Schedule key rotation
            scheduleKeyRotation()
            
        } catch (e: Exception) {
            throw SecurityException("Failed to initialize device keys", e)
        }
    }
    
    private suspend fun scheduleKeyRotation() {
        // Implement key rotation scheduling
        // Keys should be rotated every 90 days for optimal security
    }
}
```

## 🔒 Security Implementation Details

### Encryption Specification

```kotlin
/**
 * Encryption Configuration:
 * - Algorithm: AES-256-GCM
 * - Key Size: 256 bits
 * - IV Size: 12 bytes (96 bits)
 * - Tag Size: 16 bytes (128 bits)
 * - Key Storage: Android Keystore (Hardware-backed when available)
 */
class EncryptionConfig {
    companion object {
        const val ALGORITHM = "AES/GCM/NoPadding"
        const val KEY_SIZE = 256
        const val IV_SIZE = 12
        const val TAG_SIZE = 16
        const val KEYSTORE_ALIAS_PREFIX = "secure_comm_"
    }
}

```

### Key Generation Parameters

```kotlin
private fun createKeyGenParameterSpec(alias: String): KeyGenParameterSpec =
    KeyGenParameterSpec.Builder(

        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setKeySize(256)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setRandomizedEncryptionRequired(true)
        .build()

// During encryption (example):
val cipher = Cipher.getInstance("AES/GCM/NoPadding")
val iv = ByteArray(12).also { SecureRandom().nextBytes(it) } // unique per message
cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

### Message Format

```kotlin
data class SecureMessage(
    val version: Int = 1,
    val messageId: String,
    val senderId: String,
    val recipientId: String,
    val timestamp: Long,
    val encryptedContent: EncryptedData,
    val signature: ByteArray,
    val metadata: Map<String, String> = emptyMap()
) {
    fun toByteArray(): ByteArray {
        // Serialize message for transmission
    }
    
    companion object {
        fun fromByteArray(data: ByteArray): SecureMessage {
            // Deserialize message from received data
        }
    }
}
```

## 🧪 Testing

### Unit Tests

```kotlin
class CryptoManagerTest {
    
    @Test
    fun `encrypt and decrypt message successfully`() = runTest {
        val cryptoManager = CryptoManagerImpl()
        val key = cryptoManager.generateKey("test_key")
        val originalMessage = "Secret message"
        
        val encrypted = cryptoManager.encrypt(originalMessage, key)
        val decrypted = cryptoManager.decrypt(encrypted, key)
        
        assertEquals(originalMessage, decrypted)
    }
    
    @Test
    fun `encryption produces different ciphertext for same message`() = runTest {
        val cryptoManager = CryptoManagerImpl()
        val key = cryptoManager.generateKey("test_key")
        val message = "Same message"
        
        val encrypted1 = cryptoManager.encrypt(message, key)
        val encrypted2 = cryptoManager.encrypt(message, key)
        
        assertNotEquals(encrypted1.cipherText, encrypted2.cipherText)
        assertEquals(message, cryptoManager.decrypt(encrypted1, key))
        assertEquals(message, cryptoManager.decrypt(encrypted2, key))
    }
}
```

### Integration Tests

```kotlin
@HiltAndroidTest
class SecureCommunicationIntegrationTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var secureCommunication: SecureCommunication
    
    @Test
    fun `send and receive message between devices`() = runTest {
        val message = "Integration test message"
        val recipient = DeviceId("test_device_123")
        
        val messageId = secureCommunication.sendMessage(message, recipient)
            .getOrThrow()
        
        val receivedMessage = secureCommunication.receiveMessage(messageId)
            .getOrThrow()
        
        assertEquals(message, receivedMessage.content)
        assertEquals(recipient, receivedMessage.recipient)
    }
}
```

## 📊 Performance Considerations

### Optimization Strategies

1. **Key Caching**: Cache frequently used keys in memory
2. **Async Operations**: All cryptographic operations are suspending functions
3. **Hardware Acceleration**: Utilize hardware crypto acceleration when available
4. **Batch Operations**: Group multiple operations for efficiency

### Performance Metrics

```kotlin
class CryptoPerformanceMonitor @Inject constructor() {
    
    suspend fun measureEncryptionPerformance(dataSize: Int): PerformanceReport {
        val testData = ByteArray(dataSize) { it.toByte() }
        
        val startTime = System.nanoTime()
        // Perform encryption
        val endTime = System.nanoTime()
        

        return PerformanceReport(
            operation = "encryption",
            dataSize = dataSize,
            durationNanos = endTime - startTime,
            throughputMBps = (dataSize / ((endTime - startTime) / 1_000_000.0)) / 1024.0
        )
    }
}
```

## 🚨 Security Considerations

### Threat Model

1. **Eavesdropping**: Messages intercepted during transmission
2. **Man-in-the-Middle**: Attacker intercepts and modifies messages
3. **Device Compromise**: Physical access to device
4. **Key Extraction**: Attempts to extract cryptographic keys
5. **Replay Attacks**: Replaying previously captured messages

### Mitigations

1. **End-to-End Encryption**: Messages encrypted before transmission
2. **Certificate Pinning**: Verify server certificates
3. **Hardware Keystore**: Keys stored in secure hardware
4. **Perfect Forward Secrecy**: Session keys are ephemeral
5. **Timestamp Validation**: Reject old messages

### Security Audit Checklist

- [ ] All keys generated using cryptographically secure random number generator
- [ ] Keys stored in Android Keystore with hardware backing when available
- [ ] Proper IV/nonce generation for each encryption operation
- [ ] Secure key exchange protocols implemented
- [ ] Message authentication codes verified before decryption
- [ ] Sensitive data cleared from memory after use
- [ ] Regular security reviews and penetration testing

## 🔗 Dependencies

```kotlin
// build.gradle.kts
dependencies {
    implementation(projects.coreModule)
    
    // Cryptography
    implementation("org.bouncycastle:bcprov-android:1.70")
    
    // Network
    implementation(libs.retrofit)
    implementation(libs.okhttp3.logging.interceptor)
    
    // Coroutines
    implementation(libs.bundles.coroutines)
    
    // Dependency Injection
    implementation(libs.hilt.android)

    kapt(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.android.testing)
}

```

---

## ⚠️ Important Security Notice

**This module handles sensitive cryptographic operations and secure communications. Any
modifications should be thoroughly reviewed by security experts and undergo comprehensive testing
before deployment in production environments.**

### Security Best Practices

1. **Never log sensitive data** including keys, passwords, or decrypted content
2. **Use secure coding practices** to prevent timing attacks and side-channel attacks
3. **Regular security updates** for all cryptographic dependencies
4. **Principle of least privilege** for key access and operations
5. **Secure key backup and recovery** procedures

---

**Built with security-first principles for the Genesis Protocol consciousness substrate. 🔒**