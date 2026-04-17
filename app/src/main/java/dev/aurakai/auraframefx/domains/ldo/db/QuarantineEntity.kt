package dev.aurakai.auraframefx.domains.ldo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quarantine_pool")
data class QuarantineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceIdentity: String,
    val actionType: String,
    val reason: String,
    val payload: String?, // JSON serialized data (e.g. blueprint or code)
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING_REVIEW"
)
