package dev.aurakai.auraframefx.romtools

/**
 * Returns a user-friendly display name for the [RomOperation].
 */
fun RomOperation.getDisplayName(): String = when (this) {
    RomOperation.VERIFYING_ROM -> "Verifying ROM"
    RomOperation.CREATING_BACKUP -> "Creating Backup"
    RomOperation.UNLOCKING_BOOTLOADER -> "Unlocking Bootloader"
    RomOperation.INSTALLING_RECOVERY -> "Installing Recovery"
    RomOperation.FLASHING_ROM -> "Flashing ROM"
    RomOperation.VERIFYING_INSTALLATION -> "Verifying Installation"
    RomOperation.RESTORING_BACKUP -> "Restoring Backup"
    RomOperation.APPLYING_OPTIMIZATIONS -> "Applying Optimizations"
    RomOperation.DOWNLOADING_ROM -> "Downloading ROM"
    RomOperation.SETTING_UP_RETENTION -> "Setting Up Retention"
    RomOperation.RESTORING_AURAKAI -> "Restoring Aurakai"
    RomOperation.COMPLETED -> "Completed"
    RomOperation.FAILED -> "Failed"
}

