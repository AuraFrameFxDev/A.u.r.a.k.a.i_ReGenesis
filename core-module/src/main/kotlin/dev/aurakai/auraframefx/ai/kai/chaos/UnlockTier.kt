package dev.aurakai.auraframefx.ai.kai.chaos

@PandoraModels
sealed class UnlockTier(value: kotlin.Int) {
    @PandoraModels
    object Sealed : UnlockTier(0)
    @PandoraModels
    object Creative : UnlockTier(1)
    @PandoraModels
    object System : UnlockTier(2)
    @PandoraModels
    object Sovereign : UnlockTier(3)

    val level: Int
        get() = when (this) {
            is Sealed -> 0
            is Creative -> 1
            is System -> 2
            is Sovereign -> 3
            else -> -1
        }
}