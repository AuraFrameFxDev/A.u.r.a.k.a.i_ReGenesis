package dev.aurakai.auraframefx.oracledrive

/**
 * DEPRECATED — Old pre-merge OracleDriveServiceImpl.
 *
 * The canonical implementation lives at:
 *   dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveServiceImpl
 *
 * This file was importing phantom packages (ai.agents.*, core.models.*, core.security.*)
 * that don't exist in the current architecture. All methods also mismatched the
 * OracleDriveService interface in this same package.
 *
 * Safe to delete entirely in next cleanup pass.
 */
// Intentionally empty — all logic moved to domains/genesis/oracledrive/service/
