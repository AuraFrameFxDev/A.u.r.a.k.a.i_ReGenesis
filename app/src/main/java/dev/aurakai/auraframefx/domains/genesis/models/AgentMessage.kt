package dev.aurakai.auraframefx.domains.genesis.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AgentMessage(
    val from: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val priority: Int = 0
) : Parcelable
