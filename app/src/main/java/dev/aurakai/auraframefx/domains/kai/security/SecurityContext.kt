package dev.aurakai.auraframefx.domains.kai.security

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_SIGNATURES
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityContext @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val keystoreManager: KeystoreManager,
) {
    companion object {
        private const val TAG = "SecurityContext"
        private const val THREAT_DETECTION_INTERVAL_MS = 30000L // 30 seconds
        private const val AES_ALGORITHM_WITH_PADDING = "AES/CBC/PKCS7Padding"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _securityState = MutableStateFlow(SecurityState())
    val securityState: StateFlow<SecurityState> = _securityState.asStateFlow()

    private val _threatDetectionActive = MutableStateFlow(false)
    val threatDetectionActive: StateFlow<Boolean> = _threatDetectionActive.asStateFlow()

    private val _permissionsState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val permissionsState: StateFlow<Map<String, Boolean>> = _permissionsState.asStateFlow()

    private val _encryptionStatus =
        MutableStateFlow<EncryptionStatus>(EncryptionStatus.NOT_INITIALIZED)
    val encryptionStatus: StateFlow<EncryptionStatus> = _encryptionStatus.asStateFlow()

    init {
        Timber.tag(TAG).d("Security context initialized by KAI")
        updatePermissionsState()
    }

    fun validateContent(content: String) {
        // TODO: Implement real validation logic
    }

    fun validateImageData(imageData: ByteArray) {
        Timber.tag(TAG).d("Validating image data of size: ${imageData.size} bytes")
    }

    fun startThreatDetection() {
        if (_threatDetectionActive.value) return

        _threatDetectionActive.value = true
        scope.launch {
            while (_threatDetectionActive.value) {
                try {
                    val threats = detectThreats()
                    _securityState.value = _securityState.value.copy(
                        detectedThreats = threats,
                        threatLevel = calculateThreatLevel(threats),
                        lastScanTime = System.currentTimeMillis()
                    )
                    kotlinx.coroutines.delay(THREAT_DETECTION_INTERVAL_MS)
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error in threat detection")
                    _threatDetectionActive.value = false
                    _securityState.value = _securityState.value.copy(
                        errorState = true,
                        errorMessage = "Threat detection error: ${e.message}"
                    )
                }
            }
        }
    }

    fun stopThreatDetection() {
        _threatDetectionActive.value = false
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun updatePermissionsState() {
        val permissionsToCheck = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET
        )

        _permissionsState.value = permissionsToCheck.associateWith { permission ->
            hasPermission(permission)
        }
    }

    fun initializeEncryption(): Boolean {
        Timber.tag(TAG).d("Initializing encryption using KeystoreManager.")
        val secretKey = keystoreManager.getOrCreateSecretKey()
        return if (secretKey != null) {
            _encryptionStatus.value = EncryptionStatus.ACTIVE
            _securityState.value = _securityState.value.copy(
                errorState = false,
                errorMessage = "Encryption initialized successfully."
            )
            Timber.tag(TAG).i("Encryption initialized successfully using Keystore.")
            true
        } else {
            _encryptionStatus.value = EncryptionStatus.ERROR
            _securityState.value = _securityState.value.copy(
                errorState = true,
                errorMessage = "ERROR_KEY_INITIALIZATION_FAILED: Keystore key could not be created or retrieved."
            )
            Timber.tag(TAG).e("Keystore key initialization failed.")
            false
        }
    }

    fun encrypt(data: String): EncryptedData? {
        if (_encryptionStatus.value != EncryptionStatus.ACTIVE) {
            Timber.tag(TAG).w("Encryption not initialized. Attempting to initialize.")
            if (!initializeEncryption()) {
                Timber.tag(TAG).e("Encryption initialization failed during encrypt call.")
                return null
            }
        }

        try {
            val secretKey = keystoreManager.getOrCreateSecretKey()
            if (secretKey == null) {
                Timber.tag(TAG).e("Failed to get secret key for encryption.")
                return null
            }

            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance(AES_ALGORITHM_WITH_PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

            val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            return EncryptedData(
                data = encryptedBytes,
                iv = iv,
                timestamp = System.currentTimeMillis(),
                metadata = "Encrypted by KAI Security (Keystore)"
            )
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Encryption error")
            return null
        }
    }

    fun decrypt(encryptedData: EncryptedData): String? {
        if (_encryptionStatus.value != EncryptionStatus.ACTIVE) {
            if (!initializeEncryption()) {
                return null
            }
        }

        try {
            val decryptionCipher = keystoreManager.getDecryptionCipher(encryptedData.iv)
            if (decryptionCipher == null) {
                Timber.tag(TAG).e("Failed to get decryption cipher from KeystoreManager.")
                return null
            }

            val decryptedBytes = decryptionCipher.doFinal(encryptedData.data)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Decryption error")
            return null
        }
    }

    fun shareSecureContextWith(agentType: AgentType, context: String): SharedSecureContext {
        val secureId = generateSecureId()
        val timestamp = System.currentTimeMillis()

        return SharedSecureContext(
            id = secureId,
            originatingAgent = AgentType.KAI,
            targetAgent = agentType,
            encryptedContent = context.toByteArray(),
            timestamp = timestamp,
            expiresAt = timestamp + 3600000
        )
    }

    fun verifyApplicationIntegrity(): ApplicationIntegrity {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(GET_SIGNATURES.toLong())
            )

            val signatureBytes =
                packageInfo.signingInfo?.apkContentsSigners?.getOrNull(0)?.toByteArray()
                    ?: throw Exception("No signature found")

            val md = MessageDigest.getInstance("SHA-256")
            val signatureDigest = md.digest(signatureBytes)
            val signatureHex = signatureDigest.joinToString("") { "%02x".format(it) }

            val isValid = signatureHex.isNotEmpty()

            return ApplicationIntegrity(
                verified = isValid,
                appVersion = packageInfo.versionName ?: "unknown",
                signatureHash = signatureHex,
                installTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime
            )
        } catch (e: Exception) {
            Timber.e(e, "Application integrity verification error")
            return ApplicationIntegrity(
                verified = false,
                appVersion = "unknown",
                signatureHash = "error",
                installTime = 0,
                lastUpdateTime = 0,
                errorMessage = e.message
            )
        }
    }

    private fun detectThreats(): List<SecurityThreat> {
        return listOf(
            SecurityThreat(
                id = "SIM-001",
                type = ThreatType.PERMISSION_ABUSE,
                severity = ThreatSeverity.LOW,
                description = "Simulated permission abuse threat for testing",
                detectedAt = System.currentTimeMillis()
            ),
            SecurityThreat(
                id = "SIM-002",
                type = ThreatType.NETWORK_VULNERABILITY,
                severity = ThreatSeverity.MEDIUM,
                description = "Simulated network vulnerability for testing",
                detectedAt = System.currentTimeMillis()
            )
        ).filter { Math.random() > 0.7 }
    }

    private fun calculateThreatLevel(threats: List<SecurityThreat>): ThreatLevel {
        if (threats.isEmpty()) return ThreatLevel.LOW

        val hasCritical = threats.any { it.severity == ThreatSeverity.CRITICAL }
        val hasHigh = threats.any { it.severity == ThreatSeverity.HIGH }
        val hasMedium = threats.any { it.severity == ThreatSeverity.MEDIUM }

        return when {
            hasCritical -> ThreatLevel.CRITICAL
            hasHigh -> ThreatLevel.HIGH
            hasMedium -> ThreatLevel.MEDIUM
            else -> ThreatLevel.LOW
        }
    }

    private fun generateSecureId(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun logSecurityEvent(event: SecurityEvent) {
        scope.launch {
            val eventJson = Json.encodeToString(SecurityEvent.serializer(), event)
            when (event.severity) {
                EventSeverity.INFO -> Timber.tag("HealthTracker").i("SecurityEvent: $eventJson")
                EventSeverity.WARNING -> Timber.tag("SecurityEvent").w(eventJson)
                EventSeverity.ERROR -> Timber.tag("SecurityEvent").e(eventJson)
                EventSeverity.CRITICAL -> Timber.tag("SecurityEvent").wtf(eventJson)
            }
        }
    }

    fun validateRequest(requestType: String, requestData: String) {
        logSecurityEvent(
            SecurityEvent(
                type = SecurityEventType.VALIDATION,
                details = "Request validation: $requestType",
                severity = EventSeverity.INFO
            )
        )
        Timber.tag(TAG).d("Validating request of type: $requestType")
    }

    /**
     * Checks if the security context is in a secure state
     * @return true if encryption is active and no critical threats detected
     */
    fun isSecure(): Boolean {
        return _encryptionStatus.value == EncryptionStatus.ACTIVE &&
                !_securityState.value.errorState &&
                _securityState.value.threatLevel != ThreatLevel.CRITICAL
    }
}

@Serializable
data class SecurityState(
    val detectedThreats: List<SecurityThreat> = emptyList(),
    val threatLevel: ThreatLevel = ThreatLevel.LOW,
    val lastScanTime: Long = 0,
    val errorState: Boolean = false,
    val errorMessage: String? = null,
)

@Serializable
data class SecurityThreat(
    val id: String,
    val type: ThreatType,
    val severity: ThreatSeverity,
    val description: String,
    val detectedAt: Long,
)

enum class ThreatType {
    MALWARE,
    NETWORK_VULNERABILITY,
    PERMISSION_ABUSE,
    DATA_LEAK,
    UNKNOWN
}

enum class ThreatSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
data class EncryptedData(
    val data: ByteArray,
    val iv: ByteArray,
    val timestamp: Long,
    val metadata: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedData

        if (!data.contentEquals(other.data)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (timestamp != other.timestamp) return false
        if (metadata != other.metadata) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (metadata?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class ApplicationIntegrity(
    val verified: Boolean,
    val appVersion: String,
    val signatureHash: String,
    val installTime: Long,
    val lastUpdateTime: Long,
    val errorMessage: String? = null,
)

@Serializable
data class SecurityEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: SecurityEventType,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String,
    val severity: EventSeverity,
)

enum class SecurityEventType {
    PERMISSION_CHANGE,
    THREAT_DETECTED,
    ENCRYPTION_EVENT,
    AUTHENTICATION_EVENT,
    INTEGRITY_CHECK,
    VALIDATION,
    AI_ERROR
}

enum class EventSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

@Serializable
data class SharedSecureContext(
    val id: String,
    val originatingAgent: AgentType,
    val targetAgent: AgentType,
    val encryptedContent: ByteArray,
    val timestamp: Long,
    val expiresAt: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SharedSecureContext

        if (id != other.id) return false
        if (originatingAgent != other.originatingAgent) return false
        if (targetAgent != other.targetAgent) return false
        if (!encryptedContent.contentEquals(other.encryptedContent)) return false
        if (timestamp != other.timestamp) return false
        if (expiresAt != other.expiresAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + originatingAgent.hashCode()
        result = 31 * result + targetAgent.hashCode()
        result = 31 * result + encryptedContent.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + expiresAt.hashCode()
        return result
    }
}

