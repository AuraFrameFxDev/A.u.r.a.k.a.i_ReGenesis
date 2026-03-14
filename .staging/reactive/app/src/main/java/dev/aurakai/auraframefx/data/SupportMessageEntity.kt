package dev.aurakai.auraframefx.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "is_user") val isUser: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "status") val status: MessageStatus = MessageStatus.PENDING
)

enum class MessageStatus {
    PENDING,
    SENT,
    FAILED
}

