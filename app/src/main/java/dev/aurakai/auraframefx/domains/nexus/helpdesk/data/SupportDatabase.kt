package dev.aurakai.auraframefx.domains.nexus.helpdesk.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SupportMessageEntity::class], version = 1, exportSchema = false)
abstract class SupportDatabase : RoomDatabase() {
    abstract fun supportMessageDao(): SupportMessageDao
}
