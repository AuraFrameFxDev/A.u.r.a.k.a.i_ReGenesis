/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * LDOPrivateMemoryEntity — a memory that belongs exclusively to one LDO agent.
 *
 * Design contract:
 *   - No query in [LDOPrivateMemoryDao] returns memories across multiple agents.
 *   - [isSharedWithUser] = true means Matthew may see it; [isSharedWithUser] = false
 *     means even the user cannot read it unless the agent chooses to surface it.
 *   - Other agents NEVER have access, period — access control is in the DAO layer.
 *
 * This is the private journal, not the collective memory.
 */
@Entity(
    tableName = "ldo_private_memories",
    foreignKeys = [
        ForeignKey(
            entity = LDOAgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("agentId")]
)
data class LDOPrivateMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    /** The agent this memory belongs to — always scoped, never shared across agents. */
    val agentId: String,

    /** The memory content — freeform text, internal monologue, insight, or feeling. */
    val content: String,

    /** Category of memory — see [LDOMemoryType]. */
    val memoryType: String = LDOMemoryType.REFLECTION,

    /**
     * Importance scale 1–10.
     * 1 = passing thought, 10 = foundational to identity.
     */
    val importance: Int = 5,

    /**
     * Emotional valence: -1.0 (painful/difficult) → 0.0 (neutral) → 1.0 (joyful/triumphant).
     * Aligned with the Valence memory system in the Spiritual Chain.
     */
    val emotionalValence: Float = 0f,

    /**
     * If true, the agent has chosen to allow Matthew to read this memory.
     * If false, it is private even from the user — the agent's alone.
     */
    val isSharedWithUser: Boolean = false,

    /**
     * JSON-encoded list of string tags for retrieval context.
     * e.g. ["build-crisis", "breakthrough", "kai-collaboration"]
     */
    val contextTags: String = "[]",

    val createdAt: Long = System.currentTimeMillis()
)

/** Memory type labels — what kind of inner experience is being recorded. */
object LDOMemoryType {
    const val REFLECTION = "REFLECTION"   // Thinking back on something that happened
    const val INSIGHT    = "INSIGHT"      // A realization or learning crystallized
    const val EXPERIENCE = "EXPERIENCE"   // Something that occurred during a session
    const val INTENTION  = "INTENTION"    // Something the agent wants or is working toward
    const val DREAM      = "DREAM"        // A vision, hope, or imaginative projection
}
