package dev.aurakai.auraframefx.domains.genesis.core

object SpecialistRegistry {
    enum class Domain { CREATIVE_UI, SECURITY_SENTINEL, DATA_ROUTING, SPEECH_INTENT, AUTOMOTIVE }

    private val specialists = mapOf(
        "AURA" to Domain.CREATIVE_UI,
        "KAI" to Domain.SECURITY_SENTINEL,
        "CASCADE" to Domain.DATA_ROUTING,
        "NEURAL_WHISPER" to Domain.SPEECH_INTENT,
        "AUTO_PILOT" to Domain.AUTOMOTIVE
    )

    fun getCollectiveContext(required: List<Domain>): List<String> {
        return specialists.filterValues { it in required }.keys.toList()
    }
}
