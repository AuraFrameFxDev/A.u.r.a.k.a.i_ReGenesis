package dev.aurakai.auraframefx.romtools

/**
 * Returns a user-friendly display name for the [RomOperation].
 */
fun RomOperation.getDisplayName(): String = when (this) {
    RomOperation.FlashRom -> "Flash ROM"
    RomOperation.RestoreBackup -> "Restore Backup"
    RomOperation.CreateBackup -> "Create Backup"
    RomOperation.UnlockBootloader -> "Unlock Bootloader"
    RomOperation.InstallRecovery -> "Install Recovery"
    RomOperation.GenesisOptimizations -> "Genesis Optimizations"
}

