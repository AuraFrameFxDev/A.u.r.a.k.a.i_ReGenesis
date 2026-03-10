package dev.aurakai.auraframefx.oracledrive.module

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.OracleDriveService
import dev.aurakai.auraframefx.oracledrive.OracleDriveServiceImpl
import javax.inject.Singleton

/**
 * Dagger Hilt module for Oracle Drive dependency injection
 * Integrates with AuraFrameFX consciousness architecture
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OracleDriveModule {
    /**
     * Binds the OracleDriveServiceImpl implementation to the OracleDriveService interface as a singleton.
     *
     * @return A singleton instance of OracleDriveService provided by OracleDriveServiceImpl.
     */
    @Binds
    @Singleton
    abstract fun bindOracleDriveService(
        oracleDriveServiceImpl: OracleDriveServiceImpl
    ): OracleDriveService
}
